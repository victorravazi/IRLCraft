package com.example.examplemod;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandKinectMinigame extends CommandBase {

    @Override
    public String getCommandName() {
        return "kinectminigame";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/kinectminigame";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {

        if (ModKinect.minigameManager != null) {
            ModKinect.minigameManager.startGame();
        }

        sender.addChatMessage(new ChatComponentText("[KinectMinigame] Iniciando!"));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}