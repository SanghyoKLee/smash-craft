package org.kobokorp.smashcraft;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class DisplayUpdater {

    final DamageManager damageManager;
    private final Scoreboard scoreboard;
    private final Objective belowNameObjective;

    public DisplayUpdater(DamageManager damageManager) {
        this.damageManager = damageManager;

        // Set up scoreboard objective
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();

        // Register the below name objective
        belowNameObjective = scoreboard.registerNewObjective("damage", Criteria.DUMMY, "%");
        belowNameObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
    }

    public void update(Player player) {
        double percent = damageManager.getDamage(player.getUniqueId());
        int percentInt = (int) Math.round(percent);

        // Show in name tag
        Score score = belowNameObjective.getScore(player.getName());
        score.setScore(percentInt);

        // Show as XP level
        player.setLevel(percentInt);

        // Assign scoreboard
        player.setScoreboard(scoreboard);
    }

    public void reset(Player player) {
        player.setLevel(0);
        belowNameObjective.getScoreboard().resetScores(player.getName());
    }
}
