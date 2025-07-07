package org.kobokorp.smashcraft.customitem.items;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
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

public class TnTLauncher implements CustomItem {
    private final CooldownManager cooldownManager;
    private final String name = "TnT Launcher";
    private final Material material = Material.TNT;
    private final Set<CustomItemType> customItemTypes = Set.of(CustomItemType.PRIMARY);
    private final long cooldown = 9;

    public TnTLauncher(CooldownManager manager) {
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
                        ChatColor.WHITE + "Melee damage: " + ChatColor.RED + "2%",
                        ChatColor.WHITE + " ",
                        ChatColor.WHITE + "Launch a primed TNT forward",
                        ChatColor.WHITE + "Explosion damage: " + ChatColor.RED + "25%",
                        ChatColor.WHITE + "Cooldown: " + cooldown + "s"
                )
        );
    }

    @Override
    public boolean onRightClick(Player player) {
        Location spawnLoc = player.getEyeLocation().add(player.getLocation().getDirection().normalize().multiply(1.4));
        Vector velocity = player.getLocation().getDirection().normalize().multiply(1.05);

        TNTPrimed tnt = player.getWorld().spawn(spawnLoc, TNTPrimed.class);
        tnt.setMetadata("smashcraft_tnt", new FixedMetadataValue(Smashcraft.getInstance(), player.getUniqueId().toString()));
        tnt.setVelocity(velocity);
        tnt.setFuseTicks(30);
        tnt.setSource(player);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_TNT_PRIMED, 1f, 1f);
        player.getWorld().spawnParticle(Particle.SMOKE, spawnLoc, 20, 0.3, 0.3, 0.3, 0.01);

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
