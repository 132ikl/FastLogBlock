/*
 * Copyright (c) 2020 LionZXY
 * Copyright (c) 2020 132ikl
 * This file is part of FastLogBlockServer.
 *
 * FastLogBlockServer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FastLogBlockServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FastLogBlockServer.  If not, see <https://www.gnu.org/licenses/>.
 */

package club.moddedminecraft.fastlogblockserver.handlers;

import club.moddedminecraft.fastlogblockserver.config.LogConfig;
import club.moddedminecraft.fastlogblockserver.models.BlockChangeEventModelWithWorld;
import net.minecraftforge.fml.common.FMLLog;
import club.moddedminecraft.fastlogblockserver.io.ReadRunnable;
import club.moddedminecraft.fastlogblockserver.io.WriteRunnable;
import club.moddedminecraft.fastlogblockserver.io.filesplitter.IFileSplitter;
import club.moddedminecraft.fastlogblockserver.io.filesplitter.impl.BlockHashFileSplitter;
import club.moddedminecraft.fastlogblockserver.io.filesplitter.impl.SingleFileSplitter;
import club.moddedminecraft.fastlogblockserver.io.mappers.BlockMapper;
import club.moddedminecraft.fastlogblockserver.io.mappers.NickMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SplitterRunnable implements Runnable {
    private final BlockingQueue<BlockChangeEventModelWithWorld> eventQueue = new LinkedBlockingQueue<>();
    private final WriteRunnable[] writeWorkers = new WriteRunnable[LogConfig.writeWorkersCount];
    private final Map<File, WriteRunnable> fileToWriteWorker = new HashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    private final IFileSplitter fileSplitter;
    private final NickMapper nickMapper;
    private final BlockMapper blockMapper;

    public SplitterRunnable() throws IOException {
        final File rootFile = new File(LogConfig.logFolderPath);

        switch (LogConfig.fileSplitterType) {
            case SINGLE:
                fileSplitter = new SingleFileSplitter(rootFile);
                break;
            default:
            case BLOCKHASH:
                fileSplitter = new BlockHashFileSplitter(rootFile);
                break;
        }
        this.nickMapper = new NickMapper(new File(rootFile, LogConfig.nickToIntFilePath));
        this.blockMapper = new BlockMapper(new File(rootFile, LogConfig.blockToLongFilePath));

        for (int i = 0; i < LogConfig.writeWorkersCount; i++) {
            writeWorkers[i] = new WriteRunnable(fileSplitter, nickMapper, blockMapper);
        }
    }

    public void runWorkers(Executor executor) {
        for (int i = 0; i < LogConfig.writeWorkersCount; i++) {
            executor.execute(writeWorkers[i]);
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                processEvent(eventQueue.take());
                nickMapper.sync();
                blockMapper.sync();
            } catch (InterruptedException ie) {
                FMLLog.log.info("Stop SplitterRunnable");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addEvent(BlockChangeEventModelWithWorld blockChangeEventModelWithWorld) {
        eventQueue.add(blockChangeEventModelWithWorld);
    }

    public ReadRunnable getReadRunnable() {
        return new ReadRunnable(fileSplitter, nickMapper, blockMapper);
    }

    private void processEvent(BlockChangeEventModelWithWorld event) {
        final File file = fileSplitter.getFileByPosAndWorld(event.getBlockPos(), event.getWorld());

        if (event.isIgnore()) {
            return;
        }

        WriteRunnable writeRunnable = fileToWriteWorker.get(file);
        if (writeRunnable != null) {
            writeRunnable.putEvent(event);
            return;
        }

        if (counter.get() >= LogConfig.writeWorkersCount) {
            counter.set(0);
        }

        writeRunnable = writeWorkers[counter.getAndIncrement()];
        fileToWriteWorker.put(file, writeRunnable);
        writeRunnable.putEvent(event);
    }
}
