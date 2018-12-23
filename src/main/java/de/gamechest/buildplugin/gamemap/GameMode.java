package de.gamechest.buildplugin.gamemap;

import lombok.Getter;

import java.util.ArrayList;

/**
 * Created by ByteList on 22.12.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public enum GameMode {

    SHULKER_DEFENCE("ShulkerDefence"),
    DEATH_RUN("DeathRun"),
    CLICK_ATTACK("ClickAttack");

    @Getter
    private final String mode;

    GameMode(String mode) {
        this.mode = mode;
    }

    public static GameMode getGameMode(String mode) {
        for (GameMode gameMode: values()) {
            if(gameMode.getMode().equals(mode))
                return gameMode;
        }
        return null;
    }

    public static ArrayList<String> getModes() {
        ArrayList<String> list = new ArrayList<>();
        for (GameMode gameMode: values()) {
            list.add(gameMode.getMode());
        }
        return list;
    }
}
