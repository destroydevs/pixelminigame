package ru.destroy.pixelminigame;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ItemWarsGame implements Listener {

    static int gameNumber = 0;

    int thisGameNumber = 0;

    ScheduledExecutorService service;

    Collection<? extends Player> players;

    World world;

    public ItemWarsGame(Collection<? extends Player> players) {
        this.players = players;
        service = Executors.newSingleThreadScheduledExecutor();
    }

    public ItemWarsGame() {
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        for (ItemWarsGame ps : hash.values()) {
            if (e.getEntity() instanceof Player p) {
                if (ps.players.contains(p)) {
                    if (e.getCause() == EntityDamageEvent.DamageCause.VOID && p.getGameMode() != GameMode.SPECTATOR) {
                        p.setGameMode(GameMode.SPECTATOR);
                        p.teleport(ps.world.getSpawnLocation().add(0.5, 4, 0.5));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.1F, 1);
                        ps.players.forEach(pz-> {
                            if (p.getKiller() != null) {
                                Player killer = p.getKiller();
                                pz.sendMessage("§6☺ §8| §7" + p.getName() + "§e выбыл из игры благодаря §d"+killer.getName());
                            } else {
                                pz.sendMessage("§6☺ §8| §7" + p.getName() + "§e выбыл из игры!");
                            }
                            pz.playSound(pz.getLocation(), Sound.ENTITY_VILLAGER_HURT,1,1);
                        });
                        e.setCancelled(true);
                        break;
                    }
                    return;
                }
            }
        }
        if (e.getEntity() instanceof Player) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.getPlayer().isOp()) return;
        for (ItemWarsGame ps : hash.values()) {
            if (ps.players.contains(e.getPlayer())) {
                return;
            }
        }
        e.setCancelled(true);
    }
    @EventHandler
    public void onClick(PlayerInteractEntityEvent e) {
        for (ItemWarsGame ps : hash.values()) {
            if (ps.players.contains(e.getPlayer())) {
                if (e.getRightClicked() instanceof Player p) {
                    Player player = e.getPlayer();
                    player.openInventory(p.getInventory());
                }
            }
        }
    }
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player p) {
            for (ItemWarsGame ps : hash.values()) {
                if (ps.players.contains(p)) {
                    if (e.getClickedInventory() == null) return;
                    if (e.getClickedInventory() == p.getInventory() || e.getClickedInventory().getType() != InventoryType.PLAYER) {
                        return;
                    }
                    e.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void preLogin(AsyncPlayerPreLoginEvent e) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId().equals(e.getUniqueId())) {
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Игрок уже играет на сервере");
                return;
            }
        }
    }

    @EventHandler
    public void onShot(ProjectileHitEvent e) {
        if (e.getHitEntity() instanceof Player p) {
            for (ItemWarsGame ps : hash.values()) {
                if (ps.players.contains(p)) {
                    if (e.getEntity() instanceof Egg || e.getEntity() instanceof Snowball) {
                        Projectile projectile = e.getEntity();
                        p.damage(0.5D, (Entity) projectile.getShooter());
                        p.setVelocity(projectile.getVelocity().normalize().multiply(1.5));
                        return;
                    }
                }
            }
        } else {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (e.getPlayer().isOp()) return;
        for (ItemWarsGame ps : hash.values()) {
            if (ps.players.contains(e.getPlayer())) {
                return;
            }
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onChatAsync(AsyncPlayerChatEvent e) {
        if (e.getMessage().startsWith("/")) {
            return;
        }
        e.setCancelled(true);
        Player pl = e.getPlayer();
        for (Player p : Bukkit.getOnlinePlayers()) {
            String arrow = " §x§D§0§B§E§0§0» §x§A§A§A§A§A§A";
            if (p.getName().equals(pl.getName())) {
                arrow = " §x§D§0§5§9§0§0» §x§A§A§A§A§A§A";
                Bukkit.getConsoleSender().sendMessage("§x§4§D§F§F§5§6(◉) §8| §7" + pl.getName() + arrow + e.getMessage());
            }
            String message = "§x§4§D§F§F§5§6(◉) §8| §7" + pl.getName() + arrow + e.getMessage();

            p.sendMessage(message);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        for (ItemWarsGame ps : hash.values()) {
            if (ps.players.contains(e.getPlayer())) {
                Player p = e.getPlayer();
                p.teleport(Bukkit.getWorld("world").getSpawnLocation());
                bar.removePlayer(e.getPlayer());
                ps.players.forEach(pz-> {
                    pz.sendMessage("§6☺ §8| §7"+p.getName()+"§e выбыл из игры!");
                    pz.playSound(pz.getLocation(), Sound.ENTITY_VILLAGER_HURT,1,1);
                });
                for (int i = 0; i < p.getInventory().getSize(); i++) {
                    p.getInventory().setItem(i, null);
                }
            }
        }
    }
    @EventHandler
    public void onDeath(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            for (ItemWarsGame ps : hash.values()) {
                if (ps.players.contains(p)) {
                    if (p.getHealth() < e.getDamage() && (p.getInventory().getItemInMainHand().getType() != Material.TOTEM_OF_UNDYING || p.getInventory().getItemInOffHand().getType() != Material.TOTEM_OF_UNDYING) && p.getGameMode() != GameMode.SPECTATOR) {
                        p.setGameMode(GameMode.SPECTATOR);
                        p.teleport(ps.world.getSpawnLocation().add(0.5, 4, 0.5));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.1F, 1);
                        ps.players.forEach(pz -> {
                            if (p.getKiller() != null) {
                                Player killer = p.getKiller();
                                pz.sendMessage("§6☺ §8| §7" + p.getName() + "§e выбыл из игры благодаря §d"+killer.getName());
                            } else {
                                pz.sendMessage("§6☺ §8| §7" + p.getName() + "§e выбыл из игры!");
                            }
                            pz.playSound(pz.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 1);
                        });
                        e.setCancelled(true);
                        break;
                    }
                }
            }
        }
    }

    public void createWorld() {
        if (Bukkit.getWorld("game_" + gameNumber) != null) {
            World del = Bukkit.getWorld("game_" + gameNumber);
            deleteWorld(del);
        }
        WorldCreator worldCreator = new WorldCreator("game_" + gameNumber);
        worldCreator.type(WorldType.FLAT);
        worldCreator.generator(new VoidGenerator());
        worldCreator.generateStructures(false);
        service.scheduleAtFixedRate(() -> {
            for (Player p : players) {
                p.sendTitle("§6Настройка карты...", "§fПодождите немного", 20, 5, 5);
                p.sendMessage("Запуск игры...");
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            }
        }, 0, 1, TimeUnit.SECONDS);
        world = Bukkit.createWorld(worldCreator);
        world.setSpawnLocation(0, 40, 0);
        WorldBorder border = world.getWorldBorder();
        border.setCenter(0, 0);
        border.setSize(60);
        border.setSize(3, 330);
        border.setDamageAmount(5);
        world.setDifficulty(Difficulty.HARD);
        thisGameNumber = gameNumber;
        gameNumber += 1;
    }

    public void deleteWorld() {
        if (world != null) {
            Bukkit.unloadWorld(world, false);
        }

        File worldFolder = new File(Bukkit.getWorldContainer(), world.getName());

        if (worldFolder.exists()) {
            deleteDirectory(worldFolder);
        }
    }
    public void deleteWorld(World w) {
        if (w != null) {
            Bukkit.unloadWorld(w, false);
        }

        File worldFolder = new File(Bukkit.getWorldContainer(), w.getName());

        if (worldFolder.exists()) {
            deleteDirectory(worldFolder);
        }
    }

    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }

    BossBar bar = Bukkit.createBossBar("§7Игра #" + gameNumber + " §8| §eДо выдачи предмета §a8сек.", BarColor.GREEN, BarStyle.SOLID);


    BukkitTask task;
    public void startBarTask(Runnable runnable) {
        this.task = new BukkitRunnable() {
            @Override
            public void run() {

                for (int i = 0; i < 8; i++) {
                    int time = 8 - i;
                    bar.setTitle("§7Игра #" + gameNumber + " §8| §eДо выдачи предмета §a" + time + "сек.");
                    bar.setProgress((double) time / 8);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ignored) {
                        break;
                    }
                }
                bar.setProgress(1);
                if (!this.isCancelled()) {
                    runnable.run();
                }
            }
        }.runTaskAsynchronously(JavaPlugin.getPlugin(PixelMinigame.class));
    }

    public void startGameOverChecks() {
        ScheduledExecutorService se = Executors.newSingleThreadScheduledExecutor();
        se.scheduleAtFixedRate(() -> {
            Player winner = null;
            int living = 0;
            for (Player p : world.getPlayers()) {
                if (p.getGameMode().equals(GameMode.SURVIVAL)) {
                    living += 1;
                    winner = p;
                }
            }
            if (living <= 1) {
                gameOver(winner, se);
            }
        }, 1000/20, 1000/20, TimeUnit.MILLISECONDS);

    }

    public void gameOver(Player winner, ScheduledExecutorService se) {
        if (se != null) {
            service.shutdownNow();
            se.shutdownNow();
        }
        Bukkit.getScheduler().cancelTask(task.getTaskId());
        bar.removeAll();
        hash.remove(thisGameNumber);
        if (winner == null) {
            deleteWorld();
            return;
        }
        for (Player p : players) {
            p.sendTitle("Победитель: " + winner.getName(), "", 20, 20, 20);
        }
        new FireworkLauncher().launchFireworks(winner);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : players) {
                    Items.hash.remove(p.getUniqueId());
                    p.teleport(Bukkit.getWorld("world").getSpawnLocation());
                    p.setGameMode(GameMode.SURVIVAL);
                    for (int i = 0; i < p.getInventory().getSize(); i++) {
                        p.getInventory().setItem(i, null);
                    }
                }
                if (PixelMinigame.wins.containsKey(winner.getUniqueId())) {
                    PixelMinigame.wins.put(winner.getUniqueId(),PixelMinigame.wins.get(winner.getUniqueId())+1);
                } else {
                    PixelMinigame.wins.put(winner.getUniqueId(),1);
                }
                Command.game=null;
                deleteWorld();
            }
        }.runTaskLater(JavaPlugin.getPlugin(PixelMinigame.class), 20 * 5);
    }

    static HashMap<Integer, ItemWarsGame> hash = new HashMap<>();

    public void start() {
        createWorld();
        List<Location> locations = new PlatformGenerator().generatePlatform(world, players.size());
        service.shutdownNow();
        service = Executors.newSingleThreadScheduledExecutor();
        int psize = players.size() - 1;
        players.forEach(p -> {
            p.setGameMode(GameMode.SURVIVAL);
            for (int i = 0; i < p.getInventory().getSize(); i++) {
                p.getInventory().setItem(i, null);
            }
            p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 60, 255));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 255));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 255));
        });
        hash.put(thisGameNumber, this);
        for (Location loc : locations) {
            players.stream().toList().get(psize).teleport(loc);
            if (psize > 0) {
                psize -= 1;
            }
        }
        if (!service.isShutdown()) {
            service.scheduleAtFixedRate(() -> {


                for (Player p : players) {
                    if (!bar.getPlayers().contains(p)) {
                        bar.addPlayer(p);
                    }
                    startBarTask(() -> {
                        try {
                            if (p.getGameMode() != GameMode.SPECTATOR) {
                                if (!this.task.isCancelled()) {
                                    p.getInventory().addItem(getRandomItem());
                                }
                                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    });
                }

            }, 0, 8, TimeUnit.SECONDS);
        }
        startGameOverChecks();


    }

    private ItemStack getRandomItem() {
        ItemStack item = new ItemStack(Material.values()[(int) (Math.random() * Material.values().length)]);

        if (item.getType().toString().contains("POTION") || item.getType().toString().contains("TIPPED_ARROW")) {
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.addCustomEffect(new PotionEffect(PotionEffectType.values()[(int) (Math.random() * PotionEffectType.values().length)], (int) (Math.random() * 20 * 10), (int) (Math.random() * 3)), true);
            item.setItemMeta(meta);
        } else {
            int chance = (int) (Math.random() * 100);
            if (chance <= 30) {
                ItemMeta meta = item.getItemMeta();
                meta.addEnchant(Enchantment.values()[(int) (Math.random() * Enchantment.values().length)], (int) (Math.random() * 5)+1, true);
                item.setItemMeta(meta);
            }
        }
        if (!item.getType().isItem() /*|| !item.getType().isEnabledByFeature(world)*/ || item.getType().toString().contains("AIR") || item.getType().toString().contains("POTTED")) {
            return getRandomItem();
        }

        int chance = (int) (Math.random() * 100);
        if (chance <= 2) {
            List<ItemStack> items = new ArrayList<>();
            items.add(Items.feather.clone());
            items.add(Items.inkSac.clone());
            items.add(Items.golden_apple.clone());
            items.add(Items.glass.clone());
            ItemStack a = items.get((int) (Math.random() * items.size()));
            if (a == null) {
                return getRandomItem();
            }
            return a;
        }
        return item;
    }
}
