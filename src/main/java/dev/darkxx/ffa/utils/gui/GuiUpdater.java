package dev.darkxx.ffa.utils.gui;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiUpdater {

    private BukkitTask task = null;

    private long refreshInterval = 10;
    private final Map<Integer, Supplier<ItemStack>> suppliers = new HashMap<>();
    private final Map<Integer, Consumer<InventoryClickEvent>> handlers = new HashMap<>();

    public GuiUpdater refreshInterval(long interval) {
        this.refreshInterval = interval;
        return this;
    }

    public GuiUpdater updateItem(Integer slot, Supplier<ItemStack> supplier, Consumer<InventoryClickEvent> handler) {
        this.suppliers.put(slot, supplier);
        this.handlers.put(slot, handler);
        return this;
    }

    public GuiUpdater updateItem(Integer slot, Supplier<ItemStack> supplier) {
        return updateItem(slot, supplier, null);
    }

    public GuiUpdater updateItems(Integer from, Integer to, Supplier<ItemStack> supplier, Consumer<InventoryClickEvent> handler) {
        for(int i = from; i <= to; i++) {
            updateItem(i, supplier, handler);
        }
        return this;
    }

    public void startUpdating(GuiBuilder guiBuilder) {

        stopUpdating();

        for(Map.Entry<Integer, Supplier<ItemStack>> entry : suppliers.entrySet()) {
            guiBuilder.setItem(entry.getKey(), entry.getValue().get(), handlers.getOrDefault(entry.getKey(), null));
        }

        this.task = Bukkit.getScheduler().runTaskTimer(
                JavaPlugin.getProvidingPlugin(GuiUpdater.class),
                () -> {

                    for(Map.Entry<Integer, Supplier<ItemStack>> entry : suppliers.entrySet()) {
                        guiBuilder.getInventory().setItem(entry.getKey(), entry.getValue().get());
                    }

                },
                refreshInterval, // delay
                refreshInterval // period
        );
    }

    public void stopUpdating() {
        if(task != null) {
            task.cancel();
            task = null;
        }
    }
}