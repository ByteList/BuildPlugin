package de.gamechest.buildplugin.listener;

import de.gamechest.GameChest;
import de.gamechest.buildplugin.BuildPlugin;
import de.gamechest.database.DatabaseManager;
import de.gamechest.database.DatabasePlayer;
import de.gamechest.database.DatabasePlayerObject;
import de.gamechest.database.uuidbuffer.DatabaseUuidBuffer;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Created by ByteList on 03.11.17.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class LoginListener implements Listener {

    private final GameChest gameChest = GameChest.getInstance();

    @EventHandler(priority = EventPriority.HIGH)
    public void onLogin(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        DatabaseManager databaseManager = gameChest.getDatabaseManager();

        DatabasePlayer dbPlayer = new DatabasePlayer(databaseManager, player.getUniqueId());

        if(!dbPlayer.existsPlayer()) {
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            e.setKickMessage(gameChest.prefix+"§cDu hast keine Berechtigung für den Build-Server!");
            return;
        }

        dbPlayer.updatePlayer();
        DatabaseUuidBuffer databaseUuidBuffer = databaseManager.getDatabaseUuidBuffer();
        String lastName = null;
        if (dbPlayer.getDatabaseElement(DatabasePlayerObject.LAST_NAME).getObject() != null) {
            lastName = dbPlayer.getDatabaseElement(DatabasePlayerObject.LAST_NAME).getAsString();
        }

        if (lastName == null) {
            databaseUuidBuffer.createPlayer(player.getName(), player.getUniqueId());
        } else if (!lastName.equals(player.getName())) {
            databaseUuidBuffer.removePlayer(lastName);
            databaseUuidBuffer.createPlayer(player.getName(), player.getUniqueId());
        }

        Document configurations = dbPlayer.getDatabaseElement(DatabasePlayerObject.CONFIGURATIONS).getAsDocument();

        if(configurations.getInteger(DatabasePlayerObject.Configurations.BUILD_CONNECT.getName()) == 0) {
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            e.setKickMessage(gameChest.prefix+"§cDu hast keine Berechtigung für den Build-Server!");
            return;
        }

        BuildPlugin.getInstance().getPlayerManager().configurationsCache.put(player.getUniqueId(), configurations);

        BuildPlugin.getInstance().getPermissionManager().setPlotPermissions(player, true);
    }
}
