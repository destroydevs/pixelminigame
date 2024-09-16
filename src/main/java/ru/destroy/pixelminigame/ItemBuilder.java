package ru.destroy.pixelminigame;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemBuilder {
    private final ItemStack item;
    private final ItemMeta meta;
    private final Map<String, String> persistenceData;

    public ItemBuilder(Material material) {
        item = new ItemStack(material);
        meta = item.getItemMeta();
        persistenceData = new HashMap<>();
    }
    public ItemBuilder setLore(String... lore) {
        List<String> lore1 = Arrays.asList(lore);
        meta.setLore(lore1);
        return this;
    }

    public ItemBuilder setName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setPersistence(String key, String value) {
        persistenceData.put(key, value);
        return this;
    }

    public ItemStack build() {
        for (Map.Entry<String, String> entry : persistenceData.entrySet()) {
            meta.getPersistentDataContainer().set(new NamespacedKey(JavaPlugin.getPlugin(PixelMinigame.class), entry.getKey()), PersistentDataType.STRING, entry.getValue());
        }
        item.setItemMeta(meta);
        return item;
    }

}

