package de.gamechest.buildplugin.command;

import de.gamechest.buildplugin.BuildPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by ByteList on 03.11.17.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class DisabledCommand implements CommandExecutor {

    private final BuildPlugin buildPlugin = BuildPlugin.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(buildPlugin.prefix+"§cDer Befehl ist auf dem Build-Server deaktiviert.");
        return true;
    }
}
