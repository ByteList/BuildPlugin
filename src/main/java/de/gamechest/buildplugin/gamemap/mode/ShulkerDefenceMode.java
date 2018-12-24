package de.gamechest.buildplugin.gamemap.mode;

import de.gamechest.ItemBuilder;
import de.gamechest.buildplugin.BuildPlugin;
import de.gamechest.buildplugin.gamemap.GameMap;
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
import org.bukkit.entity.Shulker;
import org.bukkit.event.block.Action;
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
    private final GameMap gameMap;


    @Getter
    private final GameMode mode = GameMode.SHULKER_DEFENCE;

    private Location redTeamSpawnLocation, blueTeamSpawnLocation, redTeamShopLocation, blueTeamShopLocation, redTeamShulkerLocation, blueTeamShulkerLocation;
    private final ArrayList<Location> bronzeSpawnLocations = new ArrayList<>(), silverSpawnLocations = new ArrayList<>(), goldSpawnLocations = new ArrayList<>();

    private ShopFakePlayer redTeamShopFakePlayer, blueTeamShopFakePlayer;
    private SpawnFakePlayer redTeamSpawnFakePlayer, blueTeamSpawnFakePlayer;
    private Shulker redTeamShulker, blueTeamShulker;

    public ShulkerDefenceMode(GameMap gameMap) {
        this.gameMap = gameMap;

        this.redTeamSpawnLocation = locationFromString(gameMap.getConfiguration().getString("team.red.spawn"));
        this.blueTeamSpawnLocation = locationFromString(gameMap.getConfiguration().getString("team.blue.spawn"));
        this.redTeamShopLocation = locationFromString(gameMap.getConfiguration().getString("team.red.shop"));
        this.blueTeamShopLocation = locationFromString(gameMap.getConfiguration().getString("team.blue.shop"));
        this.redTeamShulkerLocation = locationFromString(gameMap.getConfiguration().getString("team.red.shulker"));
        this.blueTeamShulkerLocation = locationFromString(gameMap.getConfiguration().getString("team.blue.shulker"));
        gameMap.getConfiguration().getStringList("drop.bronze.locations").forEach(s -> bronzeSpawnLocations.add(locationFromString(s)));
        gameMap.getConfiguration().getStringList("drop.silver.locations").forEach(s -> silverSpawnLocations.add(locationFromString(s)));
        gameMap.getConfiguration().getStringList("drop.gold.locations").forEach(s -> goldSpawnLocations.add(locationFromString(s)));

        gameMap.getTasks().add(Bukkit.getScheduler().scheduleSyncRepeatingTask(buildPlugin, ()-> {
            new ArrayList<>(bronzeSpawnLocations).forEach(location -> spawnDrop("bronze", location));
            new ArrayList<>(silverSpawnLocations).forEach(location -> spawnDrop("silver", location));
            new ArrayList<>(goldSpawnLocations).forEach(location -> spawnDrop("gold", location));
        }, 20L, 20L));

        spawnShulker(true);
        spawnShulker(false);
        spawnSpawnFakePlayer(gameMap.getPlayer(), true);
        spawnSpawnFakePlayer(gameMap.getPlayer(), false);
        spawnShopFakePlayer(gameMap.getPlayer(), true);
        spawnShopFakePlayer(gameMap.getPlayer(), false);
    }

    private Location locationFromString(String string) {
        if(string == null) return null;

        String[] splitted = string.split(";");
        return new Location(gameMap.getWorld(),
                Double.valueOf(splitted[0]), Double.valueOf(splitted[1]), Double.valueOf(splitted[2]),
                Float.valueOf(splitted[3]),Float.valueOf(splitted[4]));
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

    private void spawnShulker(boolean redTeam) {
        removeShulker(redTeam);

        if(redTeam) {
            if(this.redTeamShulkerLocation != null) {
                Shulker shulker = (Shulker) gameMap.getWorld().spawnEntity(this.redTeamShulkerLocation, EntityType.SHULKER);
                shulker.setAI(false);
                shulker.setCustomName("§cShulker");
                shulker.setCustomNameVisible(true);
                this.redTeamShulker = shulker;
            }
            return;
        }

        if(this.blueTeamShulkerLocation != null) {
            Shulker shulker = (Shulker) gameMap.getWorld().spawnEntity(this.blueTeamShulkerLocation, EntityType.SHULKER);
            shulker.setAI(false);
            shulker.setCustomName("§bShulker");
            shulker.setCustomNameVisible(true);
            this.blueTeamShulker = shulker;
        }
    }

    private void removeShulker(boolean redTeam) {
        if(redTeam) {
            if(this.redTeamShulker != null) {
                this.redTeamShulker.remove();
            }
            return;
        }

        if(this.blueTeamShulker != null) {
            this.blueTeamShulker.remove();
        }
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
        inventory.setItem(2, ItemBuilder.newBuilder(Material.BED)
                .displayname("§6Set Shulker").lore("§eDeine Position wird übernommen").get());
        inventory.setItem(4, ItemBuilder.newBuilder(Material.CLAY_BRICK)
                .displayname("§6Set Bronze-Drop").lore("§eDeine Position wird übernommen").get());
        inventory.setItem(5, ItemBuilder.newBuilder(Material.IRON_INGOT)
                .displayname("§6Set Silver-Drop").lore("§eDeine Position wird übernommen").get());
        inventory.setItem(6, ItemBuilder.newBuilder(Material.GOLD_INGOT)
                .displayname("§6Set Gold-Drop").lore("§eDeine Position wird übernommen").get());
    }

    @Override
    public boolean export(Player player, YamlConfiguration configuration) {
        if(this.redTeamSpawnLocation == null) {
            player.sendMessage("§8\u00BB §cRed Team Spawn not set!");
            return false;
        }
        if(this.blueTeamSpawnLocation == null) {
            player.sendMessage("§8\u00BB §cBlue Team Spawn not set!");
            return false;
        }
        if(this.redTeamShopLocation == null) {
            player.sendMessage("§8\u00BB §cRed Team Shop not set!");
            return false;
        }
        if(this.blueTeamShopLocation == null) {
            player.sendMessage("§8\u00BB §cBlue Team Shop not set!");
            return false;
        }
        if(this.redTeamShulkerLocation == null) {
            player.sendMessage("§8\u00BB §cRed Team Shulker not set!");
            return false;
        }
        if(this.blueTeamShulkerLocation == null) {
            player.sendMessage("§8\u00BB §cBlue Team Shulker not set!");
            return false;
        }
        if(this.bronzeSpawnLocations.size() == 0) {
            player.sendMessage("§8\u00BB §cBronze Spawn Locations not set!");
            return false;
        }
        if(this.silverSpawnLocations.size() == 0) {
            player.sendMessage("§8\u00BB §cSilver Spawn Locations not set!");
            return false;
        }
        if(this.goldSpawnLocations.size() == 0) {
            player.sendMessage("§8\u00BB §cGold Spawn Locations not set!");
            return false;
        }

        removeShulker(true);
        removeShulker(false);
        this.redTeamShopFakePlayer.remove(player);
        this.blueTeamShopFakePlayer.remove(player);
        this.redTeamSpawnFakePlayer.remove(player);
        this.blueTeamSpawnFakePlayer.remove(player);

        configuration.set("team.red.spawn", this.redTeamSpawnLocation.getX()+";"+this.redTeamSpawnLocation.getY()+";"+
                this.redTeamSpawnLocation.getZ()+";"+this.redTeamSpawnLocation.getYaw()+";"+this.redTeamSpawnLocation.getPitch());
        configuration.set("team.blue.spawn", this.blueTeamSpawnLocation.getX()+";"+this.blueTeamSpawnLocation.getY()+";"+
                this.blueTeamSpawnLocation.getZ()+";"+this.blueTeamSpawnLocation.getYaw()+";"+this.blueTeamSpawnLocation.getPitch());

        configuration.set("team.red.shulker", this.redTeamShulkerLocation.getX()+";"+this.redTeamShulkerLocation.getY()+";"+
                this.redTeamShulkerLocation.getZ()+";0;0");
        configuration.set("team.blue.shulker", this.blueTeamShulkerLocation.getX()+";"+this.blueTeamShulkerLocation.getY()+";"+
                this.blueTeamShulkerLocation.getZ()+";0;0");

        configuration.set("team.red.shop", this.redTeamShopLocation.getX()+";"+this.redTeamShopLocation.getY()+";"+
                this.redTeamShopLocation.getZ()+";0;0");
        configuration.set("team.blue.shop", this.blueTeamShopLocation.getX()+";"+this.blueTeamShopLocation.getY()+";"+
                this.blueTeamShopLocation.getZ()+";0;0");
        ArrayList<String> bronze = new ArrayList<>(), silver = new ArrayList<>(), gold = new ArrayList<>();
        this.bronzeSpawnLocations.forEach(location -> bronze.add(location.getX()+";"+location.getY()+";"+location.getZ()+";0;0"));
        this.silverSpawnLocations.forEach(location -> silver.add(location.getX()+";"+location.getY()+";"+location.getZ()+";0;0"));
        this.goldSpawnLocations.forEach(location -> gold.add(location.getX()+";"+location.getY()+";"+location.getZ()+";0;0"));

        configuration.set("drop.bronze.locations", bronze);
        configuration.set("drop.silver.locations", silver);
        configuration.set("drop.gold.locations", gold);

        configuration.set("drop.bronze.delay", 1);
        configuration.set("drop.silver.delay", 8);
        configuration.set("drop.gold.delay", 20);
        return true;
    }

    @Override
    public void onInteract(PlayerInteractEvent e) {
        switch (e.getItem().getItemMeta().getDisplayName()) {
            case "§6Set Spawn":
                e.setCancelled(true);
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
                e.setCancelled(true);
                if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    this.redTeamShopLocation = e.getPlayer().getLocation();
                    spawnShopFakePlayer(e.getPlayer(), true);
                } else
                if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    this.blueTeamShopLocation = e.getPlayer().getLocation();
                    spawnShopFakePlayer(e.getPlayer(), false);
                } else
                    e.getPlayer().sendMessage("§8\u00BB §cUngültige Aktion!");
                break;
            case "§6Set Shulker":
                e.setCancelled(true);
                if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    this.redTeamShulkerLocation = e.getPlayer().getLocation();
                    spawnShulker(true);
                } else
                if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    this.blueTeamShulkerLocation = e.getPlayer().getLocation();
                    spawnShulker(false);
                } else
                    e.getPlayer().sendMessage("§8\u00BB §cUngültige Aktion!");
                break;
            case "§6Set Bronze-Drop":
                e.setCancelled(true);
                this.bronzeSpawnLocations.add(e.getPlayer().getLocation());
                e.getPlayer().sendMessage("§8\u00BB §7Location hinzugefügt: §eBronze");
                break;
            case "§6Set Silver-Drop":
                e.setCancelled(true);
                this.silverSpawnLocations.add(e.getPlayer().getLocation());
                e.getPlayer().sendMessage("§8\u00BB §7Location hinzugefügt: §eSilver");
                break;
            case "§6Set Gold-Drop":
                e.setCancelled(true);
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
