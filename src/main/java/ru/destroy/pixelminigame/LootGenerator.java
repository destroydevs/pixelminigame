package ru.destroy.pixelminigame;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

// Created by AI
public class LootGenerator {

    private static final Random random = new Random();

    private static List<ItemStack> createLoot() {
        List<ItemStack> commonItems = new ArrayList<>();
        List<ItemStack> uncommonItems = new ArrayList<>();
        List<ItemStack> rareItems = new ArrayList<>();

        commonItems.add(new ItemStack(Material.BIRCH_LOG, 1));
        commonItems.add(new ItemStack(Material.ACACIA_LOG, 1));
        commonItems.add(new ItemStack(Material.WOODEN_PICKAXE, 1));
        commonItems.add(new ItemStack(Material.COBBLESTONE, 1));

        uncommonItems.add(new ItemStack(Material.JUNGLE_LOG, 1));
        uncommonItems.add(new ItemStack(Material.OAK_LOG, 1));
        uncommonItems.add(new ItemStack(Material.TNT, 1));
        uncommonItems.add(new ItemStack(Material.FIRE_CHARGE, 1));
        uncommonItems.add(new ItemStack(Material.DIAMOND_SWORD, 1));
        uncommonItems.add(new ItemStack(Material.DIAMOND_HELMET, 1));
        uncommonItems.add(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
        uncommonItems.add(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
        uncommonItems.add(new ItemStack(Material.DIAMOND_BOOTS, 1));
        uncommonItems.add(new ItemStack(Material.STONE, 1));
        uncommonItems.add(new ItemStack(Material.CHAINMAIL_HELMET, 1));
        uncommonItems.add(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
        uncommonItems.add(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
        uncommonItems.add(new ItemStack(Material.CHAINMAIL_BOOTS, 1));
        uncommonItems.add(new ItemStack(Material.BOW, 1));
        uncommonItems.add(new ItemStack(Material.ARROW, 1));
        uncommonItems.add(new ItemStack(Material.SNOWBALL, 1));
        //uncommonItems.add(new ItemStack(Material.CHERRY_LOG, 1));
        uncommonItems.add(Items.inkSac);

        rareItems.add(Items.glass);
        rareItems.add(Items.feather);
        rareItems.add(Items.golden_apple);

        List<ItemStack> list = new ArrayList<>();
        list.addAll(commonItems);
        list.addAll(uncommonItems);
        list.addAll(rareItems);
        return list;
    }

    public static ItemStack[] generateRandomItems(int count) {
        List<ItemStack> items = new ArrayList<>(createLoot());


        // Создаем массив ItemStack
        ItemStack[] loot = new ItemStack[count];
        for (int i = 0; i < count; i++) {
            // Выбираем случайный предмет из списка
            ItemStack itemStack = items.get(random.nextInt(items.size()));
            if (itemStack.getType().getMaxStackSize()>1) {
                itemStack.setAmount(ThreadLocalRandom.current().nextInt(1, 2 + 1));
            }

            // Дублируем случайный предмет
            ItemMeta itemMeta = itemStack.getItemMeta();

            int chance = (int) (Math.random() * 100);
            if (chance <= 30) {
                ItemMeta meta = itemStack.getItemMeta();
                meta.addEnchant(Enchantment.values()[(int) (Math.random() * Enchantment.values().length)], (int) (Math.random() * 5), true);
                itemStack.setItemMeta(meta);
            }
            itemStack.setItemMeta(itemMeta);

            loot[i] = itemStack;
        }

        return loot;
    }
}

