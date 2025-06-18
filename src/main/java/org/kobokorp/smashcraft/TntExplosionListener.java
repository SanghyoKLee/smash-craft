package org.kobokorp.smashcraft;

import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class TntExplosionListener implements Listener {

    @EventHandler
    public void onTntExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof TNTPrimed tnt)) return;
        if (!tnt.hasMetadata("smashcraft_tnt")) return;

        // Prevent all block damage
        event.blockList().clear();
    }
}
