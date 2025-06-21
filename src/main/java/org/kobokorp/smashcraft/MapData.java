package org.kobokorp.smashcraft;

import org.bukkit.Location;

import java.util.List;
import java.util.Random;

public class MapData {
    private final String name;
    private final int deathY;
    private final List<Location> spawnPoints;

    public MapData(String name, int deathY, List<Location> spawnPoints) {
        this.name = name;
        this.deathY = deathY;
        this.spawnPoints = spawnPoints;
    }

    public String getName() {
        return name;
    }

    public int getDeathY() {
        return deathY;
    }

    public List<Location> getSpawnPoints() {
        return spawnPoints;
    }

    public Location getRandomSpawn() {
        return spawnPoints.get(new Random().nextInt(spawnPoints.size()));
    }
}
