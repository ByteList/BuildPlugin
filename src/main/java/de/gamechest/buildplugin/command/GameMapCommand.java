package de.gamechest.buildplugin.command;

import de.gamechest.buildplugin.BuildPlugin;
import de.gamechest.buildplugin.gamemap.GameMap;
import de.gamechest.buildplugin.gamemap.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by ByteList on 22.12.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameMapCommand implements CommandExecutor, TabExecutor {

    private final BuildPlugin buildPlugin = BuildPlugin.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur als Spieler nutzbar!");
            return true;
        }
        Player player = (Player) sender;

        if(args.length == 2) {
            String mode = args[0];
            GameMode gameMode = GameMode.getGameMode(mode);
            if(gameMode == null) {
                sender.sendMessage("§8\u00BB Unbekannter Gamemode!");
                return true;
            }
            buildPlugin.getPlayerManager().setGameMap(player, new GameMap(player, player.getWorld(), gameMode, args[1]));
        }

        sender.sendMessage("§8\u00BB §c/gamemap <gamemode> <name>");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length >= 1 && args.length < 2) {
            return GameMode.getModes();
        }
        return null;
    }
}