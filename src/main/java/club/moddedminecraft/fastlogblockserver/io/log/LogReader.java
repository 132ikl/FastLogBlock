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

import club.moddedminecraft.fastlogblockserver.io.base.IterrateByteFile;
import club.moddedminecraft.fastlogblockserver.io.mappers.BlockMapper;
import club.moddedminecraft.fastlogblockserver.io.mappers.NickMapper;
import club.moddedminecraft.fastlogblockserver.models.ASCIString;
import club.moddedminecraft.fastlogblockserver.models.BlockChangeEventModel;
import club.moddedminecraft.fastlogblockserver.models.BlockChangeType;
import club.moddedminecraft.fastlogblockserver.models.PrepareReadBlockChangeEvent;
import club.moddedminecraft.fastlogblockserver.utils.CollectionUtils;
import club.moddedminecraft.fastlogblockserver.utils.Constants;
import gnu.trove.list.array.TByteArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TIntHashSet;
import gnu.trove.set.hash.TLongHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLLog;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LogReader extends IterrateByteFile {
    private final BlockMapper blockMapper;
    private final NickMapper nickMapper;

    public LogReader(final File file, final BlockMapper blockMapper, final NickMapper nickMapper) {
        super(file);
        this.blockMapper = blockMapper;
        this.nickMapper = nickMapper;
    }


    public List<BlockChangeEventModel> readEventByPos(final BlockPos blockPos) throws IOException {
        FMLLog.log.info("readEventByPos() for has very long execution time! Start measuring...");
        long time = System.currentTimeMillis();

        final int needPosX = blockPos.getX();
        final int needPosY = blockPos.getY();
        final int needPosZ = blockPos.getZ();

        final List<PrepareReadBlockChangeEvent> prepareEvents = new ArrayList<>();
        final TLongSet needBlockName = new TLongHashSet();
        final TIntSet needNickName = new TIntHashSet();
        iterateByFile((byteList) -> {
            try {
                final PrepareReadBlockChangeEvent prepareReadBlockChangeEvent = prepareOrNullByPos(byteList.toArray(),
                        needPosX, needPosY, needPosZ);
                if (prepareReadBlockChangeEvent == null) {
                    return;
                }

                needBlockName.add(prepareReadBlockChangeEvent.getBlockId());
                needNickName.add(prepareReadBlockChangeEvent.getPlayerid());

                prepareEvents.add(prepareReadBlockChangeEvent);
            } catch (Exception e) {
                FMLLog.log.warn("Your logfile is corrupt!", e);
            }
        });

        final TIntObjectMap<ASCIString> idToNick = CollectionUtils.toHashMap(needNickName, id -> nickMapper.getById(id));
        final TLongObjectMap<ASCIString> idToBlock = blockMapper.idsToNames(needBlockName);

        FMLLog.log.info("readEventByPos(): " + (System.currentTimeMillis() - time) + "ms");
        return prepareEvents.stream().map(pE -> pE.toBlockChangeEventModel(idToNick, idToBlock)).collect(Collectors.toList());
    }

    private PrepareReadBlockChangeEvent prepareOrNullByPos(final byte[] bytes, final int needPosX, final int needPosY, final int needPosZ) {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        final int posX = byteBuffer.getInt();
        if (posX != needPosX) {
            return null;
        }
        final int posY = byteBuffer.getInt();
        if (posY != needPosY) {
            return null;
        }
        final int posZ = byteBuffer.getInt();
        if (posZ != needPosZ) {
            return null;
        }
        final BlockChangeType blockChangeType = BlockChangeType.valueOf(byteBuffer.get());
        final int playerId = byteBuffer.getInt();
        final long blockId = byteBuffer.getLong();
        final long timestamp = byteBuffer.getLong();

        return new PrepareReadBlockChangeEvent(posX, posY, posZ, blockId, playerId, timestamp, blockChangeType);
    }

    @Override
    protected void writeToFile(final OutputStream outputStream) throws IOException {
        // nothing
    }

    @Override
    public void sync() throws IOException {
        // nothing
    }

    @Override
    protected boolean checkLineEnd(final TByteArrayList arrayList, final byte endByte) {
        return endByte == Constants.DEVIDER_SYMBOL;
    }
}
