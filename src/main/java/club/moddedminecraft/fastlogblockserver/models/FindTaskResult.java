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

import java.util.List;

public class FindTaskResult {
    private final List<BlockChangeEventModel> blockChangeEventModels;
    private final EntityPlayer entityPlayer;

    public FindTaskResult(List<BlockChangeEventModel> blockChangeEventModels, EntityPlayer entityPlayer) {
        this.blockChangeEventModels = blockChangeEventModels;
        this.entityPlayer = entityPlayer;
    }

    public EntityPlayer getEntityPlayer() {
        return entityPlayer;
    }

    public List<BlockChangeEventModel> getBlockChangeEventModels() {

        return blockChangeEventModels;
    }
}
