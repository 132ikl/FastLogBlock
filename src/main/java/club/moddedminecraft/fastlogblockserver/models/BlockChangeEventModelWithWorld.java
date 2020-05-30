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

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.sql.Timestamp;

public class BlockChangeEventModelWithWorld extends BlockChangeEventModel {
    private final World world;

    public BlockChangeEventModelWithWorld(BlockPos blockPos, String nameblock, String playernick, Timestamp timestamp, BlockChangeType blockChangeType, World world) {
        super(blockPos, nameblock, playernick, timestamp, blockChangeType);
        this.world = world;
    }

    public World getWorld() {
        return world;
    }
}
