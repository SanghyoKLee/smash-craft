package org.kobokorp.smashcraft;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.kobokorp.smashcraft.customitem.*;
import org.kobokorp.smashcraft.shield.ShieldManager;

public final class Smashcraft extends JavaPlugin {
    private static Smashcraft instance;
    private DamageManager damageManager;
    private DisplayUpdater displayUpdater;
    private DamageListener damageListener; // ✅ Add this
    private ShieldManager shieldManager;
    private CustomItemManager customItemManager;
    private PlayerItemLoadoutManager playerItemLoadoutManager;
    private CooldownManager cooldownManager;
    private GameManager gameManager;
    private MovementTracker movementTracker;
    private TripleJumpListener tripleJumpListener;

    public static Smashcraft getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        getLogger().info("Smashcraft enabled");
        instance = this;
        damageManager = new DamageManager();
        displayUpdater = new DisplayUpdater(damageManager);
        shieldManager = new ShieldManager(); // ✅ Moved up here
        damageListener = new DamageListener(damageManager, displayUpdater, this, shieldManager);
        customItemManager = new CustomItemManager();
        playerItemLoadoutManager = new PlayerItemLoadoutManager();
        cooldownManager = new CooldownManager();
        movementTracker = new org.kobokorp.smashcraft.MovementTracker();
        gameManager = new GameManager(this, damageManager, displayUpdater);
        tripleJumpListener = new TripleJumpListener(this);

        getServer().getPluginManager().registerEvents(tripleJumpListener, this);
        getServer().getPluginManager().registerEvents(new HungerListener(), this);
        getServer().getPluginManager().registerEvents(damageListener, this); // ✅ Reuse your created instance
        getServer().getPluginManager().registerEvents(new GeneralDamageListener(damageManager, displayUpdater, shieldManager), this);
        Bukkit.getPluginManager().registerEvents(new ItemRightClickListener(customItemManager, cooldownManager), this);
        getServer().getPluginManager().registerEvents(new CustomItemSelectorGUI(customItemManager, playerItemLoadoutManager, this), this);
        getServer().getPluginManager().registerEvents(new TntExplosionListener(), this);

        getCommand("smashtest").setExecutor(new SmashTestCommand(damageManager, displayUpdater, this));
        getCommand("smashsetdamage").setExecutor(new SmashSetDamageCommand(damageManager, displayUpdater));

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            shieldManager.tick();
        }, 1L, 1L);

        // Register Custom Items
        CustomItemRegistry.registerAll(customItemManager, cooldownManager, movementTracker, tripleJumpListener, damageManager);

        getCommand("chooseitems").setExecutor(new ChooseItemsCommand(customItemManager, playerItemLoadoutManager, this));
        getCommand("start").setExecutor(new GameCommand(gameManager));

        getServer().getPluginManager().registerEvents(new GameListener(gameManager), this);
        saveDefaultConfig(); // ensures plugin folder exists
        saveResource("config.yml", false); // ensure it's copied to plugin folder
        MapManager.loadMaps(this); // load map data
    }


    @Override
    public void onDisable() {
        getLogger().info("Smashcraft disabled");
    }
}
