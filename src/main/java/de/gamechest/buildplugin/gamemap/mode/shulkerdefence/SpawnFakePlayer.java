package de.gamechest.buildplugin.gamemap.mode.shulkerdefence;

import de.gamechest.GameChest;
import de.gamechest.buildplugin.BuildPlugin;
import de.gamechest.fakeplayer.FakePlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by ByteList on 23.12.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class SpawnFakePlayer {

    private final GameChest gameChest = GameChest.getInstance();

    @Getter
    private HashMap<UUID, Integer> entityIds = new HashMap<>();
    @Getter
    private ArrayList<UUID> interacted = new ArrayList<>();

    @Getter
    private final Location fakePlayerLocation;
    private final double spawnDistance = 40;
    @Getter
    private final String displayname;

    public SpawnFakePlayer(Location fakePlayerLocation, String teamColor) {
        this.fakePlayerLocation = fakePlayerLocation;
        this.displayname = teamColor+"Spawn";
    }

    public void create(Player player) {
        if(this.entityIds.containsKey(player.getUniqueId())) return;

        FakePlayer npc = FakePlayer.FakePlayerBuilder.newInstance()
                .player(player)
                .location(this.fakePlayerLocation)
                .displayname(this.displayname)
                .runnable((fakePlayer, targetPlayer) -> {
                    if (player.getLocation().distance(this.fakePlayerLocation) <= this.spawnDistance) {
                        if(!fakePlayer.isSpawned()) {
                            fakePlayer.spawn();
                            fakePlayer.lookAtLocation(this.fakePlayerLocation);
                            Bukkit.getScheduler().runTaskLaterAsynchronously(BuildPlugin.getInstance(), fakePlayer::removeFromTabList, 20L);
                        }
                    } else {
                        fakePlayer.destroy();
                    }
                }).build();

        gameChest.getFakePlayerManager().addNewFakePlayer(player.getUniqueId(), false, npc);
        this.entityIds.put(player.getUniqueId(), npc.getEntityId());
    }

    public void remove(Player player) {
        if(!this.entityIds.containsKey(player.getUniqueId()))
            return;

        int npcId = this.entityIds.remove(player.getUniqueId());
        gameChest.getFakePlayerManager().removeFakePlayer(player.getUniqueId(), npcId);
    }
}
