package org.kobokorp.smashcraft.customitem.items;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.kobokorp.smashcraft.MovementTracker;
import org.kobokorp.smashcraft.customitem.CooldownManager;
import org.kobokorp.smashcraft.customitem.CustomItem;
import org.kobokorp.smashcraft.customitem.CustomItemType;
import org.kobokorp.smashcraft.customitem.ItemBuilder;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Dash implements CustomItem {
    private final CooldownManager cooldownManager;
    private final MovementTracker movementTracker;

    String name;
    Material material;
    Set<CustomItemType> customItemTypes;
    long cooldown;

    public Dash(CooldownManager manager, MovementTracker movementTracker) {
        this.cooldownManager = manager;
        this.movementTracker = movementTracker;
        this.name = "Dash";
        this.material = Material.FEATHER;
        this.customItemTypes = Set.of(CustomItemType.SECONDARY);
        this.cooldown = 6;
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
                        ChatColor.WHITE + "Dash in the direction of your momentum",
                        ChatColor.WHITE + "Cooldown: " + cooldown + "s"
                )
        );
    }

    //@Override
//    public void onRightClick(Player player) {
//        // Get movement direction or fallback to facing direction
//        Vector moveDir = player.getVelocity();
//        if (moveDir.lengthSquared() < 0.01) {
//            moveDir = player.getLocation().getDirection();
//        }
//        moveDir = moveDir.setY(0).normalize().multiply(1.5); // Dash distance and speed
//
//        System.out.println("[DASH] Player: " + player.getName());
//        System.out.println("[DASH] Velocity before: " + player.getVelocity());
//        System.out.println("[DASH] Dash vector: " + moveDir);
//        player.sendMessage(moveDir.toString());
//        // Apply motion
//        player.setVelocity(moveDir);
//
//        // Particle effect
//        Particle.DustOptions dust = new Particle.DustOptions(Color.WHITE, 1.2f);
//        player.getWorld().spawnParticle(Particle.DUST, player.getLocation(), 20, 0.2, 0.4, 0.2, dust);
//
//        // Sound
//        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1.1f);
//    }
    @Override
    public void onRightClick(Player player) {
        UUID id = player.getUniqueId();

        // Determine dash direction
        Vector moveDir = movementTracker.getLastDirection(player);
        if (moveDir.lengthSquared() < 0.01) {
            moveDir = player.getLocation().getDirection();
        }

        moveDir.setY(0).normalize();

        if (moveDir.lengthSquared() < 0.01) {
            player.sendMessage(ChatColor.RED + "Can't dash: not enough movement.");
            return;
        }

        // Weaken dash strength if airborne
        double strength = player.isOnGround() ? 1.5 : 0.85;
        moveDir.multiply(strength);

        System.out.println("[DASH] Dash direction: " + moveDir);
        player.sendMessage(ChatColor.GRAY + "Dashing: " + moveDir);

        // Apply velocity
        player.setVelocity(moveDir);

        // Particle effect
        Particle.DustOptions dust = new Particle.DustOptions(Color.WHITE, 1.2f);
        player.getWorld().spawnParticle(Particle.DUST, player.getLocation(), 80, 0.4, 0.4, 0.4, dust);

        // Sound effect
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1.1f);
    }

    @Override
    public void onHeld(Player player) {
    }

    @Override
    public Set<CustomItemType> getAllowedTypes() {
        return customItemTypes;
    }

    @Override
    public long getCooldownSeconds() {
        return cooldown;
    }
}
