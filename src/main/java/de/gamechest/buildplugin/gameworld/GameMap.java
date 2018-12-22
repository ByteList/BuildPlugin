package de.gamechest.buildplugin.gameworld;

import de.gamechest.ItemBuilder;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by ByteList on 22.12.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameMap {

    @Getter
    private final Player player;
    @Getter
    private final World world;
    @Getter
    private GameMode gameMode;

    private ItemStack[] armorContents, contents;

    public GameMap(Player player, World world, GameMode gameMode) {
        this.player = player;
        this.world = world;

        this.armorContents = player.getInventory().getArmorContents();
        this.contents = player.getInventory().getContents();
        player.getInventory().clear();

        player.getInventory().setItem(0, ItemBuilder.newBuilder());

        // TODO: 22.12.2018 jeder mode anderes player inv
    }



}
