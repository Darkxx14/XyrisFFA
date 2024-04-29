package dev.darkxx.ffa.api;

import dev.darkxx.ffa.kits.KitManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class IKits {

    /**
     * Sends an invalid command message to the sender.
     *
     * @param sender The command sender to whom the message will be sent.
     */
    public static void sendInvalidCommandMessage(CommandSender sender) {
        KitManager.sendInvalidCommandMessage(sender);
    }

    /**
     * Saves a kit with the specified name.
     *
     * @param sender  The command sender saving the kit.
     * @param kitName The name of the kit to save.
     */
    public static void saveKit(CommandSender sender, String kitName) {
        KitManager.saveKit(sender, kitName);
    }

    /**
     * Deletes the kit with the specified name.
     *
     * @param kitName The name of the kit to delete.
     */
    public static void deleteKit(String kitName) {
        KitManager.deleteKit(kitName);
    }

    /**
     * Lists all available kits to the sender.
     *
     * @param sender The command sender to whom the kit list will be sent.
     */
    public static void listKits(CommandSender sender) {
        KitManager.listKits(sender);
    }

    /**
     * Gives a kit to the specified player.
     *
     * @param player  The player receiving the kit.
     * @param kitName The name of the kit to give.
     */
    public static void giveKit(Player player, String kitName) {
        KitManager.giveKit(player, kitName);
    }

    /**
     * Retrieves a list of all kit names.
     *
     * @return A list containing the names of all available kits.
     */
    public static List<String> getKitNames() {
        return KitManager.getKitNames();
    }

    /**
     * Checks if a kit with the specified name exists.
     *
     * @param kitName The name of the kit to check.
     * @return {@code true} if the kit exists, {@code false} otherwise.
     */
    public static boolean isKitExists(String kitName) {
        return KitManager.isKitExisting(kitName);
    }
}
