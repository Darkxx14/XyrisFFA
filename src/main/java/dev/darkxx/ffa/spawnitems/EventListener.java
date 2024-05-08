package dev.darkxx.ffa.spawnitems;

import dev.darkxx.ffa.Main;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class EventListener implements Listener {
    private Main main;

    public EventListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) { return;
        }

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) { return;
        }

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        if (!meta.getPersistentDataContainer().has(new NamespacedKey(main, "right-click-command"), PersistentDataType.STRING)) { return;
        }

        String command = meta.getPersistentDataContainer().get(new NamespacedKey(main, "right-click-command"), PersistentDataType.STRING);
        assert command != null;
        Bukkit.dispatchCommand(event.getPlayer(), command);
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            if (meta.getPersistentDataContainer().has(new NamespacedKey(main, "right-click-command"), PersistentDataType.STRING)) {
                event.setCancelled(true);
            }
        }
    }
}
