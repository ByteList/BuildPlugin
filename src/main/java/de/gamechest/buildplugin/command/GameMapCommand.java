package de.gamechest.buildplugin.command;

import de.gamechest.buildplugin.BuildPlugin;
import de.gamechest.buildplugin.gamemap.GameMap;
import de.gamechest.buildplugin.gamemap.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by ByteList on 22.12.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameMapCommand implements CommandExecutor {

    private final BuildPlugin buildPlugin = BuildPlugin.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur als Spieler nutzbar!");
            return true;
        }
        Player player = (Player) sender;

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("disable")) {
                GameMap gameMap = buildPlugin.getPlayerManager().getGameMap(player.getUniqueId());
                if(gameMap == null) {
                    sender.sendMessage("§8\u00BB §cGamemap is already disabled!");
                    return true;
                }
                gameMap.disable();
                sender.sendMessage("§8\u00BB §eGamemap disabled!");
                return true;
            }
        }

        if(args.length == 2) {
            String mode = args[0];
            GameMode gameMode = GameMode.getGameMode(mode);
            if(gameMode == null) {
                sender.sendMessage("§8\u00BB Unbekannter Gamemode!");
                return true;
            }
            GameMap gameMap = buildPlugin.getPlayerManager().getGameMap(player.getUniqueId());
            if(gameMap != null) {
                sender.sendMessage("§8\u00BB §cGamemap is already enabled!");
                return true;
            }

            buildPlugin.getPlayerManager().setGameMap(player, new GameMap(player, player.getWorld(), gameMode, args[1]));
            sender.sendMessage("§8\u00BB §eGamemap enabled!");
            return true;
        }

        sender.sendMessage("§8\u00BB §c/gamemap <gamemode> <name>");
        sender.sendMessage("§8\u00BB §c/gamemap disable");
        return true;
    }
}
