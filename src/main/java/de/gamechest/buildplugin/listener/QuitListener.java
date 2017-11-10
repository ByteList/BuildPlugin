package de.gamechest.buildplugin.listener;

import de.gamechest.GameChest;
import de.gamechest.buildplugin.BuildPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by ByteList on 03.11.17.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class QuitListener implements Listener {

    private final BuildPlugin buildPlugin = BuildPlugin.getInstance();
    private final GameChest gameChest = GameChest.getInstance();

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        buildPlugin.getPlayerManager().configurationsCache.remove(player.getUniqueId());

        e.setQuitMessage("§8\u00BB "+gameChest.getDisplayname(player)+"§7 hat den Server verlassen.");

        gameChest.getPacketInjector().removePlayer(player);
    }
}
