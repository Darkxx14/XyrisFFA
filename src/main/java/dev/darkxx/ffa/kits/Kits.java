package dev.darkxx.ffa.kits;

import org.bukkit.entity.Player;
import java.util.List;

public class Kits {

    public static List<String> getKitNames() {
        return KitManager.getKitNames();
    }

/*
    public static String getCurrentKit(Player player) {
        String currentKit = KitManager.currentKit.get(player);
        return currentKit != null ? currentKit : "none";
    }
*/

    public static String getLastKit(Player player) {
        String lastKit = KitManager.lastKit.get(player);
        return lastKit != null ? lastKit : "none";
    }
}