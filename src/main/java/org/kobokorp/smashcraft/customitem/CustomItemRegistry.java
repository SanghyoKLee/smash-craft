package org.kobokorp.smashcraft.customitem;

import org.kobokorp.smashcraft.MovementTracker;
import org.kobokorp.smashcraft.TripleJumpListener;
import org.kobokorp.smashcraft.customitem.items.*;


public class CustomItemRegistry {
    public static void registerAll(CustomItemManager manager, CooldownManager cooldownManager, MovementTracker movementTracker, TripleJumpListener tripleJumpListener) {
        manager.register(new Flash(cooldownManager));
        manager.register(new Dash(cooldownManager, movementTracker));
        manager.register(new TnTLauncher(cooldownManager));
        manager.register(new SplinterShot(cooldownManager));
        manager.register(new AncientStoneSword(cooldownManager));
        manager.register(new Nightfall(cooldownManager, tripleJumpListener));
    }
}