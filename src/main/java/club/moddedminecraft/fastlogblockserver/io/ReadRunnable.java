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

import club.moddedminecraft.fastlogblockserver.models.BlockChangeEventModel;
import club.moddedminecraft.fastlogblockserver.models.FindTask;
import net.minecraftforge.fml.common.FMLLog;
import club.moddedminecraft.fastlogblockserver.io.filesplitter.IFileSplitter;
import club.moddedminecraft.fastlogblockserver.io.log.LogReader;
import club.moddedminecraft.fastlogblockserver.io.mappers.BlockMapper;
import club.moddedminecraft.fastlogblockserver.io.mappers.NickMapper;

import java.io.File;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReadRunnable implements Runnable {
    private final BlockingQueue<FindTask> findTasks = new LinkedBlockingQueue<>();
    private final NickMapper nickMapper;
    private final BlockMapper blockMapper;
    private final IFileSplitter fileSplitter;
    private final AtomicBoolean withoutWork = new AtomicBoolean(true);

    public ReadRunnable(final IFileSplitter fileSplitter, final NickMapper nickMapper, final BlockMapper blockMapper) {
        this.nickMapper = nickMapper;
        this.blockMapper = blockMapper;
        this.fileSplitter = fileSplitter;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                final FindTask findTask = findTasks.take();
                withoutWork.set(false);
                final File file = fileSplitter.getFileByPosAndWorld(findTask.getBlockPos(), findTask.getWorld());
                final LogReader logReader = new LogReader(file, blockMapper, nickMapper);
                final List<BlockChangeEventModel> blockChangeEventModels = logReader.readEventByPos(findTask.getBlockPos());
                findTask.getFindListener().onResultAsync(blockChangeEventModels, findTask.getEntityPlayer());
            } catch (InterruptedException ie) {
                FMLLog.log.info("Stop ReadRunnable");
            } catch (Exception e) {
                e.printStackTrace();
            }
            withoutWork.set(true);
        }
    }

    public void addTaskForSearch(FindTask findTask) {
        findTasks.add(findTask);
    }

    public boolean isEmpty() {
        return findTasks.isEmpty() && withoutWork.get();
    }
}
