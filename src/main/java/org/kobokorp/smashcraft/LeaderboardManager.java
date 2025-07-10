package org.kobokorp.smashcraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LeaderboardManager {

    private final Map<UUID, Integer> wins = new ConcurrentHashMap<>();
    private final Smashcraft plugin;
    private File file;
    private FileConfiguration config;

    public LeaderboardManager(Smashcraft plugin) {
        this.plugin = plugin;
        loadData();
    }

    public void incrementWins(UUID playerId) {
        wins.put(playerId, wins.getOrDefault(playerId, 0) + 1);
    }

    public int getWins(UUID playerId) {
        return wins.getOrDefault(playerId, 0);
    }

    public List<Map.Entry<UUID, Integer>> getTopPlayers(int topN) {
        return wins.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(topN)
                .toList();
    }

    private void loadData() {
        file = new File(plugin.getDataFolder(), "leaderboard.yml");

        // Ensure the plugin folder exists
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create leaderboard.yml: " + e.getMessage());
            }
        }
        config = YamlConfiguration.loadConfiguration(file);

        if (config.contains("wins")) {
            for (String uuidString : config.getConfigurationSection("wins").getKeys(false)) {
                UUID id = UUID.fromString(uuidString);
                int winCount = config.getInt("wins." + uuidString);
                wins.put(id, winCount);
            }
            plugin.getLogger().info("Loaded leaderboard data.");
        }
    }

    public void saveData() {
        if (config == null) {
            config = new YamlConfiguration();
        }

        config.set("wins", null);
        for (Map.Entry<UUID, Integer> entry : wins.entrySet()) {
            config.set("wins." + entry.getKey().toString(), entry.getValue());
        }

        try {
            config.save(file);
            plugin.getLogger().info("Saved leaderboard data.");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save leaderboard data: " + e.getMessage());
        }
    }

    public void broadcastLeaderboard() {
        List<Map.Entry<UUID, Integer>> topPlayers = getTopPlayers(3);

        if (topPlayers.isEmpty()) {
            Bukkit.broadcastMessage("§eNo wins recorded yet.");
            return;
        }

        Bukkit.broadcastMessage("§b🏆 Smashcraft Leaderboard 🏆");

        int rank = 1;
        for (Map.Entry<UUID, Integer> entry : topPlayers) {
            UUID id = entry.getKey();
            int winCount = entry.getValue();
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(id);
            String name = offlinePlayer.getName() != null ? offlinePlayer.getName() : "Unknown";

            Bukkit.broadcastMessage(
                    ChatColor.GOLD + "" + rank + ". " +
                            ChatColor.YELLOW + name +
                            ChatColor.GRAY + " - " +
                            ChatColor.GREEN + winCount + " win(s)"
            );
            rank++;
        }
    }
}
