package com.yourname.mmoprodro.commands;

import com.yourname.mmoprodro.MMOProDrop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandManager implements CommandExecutor {
    private final MMOProDrop plugin;

    public CommandManager(MMOProDrop plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("mmoprodro.admin")) {
            sender.sendMessage(plugin.getLang().getMessage("lenh.khong_co_quyen"));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadPlugin();
            sender.sendMessage(plugin.getLang().getMessage("lenh.reload_thanh_cong"));
            return true;
        }

        sender.sendMessage(plugin.getLang().getMessage("lenh.lenh_khong_ton_tai"));
        return true;
    }
}