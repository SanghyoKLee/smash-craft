package org.kobokorp.smashcraft;

import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

public class GeneralDamageListener implements Listener {

    private final DamageManager damageManager;
    private final DisplayUpdater displayUpdater;
    private final ShieldManager shieldManager;

    public GeneralDamageListener(DamageManager manager, DisplayUpdater updater, ShieldManager shieldManager) {
        this.damageManager = manager;
        this.displayUpdater = updater;
        this.shieldManager = shieldManager;
    }

    @EventHandler
    public void onAnyDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        // Ignore melee attacks handled elsewhere
        if (event instanceof EntityDamageByEntityEvent) return;

        EntityDamageEvent.DamageCause cause = event.getCause();
        UUID playerId = player.getUniqueId();

        boolean isExplosion = cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ||
                cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION;

        boolean isBypassShield = cause == EntityDamageEvent.DamageCause.FIRE ||
                cause == EntityDamageEvent.DamageCause.FIRE_TICK ||
                cause == EntityDamageEvent.DamageCause.POISON ||
                isExplosion;

        // Cancel if shielding and damage shouldn't bypass
        if (shieldManager.isShielding(playerId) && !isBypassShield) {
            event.setCancelled(true);
            //player.sendMessage(ChatColor.BLUE + "Shield blocked the attack!");
            return;
        }

        // Damage values
        double damage = switch (cause) {
            case FALL, POISON -> 4;
            case FIRE, FIRE_TICK -> 2;
            case LAVA -> 5;
            case BLOCK_EXPLOSION, ENTITY_EXPLOSION -> 25;
            case MAGIC -> 6;
            case THORNS -> 3;
            default -> 1;
        };

        // Apply damage
        damageManager.addDamage(playerId, damage);
        displayUpdater.update(player);

        // Knockback logic
        if (!cause.equals(EntityDamageEvent.DamageCause.POISON) &&
                !cause.equals(EntityDamageEvent.DamageCause.FIRE) &&
                !cause.equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {

            double percent = damageManager.getDamage(playerId);
            Vector knockback = new Vector(0, 0.5 + percent / 150.0, 0);
            player.setVelocity(knockback);
        }

//        player.sendMessage("Damage taken: +" + damage + "% → Total: " +
//                displayUpdater.damageManager.getFormattedDamage(playerId));

        event.setDamage(0);
    }

    @EventHandler
    public void onTntDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getDamager() instanceof TNTPrimed tnt)) return;
        if (!tnt.hasMetadata("smashcraft_tnt")) return;

        event.setCancelled(true);

        UUID shooter = UUID.fromString(tnt.getMetadata("smashcraft_tnt").get(0).asString());

        double customDamage = 22;
        damageManager.addDamage(player.getUniqueId(), customDamage);
        displayUpdater.update(player);

        double percent = damageManager.getDamage(player.getUniqueId());
        // Base knockback direction away from TNT
        Vector knockback = player.getLocation().toVector()
                .subtract(tnt.getLocation().toVector())
                .setY(0)
                .normalize();

        // Add tiny random horizontal vector
        double randomAngle = Math.random() * 2 * Math.PI;
        double randomStrength = 3; // adjust for how “tiny” you want the nudge
        Vector randomOffset = new Vector(
                Math.cos(randomAngle) * randomStrength,
                0,
                Math.sin(randomAngle) * randomStrength
        );

        knockback.add(randomOffset);
        //knockback.normalize().multiply(0.5 + percent / 70.0);
        knockback.setY(0.5 + percent / 120.0);

        player.setVelocity(knockback);
        //player.sendMessage("Damage taken: +" + customDamage + "% → Total: " + displayUpdater.damageManager.getFormattedDamage(player.getUniqueId()));
    }

    public class TntExplosionListener implements Listener {

        @EventHandler
        public void onTntExplode(EntityExplodeEvent event) {
            if (!(event.getEntity() instanceof TNTPrimed tnt)) return;
            if (!tnt.hasMetadata("smashcraft_tnt")) return;

            // Prevent all block damage
            event.blockList().clear();
        }
    }
}


