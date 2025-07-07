package org.kobokorp.smashcraft;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShieldManager {
    private static final int MAX_DURATION_TICKS = 100; // 5 seconds
    private static final int COOLDOWN_TICKS = 60;       // 3 seconds

    private final Map<UUID, Integer> activeShields = new HashMap<>();
    private final Map<UUID, Integer> cooldowns = new HashMap<>();

    public void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID id = player.getUniqueId();

            // Handle active shield
            if (activeShields.containsKey(id)) {
                int ticksLeft = activeShields.get(id) - 1;
                if (ticksLeft <= 0) {
                    disableShield(id, true); // Break
                } else {
                    activeShields.put(id, ticksLeft);
                }
                continue;
            }

            // Try to activate if sneak is held and not on cooldown
            if (player.isSneaking() && !cooldowns.containsKey(id)) {
                activeShields.put(id, MAX_DURATION_TICKS);
                player.setGlowing(true);
                player.sendMessage("§bShield activated!");
                player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1f, 1f);
            }
        }

        // Tick down cooldowns
        for (UUID id : cooldowns.keySet().toArray(UUID[]::new)) {
            int ticksLeft = cooldowns.get(id) - 1;
            if (ticksLeft <= 0) {
                cooldowns.remove(id);
            } else {
                cooldowns.put(id, ticksLeft);
            }
        }

        // Cancel shield if player released sneak early
        for (UUID id : activeShields.keySet().toArray(UUID[]::new)) {
            Player player = Bukkit.getPlayer(id);
            if (player == null || !player.isSneaking()) {
                disableShield(id, false); // Manual release
            }
        }
    }

    private void disableShield(UUID playerId, boolean broke) {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null) return;

        if (activeShields.remove(playerId) != null) {
            player.setGlowing(false);

            // Always apply cooldown
            cooldowns.put(playerId, COOLDOWN_TICKS);

            if (broke) {
                player.sendMessage("§cShield broke!");
                player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, 1f);
            } else {
                player.sendMessage("§7Shield released.");
            }
        }
    }


    public boolean isShielding(UUID playerId) {
        return activeShields.containsKey(playerId);
    }

    public void clearAll() {
        for (UUID id : activeShields.keySet()) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) p.setGlowing(false);
        }
        activeShields.clear();
        cooldowns.clear();
    }
}
