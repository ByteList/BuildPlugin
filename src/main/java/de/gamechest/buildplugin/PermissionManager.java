package de.gamechest.buildplugin;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by ByteList on 23.03.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PermissionManager {

    private final BuildPlugin buildPlugin = BuildPlugin.getInstance();

    private HashMap<UUID, PermissionAttachment> permissionAttachments = new HashMap<>();

    public void setPlotPermissions(Player player, boolean set) {
        setPermission(player, set, getPlotPermissions());
    }

    public String[] getPlotPermissions() {
        return new String[] {
                "worldedit.*",
                "voxelsniper.brush.*",
                "voxelsniper.sniper",
                "voxelsniper.goto"
        };
    }


    public void setPermission(Player player, boolean b, String... permissions) {
        PermissionAttachment permissionAttachment;

        if(!permissionAttachments.containsKey(player.getUniqueId())) {
            permissionAttachment = player.addAttachment(buildPlugin);
            permissionAttachments.put(player.getUniqueId(), permissionAttachment);
        } else {
            permissionAttachment = permissionAttachments.get(player.getUniqueId());
        }

        if(b) {
            for(String perm : permissions) permissionAttachment.setPermission(perm, true);
        } else {
            for(String perm : permissions) permissionAttachment.unsetPermission(perm);
            player.removeAttachment(permissionAttachment);
            permissionAttachments.remove(player.getUniqueId());
        }
    }
}
