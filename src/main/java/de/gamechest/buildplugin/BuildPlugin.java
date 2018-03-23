package de.gamechest.buildplugin;

import de.gamechest.GameChest;
import de.gamechest.buildplugin.command.*;
import de.gamechest.buildplugin.listener.*;
import de.gamechest.buildplugin.task.CheckGamemodeTask;
import de.gamechest.buildplugin.util.BuildMode;
import de.gamechest.buildplugin.util.PlayerManager;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.DatabasePlayerObject;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

/**
 * Created by ByteList on 03.11.17.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class BuildPlugin extends JavaPlugin {

    @Getter
    private static BuildPlugin instance;

    public final String prefix = "§6BuildPlugin §8\u00BB ";

    @Getter
    private PlayerManager playerManager;

    @Getter
    private PermissionManager permissionManager;

    @Override
    public void onEnable() {
        instance = this;
        this.playerManager = new PlayerManager();
        this.permissionManager = new PermissionManager();

        Listener[] listeners = {
                new JoinListener(),
                new LoginListener(),
                new QuitListener(),
                new AsyncPlayerChatListener(),
                new ServerPingListener()
        };

        for(Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }

        getServer().getPluginCommand("nick").setExecutor(new DisabledCommand());
        getServer().getPluginCommand("opme").setExecutor(new DisabledCommand());
        getServer().getPluginCommand("serverid").setExecutor(new DisabledCommand());
        getServer().getPluginCommand("fakeplugins").setExecutor(new DisabledCommand());

        getCommand("gamemode").setExecutor(new GamemodeCommand());
        getCommand("speed").setExecutor(new SpeedCommand());
        getCommand("buildconnect").setExecutor(new BuildConnectCommand());
        getCommand("buildmode").setExecutor(new BuildModeCommand());
        getCommand("loadschematic").setExecutor(new LoadSchematicCommand());

        getServer().getScheduler().runTaskTimerAsynchronously(this, new CheckGamemodeTask(), 10L, 10L);

        getServer().getConsoleSender().sendMessage(prefix+"§aEnabled!");
    }

    @Override
    public void onDisable() {

        getServer().getConsoleSender().sendMessage(prefix+"§cDisabled!");
    }

    public BuildMode getBuildMode(UUID uuid) {
        Document configurations = this.playerManager.configurationsCache.get(uuid);

        if(configurations == null) {
            DatabasePlayer dbPlayer = new DatabasePlayer(GameChest.getInstance().getDatabaseManager(), uuid, DatabasePlayerObject.CONFIGURATIONS);
            configurations = this.playerManager.configurationsCache.put(uuid, dbPlayer.getDatabaseElement(DatabasePlayerObject.CONFIGURATIONS).getAsDocument());
        }
        int id = configurations.getInteger(DatabasePlayerObject.Configurations.BUILD_MODE.getName());
        return  BuildMode.getBuildMode(id);
    }
}
