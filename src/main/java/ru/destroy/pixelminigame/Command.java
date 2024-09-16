package ru.destroy.pixelminigame;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Command implements CommandExecutor {

    static ItemWarsGame game;

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {

        Set<Player> playersInGame = ItemWarsGame.hash.values()
                .stream()
                .flatMap(z -> z.players.stream())
                .collect(Collectors.toCollection(HashSet::new));

        List<Player> freePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        freePlayers.removeAll(playersInGame);


        if (freePlayers.size()>=2 && game == null) {
            game = new ItemWarsGame(freePlayers);
            game.start();
        } else {
            commandSender.sendMessage("недостаточно игроков или игра уже запущена, подождите пожалуйста");
        }
        return true;
    }
}
