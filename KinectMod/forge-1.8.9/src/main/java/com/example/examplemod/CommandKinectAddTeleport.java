package com.example.examplemod;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandKinectAddTeleport extends CommandBase {

    @Override
    public String getCommandName() {
        return "kinectaddtp";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/kinectaddtp <x> <y> <z> [yaw] [pitch]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {

        if (args.length < 3) {
            sender.addChatMessage(new ChatComponentText("Uso: " + getCommandUsage(sender)));
            return;
        }

        try {

            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);

            float yaw = args.length > 3 ? Float.parseFloat(args[3]) : 0.0F;
            float pitch = args.length > 4 ? Float.parseFloat(args[4]) : 0.0F;

            if (ModKinect.teleportManager != null) {
                ModKinect.teleportManager.addPoint(x, y, z, yaw, pitch);
            }

            sender.addChatMessage(new ChatComponentText(
                    "[KinectTeleport] Ponto salvo: " + x + ", " + y + ", " + z
            ));

        } catch (NumberFormatException e) {
            sender.addChatMessage(new ChatComponentText("Coordenadas inválidas."));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}