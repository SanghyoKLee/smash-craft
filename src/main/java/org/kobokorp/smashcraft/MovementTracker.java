package org.kobokorp.smashcraft;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MovementTracker implements Listener {
    private final Map<UUID, Vector> lastMovementDirections = new HashMap<>();

    public MovementTracker() {
        Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugins()[0]); // Register with any loaded plugin
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Vector from = event.getFrom().toVector();
        Vector to = event.getTo().toVector();
        Vector direction = to.subtract(from);

        if (direction.lengthSquared() > 0.001) {
            direction.setY(0).normalize(); // Flatten
            lastMovementDirections.put(event.getPlayer().getUniqueId(), direction);
        }
    }

    public Vector getLastDirection(Player player) {
        return lastMovementDirections.getOrDefault(player.getUniqueId(), player.getLocation().getDirection().setY(0).normalize());
    }
}
