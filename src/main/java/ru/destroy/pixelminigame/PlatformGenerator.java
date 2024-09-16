package ru.destroy.pixelminigame;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// Created by AI
public class PlatformGenerator {
    private static final int PLATFORM_SIZE = 3;
    private static final int CHEST_OFFSET = 1;
    private static final int PLAYER_RADIUS = 18;

    public List<Location> generatePlatform(World world, int playerCount) {
        List<Location> locations = new LinkedList<>();

        // Create platform
        Location center = world.getSpawnLocation();
        for (int x = -PLATFORM_SIZE / 2; x <= PLATFORM_SIZE / 2; x++) {
            for (int z = -PLATFORM_SIZE / 2; z <= PLATFORM_SIZE / 2; z++) {
                Block block = center.clone().add(x, 0, z).getBlock();
                block.setType(Material.BEDROCK);
            }
        }

        // Create chest
        Block chestBlock = center.clone().add(0, CHEST_OFFSET, 0).getBlock();
        chestBlock.setType(Material.CHEST);
        center.clone().add(0, CHEST_OFFSET+2, 0).getBlock().setType(Material.SPAWNER);
        Chest chest = (Chest) chestBlock.getState();
        ItemStack[] items = LootGenerator.generateRandomItems(5);
        for (ItemStack item : items) {
            chest.getBlockInventory().setItem(ThreadLocalRandom.current().nextInt(0,chest.getBlockInventory().getSize()), item);
        }


        // Generate player locations
        double angleOffset = 2 * Math.PI / playerCount;
        double radius = PLAYER_RADIUS;

        for (int i = 0; i < playerCount; i++) {
            double angle = i * angleOffset;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            Location location = new Location(world, x, center.getY() + CHEST_OFFSET, z);
            int s = 8;
            location.clone().add(0,s-1,0).getBlock().setType(Material.BEDROCK);
            locations.add(location.add(0,s,0));
        }

        return locations;
    }
}



