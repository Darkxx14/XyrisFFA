package dev.darkxx.ffa.combat;

import dev.darkxx.ffa.Main;
import dev.darkxx.ffa.api.events.CombatEndEvent;
import dev.darkxx.ffa.api.events.CombatStartEvent;
import dev.darkxx.ffa.utils.ActionBarUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static dev.darkxx.ffa.Main.formatColors;

public class CombatTagger implements Listener {

    private final Main main;
    private static final List<Combat> combatLogs = new ArrayList<>();
    private final int combatLogDurationInSeconds;
    private BukkitTask task;

    public CombatTagger(Main main, int combatLogDurationInSeconds) {
        this.main = main;
        this.combatLogDurationInSeconds = combatLogDurationInSeconds;
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(main, this::updateCombatLogs, 4L, 8L);
    }

    private void updateCombatLogs() {
        String actionBarMessage = Main.getInstance().getConfig().getString("combat-tagger.action-bar", "&7You're in combat for &b%seconds% &7seconds.");
        for (Iterator<Combat> iterator = combatLogs.iterator(); iterator.hasNext(); ) {
            Combat combatLog = iterator.next();

            if (combatLog.isExpired()) {
                iterator.remove();
                endCombat(combatLog);
                continue;
            }
            int secondsLeft = combatLog.getTimeLeftSeconds() + 1;
            String actionBarMessageFormatted = formatColors(actionBarMessage.replace("%seconds%", String.valueOf(secondsLeft)));
            combatLog.getPlayers().forEach(player -> {
                ActionBarUtil.sendActionBar(player, actionBarMessageFormatted);
                player.setExp(Math.min(1.0f, Math.max(0.0f, secondsLeft / (float) combatLog.getDurationSeconds())));
            });
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player))
            return;

        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();


        startCombat(attacker, victim);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDroppedExp(0);
        getCombatLogs(event.getEntity()).forEach(this::endCombat);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Combat combatLog = getLastCombatLog(player);
        if (combatLog != null) {
            Player other = combatLog.getAttacker().equals(player) ? combatLog.getVictim() : combatLog.getAttacker();
            player.setHealth(0);
        }
        getCombatLogs(player).forEach(this::endCombat);
    }

    public void startCombat(Player attacker, Player victim) {
        CombatStartEvent combatStartEvent = new CombatStartEvent(attacker, victim);
        Bukkit.getServer().getPluginManager().callEvent(combatStartEvent);
        if (combatStartEvent.isCancelled()) {
            return;
        }
        Combat combatLog = getCombatLog(attacker, victim);
        if (combatLog == null) {
            if (getCombatLogs(attacker).isEmpty())
                attacker.sendMessage(formatColors(Main.getInstance().getConfig().getString("combat-tagger.damage-to-enemy", "&7You attacked &b%victim%&7. Do not log out.").replace("%victim%", victim.getName())));
            if (getCombatLogs(victim).isEmpty())
                victim.sendMessage(formatColors(Main.getInstance().getConfig().getString("combat-tagger.damage-from-enemy", "&7You got attacked by &b%attacker%&7. Do not log out.").replace("%attacker%", attacker.getName())));
            combatLogs.add(new Combat(attacker, victim, combatLogDurationInSeconds));
        } else
            combatLog.reset(combatLogDurationInSeconds);
    }

    public void endCombat(Combat combatLog) {
        Set<Player> players = combatLog.getPlayers();
        CombatEndEvent combatEndEvent = new CombatEndEvent(players);
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            Bukkit.getServer().getPluginManager().callEvent(combatEndEvent);
            if (combatEndEvent.isCancelled()) {
                return;
            }
            combatLogs.remove(combatLog);
            String messageType = Main.getInstance().getConfig().getString("combat-tagger.message-type", "action bar");
            String notInCombatMessage = Main.getInstance().getConfig().getString("combat-tagger.combat-end", "&7You are no longer in combat.");

            combatLog.getPlayers().forEach(player -> {
                player.setLevel(0);
                player.setExp(0);

                if (messageType.equalsIgnoreCase("action bar")) {
                    String actionBarMessage = formatColors(notInCombatMessage);
                    ActionBarUtil.sendActionBar(player, actionBarMessage);
                } else {
                    String chatMessage = formatColors(notInCombatMessage);
                    player.sendMessage(chatMessage);
                }

                Bukkit.getScheduler().runTaskLater(main, () -> {
                    if (!messageType.equalsIgnoreCase("action bar")) {
                        String chatMessage = formatColors(notInCombatMessage);
                        player.sendMessage(chatMessage);
                    }
                }, 3L);
            });
        });
    }

    public boolean isInCombat(Player player) {
        return !getCombatLogs(player).isEmpty();
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (isInCombat(player)) {
            String command = event.getMessage().split(" ")[0].substring(1);
            FileConfiguration config = Main.getInstance().getConfig();
            List<String> whitelistedCommands = config.getStringList("combat-tagger.whitelisted_commands");

            if (!whitelistedCommands.contains(command)) {
                String disableCommands = config.getString("disable-commands-message", "&7You cannot use commands while in combat!");
                player.sendMessage(formatColors(disableCommands));
                event.setCancelled(true);
            }
        }
    }

    public static List<Combat> getCombatLogs(Player player) {
        return combatLogs.stream().filter(c -> c.getPlayers().contains(player)).toList();
    }

    public Combat getLastCombatLog(Player player) {
        List<Combat> list = getCombatLogs(player);
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    public Combat getCombatLog(Player player1, Player player2) {
        return combatLogs.stream().filter(c -> c.getPlayers().contains(player1) && c.getPlayers().contains(player2)).findAny().orElse(null);
    }

    public void cancelTask() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
