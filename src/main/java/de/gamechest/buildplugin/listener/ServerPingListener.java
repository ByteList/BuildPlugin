package de.gamechest.buildplugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * Created by ByteList on 15.01.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ServerPingListener implements Listener {

    @EventHandler
    public void onServerPing(ServerListPingEvent e) {
        e.setMotd("§6Game-Chest§f.§6de §7\u00BB §eBuild-Server §8[§b1.9 §f- §c1.12§8]\n§aThe future is now, old men!");
    }
}
