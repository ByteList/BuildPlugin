package de.gamechest.buildplugin.command;

import de.gamechest.GameChest;
import de.gamechest.UUIDFetcher;
import de.gamechest.buildplugin.BuildPlugin;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.rank.Rank;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by ByteList on 03.11.17.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class BuildConnectCommand implements CommandExecutor {

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

        if(args.length == 1) {
            String targetName = args[0];
            UUID uuid = UUIDFetcher.getUUID(targetName);

            if(uuid == null) {
                sender.sendMessage(buildPlugin.prefix+"§cKonnte den User nicht in der Datenbank finden!");
                return true;
            }

            gameChest.getDatabaseManager().getAsync().getPlayer(uuid, dbPlayer -> {
                Document configurations = dbPlayer.getDatabaseElement(DatabasePlayerObject.CONFIGURATIONS).getAsDocument();
                int connect = configurations.getInteger(DatabasePlayerObject.Configurations.BUILD_CONNECT.getName());

                if(connect != 0) {
                    connect = 0;
                    sender.sendMessage(buildPlugin.prefix+"§7Build-Connect für "+targetName+" entfernt.");

                    Player target = Bukkit.getPlayer(uuid);
                    if(target != null) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(buildPlugin, ()-> target.kickPlayer(gameChest.prefix+"§cDu hast für den Build-Server keine Berechtigung mehr!"));
                    }
                } else {
                    connect = 1;
                    sender.sendMessage(buildPlugin.prefix+"§7Build-Connect für "+targetName+" hinzugefügt.");
                }

                configurations.put(DatabasePlayerObject.Configurations.BUILD_CONNECT.getName(), connect);
                dbPlayer.setDatabaseObject(DatabasePlayerObject.CONFIGURATIONS, configurations);
            });
            return true;
        }

        sender.sendMessage(buildPlugin.prefix+"§c/buildconnect <player>");
        return false;
    }
}
