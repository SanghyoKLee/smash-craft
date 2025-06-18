package org.kobokorp.smashcraft;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.kobokorp.smashcraft.customitem.*;
import org.kobokorp.smashcraft.shield.ShieldManager;

import java.util.List;

public final class Smashcraft extends JavaPlugin {

    private DamageManager damageManager;
    private DisplayUpdater displayUpdater;
    private DamageListener damageListener; // ✅ Add this
    private ShieldManager shieldManager;
    private CustomItemManager customItemManager;
    private PlayerItemLoadoutManager playerItemLoadoutManager;
    private CooldownManager cooldownManager;
    private GameManager gameManager;

    private org.kobokorp.smashcraft.MovementTracker movementTracker;

    private static Smashcraft instance;

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


        getServer().getPluginManager().registerEvents(new TripleJumpListener(this), this);
        getServer().getPluginManager().registerEvents(new HungerListener(), this);
        getServer().getPluginManager().registerEvents(damageListener, this); // ✅ Reuse your created instance
        getServer().getPluginManager().registerEvents(new GeneralDamageListener(damageManager, displayUpdater), this);
        Bukkit.getPluginManager().registerEvents(new ItemRightClickListener(customItemManager, cooldownManager), this);
        getServer().getPluginManager().registerEvents(new CustomItemSelectorGUI(customItemManager, playerItemLoadoutManager, this), this);
        getServer().getPluginManager().registerEvents(new TntExplosionListener(), this);

        getCommand("smashtest").setExecutor(new SmashTestCommand(damageManager, displayUpdater, this));
        getCommand("smashsetdamage").setExecutor(new SmashSetDamageCommand(damageManager, displayUpdater));

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            shieldManager.tick();
        }, 1L, 1L);

        // Register Custom Items
        CustomItemRegistry.registerAll(customItemManager, cooldownManager, movementTracker);

        getCommand("chooseitems").setExecutor(new ChooseItemsCommand(customItemManager, playerItemLoadoutManager, this));
        getCommand("start").setExecutor(new GameCommand(gameManager));

        World world = Bukkit.getWorld("world");
        getServer().getPluginManager().registerEvents(new GameListener(gameManager), this);
        MapManager.registerMap("world", List.of(
                new Location(world, -201, 96, -23)

        ));
    }


    @Override
    public void onDisable() {
        getLogger().info("Smashcraft disabled");
    }
}
