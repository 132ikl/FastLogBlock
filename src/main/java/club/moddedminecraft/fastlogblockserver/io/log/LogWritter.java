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

package club.moddedminecraft.fastlogblockserver.io.log;

import club.moddedminecraft.fastlogblockserver.models.BlockChangeEventModel;
import club.moddedminecraft.fastlogblockserver.io.mappers.BlockMapper;
import club.moddedminecraft.fastlogblockserver.io.mappers.NickMapper;
import club.moddedminecraft.fastlogblockserver.utils.Constants;
import club.moddedminecraft.fastlogblockserver.utils.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class LogWritter {
    private final BlockMapper blockMapper;
    private final NickMapper nickMapper;
    private final File file;
    private BufferedOutputStream os;

    public LogWritter(final File file, final BlockMapper blockMapper, final NickMapper nickMapper) throws IOException {
        this.file = file;
        this.blockMapper = blockMapper;
        this.nickMapper = nickMapper;

        FileUtils.createFileIfNotExist(file);

        os = new BufferedOutputStream(new FileOutputStream(file, true));
    }

    /**
     * Name	posX posY posZ typeaction playerid blockid timestamp
     *
     * @param blockChangeEventModel
     */
    public void putEvent(final BlockChangeEventModel blockChangeEventModel) {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(Constants.SIZE_LOGLINE);
        byteBuffer.putInt(blockChangeEventModel.getPosX());
        byteBuffer.putInt(blockChangeEventModel.getPosY());
        byteBuffer.putInt(blockChangeEventModel.getPosZ());
        byteBuffer.put(blockChangeEventModel.getBlockChangeType().getTypeId());
        byteBuffer.putInt(nickMapper.getOrPutUser(blockChangeEventModel.getPlayernick()));
        byteBuffer.putLong(blockMapper.getOrPutBlock(blockChangeEventModel.getNameblock()));
        byteBuffer.putLong(blockChangeEventModel.getTimestamp().getTime());
        byteBuffer.put(Constants.DEVIDER_SYMBOL);

        try {
            os.write(byteBuffer.array());
        } catch (final IOException e) {
            try {
                sync();
                os.write(byteBuffer.array());
            } catch (final IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public void sync() throws IOException {
        FileUtils.createFileIfNotExist(file);

        try {
            os.flush();
        } catch (final IOException e) {
            os = new BufferedOutputStream(new FileOutputStream(file, true));
            os.flush();
        }
    }
}
