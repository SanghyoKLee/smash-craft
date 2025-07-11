package org.kobokorp.smashcraft;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

public class GameManager {
    private final Smashcraft plugin;
    private final Map<UUID, Integer> playerLives = new HashMap<>();
    private final Set<UUID> invulnerablePlayers = new HashSet<>();
    private final Set<UUID> activePlayers = new HashSet<>();
    private boolean gameRunning = false;
    private String currentMap;
    private final DamageManager damageManager;
    private final DisplayUpdater displayUpdater;

    public GameManager(Smashcraft plugin, DamageManager damageManager, DisplayUpdater displayUpdater) {
        this.plugin = plugin;
        this.damageManager = damageManager;
        this.displayUpdater = displayUpdater;
    }

    public void startGame(String mapName) {
        MapData map = MapManager.getMap(mapName);
        if (map == null || map.getSpawnPoints().isEmpty()) {
            Bukkit.broadcastMessage(ChatColor.RED + "Map not found or has no spawn points.");
            return;
        }

        playerLives.clear();
        activePlayers.clear();
        invulnerablePlayers.clear();
        gameRunning = true;
        currentMap = mapName;

        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            UUID id = p.getUniqueId();

            playerLives.put(id, 3);
            activePlayers.add(id);
            p.setGameMode(GameMode.ADVENTURE);
            damageManager.resetDamage(id);
            displayUpdater.update(p);
            teleportAndInvuln(p, map.getSpawnPoints().get(i % map.getSpawnPoints().size()));
        }

        Bukkit.broadcastMessage(ChatColor.GREEN + "Game started on " + mapName + "!");
    }

    public void handleFall(Player player) {
        if (!gameRunning || !activePlayers.contains(player.getUniqueId())) return;

        int lives = playerLives.getOrDefault(player.getUniqueId(), 0) - 1;
        playerLives.put(player.getUniqueId(), lives);
        MapData map = MapManager.getMap(currentMap);

        if (lives <= 0) {
            activePlayers.remove(player.getUniqueId());
            player.setGameMode(GameMode.SPECTATOR);
            Bukkit.broadcastMessage(
                    ChatColor.RED + player.getName() + " is out!"
            );
            checkWinCondition();
            List<Location> spawns = map.getSpawnPoints();
            if (spawns != null && !spawns.isEmpty()) {
                player.teleport(map.getSpawnPoints().get(0));
            }
        } else {
            damageManager.resetDamage(player.getUniqueId());
            displayUpdater.update(player);

            map.getRandomSpawn();
            if (map != null) {
                teleportAndInvuln(player, map.getRandomSpawn());
            }

            Bukkit.broadcastMessage(
                    ChatColor.YELLOW + player.getName() + " has " + lives + " lives remaining!"
            );
        }
    }

    private void teleportAndInvuln(Player player, Location loc) {
        player.teleport(loc);
        player.setNoDamageTicks(100); // 5 seconds
        invulnerablePlayers.add(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(plugin, () -> invulnerablePlayers.remove(player.getUniqueId()), 100L);
    }

    public boolean isInvulnerable(Player player) {
        return invulnerablePlayers.contains(player.getUniqueId());
    }

    private void checkWinCondition() {
        if (activePlayers.size() == 1) {
            UUID winnerId = activePlayers.iterator().next();
            Player winner = Bukkit.getPlayer(winnerId);
            if (winner != null) {
                Bukkit.broadcastMessage(ChatColor.GOLD + winner.getName() + " wins!");
                winner.getWorld().spawnParticle(Particle.FIREWORK, winner.getLocation(), 100);
                winner.getWorld().playSound(winner.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f, 1f);
                // Increment wins
                plugin.getLeaderboardManager().incrementWins(winner.getUniqueId());

                // Save updated leaderboard
                plugin.getLeaderboardManager().saveData();

                // Broadcast the leaderboard
                plugin.getLeaderboardManager().broadcastLeaderboard();
            }
            gameRunning = false;
        }
    }

    public String getCurrentMap() {
        return currentMap;
    }
}
