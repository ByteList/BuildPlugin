package de.gamechest.buildplugin.command;

import de.gamechest.GameChest;
import de.gamechest.buildplugin.BlockInfo;
import de.gamechest.buildplugin.BuildPlugin;
import de.gamechest.database.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by ByteList on 04.11.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class BlockInfoCommand implements CommandExecutor {

    private final BuildPlugin buildPlugin = BuildPlugin.getInstance();
    private final GameChest gameChest = GameChest.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur als Spieler nutzbar!");
            return true;
        }
        Player player = (Player) sender;

        if(!gameChest.hasRank(player.getUniqueId(), Rank.DEVELOPER)) {
            gameChest.sendNoPermissionMessage(sender);
            return true;
        }

        BlockInfo.setInfoMode(player, !BlockInfo.isInfoModeSet(player.getUniqueId()));
        return true;
    }
}
