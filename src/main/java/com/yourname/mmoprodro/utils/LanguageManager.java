package com.yourname.mmoprodro.utils;

import com.yourname.mmoprodro.MMOProDrop;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LanguageManager {
    private final MMOProDrop plugin;
    private FileConfiguration messagesConfig;
    private String prefix;

    public LanguageManager(MMOProDrop plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        this.prefix = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("tien_to", "&8[&eMMOProDrop&8] &r"));
    }

    public String getMessage(String path) {
        String message = messagesConfig.getString(path, "&cMessage not found: " + path);
        return prefix + ChatColor.translateAlternateColorCodes('&', message);
    }
}