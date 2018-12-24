package de.gamechest.buildplugin.gamemap.mode;

import de.gamechest.buildplugin.gamemap.GameMode;
import de.gamechest.buildplugin.gamemap.IMode;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 * Created by ByteList on 23.12.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class DeathRunMode implements IMode {

    @Getter
    private final GameMode mode = GameMode.DEATH_RUN;

    @Override
    public void loadInventory(Player player) {

    }

    @Override
    public boolean export(Player player, YamlConfiguration configuration) {
        return false;
    }

    @Override
    public void disable() {

    }

    @Override
    public void onInteract(PlayerInteractEvent e) {

    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {

    }

    @Override
    public void onInventoryClose(InventoryCloseEvent e) {

    }

    @Override
    public void onPickUp(PlayerPickupItemEvent e) {

    }
}
