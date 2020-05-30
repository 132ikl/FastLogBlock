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

package club.moddedminecraft.fastlogblockserver.io.filesplitter.impl;

import club.moddedminecraft.fastlogblockserver.config.LogConfig;
import club.moddedminecraft.fastlogblockserver.io.filesplitter.IFileSplitter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BlockHashFileSplitter extends IFileSplitter {
    private static final int MAGIC_HASH_NUMBER = 31;

    public BlockHashFileSplitter(final File modFolder) {
        super(modFolder);
    }

    private static int hashByBlock(final BlockPos blockPos) {
        final int signedHash = ((blockPos.getX() * MAGIC_HASH_NUMBER +
                blockPos.getY()) * MAGIC_HASH_NUMBER +
                blockPos.getZ()) * MAGIC_HASH_NUMBER;
        final int hash = Math.abs(signedHash) % LogConfig.HASH_CONFIG.fileCount;
        return hash < 0 ? 0 : hash;
    }

    @Override
    public File[] getAllLogFile() {
        final List<File> fileList = new ArrayList<>(LogConfig.HASH_CONFIG.fileCount);
        for (int i = 0; i < LogConfig.HASH_CONFIG.fileCount; i++) {
            fileList.add(new File(this.modFolder,
                    String.format(LogConfig.HASH_CONFIG.fileNamePattern, i)
            ));
        }
        return fileList.toArray(new File[fileList.size()]);
    }

    @Override
    public File getFileByPosAndWorld(final BlockPos blockPos, World world) {
        if (world == null) {
            return new File(modFolder, String.format(LogConfig.HASH_CONFIG.fileNamePattern, hashByBlock(blockPos)));
        }

        File saveFile = DimensionManager.getCurrentSaveRootDirectory();
        if (saveFile == null) {
            saveFile = new File("save0");
        }
        final File saveFolder = new File(modFolder, saveFile.getName());
        String worldSave = world.provider.getSaveFolder();
        if (worldSave == null) {
            worldSave = "DIM0";
        }
        final File dimFolder = new File(saveFolder, new File(worldSave).getName());
        return new File(dimFolder, String.format(LogConfig.HASH_CONFIG.fileNamePattern, hashByBlock(blockPos)));
    }


}
