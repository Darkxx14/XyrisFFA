package dev.darkxx.ffa.regeneration;

import com.fastasyncworldedit.core.FaweAPI;
import com.fastasyncworldedit.core.util.TaskManager;
import java.util.Objects;
import org.bukkit.scheduler.BukkitRunnable;

public class Runnable extends BukkitRunnable {
    private final RegenerationImpl arena;

    public Runnable(RegenerationImpl arena) {
        this.arena = arena;
    }

    public void run() {
        TaskManager var10000 = FaweAPI.getTaskManager();
        RegenerationImpl var10001 = this.arena;
        Objects.requireNonNull(var10001);
        Objects.requireNonNull(var10001);
        Objects.requireNonNull(var10001);
        var10000.async(var10001::paste);
    }
}