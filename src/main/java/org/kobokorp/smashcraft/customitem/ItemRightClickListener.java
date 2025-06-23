package org.kobokorp.smashcraft.customitem;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemRightClickListener implements Listener {
    private final CustomItemManager itemManager;
    private final CooldownManager cooldownManager;

    public ItemRightClickListener(CustomItemManager itemManager, CooldownManager cooldownManager) {
        this.itemManager = itemManager;
        this.cooldownManager = cooldownManager;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        var item = event.getItem();
        var player = event.getPlayer();
        var custom = itemManager.getByItem(item);
        if (custom == null) return;

        String key = custom.getName();
        var playerId = player.getUniqueId();

        if (cooldownManager.isOnCooldown(key, playerId)) {
            long remaining = cooldownManager.getRemaining(key, playerId) / 1000;
            player.sendMessage("§c" + custom.getName() + " is on cooldown for " + remaining + "s!");
            return;
        }

        if (custom.onRightClick(player)) {
            // Set cooldown and perform ability
            long cooldownMillis = custom.getCooldownSeconds() * 1000;
            if (cooldownMillis > 0) {
                cooldownManager.setCooldown(key, playerId, cooldownMillis);
            }
        }
    }
}
