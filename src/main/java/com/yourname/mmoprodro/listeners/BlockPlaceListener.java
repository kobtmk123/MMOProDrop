package com.yourname.mmoblockguard.listeners;

import com.yourname.mmoblockguard.MMOBlockGuard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    private final MMOBlockGuard plugin;

    public BlockPlaceListener(MMOBlockGuard plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // Thêm vị trí của block vừa đặt vào danh sách
        plugin.getPlayerPlacedBlocks().add(event.getBlock().getLocation());
    }
}