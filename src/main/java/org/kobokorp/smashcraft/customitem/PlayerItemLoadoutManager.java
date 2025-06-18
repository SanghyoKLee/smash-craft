package org.kobokorp.smashcraft.customitem;

import java.util.*;

public class PlayerItemLoadoutManager {
    private final Map<UUID, Map<CustomItemType, CustomItem>> loadouts = new HashMap<>();

    public void setItem(UUID playerId, CustomItemType slot, CustomItem item) {
        loadouts.computeIfAbsent(playerId, k -> new EnumMap<>(CustomItemType.class)).put(slot, item);
    }

    public CustomItem getItem(UUID playerId, CustomItemType slot) {
        return loadouts.getOrDefault(playerId, Map.of()).get(slot);
    }

    public Collection<CustomItem> getAll(UUID playerId) {
        return loadouts.getOrDefault(playerId, Map.of()).values();
    }
}
