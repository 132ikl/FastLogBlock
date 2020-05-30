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

package club.moddedminecraft.fastlogblockserver.io;

import club.moddedminecraft.fastlogblockserver.io.filesplitter.IFileSplitter;
import club.moddedminecraft.fastlogblockserver.io.log.LogWritter;
import club.moddedminecraft.fastlogblockserver.io.mappers.BlockMapper;
import club.moddedminecraft.fastlogblockserver.io.mappers.NickMapper;
import club.moddedminecraft.fastlogblockserver.models.BlockChangeEventModelWithWorld;
import net.minecraftforge.fml.common.FMLLog;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class WriteRunnable implements Runnable {
    private final BlockingQueue<BlockChangeEventModelWithWorld> eventQueue = new LinkedBlockingQueue<>();
    private final Map<File, LogWritter> writterMap = new HashMap<>();
    private final NickMapper nickMapper;
    private final BlockMapper blockMapper;
    private final IFileSplitter fileSplitter;
    private final AtomicBoolean withoutWork = new AtomicBoolean(true);

    public WriteRunnable(final IFileSplitter fileSplitter, final NickMapper nickMapper, final BlockMapper blockMapper) {
        this.nickMapper = nickMapper;
        this.blockMapper = blockMapper;
        this.fileSplitter = fileSplitter;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                do {
                    final BlockChangeEventModelWithWorld event = eventQueue.take();
                    withoutWork.set(false);
                    final File file = fileSplitter.getFileByPosAndWorld(event.getBlockPos(), event.getWorld());
                    LogWritter writter = writterMap.get(file);
                    if (writter == null) {
                        writter = new LogWritter(file, blockMapper, nickMapper);
                        writterMap.put(file, writter);
                    }
                    writter.putEvent(event);
                } while (!eventQueue.isEmpty());

                writterMap.values().forEach(it -> {
                    try {
                        it.sync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                withoutWork.set(true);
            } catch (InterruptedException ie) {
                FMLLog.log.info("Stop ReadRunnable");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        writterMap.values().forEach(it -> {
            try {
                it.sync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void putEvent(BlockChangeEventModelWithWorld blockChangeEventModel) {
        eventQueue.add(blockChangeEventModel);
    }

    public boolean isEmpty() {
        return eventQueue.isEmpty() && withoutWork.get();
    }
}
