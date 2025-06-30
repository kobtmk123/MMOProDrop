package com.yourname.mmoprodro.listeners;

import com.yourname.mmoprodro.MMOProDrop;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    private final MMOProDrop plugin;

    public BlockBreakListener(MMOProDrop plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        Block block = event.getBlock();
        Location loc = block.getLocation();

        // 1. KIỂM TRA BLOCK DO NGƯỜI CHƠI ĐẶT (nếu tính năng được bật)
        if (plugin.preventFarmFromPlacedBlocks) {
            if (plugin.getPlacedBlockManager().isPlayerPlaced(loc)) {
                // Đây là block người chơi đặt. Xóa khỏi bộ nhớ và cho phép drop vanilla.
                plugin.getPlacedBlockManager().removeBlock(loc);
                return; // Kết thúc, không xử lý drop MMOItems.
            }
        }

        // 2. XỬ LÝ DROP MMOITEMS (và điều kiện tuổi bên trong)
        boolean didMMODrop = plugin.getDropHandler().tryDrop(block);

        // 3. XỬ LÝ VANILLA DROP (nếu tính năng được bật và có MMO drop)
        if (didMMODrop && plugin.preventVanillaDropsOnMMOSuccess) {
            event.setDropItems(false);
        }
    }
}