package org.kobokorp.smashcraft;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GameCommand implements CommandExecutor {
    private final GameManager gameManager;

    public GameCommand(GameManager manager) {
        this.gameManager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /start <mapname>");
            return true;
        }

        gameManager.startGame(args[0]);
        return true;
    }
}
