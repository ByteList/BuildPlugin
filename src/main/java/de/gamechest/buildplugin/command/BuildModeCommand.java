package de.gamechest.buildplugin.command;

import de.gamechest.GameChest;
import de.gamechest.TabList;
import de.gamechest.buildplugin.BuildPlugin;
import de.gamechest.buildplugin.BuildMode;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.rank.Rank;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ByteList on 03.11.17.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class BuildModeCommand implements TabExecutor {

    private final BuildPlugin buildPlugin = BuildPlugin.getInstance();
    private final GameChest gameChest = GameChest.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(!gameChest.hasRank(player.getUniqueId(), Rank.DEVELOPER)) {
                player.sendMessage(buildPlugin.prefix+"§cDu hast keine Berechtigung für diesen Befehl!");
                return true;
            }
        }

        if(args.length == 2) {
            String modeName = args[0];
            String targetName = args[1];

            BuildMode buildMode = BuildMode.getBuildMode(modeName);

            if (buildMode == null) {
                sender.sendMessage(buildPlugin.prefix+"§c'"+modeName+"' is not a valid mode! (Modes: Spectate, Build, Normal)");
                return true;
            }

            Player target = Bukkit.getPlayer(targetName);

            if(target == null) {
                sender.sendMessage(buildPlugin.prefix+"§c'"+targetName+"' is not online!");
                return true;
            }

            String pos = "000";

            switch (buildMode) {
                case SPECTATE:
                    pos = "003";
                    target.setOp(false);
                    target.setGameMode(GameMode.SPECTATOR);
                    break;
                case BUILD:
                    pos = "002";
                    target.setOp(false);
                    target.setGameMode(GameMode.CREATIVE);
                    break;
                case OPERATOR:
                    pos = "001";
                    target.setOp(true);
                    target.setGameMode(GameMode.CREATIVE);
                    break;
            }
            TabList.updateCustom(target, pos+gameChest.getRank(target.getUniqueId()).getId(), buildMode.getColor()+buildMode.getShortName()+" §8\u00BB "+gameChest.getRank(target.getUniqueId()).getColor(), "§r");

            Document configurations = buildPlugin.getPlayerManager().configurationsCache.get(target.getUniqueId());

            configurations.put(DatabasePlayerObject.Configurations.BUILD_MODE.getName(), buildMode.getId());
            buildPlugin.getPlayerManager().configurationsCache.put(target.getUniqueId(), configurations);
            gameChest.getDatabaseManager().getAsync().getPlayer(target.getUniqueId(), dbPlayer ->
                    dbPlayer.setDatabaseObject(DatabasePlayerObject.CONFIGURATIONS, configurations), DatabasePlayerObject.CONFIGURATIONS);

            sender.sendMessage(buildPlugin.prefix+"§7Neuer Build-Mode: "+buildMode.getColor()+buildMode.getName()+"§7 ("+gameChest.getDisplayname(target)+"§7)");
            target.sendMessage(buildPlugin.prefix+"§7Neuer Build-Mode: "+buildMode.getColor()+buildMode.getName());

            return true;
        }

        sender.sendMessage(buildPlugin.prefix+"§c/buildmode <mode> <player>");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!gameChest.hasRank(player.getUniqueId(), Rank.DEVELOPER)) {
                sender.sendMessage(gameChest.prefix + "§cDu hast keine Berechtigung für diesen Befehl!");
                return new ArrayList<>();
            }
        }

        if (args.length > 2 || args.length == 0) {
            return Collections.emptyList();
        }

        List<String> matches = new ArrayList<>();
        if (args.length == 1) {
            String search = args[0].toLowerCase();

            for(BuildMode buildMode : BuildMode.values()) {
                if(buildMode.getName().startsWith(search)) {
                    matches.add(buildMode.getName());
                }
            }
        }

        if(args.length == 2) {
            String search = args[1].toLowerCase();

            for (Player player : buildPlugin.getServer().getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(search)) {
                    matches.add(player.getName());
                }
            }
        }
        return matches;
    }
}
