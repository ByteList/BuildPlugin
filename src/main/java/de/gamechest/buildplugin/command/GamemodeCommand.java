package de.gamechest.buildplugin.command;

import de.gamechest.GameChest;
import de.gamechest.buildplugin.BuildPlugin;
import de.gamechest.buildplugin.BuildMode;
import de.gamechest.database.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by ByteList on 03.11.17.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GamemodeCommand implements CommandExecutor {

    private final BuildPlugin buildPlugin = BuildPlugin.getInstance();
    private final GameChest gameChest = GameChest.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cNur als Spieler nutzbar!");
            return true;
        }

        Player player = (Player) sender;

        if(buildPlugin.getBuildMode(player.getUniqueId()) == BuildMode.SPECTATE) {
            player.sendMessage(buildPlugin.prefix+"§cDu hast keine Berechtigung für diesen Befehl!");
            return true;
        }

        if(args.length == 1) {
            int mode;

            try {
                mode = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                player.sendMessage(buildPlugin.prefix+"§cNot a valid number!");
                return true;
            }

            switch (mode) {
                case 0:
                    player.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage(buildPlugin.prefix+"§7Neuer Gamemode: §aSurvival");
                    return true;
                case 1:
                    player.setGameMode(GameMode.CREATIVE);
                    player.sendMessage(buildPlugin.prefix+"§7Neuer Gamemode: §eCreative");
                    return true;
                case 2:
                    player.setGameMode(GameMode.ADVENTURE);
                    player.sendMessage(buildPlugin.prefix+"§7Neuer Gamemode: §2Adventure");
                    return true;
                case 3:
                    player.setGameMode(GameMode.SPECTATOR);
                    player.sendMessage(buildPlugin.prefix+"§7Neuer Gamemode: §bSpectator");
                    return true;
                default:
                    player.sendMessage(buildPlugin.prefix+"§cNot a valid number!");
                    return true;
            }
        }

        if(args.length == 2) {
            if(!gameChest.hasRank(player.getUniqueId(), Rank.DEVELOPER)) {
                player.sendMessage(buildPlugin.prefix+"§cDu hast keine Berechtigung für diesen Befehl!");
                return true;
            }
            int mode;
            try {
                mode = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                player.sendMessage(buildPlugin.prefix+"§cNot a valid number!");
                return true;
            }

            String targetName = args[1];
            Player target = Bukkit.getPlayer(targetName);

            if(target == null) {
                player.sendMessage(buildPlugin.prefix+"§cDer Spieler ist nicht online!");
                return true;
            }

            switch (mode) {
                case 0:
                    target.setGameMode(GameMode.SURVIVAL);
                    target.sendMessage(buildPlugin.prefix+"§7Neuer Gamemode: §aSurvival");
                    player.sendMessage(buildPlugin.prefix+"§7Neuer Gamemode: §aSurvival§7 ("+target.getName()+")");
                    return true;
                case 1:
                    target.setGameMode(GameMode.CREATIVE);
                    target.sendMessage(buildPlugin.prefix+"§7Neuer Gamemode: §eCreative");
                    player.sendMessage(buildPlugin.prefix+"§7Neuer Gamemode: §eCreative§7 ("+target.getName()+")");
                    return true;
                case 2:
                    target.setGameMode(GameMode.ADVENTURE);
                    target.sendMessage(buildPlugin.prefix+"§7Neuer Gamemode: §2Adventure");
                    player.sendMessage(buildPlugin.prefix+"§7Neuer Gamemode: §2Adventure§7 ("+target.getName()+")");
                    return true;
                case 3:
                    target.setGameMode(GameMode.SPECTATOR);
                    target.sendMessage(buildPlugin.prefix+"§7Neuer Gamemode: §bSpectator");
                    player.sendMessage(buildPlugin.prefix+"§7Neuer Gamemode: §bSpectator§7 ("+target.getName()+")");
                    return true;
                default:
                    player.sendMessage(buildPlugin.prefix+"§cNot a valid number!");
                    return true;
            }
        }

        if(gameChest.hasRank(player.getUniqueId(), Rank.DEVELOPER)) {
            player.sendMessage(buildPlugin.prefix+"§c/gamemode <mode> [player]");
        } else {
            player.sendMessage(buildPlugin.prefix+"§c/gamemode <mode>");
        }
        return true;
    }
}
