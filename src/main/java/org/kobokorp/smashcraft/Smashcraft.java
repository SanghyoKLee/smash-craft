package org.kobokorp.smashcraft;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.kobokorp.smashcraft.commands.GameCommand;
import org.kobokorp.smashcraft.commands.LoadoutCommand;
import org.kobokorp.smashcraft.commands.SmashSetDamageCommand;
import org.kobokorp.smashcraft.commands.SmashTestCommand;
import org.kobokorp.smashcraft.customitem.*;

public final class Smashcraft extends JavaPlugin {

    // --- Singleton Instance ---
    private static Smashcraft instance;

    public static Smashcraft getInstance() {
        return instance;
    }

    // --- Core Systems ---
    private DamageManager damageManager;
    private DisplayUpdater displayUpdater;
    private ShieldManager shieldManager;
    private DamageListener damageListener;
    private MovementTracker movementTracker;
    private LeaderboardManager leaderboardManager;

    // --- Game Systems ---
    private GameManager gameManager;
    private TripleJumpListener tripleJumpListener;

    // --- Custom Items ---
    private CooldownManager cooldownManager;
    private CustomItemManager customItemManager;
    private PlayerItemLoadoutManager playerItemLoadoutManager;

    @Override
    public void onEnable() {
        getLogger().info("Smashcraft enabled");

        instance = this;

        // --- Initialize Core Systems ---
        damageManager = new DamageManager();
        displayUpdater = new DisplayUpdater(damageManager);
        shieldManager = new ShieldManager();
        damageListener = new DamageListener(damageManager, displayUpdater, this, shieldManager);
        movementTracker = new MovementTracker();
        leaderboardManager = new LeaderboardManager(this);

        // --- Initialize Game Systems ---
        gameManager = new GameManager(this, damageManager, displayUpdater);
        tripleJumpListener = new TripleJumpListener(this);

        // --- Initialize Custom Item Systems ---
        cooldownManager = new CooldownManager();
        customItemManager = new CustomItemManager();
        playerItemLoadoutManager = new PlayerItemLoadoutManager();

        // --- Register Event Listeners ---
        var pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(tripleJumpListener, this);
        pluginManager.registerEvents(new HungerListener(), this);
        pluginManager.registerEvents(damageListener, this);
        pluginManager.registerEvents(new GeneralDamageListener(damageManager, displayUpdater, shieldManager), this);
        pluginManager.registerEvents(new ItemRightClickListener(customItemManager, cooldownManager), this);
        pluginManager.registerEvents(new CustomItemSelectorGUI(customItemManager, playerItemLoadoutManager, this), this);
        pluginManager.registerEvents(new TntExplosionListener(), this);
        pluginManager.registerEvents(new GameListener(gameManager), this);
        pluginManager.registerEvents(new BeastScrollListener(), this);

        // --- Register Commands ---
        getCommand("smashtest").setExecutor(new SmashTestCommand(damageManager, displayUpdater, this));
        getCommand("smashsetdamage").setExecutor(new SmashSetDamageCommand(damageManager, displayUpdater));
        getCommand("loadout").setExecutor(new LoadoutCommand(customItemManager, playerItemLoadoutManager, this));
        getCommand("start").setExecutor(new GameCommand(gameManager));

        // --- Schedule Tasks ---
        Bukkit.getScheduler().runTaskTimer(this, shieldManager::tick, 1L, 1L);

        // --- Register Custom Items ---
        CustomItemRegistry.registerAll(customItemManager, cooldownManager, movementTracker, tripleJumpListener, damageManager, displayUpdater);

        // --- Load Configurations ---
        saveDefaultConfig();
        saveResource("config.yml", false);
        MapManager.loadMaps(this);
    }

    @Override
    public void onDisable() {
        leaderboardManager.saveData();
        getLogger().info("Smashcraft disabled");
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }

    public CustomItemManager getCustomItemManager() {
        return customItemManager;
    }

    public DamageManager getDamageManager() {
        return damageManager;
    }

    public DisplayUpdater getDisplayUpdater() {
        return displayUpdater;
    }
}
