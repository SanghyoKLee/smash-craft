package org.kobokorp.smashcraft.customitem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final Map<String, Map<UUID, Long>> cooldowns = new HashMap<>();

    public boolean isOnCooldown(String key, UUID playerId) {
        long now = System.currentTimeMillis();
        return cooldowns.containsKey(key) &&
                cooldowns.get(key).getOrDefault(playerId, 0L) > now;
    }

    public void setCooldown(String key, UUID playerId, long durationMillis) {
        cooldowns.computeIfAbsent(key, k -> new HashMap<>())
                .put(playerId, System.currentTimeMillis() + durationMillis);
    }

    public long getRemaining(String key, UUID playerId) {
        long now = System.currentTimeMillis();
        return Math.max(0, cooldowns.getOrDefault(key, Map.of()).getOrDefault(playerId, 0L) - now);
    }
}
