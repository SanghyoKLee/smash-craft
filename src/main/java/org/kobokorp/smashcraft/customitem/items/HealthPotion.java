package org.kobokorp.smashcraft.customitem.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.kobokorp.smashcraft.DamageManager;
import org.kobokorp.smashcraft.DisplayUpdater;
import org.kobokorp.smashcraft.customitem.CooldownManager;
import org.kobokorp.smashcraft.customitem.CustomItem;
import org.kobokorp.smashcraft.customitem.CustomItemType;
import org.kobokorp.smashcraft.customitem.ItemBuilder;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class HealthPotion implements CustomItem {

    private final CooldownManager cooldownManager;
    private final DamageManager damageManager;
    private final String name = "Health Potion";
    private final Material material = Material.DRAGON_BREATH;
    private final Set<CustomItemType> customItemTypes = Set.of(CustomItemType.TERTIARY);
    private final long cooldown = 45;
    private final DisplayUpdater displayUpdater;

    public HealthPotion(CooldownManager cooldownManager, DamageManager damageManager, DisplayUpdater displayUpdater) {
        this.cooldownManager = cooldownManager;
        this.damageManager = damageManager;
        this.displayUpdater = displayUpdater;
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
                        ChatColor.WHITE + "Drink to " + ChatColor.GREEN + "heal " + ChatColor.RED + "15% " + ChatColor.WHITE + "damage.",
                        ChatColor.GRAY + "Cooldown: " + cooldown + "s"
                )
        );
    }

    @Override
    public boolean onRightClick(Player player) {
        UUID id = player.getUniqueId();

        double currentDamage = damageManager.getDamage(id);
        double healed = Math.min(15.0, currentDamage);
        double newDamage = Math.max(0, currentDamage - healed);

        damageManager.addDamage(id, -healed); // subtract damage
        player.sendMessage(ChatColor.GREEN + "Recovered " + healed + "% damage. Current damage: " + damageManager.getFormattedDamage(id));

        // Visuals
        player.getWorld().spawnParticle(
                Particle.HEART,
                player.getLocation().add(0, 1, 0),
                15, // count
                0.5, 0.5, 0.5,
                0.1
        );
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        displayUpdater.update(player);
        return true;
    }

    @Override
    public void onHeld(Player player) {
        // no-op
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
