package org.kobokorp.smashcraft.customitem;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.kobokorp.smashcraft.customitem.items.BeastScroll;

public class BeastScrollListener implements Listener {

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity mob)) return;
        if (!(event.getTarget() instanceof Player targetPlayer)) return;

        if (!mob.getPersistentDataContainer().has(BeastScroll.OWNER_KEY, PersistentDataType.STRING)) return;

        String ownerUuid = mob.getPersistentDataContainer().get(BeastScroll.OWNER_KEY, PersistentDataType.STRING);
        if (ownerUuid == null) return;

        if (targetPlayer.getUniqueId().toString().equals(ownerUuid)) {
            event.setCancelled(true);
        }
    }
}
