package org.kobokorp.smashcraft;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class TripleJumpListener implements Listener {

    private final HashMap<UUID, Integer> jumpCounts = new HashMap<>();
    private final JavaPlugin plugin;

    private static final int MAX_AIR_JUMPS = 2;

    public TripleJumpListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        jumpCounts.put(player.getUniqueId(), 0);
        player.setAllowFlight(false);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        jumpCounts.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!isInValidGameMode(player)) return;

        if (player.isOnGround()) {
            jumpCounts.put(player.getUniqueId(), 0);
            player.setAllowFlight(true); // Allow flight so player can use space to jump mid-air
        } else {
            int usedJumps = jumpCounts.getOrDefault(player.getUniqueId(), 0);
            if (usedJumps < MAX_AIR_JUMPS) {
                player.setAllowFlight(true); // Enable flight if they still have jumps left
            } else {
                player.setAllowFlight(false); // Disable once out of jumps
            }
        }
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (!isInValidGameMode(player)) return;

        int usedJumps = jumpCounts.getOrDefault(player.getUniqueId(), 0);
        if (usedJumps >= MAX_AIR_JUMPS) {
            player.setAllowFlight(false);
            return;
        }

        event.setCancelled(true);
        player.setFlying(false);

        // Stronger forward jump
        Vector jumpVector = player.getLocation().getDirection().multiply(0.33).setY(0.76);
        player.setVelocity(jumpVector);

        // Mid-air animation
        player.getWorld().spawnParticle(
                org.bukkit.Particle.CLOUD, // You can try CLOUD, CRIT, etc.
                player.getLocation().add(0, 1, 0), // Slightly above feet
                10, // Count
                0.3, 0.3, 0.3, // Offset
                0 // Speed (0 for default)
        );

        player.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1f, 1.0f);
        jumpCounts.put(player.getUniqueId(), usedJumps + 1);

        if (usedJumps + 1 < MAX_AIR_JUMPS) {
            player.setAllowFlight(true);
        } else {
            player.setAllowFlight(false);
        }
    }


    private boolean isInValidGameMode(Player player) {
        return player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE;
    }

    public int getUsedJumps(Player player) {
        return jumpCounts.getOrDefault(player.getUniqueId(), 0);
    }
}
