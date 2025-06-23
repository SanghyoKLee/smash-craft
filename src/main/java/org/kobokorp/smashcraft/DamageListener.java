package org.kobokorp.smashcraft;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.kobokorp.smashcraft.shield.ShieldManager;

import java.util.UUID;

public class DamageListener implements Listener {
    private final DisplayUpdater displayUpdater;
    private final DamageManager damageManager;
    private final JavaPlugin plugin;
    private final ShieldManager shieldManager;

    public DamageListener(DamageManager manager, DisplayUpdater updater, JavaPlugin plugin, ShieldManager shieldManager) {
        this.damageManager = manager;
        this.displayUpdater = updater;
        this.plugin = plugin;
        this.shieldManager = shieldManager;
    }

    @EventHandler
    public void onAnyDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setDamage(0); // No heart damage, allow knockback
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        UUID victimId = victim.getUniqueId();

        if (shieldManager.isShielding(victimId)) {
            event.setDamage(0);
            victim.sendMessage(ChatColor.BLUE + "Shielded the attack!");
            return;
        }

        double damagePercent = 0;
        boolean fullyCharged = false;
        Location sourceLoc = null;

        if (event.getDamager() instanceof Player attacker) {
            sourceLoc = attacker.getLocation();
            ItemStack item = attacker.getInventory().getItemInMainHand();
            Material material = item.getType();

            double cooldown = attacker.getAttackCooldown();
            fullyCharged = cooldown >= 0.9;

            if (!fullyCharged) {
                damagePercent = 2;
            } else {
                damagePercent = switch (material) {
                    case WOODEN_SWORD -> 4;
                    case WOODEN_AXE -> 6;
                    case STONE_SWORD -> 6;
                    case STONE_AXE -> 9;
                    case IRON_SWORD -> 8;
                    case IRON_AXE -> 12;
                    case DIAMOND_SWORD -> 10;
                    case DIAMOND_AXE -> 15;
                    case NETHERITE_SWORD -> 12;
                    case NETHERITE_AXE -> 18;
                    case MACE -> 15;
                    default -> 2;
                };
            }

            if (fullyCharged) {
                spawnHitEffect(victim.getLocation());
            }

            applySmashKnockback(sourceLoc, attacker, victim, damagePercent);
            event.setDamage(0);

        } else if (event.getDamager() instanceof Arrow arrow) {
            if (arrow.getShooter() instanceof Player shooter) {
                sourceLoc = shooter.getLocation();
            } else {
                sourceLoc = arrow.getLocation().clone().subtract(arrow.getVelocity().normalize().multiply(1.5));
            }

            damagePercent = 15;
            applySmashKnockback(sourceLoc, null, victim, damagePercent);
            event.setDamage(0);
        }
    }

    private void spawnHitEffect(Location loc) {
        loc.getWorld().spawnParticle(Particle.CRIT, loc.clone().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.1);
        loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_ATTACK_STRONG, 1f, 1.2f);
    }

    public void applySmashKnockback(Location attackerLocation, Player attacker, Player victim, double damage) {
        applyKnockbackLogic(attackerLocation, attacker, victim, damage);
    }

    public static void applyTestKnockback(Location attackerLocation, Player victim, double damage,
                                          DamageManager damageManager, DisplayUpdater displayUpdater, JavaPlugin plugin) {
        applyKnockbackLogic(attackerLocation, null, victim, damage, damageManager, displayUpdater, plugin);
    }

    private static void applyKnockbackLogic(Location attackerLocation, Player attacker, Player victim, double damage,
                                            DamageManager damageManager, DisplayUpdater displayUpdater, JavaPlugin plugin) {
        UUID victimId = victim.getUniqueId();
        damageManager.addDamage(victimId, damage);
        displayUpdater.update(victim);

        double victimPercent = damageManager.getDamage(victimId);
        double horizontalForce = 0.3 + (victimPercent / 50.0);
        double verticalForce = 0.5 + (victimPercent / 130.0);
        int ticksToApply = 5 + (int) (victimPercent / 30.0);

        Vector knockback = victim.getLocation().toVector()
                .subtract(attackerLocation.toVector())
                .setY(0)
                .normalize()
                .multiply(horizontalForce);
        knockback.setY(verticalForce);

        for (int i = 0; i <= ticksToApply; i++) {
            final int tickDelay = i;
            Vector tickVelocity = knockback.clone().multiply(Math.pow(0.9, i));
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!Double.isFinite(tickVelocity.getX()) || !Double.isFinite(tickVelocity.getZ())) return;
                victim.setVelocity(tickVelocity);
                victim.setFallDistance(0);
            }, tickDelay);
        }

        if (attacker != null) {
            attacker.sendMessage("Dealt " + damage + "% → " + damageManager.getFormattedDamage(victimId));
        }
        victim.sendMessage("Current Damage: " + damageManager.getFormattedDamage(victimId));
    }

    private void applyKnockbackLogic(Location attackerLocation, Player attacker, Player victim, double damage) {
        applyKnockbackLogic(attackerLocation, attacker, victim, damage, damageManager, displayUpdater, plugin);
    }

    @EventHandler
    public void onSplinterHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Arrow arrow)) return;
        if (!arrow.hasMetadata("splinter_shot")) return;

        victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 0)); // Poison I for 3 seconds
        arrow.remove();
        victim.sendMessage(ChatColor.DARK_GREEN + "You were hit by a poisoned splinter!");
    }

    @EventHandler
    public void onRockHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Snowball rock)) return;
        if (!rock.hasMetadata("ancient_stone_sword_projectile")) return;

        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 2)); // Slowness III for 4 seconds
        rock.remove();
        victim.sendMessage(ChatColor.DARK_GRAY + "You've been hit by a Stone Slam!");
    }

    @EventHandler
    public void onNightfallLand(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Not using Nightfall — ignore
        if (!player.hasMetadata("using_nightfall")) return;

        // Already on the ground or inside a vehicle — ignore
        if (player.isOnGround() || player.isInsideVehicle()) {
            // Check if they were just in the air
            if (!(player.getFallDistance() > 0)) return;

            // Remove the metadata to prevent retriggering
            player.removeMetadata("using_nightfall", plugin);

            // Trigger slam effect
            Location loc = player.getLocation();
            double radius = 6.0;

            for (Entity entity : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
                if (!(entity instanceof LivingEntity target)) continue;
                if (target.equals(player)) continue;

                if (target instanceof Player targetPlayer) {
                    if (shieldManager.isShielding(targetPlayer.getUniqueId())) {
                        targetPlayer.sendMessage(ChatColor.BLUE + "Blocked Nightfall with shield!");
                        continue;
                    }

                    this.applySmashKnockback(loc, player, targetPlayer, 35.0);
                } else {
                    target.damage(7.0, player);

                    Vector knockback = target.getLocation().toVector()
                            .subtract(player.getLocation().toVector())
                            .setY(0)
                            .normalize()
                            .multiply(1.1);
                    knockback.setY(0.6);

                    target.setVelocity(knockback.add(new Vector(0, 0.2, 0)));

                    target.getWorld().playSound(target.getLocation(), Sound.ENTITY_GENERIC_HURT, 1f, 1f);
                    target.getWorld().spawnParticle(
                            Particle.DAMAGE_INDICATOR,
                            target.getLocation().add(0, 1, 0),
                            10, 0.3, 0.3, 0.3, 0.1
                    );
                }
            }

            Location center = player.getLocation();
            World world = center.getWorld();

            // Fill circle by iterating over x and z offsets
            for (double x = -radius; x <= radius; x += 0.5) {
                for (double z = -radius; z <= radius; z += 0.5) {
                    if (x * x + z * z <= radius * radius) {
                        Location particleLoc = center.clone().add(x, 0.1, z);
                        world.spawnParticle(
                                Particle.BLOCK,
                                particleLoc,
                                2, // number of particles per spot
                                0.2, 0.1, 0.2, // spread for variation
                                Material.DEEPSLATE.createBlockData()
                        );
                    }
                }
            }
        }
    }


}
