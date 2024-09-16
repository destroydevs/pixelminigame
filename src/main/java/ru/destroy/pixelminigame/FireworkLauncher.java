package ru.destroy.pixelminigame;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class FireworkLauncher {
    private static final Random random = new Random();

    public void launchFireworks(Player player) {
        int duration = 5; // 5 seconds

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= duration) {
                    cancel();
                    return;
                }
                Location location = player.getLocation().add(0, 1, 0);
                Firework firework = player.getWorld().spawn(location, Firework.class);
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffect(FireworkEffect.builder()
                        .withColor(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                        .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                        .with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)])
                        .build());
                meta.setPower(random.nextInt(2) + 1);
                firework.setFireworkMeta(meta);

                ticks+=1;
            }
        }.runTaskTimer(JavaPlugin.getPlugin(PixelMinigame.class), 0, 20);
    }
}

