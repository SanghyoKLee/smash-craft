package org.kobokorp.smashcraft.customitem;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemBuilder {
    public static ItemStack named(Material material, String plainName) {
        return named(material, plainName, null);
    }

    public static ItemStack named(Material material, String plainName, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plainName);  // No color formatting
            if (lore != null) {
                meta.setUnbreakable(true);
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }


}