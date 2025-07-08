package org.kobokorp.smashcraft.customitem.items;

import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.kobokorp.smashcraft.customitem.CooldownManager;
import org.kobokorp.smashcraft.customitem.CustomItem;
import org.kobokorp.smashcraft.customitem.CustomItemType;
import org.kobokorp.smashcraft.customitem.ItemBuilder;

import java.util.List;
import java.util.Set;

public class ArrowShot implements CustomItem {

    private final CooldownManager cooldownManager;
    private final String name = "Arrow Shot";
    private final Material material = Material.ARROW;
    private final Set<CustomItemType> customItemTypes = Set.of(CustomItemType.SECONDARY);
    private final long cooldownSeconds = 18;

    public ArrowShot(CooldownManager cooldownManager) {
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
                        ChatColor.WHITE + "Fire an arrow that deals: " + ChatColor.RED + "15% " + ChatColor.WHITE + "damage.",
                        ChatColor.WHITE + "Cooldown: " + cooldownSeconds + "s"
                )
        );
    }

    @Override
    public boolean onRightClick(Player player) {
        Location eyeLoc = player.getEyeLocation();
        Vector direction = eyeLoc.getDirection().normalize();

        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setVelocity(direction.multiply(1.2));
        arrow.setShooter(player);
        arrow.setCritical(true);
        arrow.setCustomName(name);
        arrow.setCustomNameVisible(false);
        arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);

        // Particles and sounds
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.1f);
        player.getWorld().spawnParticle(Particle.CRIT, eyeLoc, 10, 0.2, 0.2, 0.2, 0.01);

        return true;
    }

    @Override
    public void onHeld(Player player) {
        // No passive effects
    }

    @Override
    public Set<CustomItemType> getAllowedTypes() {
        return customItemTypes;
    }

    @Override
    public long getCooldownSeconds() {
        return cooldownSeconds;
    }
}
