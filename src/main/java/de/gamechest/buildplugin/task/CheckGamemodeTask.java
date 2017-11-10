package de.gamechest.buildplugin.task;

import de.gamechest.buildplugin.BuildPlugin;
import de.gamechest.buildplugin.util.BuildMode;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * Created by ByteList on 04.11.17.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class CheckGamemodeTask implements Runnable {

    private final BuildPlugin buildPlugin = BuildPlugin.getInstance();

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(buildPlugin.getBuildMode(player.getUniqueId()) == BuildMode.SPECTATE && player.getGameMode() != GameMode.SPECTATOR) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(buildPlugin, () -> player.setGameMode(GameMode.SPECTATOR));
            }
        }
    }
}
