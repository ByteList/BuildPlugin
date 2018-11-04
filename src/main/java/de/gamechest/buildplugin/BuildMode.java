package de.gamechest.buildplugin;

import lombok.Getter;

import java.util.Objects;

/**
 * Created by ByteList on 03.11.17.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public enum BuildMode {

    SPECTATE(0, "Spectate", "Spec", "§7"),
    BUILD(1, "Build", "Build", "§3"),
    OPERATOR(2, "Operator", "Op", "§c");

    @Getter
    private int id;
    @Getter
    private String name, shortName, color;

    BuildMode(int id, String name, String shortName, String color) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.color = color;
    }

    public static BuildMode getBuildMode(int id) {
        for(BuildMode buildMode : values()) {
            if(buildMode.id == id) {
                return buildMode;
            }
        }
        return SPECTATE;
    }

    public static BuildMode getBuildMode(String name) {
        for(BuildMode buildMode : values()) {
            if(Objects.equals(buildMode.name, name)) {
                return buildMode;
            }
        }
        return null;
    }
}
