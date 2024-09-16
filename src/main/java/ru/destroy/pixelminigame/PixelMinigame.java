package ru.destroy.pixelminigame;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.UUID;

public final class PixelMinigame extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new Items(),this);
        Bukkit.getPluginManager().registerEvents(new ItemWarsGame(),this);
        this.getCommand("start").setExecutor(new Command());
        scheduleChecker();
        Bukkit.getConsoleSender().sendMessage("§aPlugin enabled! Ver: §ev"+getDescription().getVersion());

    }

    public static HashMap<UUID, Integer> wins = new HashMap<>();

    public void scheduleChecker() {
        // end §x§A§E§0§0§C§4
        // hell §x§E§1§0§0§3§A
        // world §x§0§0§F§E§4§1
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                Scoreboard scoreboard = p.getScoreboard();
                Team team = scoreboard.getTeam(p.getUniqueId() + "_gamepixel");
                if (team == null) {
                    team = scoreboard.registerNewTeam(p.getUniqueId() + "_gamepixel");
                }
                int win = wins.getOrDefault(p.getUniqueId(),0);

                p.setPlayerListHeader("§7Блоки? Дайте два!\n");
                p.setPlayerListFooter("\n§7для начала игры: /start");

                p.setPlayerListName("§8| §x§9§2§9§2§9§2" + p.getName()+" §7Побед: §6"+win);
                //p.setDisplayName("§8| §x§9§2§9§2§9§2" + p.getName()+" §7Побед: §6"+win);
                p.setCustomName("§8| §x§9§2§9§2§9§2" + p.getName()+" §7Побед: §6"+win);
                p.setCustomNameVisible(true);
                team.setPrefix("§7[" + win + "§7] ");
                team.setColor(ChatColor.GRAY);
                team.addEntry(p.getName());
                team.setSuffix(" §4"+((int)p.getHealth())+ "§c♥");


                p.setScoreboard(scoreboard);

            }
        }, 20, 20);
    }


    @Override
    public void onDisable() {
        for (ItemWarsGame game : ItemWarsGame.hash.values()) {
            game.gameOver(null,null);
        }
    }
}
