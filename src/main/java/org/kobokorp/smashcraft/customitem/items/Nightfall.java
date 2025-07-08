package org.kobokorp.smashcraft.customitem.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.kobokorp.smashcraft.Smashcraft;
import org.kobokorp.smashcraft.TripleJumpListener;
import org.kobokorp.smashcraft.customitem.CooldownManager;
import org.kobokorp.smashcraft.customitem.CustomItem;
import org.kobokorp.smashcraft.customitem.CustomItemType;
import org.kobokorp.smashcraft.customitem.ItemBuilder;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Nightfall implements CustomItem {
    private final CooldownManager cooldownManager;
    private final String name = "Nightfall";
    private final Material material = Material.MACE;
    private final Set<CustomItemType> customItemTypes = Set.of(CustomItemType.PRIMARY);
    private final long cooldown = 15;
    private final TripleJumpListener tripleJumpListener;

    public Nightfall(CooldownManager manager, TripleJumpListener tripleJumpListener) {
        this.cooldownManager = manager;
        this.tripleJumpListener = tripleJumpListener;
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
                        ChatColor.WHITE + "Charged damage: " + ChatColor.RED + "15%",
                        ChatColor.WHITE + "Uncharged damage: " + ChatColor.RED + "2%",
                        ChatColor.WHITE + " ",
                        ChatColor.WHITE + "Slam downward from the air.",
                        ChatColor.WHITE + "Deals " + ChatColor.RED + "30%" + ChatColor.WHITE + " damage in a 6-block radius.",
                        ChatColor.GRAY + "Only usable while mid-air.",
                        ChatColor.WHITE + "Cooldown: " + cooldown + "s"
                )
        );
    }

    @Override
    public boolean onRightClick(Player player) {
        if (player.isOnGround()) {
            player.sendMessage(ChatColor.RED + "Nightfall can only be used while in the air!");
            return false;
        }

        if (tripleJumpListener.getUsedJumps(player) < 1) {
            player.sendMessage(ChatColor.RED + "You must use a double jump before activating Nightfall!");
            return false;
        }

        UUID id = player.getUniqueId();

        // Mark player as using Nightfall
        player.setMetadata("using_nightfall", new FixedMetadataValue(Smashcraft.getInstance(), true));

        // Launch the player downward
        Vector slam = new Vector(0, -1.5, 0);
        player.setVelocity(slam);

        // Particle effect
        player.getWorld().spawnParticle(Particle.WITCH, player.getLocation(), 30, 0.6, 0.6, 0.6, 0.02);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1.5f);
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
