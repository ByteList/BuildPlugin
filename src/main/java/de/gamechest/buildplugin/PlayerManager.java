package de.gamechest.buildplugin;

import de.gamechest.buildplugin.gamemap.GameMap;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by ByteList on 03.11.17.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PlayerManager {

    public HashMap<UUID, Document> configurationsCache = new HashMap<>();

    @Getter
    private ArrayList<UUID> waitingForGameMapChangeName = new ArrayList<>();
    @Getter
    private HashMap<UUID, GameMap> gameMaps = new HashMap<>();

    public void setGameMap(Player player, GameMap gameMap) {
        this.gameMaps.put(player.getUniqueId(), gameMap);
    }

    public boolean hasGameMap(UUID uuid) {
        return this.gameMaps.containsKey(uuid);
    }

    public GameMap getGameMap(UUID uuid) {
        return this.gameMaps.get(uuid);
    }

    public void removeGameMap(UUID uuid) {
        this.gameMaps.remove(uuid);
    }
}
