package ru.lionzxy.fastlogblock;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

import ru.lionzxy.fastlogblock.handlers.EventHandlingManager;
import ru.lionzxy.fastlogblock.ui.InfoCommand;

import java.io.IOException;

@Mod(modid = FastLogBlock.MODID, version = FastLogBlock.VERSION, updateJSON = "https://raw.githubusercontent.com/LionZXY/FastLogBlock/master/update.json", serverSideOnly = true, acceptableRemoteVersions = "*")
public class FastLogBlock {
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
