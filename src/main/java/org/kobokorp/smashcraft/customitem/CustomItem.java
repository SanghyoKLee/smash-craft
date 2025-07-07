package org.kobokorp.smashcraft.customitem;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public interface CustomItem {
    String getName();

    ItemStack getDisplayItem();

    boolean onRightClick(Player player);
    // returns true if cooldown should start

    void onHeld(Player player);

    Set<CustomItemType> getAllowedTypes();

    default long getCooldownSeconds() {
        return 0;
    }
}
