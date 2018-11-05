package de.gamechest.buildplugin;

import de.gamechest.GameChest;
import de.gamechest.database.DatabasePlayerObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by ByteList on 04.11.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class BlockInfo {

    private static final BuildPlugin buildPlugin = BuildPlugin.getInstance();
    private static final String VERSION = "1.0";

    private static final HashMap<String, BlockInfo> cache = new HashMap<>();
    private static final HashMap<UUID, PlayerInventory> infoModePlayers = new HashMap<>();

    private final File file;
    private final YamlConfiguration configuration;
    @Getter
    private final String version;
    @Getter
    private final String world;

    private final HashMap<String, Block> blocks = new HashMap<>();

    private BlockInfo(String world) {
        this.file = new File(buildPlugin.getDataFolder(), "blockinfo-" + world.replace(" ", "_") + ".yml");
        this.configuration = YamlConfiguration.loadConfiguration(this.file);

        if (!this.file.exists()) {
            buildPlugin.getDataFolder().mkdirs();
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.configuration.set("version", VERSION);
            this.configuration.set("world", world);
        }

        this.version = this.configuration.getString("version");
        this.world = this.configuration.getString("world");
    }


    public Block addBlock(Player player, org.bukkit.block.Block bukkitBlock) {
        Block block = new Block(
                bukkitBlock.getLocation().clone(),
                player.getUniqueId().toString(),
                bukkitBlock.getType().name(),
                bukkitBlock.getData(),
                bukkitBlock.toString()
        );

        if(save(block)) return block;

        return null;
    }

    public Block getBlock(org.bukkit.block.Block block) {
        Location location = block.getLocation().clone();
        return this.getBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public Block getBlock(double x, double y, double z) {
        String path = path(x, y, z);
        if(this.blocks.containsKey(path)) return this.blocks.get(path);

        if (!this.configuration.contains(path)) return null;

        Block block = new Block(
                new Location(Bukkit.getWorld(world), x, y, z),
                this.configuration.getString(path + ".uuid"),
                this.configuration.getString(path + ".block.material"),
                Byte.parseByte(this.configuration.getString(path + ".block.data")),
                this.configuration.getString(path + ".block.savedString")
        );

        this.blocks.put(path, block);

        return block;
    }

    public Collection<Block> getCachedBlocks() {
        return Collections.unmodifiableCollection(this.blocks.values());
    }

    private String path(double x, double y, double z) {
        return "blocks." + x + "." + y + "." + z;
    }

    private boolean save(Block block) {
        Location location = block.getLocation().clone();
        String path = path(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        this.configuration.set(path + ".uuid", block.getUuid());
        this.configuration.set(path + ".block.material", block.getMaterial());
        this.configuration.set(path + ".block.data", String.valueOf(block.getData()));
        this.configuration.set(path + ".block.savedString", block.getSavedString());

        try {
            this.configuration.save(this.file);
            this.blocks.put(path, block);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setInfoMode(Player player, boolean enabled) {
        if(enabled) {
            if(isInfoModeSet(player.getUniqueId())) return;

            infoModePlayers.put(player.getUniqueId(), player.getInventory());
            player.getInventory().clear();

            ItemStack itemStack = new ItemStack(Material.BEDROCK);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§eBlockInfo");
            itemMeta.setLore(Arrays.asList(" ",
                    "§7Rechtsklick: §eZeigt letzte BlockInfo an platzierter Stelle an", "",
                    "§7Linksklick: §eZeigt BlockInfo des geklickten Blocks an"));
            itemStack.setItemMeta(itemMeta);
            player.getInventory().setItem(0, itemStack);
            player.getInventory().setHeldItemSlot(0);

            player.sendMessage(buildPlugin.prefix+"§7BlockInfoMode: §aEnabled");
            return;
        }
        if(!isInfoModeSet(player.getUniqueId())) return;

        player.getInventory().clear();
        PlayerInventory inventory = infoModePlayers.get(player.getUniqueId());
        player.getInventory().setArmorContents(inventory.getArmorContents());
        player.getInventory().setContents(inventory.getContents());
        player.getInventory().setExtraContents(inventory.getExtraContents());

        infoModePlayers.remove(player.getUniqueId());

        player.sendMessage(buildPlugin.prefix+"§7BlockInfoMode: §cDisabled");
    }

    public static boolean isInfoModeSet(UUID uuid) {
        return infoModePlayers.containsKey(uuid);
    }

    public static BlockInfo getBlockInfo(World world) {
        if (!cache.containsKey(world.getName())) {
            cache.put(world.getName(), new BlockInfo(world.getName()));
        }

        return cache.get(world.getName());
    }

    public static Collection<BlockInfo> getCachedBlockInfos() {
        return Collections.unmodifiableCollection(cache.values());
    }

    @AllArgsConstructor
    public class Block {
        @Getter
        private final Location location;
        @Getter
        private final String uuid;
        @Getter
        private final String material;
        @Getter
        private final byte data;
        @Getter
        private final String savedString;

        public void show(Player player) {
            GameChest.getInstance().getDatabaseManager().getAsync().getPlayer(UUID.fromString(this.uuid), dbPlayer-> {
                player.sendMessage(
                        "§8\u00BB §c"+ dbPlayer.getDatabaseElement(DatabasePlayerObject.LAST_NAME).getAsString() +
                                "§8: §a"+this.material+":"+this.data+
                                "§8 / §e"+location.getBlockX()+", "+location.getBlockY()+", "+location.getBlockZ()
                );
//                player.sendMessage(
//                        "§8\u00BB §c"+ dbPlayer.getDatabaseElement(DatabasePlayerObject.LAST_NAME).getAsString() +
//                                "§8: §a"+this.savedString
//                );
//                player.sendMessage("§8\u00BB §7SavedString: "+this.savedString);
            }, DatabasePlayerObject.LAST_NAME);

        }
    }

    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onInteract(PlayerInteractEvent e) {
            Player player = e.getPlayer();

            if(BlockInfo.isInfoModeSet(player.getUniqueId()) && !e.isCancelled() && e.getAction() == Action.LEFT_CLICK_BLOCK) {
                e.setCancelled(true);

                BlockInfo.Block block = BlockInfo.getBlockInfo(player.getWorld()).getBlock(e.getClickedBlock());

                if(block == null) {
                    player.sendMessage(buildPlugin.prefix+"§cKeine Informationen vorhanden!");
                    return;
                }
                block.show(player);
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onQuit(PlayerQuitEvent e) {
            Player player = e.getPlayer();

            if(BlockInfo.isInfoModeSet(player.getUniqueId())) BlockInfo.setInfoMode(player, false);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onBlockPlace(BlockPlaceEvent e) {
            Player player = e.getPlayer();
            BlockInfo blockInfo = BlockInfo.getBlockInfo(player.getWorld());

            if(BlockInfo.isInfoModeSet(player.getUniqueId()) && !e.isCancelled() && player.getInventory().getItemInMainHand() != null &&
                    player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§eBlockInfo")) {
                e.setCancelled(true);

                BlockInfo.Block block = blockInfo.getBlock(e.getBlock());

                if(block == null) {
                    player.sendMessage(buildPlugin.prefix+"§cKeine Informationen vorhanden!");
                    return;
                }

                block.show(player);
                return;
            }

            if(!e.isCancelled()) {
                blockInfo.addBlock(player, e.getBlock());
            }
        }
    }
}
