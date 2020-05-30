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

public enum BlockChangeType {
    INSERT(0),
    REMOVE(1),
    UPDATE(2),
    UNKNOWN(100);
    private final byte typeId;

    BlockChangeType(final int typeid) {
        this.typeId = (byte) typeid;
    }

    public static BlockChangeType valueOf(final byte typeId) {
        for (final BlockChangeType blockChangeType : BlockChangeType.values()) {
            if (blockChangeType.getTypeId() == typeId) {
                return blockChangeType;
            }
        }
        return null;
    }

    public byte getTypeId() {
        return typeId;
    }
}
