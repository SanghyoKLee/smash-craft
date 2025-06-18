package org.kobokorp.smashcraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.kobokorp.smashcraft.customitem.CustomItemManager;
import org.kobokorp.smashcraft.customitem.CustomItemSelectorGUI;
import org.kobokorp.smashcraft.customitem.PlayerItemLoadoutManager;

public class ChooseItemsCommand implements CommandExecutor {

    private final CustomItemManager itemManager;
    private final PlayerItemLoadoutManager loadoutManager;
    private final Plugin plugin;

    public ChooseItemsCommand(CustomItemManager itemManager, PlayerItemLoadoutManager loadoutManager, Plugin plugin) {
        this.itemManager = itemManager;
        this.loadoutManager = loadoutManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        CustomItemSelectorGUI gui = new CustomItemSelectorGUI(itemManager, loadoutManager, plugin);
        gui.openMainMenu(player);

        return true;
    }
}
