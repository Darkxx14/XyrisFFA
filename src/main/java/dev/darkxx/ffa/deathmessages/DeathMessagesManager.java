package dev.darkxx.ffa.deathmessages;

import dev.darkxx.ffa.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static dev.darkxx.ffa.Main.formatColors;

public class DeathMessagesManager implements Listener {
    private List<String> deathMessages;
    private FileConfiguration config;
    private Main main;

    public DeathMessagesManager(Main main) {
        this.main = main;
        deathMessages = new ArrayList<>();
        loadConfig(main);
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    private void loadConfig(JavaPlugin plugin) {
        config = plugin.getConfig();
        if (config.isList("deathMessages")) {
            deathMessages = config.getStringList("deathMessages");
        } else {
            deathMessages.add("&c%victim% &7was defeated by &a%attacker% &8(%attacker_health% ❤)");
            config.set("deathMessages", deathMessages);
            plugin.saveConfig();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player attacker = event.getEntity().getKiller();
        String attackerName = (attacker != null) ? attacker.getName() : "Unknown";
        String victimName = (victim != null) ? victim.getName() : "Unknown";
        double attackerHealth = (attacker != null) ? attacker.getHealth() : 0.00;
        double victimHealth = (victim != null) ? victim.getHealth() : 0.00;
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            Bukkit.broadcastMessage(formatColors(Placeholders(attackerName, victimName, attackerHealth, victimHealth)));
        });
    }

    private String Placeholders(String attackerName, String victimName, double attackerHealth, double victimHealth) {
        String deathMessage = getRandomDeathMessage();
        deathMessage = deathMessage.replace("%attacker%", attackerName);
        deathMessage = deathMessage.replace("%victim%", victimName);
        deathMessage = deathMessage.replace("%attacker_health%", String.format("%.2f", attackerHealth));
        deathMessage = deathMessage.replace("%victim_health%", String.format("%.2f", victimHealth));
        return deathMessage;
    }

    private String getRandomDeathMessage() {
        Random random = new Random();
        int index = random.nextInt(deathMessages.size());
        return deathMessages.get(index);
    }
}
