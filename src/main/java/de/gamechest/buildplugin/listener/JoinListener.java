package de.gamechest.buildplugin.listener;

import de.gamechest.BountifulAPI;
import de.gamechest.GameChest;
import de.gamechest.TabList;
import de.gamechest.buildplugin.BuildPlugin;
import de.gamechest.buildplugin.BuildMode;
import de.gamechest.database.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by ByteList on 03.11.17.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class JoinListener implements Listener {

    private final BuildPlugin buildPlugin = BuildPlugin.getInstance();
    private final GameChest gameChest = GameChest.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        BuildMode buildMode = buildPlugin.getBuildMode(player.getUniqueId());
        Rank rank = gameChest.getRank(player.getUniqueId());

        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        gameChest.getPacketInjector().addPlayer(player);
        BountifulAPI.sendTabTitle(player, "§6Game-Chest§f.§6de\n§fAktueller Server: §eBuild-Server", "§7Willkommen, §c" + player.getName());
        BountifulAPI.sendTitle(e.getPlayer(), 1, 2, 1, "§r", "§r");

        String pos = "000";

        switch (buildMode) {
            case SPECTATE:
                pos = "003";
                break;
            case BUILD:
                pos = "002";
                break;
            case OPERATOR:
                pos = "001";
                break;
        }
        /*
        ConsolemasterMC : Admin-1 -> 0011_custom
        ByteList : Dev-2 -> 0012_custom
         */
        TabList.updateCustom(player, pos+rank.getId(), buildMode.getColor()+buildMode.getShortName()+" §8\u00BB "+rank.getColor(), "§r");
        e.setJoinMessage("§8\u00BB "+gameChest.getDisplayname(player)+"§7 hat den Server betreten. ("+buildMode.getColor()+buildMode.getName()+"§7)");
    }
}
