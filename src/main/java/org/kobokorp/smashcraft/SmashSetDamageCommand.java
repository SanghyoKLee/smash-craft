package org.kobokorp.smashcraft;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SmashSetDamageCommand implements CommandExecutor {

    private final DamageManager damageManager;
    private final DisplayUpdater displayUpdater;

    public SmashSetDamageCommand(DamageManager damageManager, DisplayUpdater displayUpdater) {
        this.damageManager = damageManager;
        this.displayUpdater = displayUpdater;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /smashdamage <percent>");
            return true;
        }

        try {
            double value = Double.parseDouble(args[0]);
            value = Math.max(0, Math.min(value, 999)); // clamp

            damageManager.resetDamage(player.getUniqueId());
            damageManager.addDamage(player.getUniqueId(), value);
            displayUpdater.update(player);

            player.sendMessage(ChatColor.AQUA + "Your damage has been set to " + value + "%");

        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid number.");
        }

        return true;
    }
}
