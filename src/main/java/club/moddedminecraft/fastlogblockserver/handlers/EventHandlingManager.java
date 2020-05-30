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

package club.moddedminecraft.fastlogblockserver.handlers;

import club.moddedminecraft.fastlogblockserver.config.LogConfig;
import club.moddedminecraft.fastlogblockserver.models.BlockChangeEventModel;
import club.moddedminecraft.fastlogblockserver.models.BlockChangeEventModelWithWorld;
import club.moddedminecraft.fastlogblockserver.models.FindTask;
import club.moddedminecraft.fastlogblockserver.models.FindTaskResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import club.moddedminecraft.fastlogblockserver.io.ReadRunnable;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class EventHandlingManager {
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final BlockingQueue<FindTaskResult> findTaskResults = new LinkedBlockingQueue<>();
    private final ReadRunnable readRunnable;
    private final SplitterRunnable splitterRunnable;

    public EventHandlingManager() throws IOException {
        this.splitterRunnable = new SplitterRunnable();
        this.readRunnable = splitterRunnable.getReadRunnable();
        splitterRunnable.runWorkers(executor);
        executor.execute(splitterRunnable);
        executor.execute(readRunnable);
    }

    @SubscribeEvent
    public void onBlockBreak(final BlockEvent.BreakEvent event) {
        logEvent((BlockChangeEventModelWithWorld) BlockChangeEventModel.getChangeEvent(event), event);
    }

    @SubscribeEvent
    public void onBlockPlace(final BlockEvent.PlaceEvent event) {
        logEvent((BlockChangeEventModelWithWorld) BlockChangeEventModel.getChangeEvent(event), event);
    }

    @SubscribeEvent
    public void flushUIWait(TickEvent.ServerTickEvent event) {
        if (findTaskResults.isEmpty()) {
            return;
        }
        FindTaskResult findTaskResult;
        while ((findTaskResult = findTaskResults.poll()) != null) {
            for (BlockChangeEventModel blockEvent : findTaskResult.getBlockChangeEventModels()) {
                notifyAboutEvent(blockEvent, findTaskResult.getEntityPlayer());
            }
            if (findTaskResult.getBlockChangeEventModels().isEmpty()) {
                findTaskResult.getEntityPlayer().sendMessage(new TextComponentString("§3[FastLogBlock]§f Not found changes with block"));
            } else {
                findTaskResult.getEntityPlayer().sendMessage(new TextComponentString("§3[FastLogBlock]§f Done!"));
            }
        }
    }

    public void handleLogByPos(EntityPlayer entityPlayer, BlockPos blockPos, World world) {
        final FindTask findTask = new FindTask(blockPos, (list, player) -> {
            findTaskResults.add(new FindTaskResult(list, player));
        }, world);
        findTask.setEntityPlayer(entityPlayer);
        readRunnable.addTaskForSearch(findTask);
    }

    public void stop() {
        executor.shutdownNow();
    }

    private void notifyAboutEvent(BlockChangeEventModel blockEvent, EntityPlayer entityPlayer) {
        final String dateformat = I18n.translateToLocal("message.fastlogblock:blockinfo.event.dateformat");
        final EntityPlayer playerEvent = entityPlayer.getEntityWorld().getPlayerEntityByUUID(UUID.fromString(blockEvent.getPlayernick().toString()));
        String nickname;
        if (playerEvent == null) {
            if (entityPlayer.getEntityWorld().getMinecraftServer().isSinglePlayer()) {
                nickname = entityPlayer.getDisplayNameString();
            } else {
                nickname = "Unknown(UUID: " + blockEvent.getPlayernick() + ")";
            }
        } else {
            nickname = playerEvent.getDisplayNameString();
        }
        final String[] args = new String[]{new SimpleDateFormat(dateformat).format(new Date(blockEvent.getTimestamp().getTime())),
                nickname,
                blockEvent.getNameblock().toString()};
        TextComponentString textComponent;
        switch (blockEvent.getBlockChangeType()) {
            default:
            case INSERT:
                textComponent = new TextComponentString("§2[+]§6["+args[0]+"§6]§f "+args[1]+": "+args[2]);
                break;
            case REMOVE:
                textComponent = new TextComponentString("§4[-]§6["+args[0]+"§6]§f "+args[1]+": "+args[2]);
        }
        entityPlayer.sendMessage(textComponent);
    }


    private void logEvent(BlockChangeEventModelWithWorld changeEvent, BlockEvent event) {
        if (!LogConfig.loggingEnable) {
            return;
        }

        if (changeEvent == null) {
            return;
        }

        FMLLog.log.debug(changeEvent.toString());
        splitterRunnable.addEvent(changeEvent);
    }
}
