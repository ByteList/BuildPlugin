package de.gamechest.buildplugin.gamemap.mode;

import de.gamechest.ItemBuilder;
import de.gamechest.buildplugin.BuildPlugin;
import de.gamechest.buildplugin.gamemap.GameMode;
import de.gamechest.buildplugin.gamemap.IMode;
import de.gamechest.buildplugin.gamemap.mode.shulkerdefence.ShopFakePlayer;
import de.gamechest.buildplugin.gamemap.mode.shulkerdefence.SpawnFakePlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.SpawnEgg;

import java.util.ArrayList;

/**
 * Created by ByteList on 23.12.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ShulkerDefenceMode implements IMode {

    private final BuildPlugin buildPlugin = BuildPlugin.getInstance();

    @Getter
    private final GameMode mode = GameMode.SHULKER_DEFENCE;

    private Location redTeamSpawnLocation, blueTeamSpawnLocation, redTeamShopLocation, blueTeamShopLocation;
    private final ArrayList<Location> bronzeSpawnLocations = new ArrayList<>(), silverSpawnLocations = new ArrayList<>(), goldSpawnLocations = new ArrayList<>();

    private ShopFakePlayer redTeamShopFakePlayer, blueTeamShopFakePlayer;
    private SpawnFakePlayer redTeamSpawnFakePlayer, blueTeamSpawnFakePlayer;

    public ShulkerDefenceMode() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(buildPlugin, ()-> {
            new ArrayList<>(bronzeSpawnLocations).forEach(location -> spawnDrop("bronze", location));
            new ArrayList<>(silverSpawnLocations).forEach(location -> spawnDrop("silver", location));
            new ArrayList<>(goldSpawnLocations).forEach(location -> spawnDrop("gold", location));
        }, 20L, 20L);
    }

    private void spawnDrop(String drop, Location location) {
        ItemStack itemStack = null;
        switch (drop) {
            case "bronze":
                itemStack = ItemBuilder.newBuilder(Material.CLAY_BRICK).get();
                break;
            case "silver":
                itemStack = ItemBuilder.newBuilder(Material.IRON_INGOT).get();
                break;
            case "gold":
                itemStack = ItemBuilder.newBuilder(Material.GOLD_INGOT).get();
                break;
        }
        if(itemStack == null) return;

        Item item = location.getWorld().dropItemNaturally(location, itemStack);
        Bukkit.getScheduler().runTaskLaterAsynchronously(buildPlugin, item::remove, 40L);
    }

    private void spawnSpawnFakePlayer(Player player, boolean redTeam) {
        if(redTeam) {
            if(this.redTeamSpawnFakePlayer != null) {
                this.redTeamSpawnFakePlayer.remove(player);
            }
            if(this.redTeamSpawnLocation != null) {
                this.redTeamSpawnFakePlayer = new SpawnFakePlayer(this.redTeamSpawnLocation, "§c");
                this.redTeamSpawnFakePlayer.create(player);
            }
            return;
        }

        if(this.blueTeamSpawnFakePlayer != null) {
            this.blueTeamSpawnFakePlayer.remove(player);
        }
        if(this.blueTeamSpawnLocation != null) {
            this.blueTeamSpawnFakePlayer = new SpawnFakePlayer(this.blueTeamSpawnLocation, "§b");
            this.blueTeamSpawnFakePlayer.create(player);
        }
    }

    private void spawnShopFakePlayer(Player player, boolean redTeam) {
        if(redTeam) {
            if(this.redTeamShopFakePlayer != null) {
                this.redTeamShopFakePlayer.remove(player);
            }
            if(this.redTeamShopLocation != null) {
                this.redTeamShopFakePlayer = new ShopFakePlayer(this.redTeamShopLocation, "§c");
                this.redTeamShopFakePlayer.create(player);
            }
            return;
        }

        if(this.blueTeamShopFakePlayer != null) {
            this.blueTeamShopFakePlayer.remove(player);
        }
        if(this.blueTeamShopLocation != null) {
            this.blueTeamShopFakePlayer = new ShopFakePlayer(this.blueTeamShopLocation, "§b");
            this.blueTeamShopFakePlayer.create(player);
        }
    }

    @Override
    public void loadInventory(Player player) {
        PlayerInventory inventory = player.getInventory();

        inventory.setItem(0, ItemBuilder.newBuilder(Material.DIAMOND)
                .displayname("§6Set Spawn").lore("§eDeine Position wird übernommen", "§7Rechtsklick: §cTeam Rot", "§7Linksklick: §9Team Blau").get());
        inventory.setItem(1, ItemBuilder.newBuilder(new SpawnEgg(EntityType.GUARDIAN).toItemStack())
                .displayname("§6Set Shop").lore("§eDeine Position wird übernommen", "§7Rechtsklick: §cTeam Rot", "§7Linksklick: §9Team Blau").get());

        inventory.setItem(3, ItemBuilder.newBuilder(Material.CLAY_BRICK)
                .displayname("§6Set Bronze-Drop").lore("§eDeine Position wird übernommen").get());
        inventory.setItem(4, ItemBuilder.newBuilder(Material.IRON_INGOT)
                .displayname("§6Set Silver-Drop").lore("§eDeine Position wird übernommen").get());
        inventory.setItem(5, ItemBuilder.newBuilder(Material.GOLD_INGOT)
                .displayname("§6Set Gold-Drop").lore("§eDeine Position wird übernommen").get());
    }

    @Override
    public void export(YamlConfiguration configuration) {
        configuration.set("team.red.spawn", this.redTeamSpawnLocation.getX()+";"+this.redTeamSpawnLocation.getY()+";"+
                this.redTeamSpawnLocation.getZ()+";"+this.redTeamSpawnLocation.getYaw()+";"+this.redTeamSpawnLocation.getPitch());
        configuration.set("team.blue.spawn", this.blueTeamSpawnLocation.getX()+";"+this.blueTeamSpawnLocation.getY()+";"+
                this.blueTeamSpawnLocation.getZ()+";"+this.blueTeamSpawnLocation.getYaw()+";"+this.blueTeamSpawnLocation.getPitch());

        configuration.set("team.red.shop", this.redTeamShopLocation.getX()+";"+this.redTeamShopLocation.getY()+";"+
                this.redTeamShopLocation.getZ());
        configuration.set("team.blue.shop", this.blueTeamShopLocation.getX()+";"+this.blueTeamShopLocation.getY()+";"+
                this.blueTeamShopLocation.getZ());
        ArrayList<String> bronze = new ArrayList<>(), silver = new ArrayList<>(), gold = new ArrayList<>();
        this.bronzeSpawnLocations.forEach(location -> bronze.add(location.getX()+";"+location.getY()+";"+location.getZ()));
        this.silverSpawnLocations.forEach(location -> silver.add(location.getX()+";"+location.getY()+";"+location.getZ()));
        this.goldSpawnLocations.forEach(location -> gold.add(location.getX()+";"+location.getY()+";"+location.getZ()));

        configuration.set("drop.bronze.locations", bronze);
        configuration.set("drop.silver.locations", silver);
        configuration.set("drop.gold.locations", gold);

        configuration.set("drop.bronze.delay", 1);
        configuration.set("drop.silver.delay", 8);
        configuration.set("drop.gold.delay", 20);
    }

    @Override
    public void onInteract(PlayerInteractEvent e) {
        e.setCancelled(true);
        switch (e.getItem().getItemMeta().getDisplayName()) {
            case "§6Set Spawn":
                switch (e.getAction()) {
                    case RIGHT_CLICK_AIR:
                        this.redTeamSpawnLocation = e.getPlayer().getLocation();
                        spawnSpawnFakePlayer(e.getPlayer(), true);
                        break;
                    case LEFT_CLICK_AIR:
                        this.blueTeamSpawnLocation = e.getPlayer().getLocation();
                        spawnSpawnFakePlayer(e.getPlayer(), false);
                        break;
                    default:
                        e.getPlayer().sendMessage("§8\u00BB §cUngültige Aktion!");
                        break;
                }
                break;
            case "§6Set Shop":
                switch (e.getAction()) {
                    case RIGHT_CLICK_BLOCK:
                        this.redTeamShopLocation = e.getPlayer().getLocation();
                        spawnShopFakePlayer(e.getPlayer(), true);
                        break;
                    case LEFT_CLICK_BLOCK:
                        this.blueTeamShopLocation = e.getPlayer().getLocation();
                        spawnShopFakePlayer(e.getPlayer(), false);
                        break;
                    default:
                        e.getPlayer().sendMessage("§8\u00BB §cUngültige Aktion!");
                        break;
                }
                break;
            case "§6Set Bronze-Drop":
                this.bronzeSpawnLocations.add(e.getPlayer().getLocation());
                e.getPlayer().sendMessage("§8\u00BB §7Location hinzugefügt: §eBronze");
                break;
            case "§6Set Silver-Drop":
                this.silverSpawnLocations.add(e.getPlayer().getLocation());
                e.getPlayer().sendMessage("§8\u00BB §7Location hinzugefügt: §eSilver");
                break;
            case "§6Set Gold-Drop":
                this.goldSpawnLocations.add(e.getPlayer().getLocation());
                e.getPlayer().sendMessage("§8\u00BB §7Location hinzugefügt: §eGold");
                break;
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent e) {
    }

    @Override
    public void onPickUp(PlayerPickupItemEvent e) {
        e.setCancelled(true);
    }

}
