package org.kobokorp.smashcraft.customitem;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustomItemManager {
    private final Map<String, CustomItem> itemMap = new HashMap<>();

    public void register(CustomItem item) {
        itemMap.put(item.getName(), item);
    }

    public CustomItem getByItem(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) return null;
        return itemMap.get(stack.getItemMeta().getDisplayName());
    }

    public Collection<CustomItem> getAll() {
        return itemMap.values(); // 'items' is Map<String, CustomItem>
    }
}
