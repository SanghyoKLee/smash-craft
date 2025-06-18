package org.kobokorp.smashcraft.customitem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class CustomItemSelectorGUI implements Listener {
    private final CustomItemManager itemManager;
    private final PlayerItemLoadoutManager loadoutManager;
    private final Plugin plugin;

    public CustomItemSelectorGUI(CustomItemManager itemManager, PlayerItemLoadoutManager loadoutManager, Plugin plugin) {
        this.itemManager = itemManager;
        this.loadoutManager = loadoutManager;
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, "Choose Loadout Slot");
        inv.setItem(1, createSlotItem(Material.DIAMOND_SWORD, "Choose Primary"));
        inv.setItem(3, createSlotItem(Material.IRON_SWORD, "Choose Secondary"));
        inv.setItem(5, createSlotItem(Material.GOLDEN_SWORD, "Choose Tertiary"));
        inv.setItem(7, createSlotItem(Material.BARRIER, "Exit"));
        player.openInventory(inv);
    }

    private ItemStack createSlotItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + name);
        item.setItemMeta(meta);
        return item;
    }

    private void openItemSelection(Player player, CustomItemType type) {
        Inventory inv = Bukkit.createInventory(null, 27, "Choose " + type.name());
        for (CustomItem item : itemManager.getAll()) {
            if (item.getAllowedTypes().contains(type)) {
                inv.addItem(item.getDisplayItem());
            }
        }
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;

        String title = ChatColor.stripColor(event.getView().getTitle());
        String itemName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
        event.setCancelled(true); // Prevent item from being taken

        // Only respond to clicks in the top inventory (GUI), not player inventory
        if (!event.getClickedInventory().equals(event.getView().getTopInventory())) return;

        // MAIN MENU
        if (title.equalsIgnoreCase("Choose Loadout Slot")) {
            if (itemName.equalsIgnoreCase("Choose Primary")) {
                Bukkit.getScheduler().runTask(plugin, () -> openItemSelection(player, CustomItemType.PRIMARY));
            } else if (itemName.equalsIgnoreCase("Choose Secondary")) {
                Bukkit.getScheduler().runTask(plugin, () -> openItemSelection(player, CustomItemType.SECONDARY));
            } else if (itemName.equalsIgnoreCase("Choose Tertiary")) {
                Bukkit.getScheduler().runTask(plugin, () -> openItemSelection(player, CustomItemType.TERTIARY));
            } else if (itemName.equalsIgnoreCase("Exit")) {
                player.closeInventory();
            }
            return;
        }

        // SELECTION SCREENS
        for (CustomItemType type : CustomItemType.values()) {
            if (title.equalsIgnoreCase("Choose " + type.name())) {
                CustomItem chosen = itemManager.getByItem(event.getCurrentItem());
                if (chosen == null) return;

                loadoutManager.setItem(player.getUniqueId(), type, chosen);
                player.sendMessage(ChatColor.YELLOW + "Set " + chosen.getName() + " as your " + type.name());

                // Clear all inventory except hotbar slots 0–2
                for (int i = 3; i < player.getInventory().getSize(); i++) {
                    player.getInventory().clear(i);
                }

                // Set selected items in slots
                for (CustomItemType slot : CustomItemType.values()) {
                    CustomItem item = loadoutManager.getItem(player.getUniqueId(), slot);
                    if (item != null) {
                        switch (slot) {
                            case PRIMARY -> player.getInventory().setItem(0, item.getDisplayItem());
                            case SECONDARY -> player.getInventory().setItem(1, item.getDisplayItem());
                            case TERTIARY -> player.getInventory().setItem(2, item.getDisplayItem());
                        }
                    }
                }

                // Return to main menu one tick later to avoid GUI conflict
                Bukkit.getScheduler().runTask(plugin, () -> openMainMenu(player));
                return;
            }
        }

    }
}
