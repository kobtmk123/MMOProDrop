package com.yourname.mmoprodro;

import com.yourname.mmoprodro.commands.CommandManager;
import com.yourname.mmoprodro.listeners.BlockBreakListener;
import com.yourname.mmoprodro.listeners.BlockPlaceListener;
import com.yourname.mmoprodro.utils.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MMOProDrop extends JavaPlugin {

    private static MMOProDrop instance;
    private DropHandler dropHandler;
    private PlacedBlockManager placedBlockManager;
    private LanguageManager languageManager;

    public boolean preventFarmFromPlacedBlocks;
    public boolean preventVanillaDropsOnMMOSuccess;

    @Override
    public void onEnable() {
        instance = this;

        if (Bukkit.getPluginManager().getPlugin("MMOItems") == null) {
            getLogger().severe("Khong tim thay MMOItems! Plugin MMOProDrop se tu tat.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        loadConfiguration();
        this.languageManager = new LanguageManager(this);
        this.placedBlockManager = new PlacedBlockManager();
        this.dropHandler = new DropHandler(this);
        dropHandler.loadDropsFromMMOItems();

        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);

        getCommand("mmoprodro").setExecutor(new CommandManager(this));
        
        if (getConfig().getBoolean("kiem_tra_cap_nhat", true)) {
            // Thêm code kiểm tra update ở đây nếu muốn
        }

        getLogger().info("MMOProDrop da duoc bat!");
        getLogger().warning("Luu y: Hay dam bao ban da tat (them dau #) muc 'blocks:' trong drops.yml cua MMOItems!");
    }

    public void loadConfiguration() {
        saveDefaultConfig();
        reloadConfig();
        preventFarmFromPlacedBlocks = getConfig().getBoolean("cai_dat.chong_farm_tu_block_dat", true);
        preventVanillaDropsOnMMOSuccess = getConfig().getBoolean("cai_dat.ngan_drop_vanilla_khi_mmo_thanh_cong", true);
    }
    
    public void reloadPlugin() {
        loadConfiguration();
        languageManager.reload();
        dropHandler.loadDropsFromMMOItems();
    }

    @Override
    public void onDisable() {
        getLogger().info("MMOProDrop da duoc tat!");
    }

    public static MMOProDrop getInstance() { return instance; }
    public DropHandler getDropHandler() { return dropHandler; }
    public PlacedBlockManager getPlacedBlockManager() { return placedBlockManager; }
    public LanguageManager getLang() { return languageManager; }
}