package de.gamechest.buildplugin.command;

import de.gamechest.GameChest;
import de.gamechest.buildplugin.BuildPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by ByteList on 03.11.17.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class SpeedCommand implements CommandExecutor {

    private final BuildPlugin buildPlugin = BuildPlugin.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cNur als Spieler nutzbar!");
            return true;
        }

        Player player = (Player) sender;

        if(args.length == 1) {
            int speed;

            try {
                speed = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                player.sendMessage(buildPlugin.prefix+"§cNot a valid number!");
                return true;
            }

            if(speed < 1 || speed > 10) {
                player.sendMessage(buildPlugin.prefix+"§cNutze folgende Geschwindigkeiten: 1-10");
                return true;
            }

            float speedF = ((float) speed) / 10;

            player.setFlySpeed(speedF);
            player.sendMessage(buildPlugin.prefix+"§7Neue Geschwindigkeit: §e"+speed+" §7("+player.getFlySpeed()+")");
            return true;
        }

        player.sendMessage(buildPlugin.prefix+"§c/speed <1-10>");
        player.sendMessage(buildPlugin.prefix+"§7Aktuelle Geschwindigkeit: §e"+player.getFlySpeed());
        return true;
    }
}
