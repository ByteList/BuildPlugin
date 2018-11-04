package de.gamechest.buildplugin.listener;

import de.gamechest.buildplugin.BlockInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Created by ByteList on 04.11.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class BlockListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if(!e.isCancelled())
            BlockInfo.getBlockInfo(player.getWorld()).addBlock(player, e.getBlock());
    }
}
