package de.gamechest.buildplugin.listener;

import de.gamechest.GameChest;
import de.gamechest.buildplugin.BuildPlugin;
import de.gamechest.buildplugin.BuildMode;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by ByteList on 04.11.17.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class AsyncPlayerChatListener implements Listener {

    private final GameChest gameChest = GameChest.getInstance();
    private final BuildPlugin buildPlugin = BuildPlugin.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        e.setCancelled(true);

        if(buildPlugin.getPlayerManager().getWaitingForGameMapChangeName().contains(player.getUniqueId())) {
            buildPlugin.getPlayerManager().getWaitingForGameMapChangeName().remove(player.getUniqueId());
            buildPlugin.getPlayerManager().getGameMap(player.getUniqueId()).setName(e.getMessage());
            player.sendMessage("§8\u00BB §aName erfolgreich geändert: §e"+e.getMessage());
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 2F, 2F);
            return;
        }

        BuildMode buildMode = buildPlugin.getBuildMode(player.getUniqueId());
        String msg = e.getMessage();
        String displayname = gameChest.getDisplayname(player);
        String prefix = buildMode.getColor()+buildMode.getName();
        String world = "§f[§e"+player.getWorld().getName()+"§f]";

        System.out.println(player.getName()+": "+msg);
        for (Player all : Bukkit.getOnlinePlayers()) {
            all.sendMessage(world+" "+prefix+"§8 \u00BB "+displayname+"§7: §r"+msg);
        }
    }
}
