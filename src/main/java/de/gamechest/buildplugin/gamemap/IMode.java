package de.gamechest.buildplugin.gamemap;

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
public interface IMode {

    public GameMode getMode();

    public void loadInventory(Player player);

    public boolean export(Player player, YamlConfiguration configuration);

    public void disable(Player player);

    public void onInteract(PlayerInteractEvent e);

    public void onInventoryClick(InventoryClickEvent e);

    public void onInventoryClose(InventoryCloseEvent e);

    public void onPickUp(PlayerPickupItemEvent e);
}
