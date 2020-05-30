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

package club.moddedminecraft.fastlogblockserver.models;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FindTask {
    private final BlockPos blockPos;
    private final IFindResultListener findListener;
    private final World world;
    private EntityPlayer entityPlayer;

    public FindTask(BlockPos blockPos, IFindResultListener findListener, World world) {
        this.blockPos = blockPos;
        this.findListener = findListener;
        this.world = world;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public IFindResultListener getFindListener() {
        return findListener;
    }

    public World getWorld() {
        return world;
    }

    public EntityPlayer getEntityPlayer() {
        return entityPlayer;
    }

    public void setEntityPlayer(EntityPlayer entityPlayer) {
        this.entityPlayer = entityPlayer;
    }
}
