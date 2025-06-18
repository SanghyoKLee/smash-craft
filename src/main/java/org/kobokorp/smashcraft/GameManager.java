package org.kobokorp.smashcraft;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

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
        Bukkit.getLogger().info("Start command called with: " + mapName);

        World world = Bukkit.getWorld("world");
        List<Location> spawns = MapManager.getSpawns(mapName);
        if (spawns == null || spawns.isEmpty()) {
            Bukkit.broadcastMessage(ChatColor.RED + "Failed to start game: no spawns found.");
            return;
        }
        currentMap = mapName;
        gameRunning = true;
        List<Player> players = Bukkit.getOnlinePlayers().stream()
                .map(p -> (Player) p)
                .collect(Collectors.toList());


        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            UUID id = p.getUniqueId();

            // Reset lives and state
            playerLives.put(id, 3);
            activePlayers.add(id);
            p.setGameMode(GameMode.ADVENTURE);

            // ✅ Reset damage and update scoreboard
            damageManager.resetDamage(id);
            displayUpdater.update(p);

            teleportAndInvuln(p, spawns.get(i % spawns.size()));
        }

        Bukkit.broadcastMessage(ChatColor.GREEN + "Game started on " + mapName + "!");
    }

    public void handleFall(Player player) {
        if (!gameRunning || !activePlayers.contains(player.getUniqueId())) return;

        int lives = playerLives.getOrDefault(player.getUniqueId(), 0) - 1;
        playerLives.put(player.getUniqueId(), lives);

        if (lives <= 0) {
            activePlayers.remove(player.getUniqueId());
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(ChatColor.RED + "You're out!");
            checkWinCondition();

            List<Location> spawns = MapManager.getSpawns(currentMap);
            if (spawns != null && !spawns.isEmpty()) {
                player.teleport(spawns.get(0)); // or any designated spectator spot
            }
        } else {
            damageManager.resetDamage(player.getUniqueId());
            displayUpdater.update(player);

            teleportAndInvuln(player, MapManager.getRandomSpawn(currentMap));
            player.sendMessage(ChatColor.YELLOW + "You have " + lives + " lives left!");
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
            }
            gameRunning = false;
        }
    }
}
