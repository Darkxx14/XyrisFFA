package dev.darkxx.ffa.arenas;

import org.bukkit.entity.Player;
import java.util.List;

public class Arenas {

    public static List<String> getArenasList() {
        return ArenaManager.listArenas();
    }

    public static String getLastArena(Player player) {
        String lastArena = ArenaManager.lastArena.get(player);
        return lastArena != null ? lastArena : "none";
    }
}