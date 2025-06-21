package org.kobokorp.smashcraft;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapManager {
    private static final Map<String, MapData> maps = new HashMap<>();

    public static void loadMaps(JavaPlugin plugin) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(
                new File(plugin.getDataFolder(), "config.yml"));

        ConfigurationSection root = config.getConfigurationSection("maps");
        if (root == null) return;

        for (String mapName : root.getKeys(false)) {
            ConfigurationSection section = root.getConfigurationSection(mapName);
            if (section == null) continue;

            int deathY = section.getInt("deathY", 70);
            List<Map<?, ?>> rawSpawns = section.getMapList("spawns");
            List<Location> spawns = new ArrayList<>();
            World world = Bukkit.getWorld("world");

            for (Map<?, ?> spawn : rawSpawns) {
                double x = ((Number) spawn.get("x")).doubleValue();
                double y = ((Number) spawn.get("y")).doubleValue();
                double z = ((Number) spawn.get("z")).doubleValue();
                spawns.add(new Location(world, x, y, z));
            }

            maps.put(mapName.toLowerCase(), new MapData(mapName, deathY, spawns));
        }
    }

    public static MapData getMap(String name) {
        if (name == null) return null;
        return maps.get(name.toLowerCase());
    }

    public static boolean mapExists(String name) {
        return maps.containsKey(name.toLowerCase());
    }
}
