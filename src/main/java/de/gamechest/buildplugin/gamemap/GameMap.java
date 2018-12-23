package de.gamechest.buildplugin.gamemap;

import de.gamechest.ItemBuilder;
import de.gamechest.buildplugin.BuildPlugin;
import de.gamechest.buildplugin.gamemap.mode.ClickAttackMode;
import de.gamechest.buildplugin.gamemap.mode.DeathRunMode;
import de.gamechest.buildplugin.gamemap.mode.ShulkerDefenceMode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ByteList on 22.12.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameMap implements Listener {

    private final BuildPlugin buildPlugin = BuildPlugin.getInstance();

    public static final String SETTINGS_INVENTORY_NAME = "§1Einstellungen", ICON_ITEM_INVENTORY_NAME = "§1Set Icon-Item";

    @Getter
    private final Player player;
    @Getter
    private final World world;
    @Getter
    private GameMode gameMode;
    @Getter
    private File directory, configFile;
    @Getter
    private YamlConfiguration configuration;
    @Getter
    private IMode mode;
    @Getter @Setter
    private String name;
    @Getter
    private ArrayList<Integer> tasks = new ArrayList<>();

    private ItemStack[] armorContents, contents;
    private ItemStack iconItemStack;

    public GameMap(Player player, World world, GameMode gameMode, String name) {
        this.player = player;
        this.world = world;
        this.gameMode = gameMode;
        this.name = name;

        this.directory = new File(new File(BuildPlugin.getInstance().getGameMapRootDirectory(), gameMode.getMode()), name+"/");
        this.configFile = new File(this.directory, "config.yml");
        this.configuration = YamlConfiguration.loadConfiguration(this.configFile);

        if(!this.configFile.exists()) {
            this.directory.mkdirs();
            set("displayname", name).set("game", gameMode.getMode()).saveConfig();
            this.iconItemStack = ItemBuilder.newBuilder(Material.SAND).get();
        }

        switch (gameMode) {
            case SHULKER_DEFENCE:
                this.mode = new ShulkerDefenceMode(this);
                break;
            case DEATH_RUN:
                this.mode = new DeathRunMode();
                break;
            case CLICK_ATTACK:
                this.mode = new ClickAttackMode();
                break;
        }

        this.armorContents = player.getInventory().getArmorContents();
        this.contents = player.getInventory().getContents();
        player.getInventory().clear();

        this.mode.loadInventory(player);

        player.getInventory().setItem(8, ItemBuilder.newBuilder(Material.NAME_TAG)
                .displayname("§6Einstellungen").get());

        Bukkit.getPluginManager().registerEvents(this, buildPlugin);
    }

    public void saveConfig() {
        try {
            this.configuration.save(this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GameMap set(String path, Object object) {
        this.configuration.set(path, object);
        return this;
    }

    public void openSettingsInventory() {
        Inventory inventory = Bukkit.createInventory(null, 27, SETTINGS_INVENTORY_NAME);

        ItemStack itemStack = ItemBuilder.getPlaceholder();

        for (int i = 0; i < 9; i++) inventory.setItem(i, itemStack);
        for (int i = 18; i < 27; i++) inventory.setItem(i, itemStack);

        inventory.setItem(10, ItemBuilder.newBuilder(Material.PAPER).displayname("§6Set Name").get());
        inventory.setItem(13, ItemBuilder.newBuilder(Material.HOPPER).displayname("§6Set Icon-Item").lore("§7Item in mittleren Slot packen", "und Inventar schließen").get());
        inventory.setItem(16, ItemBuilder.newBuilder(Material.EMERALD).displayname("§6Export").get());

        this.player.openInventory(inventory);
    }

    public void openIconItemInventory() {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, ICON_ITEM_INVENTORY_NAME);

        ItemStack itemStack = ItemBuilder.getPlaceholder();
        inventory.setItem(0, itemStack);
        inventory.setItem(1, itemStack);
        inventory.setItem(2, this.iconItemStack);
        inventory.setItem(3, itemStack);
        inventory.setItem(4, itemStack);

        this.player.openInventory(inventory);
    }

    public void export() {
        player.sendMessage("§8\u00BB §7Die Welt wird erxportiert...");
        this.mode.export(this.configuration);
        set("displayname", this.name).set("game", this.gameMode.getMode());
        set("item.material", iconItemStack.getType().name()).set("item.data", ""+iconItemStack.getData().getData());
        saveConfig();
        disable();

        Bukkit.unloadWorld(this.world, true);

        try {
            File world = new File(this.directory, "./world/");
            world.mkdirs();
            FileUtils.copyDirectory(this.world.getWorldFolder(), world);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendMessage("§8\u00BB §7Die Welt wurde erfolgreich erxportiert: §e"+this.name);

        player.getInventory().clear();
        player.getInventory().setArmorContents(this.armorContents);
        player.getInventory().setContents(this.contents);
    }

    public void disable() {
        HandlerList.unregisterAll(this);
        this.tasks.forEach(integer -> Bukkit.getScheduler().cancelTask(integer));
        buildPlugin.getPlayerManager().removeGameMap(player.getUniqueId());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if(this.player.getName().equals(player.getName())) {
            if(e.getItem() != null) {
                e.setCancelled(true);
                ItemStack item = e.getItem();

                switch (item.getItemMeta().getDisplayName()) {
                    case "§6Einstellungen":
                        this.openSettingsInventory();
                        break;
                }

                this.mode.onInteract(e);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if(this.player.getName().equals(player.getName())) {
            if(e.getClickedInventory() != null && e.getCurrentItem() != null) {
                Inventory inventory = e.getClickedInventory();
                ItemStack item = e.getCurrentItem();

                switch (inventory.getName()) {
                    case SETTINGS_INVENTORY_NAME:
                        e.setCancelled(true);
                        switch (item.getItemMeta().getDisplayName()) {
                            case "§6Set Name":
                                player.closeInventory();
                                player.sendMessage("§8\u00BB §7Gebe den §eneuen Namen§7 in den Chat ein:");
                                buildPlugin.getPlayerManager().getWaitingForGameMapChangeName().add(player.getUniqueId());
                                break;
                            case "§6Set Icon-Item":
                                openIconItemInventory();
                                break;
                            case "§6Export":
                                export();
                        }
                        break;
                    case ICON_ITEM_INVENTORY_NAME:
                        if(e.getSlot() != 2) {
                            e.setCancelled(true);
                        } else {
                            this.iconItemStack = inventory.getItem(2);
                        }
                        break;
                }

                this.mode.onInventoryClick(e);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();

        if(this.player.getName().equals(player.getName())) {
            if(e.getInventory().getName().equals(ICON_ITEM_INVENTORY_NAME)) {
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 2F, 2F);
            }

            this.mode.onInventoryClose(e);
        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent e) {
        this.mode.onPickUp(e);
    }

}
