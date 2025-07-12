package org.kobokorp.smashcraft.customitem.items;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.kobokorp.smashcraft.Smashcraft;
import org.kobokorp.smashcraft.customitem.CooldownManager;
import org.kobokorp.smashcraft.customitem.CustomItem;
import org.kobokorp.smashcraft.customitem.CustomItemType;
import org.kobokorp.smashcraft.customitem.ItemBuilder;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BeastScroll implements CustomItem {
    public static final NamespacedKey OWNER_KEY =
            new NamespacedKey(Smashcraft.getInstance(), "beast_scroll_owner");
    private final CooldownManager cooldownManager;
    private final String name = "Beast Scroll";
    private final Material material = Material.CREEPER_BANNER_PATTERN;
    private final Set<CustomItemType> customItemTypes = Set.of(CustomItemType.PRIMARY);
    private final long baseCooldownSeconds = 25;

    public BeastScroll(CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemBuilder.named(
                material,
                name,
                List.of(
                        ChatColor.WHITE + "Summon creatures depending on your state:",
                        ChatColor.GRAY + "- On ground: " + ChatColor.RED + "Baby Zombie",
                        ChatColor.GRAY + "- In air: " + ChatColor.RED + "Breeze",
                        ChatColor.GRAY + "- While sneaking: " + ChatColor.GREEN + "Turtle that heals 1%/s when you're nearby",
                        "",
                        ChatColor.WHITE + "Charged hits reduce cooldown by " + ChatColor.YELLOW + "1s",
                        ChatColor.WHITE + "Cooldown: " + ChatColor.YELLOW + baseCooldownSeconds + "s"
                )
        );
    }

    @Override
    public boolean onRightClick(Player player) {
        UUID id = player.getUniqueId();

        if (cooldownManager.isOnCooldown(getName(), id)) {
            long remaining = cooldownManager.getRemaining(getName(), id);
            player.sendMessage(ChatColor.RED + "Beast Scroll is on cooldown for " + (remaining / 1000) + "s.");
            return false;
        }

        // Trigger ability
        if (player.isSneaking()) {
            spawnHealingTurtle(player);
        } else if (player.isOnGround()) {
            spawnBabyZombie(player);
        } else {
            spawnBreeze(player);
        }

        // Reset cooldown to base duration
        cooldownManager.setCooldown(getName(), id, getCooldownSeconds() * 1000);
        return true;
    }

    private void spawnHealingTurtle(Player player) {
        UUID id = player.getUniqueId();

        Turtle turtle = (Turtle) player.getWorld().spawnEntity(
                player.getLocation(),
                EntityType.TURTLE
        );
        turtle.setBaby();
        turtle.setInvulnerable(true);
        turtle.setAI(false);
        turtle.setCustomName(ChatColor.GREEN + player.getName() + "'s Turtle");
        turtle.setCustomNameVisible(true);

        player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, turtle.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);
        player.getWorld().playSound(turtle.getLocation(), Sound.ENTITY_TURTLE_EGG_CRACK, 1f, 1.2f);

        new BukkitRunnable() {
            int seconds = 0;

            @Override
            public void run() {
                if (!turtle.isValid() || turtle.isDead() || seconds >= 25) {
                    turtle.remove();
                    cancel();
                    return;
                }

                if (player.getLocation().distance(turtle.getLocation()) <= 3.0) {
                    Smashcraft.getInstance().getDamageManager().addDamage(id, -1.0);
                    Smashcraft.getInstance().getDisplayUpdater().update(player);

                    double radius = 3.0;
                    Location center = turtle.getLocation();
                    World world = center.getWorld();
                    for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 8) {
                        double x = radius * Math.cos(angle);
                        double z = radius * Math.sin(angle);
                        Location particleLoc = center.clone().add(x, 0.3, z);
                        world.spawnParticle(Particle.HEART, particleLoc, 1, 0.2, 0.2, 0.2, 0);
                    }
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 0.5f, 2f);
                }

                seconds++;
            }
        }.runTaskTimer(Smashcraft.getInstance(), 0L, 20L);
    }

    private void spawnBabyZombie(Player player) {
        Zombie babyZombie = (Zombie) player.getWorld().spawnEntity(
                player.getLocation().add(1, 1, 1),
                EntityType.ZOMBIE
        );
        babyZombie.setBaby();
        babyZombie.setCustomName(ChatColor.RED + player.getName() + "'s Baby Zombie");
        babyZombie.setCustomNameVisible(true);

        // Save owner
        babyZombie.getPersistentDataContainer().set(
                OWNER_KEY,
                PersistentDataType.STRING,
                player.getUniqueId().toString()
        );

        Player target = findNearestPlayer(player);
        if (target != null) {
            babyZombie.setTarget(target);
        }

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 1f, 1.0f);
        player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation(), 20, 0.5, 0.5, 0.5, 0.1);
    }

    private void spawnBreeze(Player player) {
        Entity breezeEntity = player.getWorld().spawnEntity(
                player.getLocation().add(1, 2, 1),
                EntityType.BREEZE
        );

        if (breezeEntity instanceof Breeze breeze) {
            breezeEntity.setCustomName(ChatColor.BLUE + player.getName() + "'s Breeze");
            breeze.setCustomNameVisible(true);

            breeze.getPersistentDataContainer().set(
                    OWNER_KEY,
                    PersistentDataType.STRING,
                    player.getUniqueId().toString()
            );

            Player target = findNearestPlayer(player);
            if (target != null) {
                breeze.setTarget(target);
            }
        }

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BREEZE_IDLE_AIR, 1f, 1.0f);
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 30, 0.5, 0.5, 0.5, 0.1);
    }

    private Player findNearestPlayer(Player excluding) {
        double minDistance = Double.MAX_VALUE;
        Player nearest = null;

        for (Player p : excluding.getWorld().getPlayers()) {
            if (p.equals(excluding)) continue;
            double dist = p.getLocation().distance(excluding.getLocation());
            if (dist < minDistance) {
                minDistance = dist;
                nearest = p;
            }
        }
        return nearest;
    }

    @Override
    public void onHeld(Player player) {
        // No passive effect
    }

    @Override
    public Set<CustomItemType> getAllowedTypes() {
        return customItemTypes;
    }

    @Override
    public long getCooldownSeconds() {
        return baseCooldownSeconds;
    }

    /**
     * Called externally to reduce the cooldown for this player's item.
     */
    public void reduceCooldown(Player player, int seconds) {
        UUID id = player.getUniqueId();
        long remaining = cooldownManager.getRemaining(getName(), id);

        if (remaining > 0) {
            long newRemaining = Math.max(0, remaining - (seconds * 1000));
            cooldownManager.setCooldown(getName(), id, newRemaining);
            player.sendMessage(ChatColor.GREEN + "Beast Scroll cooldown reduced by " + seconds + "s!");
        }
    }
}
