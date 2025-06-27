package org.kobokorp.smashcraft;

import java.util.HashMap;
import java.util.UUID;

public class DamageManager {

    private final HashMap<UUID, Double> playerDamage = new HashMap<>();

    public double getDamage(UUID playerId) {
        return playerDamage.getOrDefault(playerId, 0.0);
    }

    public void addDamage(UUID playerId, double amount) {
        double current = getDamage(playerId);
        double newDamage = Math.max(0, Math.min(current + amount, 999));
        playerDamage.put(playerId, newDamage);
    }

    public void resetDamage(UUID playerId) {
        playerDamage.put(playerId, 0.0);
    }

    public void remove(UUID playerId) {
        playerDamage.remove(playerId);
    }

    public String getFormattedDamage(UUID playerId) {
        return String.format("%.1f%%", getDamage(playerId));
    }
}
