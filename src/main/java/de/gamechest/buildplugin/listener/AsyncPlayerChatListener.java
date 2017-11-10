package de.gamechest.buildplugin.listener;

import de.gamechest.GameChest;
import de.gamechest.buildplugin.BuildPlugin;
import de.gamechest.buildplugin.util.BuildMode;
import org.bukkit.Bukkit;
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
        Player p = e.getPlayer();

        e.setCancelled(true);

        BuildMode buildMode = buildPlugin.getBuildMode(p.getUniqueId());
        String msg = e.getMessage();
        String displayname = gameChest.getDisplayname(p);
        String prefix = buildMode.getColor()+buildMode.getName();
        String world = "§f[§e"+p.getWorld().getName()+"§f]";

        System.out.println(p.getName()+": "+msg);
        for (Player all : Bukkit.getOnlinePlayers()) {
            all.sendMessage(world+" "+prefix+"§8 \u00BB "+displayname+"§7: §r"+msg);
        }
    }
}
