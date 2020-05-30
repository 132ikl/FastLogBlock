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

package club.moddedminecraft.fastlogblockserver;

import club.moddedminecraft.fastlogblockserver.handlers.EventHandlingManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

import club.moddedminecraft.fastlogblockserver.ui.InfoCommand;

import java.io.IOException;

@Mod(modid = FastLogBlockServer.MODID, version = FastLogBlockServer.VERSION, updateJSON = "https://raw.githubusercontent.com/LionZXY/FastLogBlock/master/update.json", serverSideOnly = true, acceptableRemoteVersions = "*")
public class FastLogBlockServer {
    public static final String MODID = "fastlogblock";
    public static final String VERSION = "1.0.2";
    private EventHandlingManager eventHandlingManager;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) throws IOException {
        FMLLog.log.info("Initializing eventHandlingManager...");
        eventHandlingManager = new EventHandlingManager();
        FMLLog.log.info("Done!");
        MinecraftForge.EVENT_BUS.register(eventHandlingManager);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new InfoCommand(eventHandlingManager));
    }

    @EventHandler
    public void serverStopped(final FMLServerStoppedEvent event) {
        eventHandlingManager.stop();
    }
}
