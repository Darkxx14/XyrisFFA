package dev.darkxx.ffa.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * This class provides utility methods for interacting with WorldGuard regions.
 */
public class WorldGuardUtils {

    /**
     * Retrieves the count of players currently inside a specified WorldGuard region.
     *
     * @param regionName the name of the WorldGuard region
     * @return the count of players inside the specified region
     */
    public static int getPlayerCountInRegion(String regionName) {
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = regionContainer.createQuery();
        int playerCount = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isInRegion(player, regionName, query)) {
                playerCount++;
            }
        }
        return playerCount;
    }

    /**
     * Checks if a player is inside a specified WorldGuard region.
     *
     * @param player the player to check
     * @param regionName the name of the WorldGuard region
     * @param query the region query object
     * @return true if the player is inside the specified region, false otherwise
     */
    private static boolean isInRegion(Player player, String regionName, RegionQuery query) {
        com.sk89q.worldedit.util.Location playerLocation = BukkitAdapter.adapt(player.getLocation());
        for (ProtectedRegion region : query.getApplicableRegions(playerLocation).getRegions()) {
            if (region.getId().equalsIgnoreCase(regionName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a player is inside a specified WorldGuard region.
     *
     * @param player the player to check
     * @param regionName the name of the WorldGuard region
     * @return true if the player is inside the specified region, false otherwise
     */
    public static boolean isinRegion(Player player, String regionName) {
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = regionContainer.createQuery();
        com.sk89q.worldedit.util.Location playerLocation = BukkitAdapter.adapt(player.getLocation());
        for (ProtectedRegion region : query.getApplicableRegions(playerLocation).getRegions()) {
            if (region.getId().equalsIgnoreCase(regionName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a given arena is considered busy based on the number of players inside.
     *
     * @param regionName the name of the WorldGuard region representing the arena
     * @return a String indicating whether the arena is busy or empty
     */
    public static String checkArenaStatus(String regionName) {
        int playerCount = getPlayerCountInRegion(regionName);

        if (playerCount >= 8) {
            return " &7(Busy)";
        } else {
            return "";
        }
    }
}
