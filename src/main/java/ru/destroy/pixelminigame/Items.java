package ru.destroy.pixelminigame;

import org.bukkit.*;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class Items implements Listener {
    public static final ItemStack feather = new ItemBuilder(Material.FEATHER)
            .setName("§6Полёт нормальный")
            .setLore("", "§7Подбрасывает вверх на 10 блоков")
            .setPersistence("ability", "launch")
            .build();

    public static final ItemStack golden_apple = new ItemBuilder(Material.BLAZE_POWDER)
            .setName("§6Золотой Щит")
            .setLore("", "§7Блокирует любой урон", "§7от одного удара.")
            .setPersistence("ability", "golden_shield")
            .build();

    public static final ItemStack inkSac = new ItemBuilder(Material.INK_SAC)
            .setName("§6Чернильный мешочек")
            .setLore("", "§7Ослепляет врагов в радиусе 4 блоков", "§7на 2 секунды.")
            .setPersistence("ability", "blind")
            .build();

    public static final ItemStack glass = new ItemBuilder(Material.HEART_OF_THE_SEA)
            .setName("§6Стеклянный Щит")
            .setLore("", "§7Поглощает 3 единицы урона", "§7от стрел.")
            .setPersistence("ability", "arrow_shield")
            .build();

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p && hash.containsKey(p.getUniqueId())) {

            if (hash.get(p.getUniqueId()).startsWith("golden_shield")) {
                int usages = Integer.parseInt(hash.get(p.getUniqueId()).replace("golden_shield",""));
                if (usages>=1) {
                    hash.put(p.getUniqueId(),"golden_shield"+(usages-1));
                    spawnParticle(p,Color.SILVER, Particle.FLAME);
                    playSound(Sound.BLOCK_ANVIL_DESTROY);
                } else {
                    hash.remove(p.getUniqueId());
                    return;
                }
                e.setCancelled(true);
                return;
            }
            if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE && hash.get(p.getUniqueId()).startsWith("arrow_shield")) {
                int usages = Integer.parseInt(hash.get(p.getUniqueId()).replace("arrow_shield",""));
                if (usages>=1) {
                    hash.put(p.getUniqueId(),"arrow_shield"+(usages-1));
                    spawnParticle(p,Color.SILVER, Particle.CRIT);
                    playSound(Sound.BLOCK_ANVIL_DESTROY);
                } else {
                    hash.remove(p.getUniqueId());
                }
                e.setCancelled(true);
            }

        }

    }

    static HashMap<UUID, String> hash = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (event.getHand() == EquipmentSlot.HAND && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {

            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            if ((itemInHand.getType().name().contains("_SPAWN_EGG") || itemInHand.getType() == Material.TNT || itemInHand.getType() == Material.FIRE_CHARGE) && event.getAction() == Action.RIGHT_CLICK_AIR) {

                EntityType mobType;
                try {
                    mobType = EntityType.valueOf(itemInHand.getType().name().replace("_SPAWN_EGG", ""));
                } catch (IllegalArgumentException e) {
                    mobType=EntityType.ARROW;
                }
                switch (itemInHand.getType()) {
                    case TNT -> mobType = EntityType.PRIMED_TNT;
                    case FIRE_CHARGE -> mobType = EntityType.FIREBALL;
                    case MOOSHROOM_SPAWN_EGG -> mobType = EntityType.MUSHROOM_COW;
                }


                Egg egg = player.launchProjectile(Egg.class);

                egg.addPassenger(player.getWorld().spawnEntity(egg.getLocation(), mobType));

                Vector direction = player.getEyeLocation().getDirection();

                //egg.setVelocity(direction.multiply(1.5));

                player.playSound(player.getLocation(), Sound.ENTITY_EGG_THROW, 1, 1);

                itemInHand.setAmount(itemInHand.getAmount() - 1);
                return;
            }
            if (item == null || item.getItemMeta() == null || !item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(JavaPlugin.getPlugin(PixelMinigame.class), "ability"), PersistentDataType.STRING)) {
                return;
            }

            String ability = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(JavaPlugin.getPlugin(PixelMinigame.class), "ability"), PersistentDataType.STRING);

            if (ability.equals("launch")) {
                player.setVelocity(player.getLocation().getDirection().multiply(1.45).setY(2));
                item.setAmount(item.getAmount() - 1);
                playSound(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH);
                spawnParticle(player, Color.WHITE);
            } else if (ability.equals("blind")) {
                for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                    if (entity instanceof Player nearbyPlayer) {
                        //nearbyPlayer.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 5*20, 1));
                        nearbyPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5*20, 1));
                        nearbyPlayer.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5*20, 1));
                    }
                }
                item.setAmount(item.getAmount() - 1);
                //playSound(Sound.ITEM_INK_SAC_USE);
                spawnParticle(player,Color.BLACK);
            } else if (ability.equals("golden_shield")) {
                hash.put(player.getUniqueId(), "golden_shield1");
                item.setAmount(item.getAmount() - 1);
                playSound(Sound.BLOCK_ANVIL_PLACE);
                spawnParticle(player,Color.ORANGE);
            } else if (ability.equals("arrow_shield")) {
                hash.put(player.getUniqueId(), "arrow_shield3");
                item.setAmount(item.getAmount() - 1);
                playSound(Sound.BLOCK_ANVIL_PLACE);
                spawnParticle(player,Color.GRAY);
            }
        }
    }
    private void playSound(Sound sound) {
        Bukkit.getOnlinePlayers().forEach(p -> p.getWorld().playSound(p.getLocation(), sound, 1, 1));
    }

    private void spawnParticle(Player p, Color color, Particle... particle) {
        Location loc = p.getLocation();
        Particle.DustOptions options = new Particle.DustOptions(color,3);
        if (particle.length >= 1) {
            loc.getWorld().spawnParticle(particle[0], loc,99,1.5,1.5,1.5);
            return;
        }
        loc.getWorld().spawnParticle(Particle.REDSTONE, loc,99,1.5,1.5,1.5,options);
    }

}
