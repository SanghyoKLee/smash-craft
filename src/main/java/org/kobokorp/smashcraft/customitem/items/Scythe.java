package org.kobokorp.smashcraft.customitem.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.kobokorp.smashcraft.customitem.CooldownManager;
import org.kobokorp.smashcraft.customitem.CustomItem;
import org.kobokorp.smashcraft.customitem.CustomItemType;
import org.kobokorp.smashcraft.customitem.ItemBuilder;

import java.util.List;
import java.util.Set;

public class Scythe implements CustomItem {
    private final CooldownManager cooldownManager;
    private final String name = "Scythe";
    private final Material material = Material.NETHERITE_HOE;
    private final Set<CustomItemType> allowedTypes = Set.of(CustomItemType.PRIMARY);
    private final long cooldownSeconds = 12;

    public Scythe(CooldownManager cooldownManager) {
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
                        ChatColor.WHITE + "Charged damage: " + ChatColor.RED + "5%",
                        ChatColor.WHITE + "Uncharged damage: " + ChatColor.RED + "2%",
                        ChatColor.WHITE + " ",
                        ChatColor.WHITE + "Perform a backwards high jump.",
                        ChatColor.WHITE + "Cooldown: " + cooldownSeconds + "s"
                )
        );
    }

    @Override
    public boolean onRightClick(Player player) {
        // Calculate the backward + upward vector
        Vector backward = player.getLocation().getDirection().normalize().multiply(-1.2);
        backward.setY(1.1);

        player.setVelocity(backward);

        // Visuals
        player.getWorld().spawnParticle(
                Particle.SCRAPE,
                player.getLocation().add(0, 1, 0),
                15,
                0.5, 0.5, 0.5,
                0.01
        );
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_HORSE_JUMP, 1.0f, 1.0f);
        return true;
    }

    @Override
    public void onHeld(Player player) {
        // no-op
    }

    @Override
    public Set<CustomItemType> getAllowedTypes() {
        return allowedTypes;
    }

    @Override
    public long getCooldownSeconds() {
        return cooldownSeconds;
    }
}
