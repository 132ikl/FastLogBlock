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

import club.moddedminecraft.fastlogblockserver.io.filesplitter.IFileSplitter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.io.File;

public class SingleFileSplitter extends IFileSplitter {
    private final File logFile;

    public SingleFileSplitter(final File modFolder) {
        super(modFolder);
        logFile = new File(modFolder, "block.bytelog");
    }

    @Override
    public File[] getAllLogFile() {
        return new File[]{logFile};
    }

    @Override
    public File getFileByPosAndWorld(final BlockPos blockPos, World world) {
        if (world == null) {
            return new File(modFolder, "block.bytelog");
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
        return new File(dimFolder, "block.bytelog");
    }
}
