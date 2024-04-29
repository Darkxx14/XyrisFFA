package dev.darkxx.ffa.combat;

import com.google.common.collect.ImmutableSet;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class Combat {

    private final Player attacker;
    private final Player victim;
    private long endTime;
    private static int durationSeconds;

    public Combat(Player attacker, Player victim, int durationSeconds) {
        this.attacker = attacker;
        this.victim = victim;
        this.durationSeconds = durationSeconds;
        reset(durationSeconds);
    }

    public Player getAttacker() {
        return attacker;
    }

    public Player getVictim() {
        return victim;
    }

    public Set<Player> getPlayers() {
        return ImmutableSet.of(attacker, victim);
    }

    public boolean isExpired() {
        return getTimeLeftMillis() <= 0;
    }

    public long getTimeLeftMillis() {
        return Math.max(0, endTime - System.currentTimeMillis());
    }

    public int getTimeLeftSeconds() {
        return (int) Math.max(0, getTimeLeftMillis() / 1000);
    }

    public static int getDurationSeconds(){
        return durationSeconds;
    }

    public static int getPlayerCombatTimer(Player player, List<Combat> combatLogs) {
        for (Combat combatLog : combatLogs) {
            if (combatLog.getPlayers().contains(player)) {
                return combatLog.getTimeLeftSeconds();
            }
        }
        return 0;
    }

    public void reset(int durationSeconds) {
        endTime = System.currentTimeMillis() + (durationSeconds * 1000L);
    }

}