package org.kobokorp.smashcraft.customitem.items;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.kobokorp.smashcraft.customitem.CooldownManager;
import org.kobokorp.smashcraft.customitem.CustomItem;
import org.kobokorp.smashcraft.customitem.CustomItemType;
import org.kobokorp.smashcraft.customitem.ItemBuilder;

import java.util.List;
import java.util.Set;

public class Flash implements CustomItem {
    private final CooldownManager cooldownManager;
    String name;
    Material material;
    Set<CustomItemType> customItemTypes;
    long cooldown;

    public Flash(CooldownManager manager) {
        this.cooldownManager = manager;
        this.name = "Flash";
        this.material = Material.YELLOW_DYE;
        this.customItemTypes = Set.of(CustomItemType.SECONDARY);
        this.cooldown = 20;
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
                        ChatColor.WHITE + "Teleport forward 9 blocks.",
                        ChatColor.WHITE + "Cooldown: " + this.cooldown + "s"
                )
        );
    }

    @Override
    public boolean onRightClick(Player player) {
        // Create yellow dust particle
        Particle.DustOptions yellowDust = new Particle.DustOptions(Color.YELLOW, 1.5f);

        // Spawn particles before teleport
        player.getLocation().getWorld().spawnParticle(Particle.DUST, player.getLocation(), 30, 0.3, 0.5, 0.3, yellowDust);

        Vector direction = player.getLocation().getDirection().normalize().multiply(9);
        Location after = player.getLocation().add(direction);

        // Teleport
        player.teleport(after);
        after.getWorld().spawnParticle(Particle.DUST, after, 30, 0.3, 0.5, 0.3, yellowDust);

        // Sound
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        return true;
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