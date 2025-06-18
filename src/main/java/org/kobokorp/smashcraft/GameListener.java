package org.kobokorp.smashcraft;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;


public class GameListener implements Listener {
    private final GameManager gameManager;

    public GameListener(GameManager manager) {
        this.gameManager = manager;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (gameManager.isInvulnerable(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerFall(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getLocation().getY() < 74) {
            gameManager.handleFall(player);
        }
    }
}
