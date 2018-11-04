package de.gamechest.buildplugin;

import de.gamechest.GameChest;
import de.gamechest.database.DatabasePlayerObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by ByteList on 04.11.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class BlockInfo {

    private final BuildPlugin buildPlugin = BuildPlugin.getInstance();
    private static final HashMap<String, BlockInfo> cache = new HashMap<>();
    private static final String VERSION = "1.0";

    private final File file;
    private final YamlConfiguration configuration;
    @Getter
    private final String version;
    @Getter
    private final String world;

    private final HashMap<String, Block> blocks = new HashMap<>();
    @Getter
    private final ArrayList<UUID> infoModePlayers = new ArrayList<>();

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
                this.configuration.getString(path + ".material"),
                Byte.valueOf(this.configuration.getString(path + ".data")),
                this.configuration.getString(path + ".savedString")
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

    public static BlockInfo getBlockInfo(World world) {
        if (!cache.containsKey(world.getName())) {
            cache.put(world.getName(), new BlockInfo(world.getName()));
        }

        return cache.get(world.getName());
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
}
