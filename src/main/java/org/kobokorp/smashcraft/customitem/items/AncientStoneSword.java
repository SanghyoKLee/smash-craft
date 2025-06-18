package org.kobokorp.smashcraft.customitem.items;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.kobokorp.smashcraft.Smashcraft;
import org.kobokorp.smashcraft.customitem.CooldownManager;
import org.kobokorp.smashcraft.customitem.CustomItem;
import org.kobokorp.smashcraft.customitem.CustomItemType;
import org.kobokorp.smashcraft.customitem.ItemBuilder;

import java.util.List;
import java.util.Set;

public class AncientStoneSword implements CustomItem {
    private final CooldownManager cooldownManager;
    private final String name = "Ancient Stone Sword";
    private final Material material = Material.STONE_SWORD;
    private final Set<CustomItemType> allowedTypes = Set.of(CustomItemType.PRIMARY);
    private final long cooldownSeconds = 6;

    public AncientStoneSword(CooldownManager cooldownManager) {
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
                        ChatColor.WHITE + "Charged damage: " + ChatColor.RED + "6%",
                        ChatColor.WHITE + "Uncharged damage: " + ChatColor.RED + "2%",
                        ChatColor.WHITE + "Fires a heavy rock that inflicts " + ChatColor.BLUE + "Slowness III " + ChatColor.WHITE + "for 4 seconds",
                        ChatColor.WHITE + "Cooldown: " + cooldownSeconds + "s"
                )
        );
    }

    @Override
    public void onRightClick(Player player) {
        Location eyeLoc = player.getEyeLocation();
        Vector direction = eyeLoc.getDirection().normalize();

        Snowball rock = player.launchProjectile(Snowball.class);
        rock.setVelocity(direction.multiply(1.6));
        rock.setCustomName("AncientStoneSwordRock");
        rock.setCustomNameVisible(false);
        rock.setShooter(player);
        rock.setMetadata("ancient_stone_sword_projectile", new FixedMetadataValue(Smashcraft.getInstance(), true));

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 1f, 1.1f);
        player.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, eyeLoc, 20, 0.2, 0.2, 0.2, 0.01, Material.STONE.createBlockData());
    }

    @Override
    public void onHeld(Player player) {
        // No passive effect
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
