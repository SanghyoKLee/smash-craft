package org.kobokorp.smashcraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kobokorp.smashcraft.DamageListener;
import org.kobokorp.smashcraft.DamageManager;
import org.kobokorp.smashcraft.DisplayUpdater;

public class SmashTestCommand implements CommandExecutor {

    private final DamageManager damageManager;
    private final DisplayUpdater displayUpdater;
    private final JavaPlugin plugin;

    public SmashTestCommand(DamageManager damageManager, DisplayUpdater displayUpdater, JavaPlugin plugin) {
        this.damageManager = damageManager;
        this.displayUpdater = displayUpdater;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /smashtest <damage>");
            return true;
        }

        try {
            double damage = Double.parseDouble(args[0]);
            Location fakeAttackerLoc = player.getLocation().clone()
                    .subtract(player.getLocation().getDirection().normalize().multiply(1.0));

            DamageListener.applyTestKnockback(fakeAttackerLoc, player, damage, damageManager, displayUpdater, plugin);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid number format.");
        }

        return true;
    }
}
