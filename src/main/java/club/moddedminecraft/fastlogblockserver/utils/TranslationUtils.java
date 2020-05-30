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

package club.moddedminecraft.fastlogblockserver.utils;

import io.netty.channel.Channel;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class TranslationUtils {
    private TranslationUtils() {
    }

    public static TextComponentBase createComponentTranslation(ICommandSender sender, String translation, Object... args) {
        return isVanillaClient(sender) ? new TextComponentString(I18n.translateToLocalFormatted(translation, args)) : new TextComponentTranslation(translation, args);
    }

    private static boolean isVanillaClient(ICommandSender sender) {
        if (sender instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) sender;
            NetHandlerPlayServer connection = playerMP.connection;
            if (connection != null) {
                NetworkManager netManager = connection.netManager;
                Channel channel = netManager.channel();
                return !channel.attr(NetworkRegistry.FML_MARKER).get();
            }
        }

        return false;
    }
}
