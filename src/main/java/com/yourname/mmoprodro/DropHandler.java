package com.yourname.mmoprodro;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DropHandler {
    private final MMOProDrop plugin;
    private final Map<Material, List<CustomDrop>> blockDrops = new HashMap<>();
    private final Random random = new Random();
    private static final Pattern AGE_PATTERN = Pattern.compile("age\\{equals=(\\d+)}");

    public DropHandler(MMOProDrop plugin) { this.plugin = plugin; }

    public void loadDropsFromMMOItems() {
        blockDrops.clear();
        File dropsFile = new File(MMOItems.plugin.getDataFolder(), "drops.yml");
        if (!dropsFile.exists()) {
            plugin.getLogger().warning(plugin.getLang().getMessage("loi.khong_tim_thay_drops_yml"));
            return;
        }

        FileConfiguration dropsConfig = YamlConfiguration.loadConfiguration(dropsFile);
        ConfigurationSection blocksSection = dropsConfig.getConfigurationSection("blocks");
        if (blocksSection == null) return;

        for (String materialName : blocksSection.getKeys(false)) {
            try {
                Material material = Material.valueOf(materialName.toUpperCase());
                List<CustomDrop> dropsForBlock = new ArrayList<>();
                ConfigurationSection dropTables = blocksSection.getConfigurationSection(materialName);
                if (dropTables == null) continue;

                for (String dropTableName : dropTables.getKeys(false)) {
                    ConfigurationSection tableConfig = dropTables.getConfigurationSection(dropTableName);
                    if (tableConfig == null || !tableConfig.contains("items")) continue;

                    int requiredAge = -1;
                    if (tableConfig.contains("condition")) {
                        for (String cond : tableConfig.getStringList("condition")) {
                            Matcher matcher = AGE_PATTERN.matcher(cond);
                            if (matcher.find()) {
                                requiredAge = Integer.parseInt(matcher.group(1));
                                break;
                            }
                        }
                    }

                    ConfigurationSection itemsSection = tableConfig.getConfigurationSection("items");
                    if (itemsSection == null) continue;

                    for(String itemTypeStr : itemsSection.getKeys(false)) {
                        Type itemType = MMOItems.plugin.getTypes().get(itemTypeStr.toUpperCase());
                        if (itemType == null) continue;
                        
                        List<String> itemEntries = itemsSection.isList(itemTypeStr) ? itemsSection.getStringList(itemTypeStr) : Collections.singletonList(itemsSection.getString(itemTypeStr));

                        for(String itemEntry : itemEntries) {
                            String[] parts = itemEntry.split(",");
                            if (parts.length < 1) continue;
                            String itemId = parts[0];
                            double chance = (parts.length > 1) ? Double.parseDouble(parts[1]) : 100.0;
                            int minAmount = (parts.length > 2) ? Integer.parseInt(parts[2]) : 1;
                            int maxAmount = (parts.length > 3) ? Integer.parseInt(parts[3]) : minAmount;
                            dropsForBlock.add(new CustomDrop(itemType, itemId, chance, minAmount, maxAmount, requiredAge));
                        }
                    }
                }
                if (!dropsForBlock.isEmpty()) {
                    blockDrops.put(material, dropsForBlock);
                }
            } catch (Exception e) {
                plugin.getLogger().severe(plugin.getLang().getMessage("loi.loi_doc_drop").replace("%block%", materialName));
            }
        }
        plugin.getLogger().info("Da tai thanh cong " + blockDrops.size() + " cau hinh drop tu block.");
    }

    public boolean tryDrop(Block block) {
        if (!blockDrops.containsKey(block.getType())) return false;

        boolean didDrop = false;
        Location dropLocation = block.getLocation().add(0.5, 0.5, 0.5);

        for (CustomDrop drop : blockDrops.get(block.getType())) {
            if (drop.requiredAge != -1) {
                if (!(block.getBlockData() instanceof Ageable)) continue;
                Ageable ageable = (Ageable) block.getBlockData();
                if (ageable.getAge() != drop.requiredAge) continue;
            }

            if (random.nextDouble() * 100 < drop.chance) {
                MMOItem mmoItem = MMOItems.plugin.getMMOItem(drop.type, drop.id);
                if (mmoItem != null) {
                    int amount = drop.minAmount == drop.maxAmount ? drop.minAmount : random.nextInt(drop.maxAmount - drop.minAmount + 1) + drop.minAmount;
                    ItemStack itemStack = mmoItem.newBuilder().build();
                    if(itemStack != null) {
                        itemStack.setAmount(amount);
                        block.getWorld().dropItemNaturally(dropLocation, itemStack);
                        didDrop = true;
                    }
                } else {
                    plugin.getLogger().warning("Khong tim thay MMOItem: " + drop.type.getId() + " - " + drop.id);
                }
            }
        }
        return didDrop;
    }

    private static class CustomDrop {
        Type type;
        String id;
        double chance;
        int minAmount, maxAmount, requiredAge;

        CustomDrop(Type type, String id, double chance, int min, int max, int age) {
            this.type = type; this.id = id; this.chance = chance;
            this.minAmount = min; this.maxAmount = max; this.requiredAge = age;
        }
    }
}