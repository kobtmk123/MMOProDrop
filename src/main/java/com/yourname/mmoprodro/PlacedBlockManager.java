package com.yourname.mmoprodro;

import org.bukkit.Location;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PlacedBlockManager {
    private final Set<Location> playerPlacedBlocks = Collections.synchronizedSet(new HashSet<>());

    public void addBlock(Location location) {
        playerPlacedBlocks.add(location.toBlockLocation());
    }

    public void removeBlock(Location location) {
        playerPlacedBlocks.remove(location.toBlockLocation());
    }

    public boolean isPlayerPlaced(Location location) {
        return playerPlacedBlocks.contains(location.toBlockLocation());
    }
}