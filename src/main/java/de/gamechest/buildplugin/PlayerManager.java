package de.gamechest.buildplugin;

import org.bson.Document;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by ByteList on 03.11.17.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PlayerManager {

    public HashMap<UUID, Document> configurationsCache = new HashMap<>();
}
