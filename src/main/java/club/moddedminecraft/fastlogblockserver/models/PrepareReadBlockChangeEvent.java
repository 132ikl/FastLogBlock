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

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TLongObjectMap;
import net.minecraft.util.math.BlockPos;

import java.sql.Timestamp;

public class PrepareReadBlockChangeEvent {
    private final int posX;
    private final int posY;
    private final int posZ;
    private final long blockId;
    private final int playerid;
    private final long timestamp;
    private final BlockChangeType blockChangeType;

    public PrepareReadBlockChangeEvent(final int posX, final int posY, final int posZ,
                                       final long blockid,
                                       final int playerid,
                                       final long timestamp,
                                       final BlockChangeType blockChangeType) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.blockId = blockid;
        this.playerid = playerid;
        this.timestamp = timestamp;
        this.blockChangeType = blockChangeType;
    }

    public long getBlockId() {
        return blockId;
    }

    public int getPlayerid() {
        return playerid;
    }

    public BlockChangeEventModel toBlockChangeEventModel(final TIntObjectMap<ASCIString> idToNick, final TLongObjectMap<ASCIString> idToBlock) {
        return new BlockChangeEventModel(new BlockPos(posX, posY, posZ),
                idToBlock.get(blockId).toString(),
                idToNick.get(playerid).toString(),
                new Timestamp(timestamp),
                blockChangeType);

    }
}
