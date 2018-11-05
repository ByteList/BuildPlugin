package de.gamechest.buildplugin.listener;

import de.gamechest.buildplugin.BuildPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by ByteList on 04.11.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PlayerInteractListener implements Listener {

    private final BuildPlugin buildPlugin = BuildPlugin.getInstance();

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

    }
}
