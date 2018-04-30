package ru.lionzxy.fastlogblock.ui;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import ru.lionzxy.fastlogblock.handlers.EventHandlingManager;


public class InfoCommand extends CommandBase {
    private final EventHandlingManager eventHandlingManager;

    public InfoCommand(EventHandlingManager eventHandlingManager) {
        this.eventHandlingManager = eventHandlingManager;
    }

    @Override
    public String getName() { return "blocklog"; }

    @Override
    public String getUsage(ICommandSender sender) { return "/blocklog"; }

    @Override
    public int getRequiredPermissionLevel() { return 4; }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            RayTraceResult lookingAt = rayTrace(8, sender);
            sender.sendMessage(new TextComponentString("§3[FastLogBlock]§f Start computing log for Position " + lookingAt.getBlockPos().getX() + lookingAt.getBlockPos().getY() + lookingAt.getBlockPos().getZ()));
            eventHandlingManager.handleLogByPos(((EntityPlayer)sender), lookingAt.getBlockPos(), ((EntityPlayer)sender).world);
        }
    }

    public RayTraceResult rayTrace(double blockReachDistance, ICommandSender sender)
    {
        Vec3d vec3d = ((EntityPlayer)sender).getPositionEyes(1.0f);
        Vec3d vec3d1 = ((EntityPlayer)sender).getLook(1.0f);
        Vec3d vec3d2 = vec3d.addVector(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        return ((EntityPlayer)sender).world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
    }

}