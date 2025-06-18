package org.kobokorp.smashcraft.customitem.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.kobokorp.smashcraft.customitem.CustomItem;
import org.kobokorp.smashcraft.customitem.CustomItemType;

import java.util.Set;

public class BlastPearl implements CustomItem {
    @Override
    public String getName() {
        return "Blast Pearl";
    }

    @Override
    public ItemStack getDisplayItem() {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Blast Pearl");
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void onRightClick(Player player) {
        Vector away = player.getLocation().getDirection().multiply(-1).setY(0.6);
        player.setVelocity(away);
    }

    @Override
    public void onHeld(Player player) {
        // None
    }

    @Override
    public Set<CustomItemType> getAllowedTypes() {
        return Set.of(CustomItemType.SECONDARY);
    }
}
