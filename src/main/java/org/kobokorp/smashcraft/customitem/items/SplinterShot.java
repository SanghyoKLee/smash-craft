package org.kobokorp.smashcraft.customitem.items;

import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.kobokorp.smashcraft.Smashcraft;
import org.kobokorp.smashcraft.customitem.CooldownManager;
import org.kobokorp.smashcraft.customitem.CustomItem;
import org.kobokorp.smashcraft.customitem.CustomItemType;
import org.kobokorp.smashcraft.customitem.ItemBuilder;

import java.util.List;
import java.util.Set;

public class SplinterShot implements CustomItem {
    private final CooldownManager cooldownManager;
    private final String name = "Splinter Shot";
    private final Material material = Material.WOODEN_SWORD;
    private final Set<CustomItemType> customItemTypes = Set.of(CustomItemType.PRIMARY);
    private final long cooldown = 8;

    public SplinterShot(CooldownManager manager) {
        this.cooldownManager = manager;
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
                        ChatColor.WHITE + "Charged damage: " + ChatColor.RED + "4%",
                        ChatColor.WHITE + "Uncharged damage: " + ChatColor.RED + "2%",
                        ChatColor.WHITE + "Shoot a splinter that" + ChatColor.DARK_GREEN + " poisons the enemy for 2 seconds.",
                        ChatColor.WHITE + "Cooldown: " + cooldown + "s"
                )
        );
    }

    @Override
    public void onRightClick(Player player) {
        Location eyeLoc = player.getEyeLocation();
        Vector direction = eyeLoc.getDirection().normalize();

        Arrow splinter = player.launchProjectile(Arrow.class);
        splinter.setVelocity(direction.multiply(2.2));
        splinter.setCustomName("SplinterShot");
        splinter.setCustomNameVisible(false);
        splinter.setShooter(player);
        splinter.setCritical(false);
        splinter.setGravity(true);
        splinter.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
        splinter.setMetadata("splinter_shot", new org.bukkit.metadata.FixedMetadataValue(Smashcraft.getInstance(), true));

        // Effects
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1f, 1.2f);
        player.getWorld().spawnParticle(Particle.CRIT, eyeLoc, 15, 0.1, 0.1, 0.1, 0.01);
    }

    @Override
    public void onHeld(Player player) {
        // No effect
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
