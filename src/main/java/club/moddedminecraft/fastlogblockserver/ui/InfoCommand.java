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

package club.moddedminecraft.fastlogblockserver.ui;

import club.moddedminecraft.fastlogblockserver.handlers.EventHandlingManager;
import club.moddedminecraft.fastlogblockserver.utils.TranslationUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;


public class InfoCommand extends CommandBase {
    private final EventHandlingManager eventHandlingManager;

    public InfoCommand(EventHandlingManager eventHandlingManager) {
        this.eventHandlingManager = eventHandlingManager;
    }

    @Override
    public String getName() {
        return "blocklog";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/blocklog";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }


    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            RayTraceResult lookingAt = rayTrace(8, sender);
            sender.sendMessage(TranslationUtils.createComponentTranslation(sender, "message.fastlogblock:blockinfo.start", lookingAt.getBlockPos().getX(), lookingAt.getBlockPos().getY(), lookingAt.getBlockPos().getZ()));
            eventHandlingManager.handleLogByPos(((EntityPlayer) sender), lookingAt.getBlockPos(), ((EntityPlayer) sender).world);
        }
    }

    public RayTraceResult rayTrace(double blockReachDistance, ICommandSender sender) {
        Vec3d vec3d = ((EntityPlayer) sender).getPositionEyes(1.0f);
        Vec3d vec3d1 = ((EntityPlayer) sender).getLook(1.0f);
        Vec3d vec3d2 = vec3d.addVector(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        return ((EntityPlayer) sender).world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
    }

}