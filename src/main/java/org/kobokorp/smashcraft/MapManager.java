package org.kobokorp.smashcraft;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MapManager {
    private static final Map<String, List<Location>> mapSpawns = new HashMap<>();

    public static void registerMap(String name, List<Location> spawns) {
        mapSpawns.put(name.toLowerCase(), spawns);
    }

    public static List<Location> getSpawns(String name) {
        return mapSpawns.get(name.toLowerCase());
    }

    public static Location getRandomSpawn(String name) {
        List<Location> spawns = getSpawns(name);
        return (spawns == null || spawns.isEmpty()) ? null :
                spawns.get(new Random().nextInt(spawns.size()));
    }
}
