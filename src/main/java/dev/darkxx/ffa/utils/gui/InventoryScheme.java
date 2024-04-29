package dev.darkxx.ffa.utils.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class InventoryScheme {

    private final List<String> masks = new ArrayList<>();
    private final Map<Character, ItemStack> items = new HashMap<>();
    private final Map<Character, Consumer<InventoryClickEvent>> handlers = new HashMap<>();

    public InventoryScheme mask(String mask) {
        Objects.requireNonNull(mask);
        this.masks.add(mask.length() > 9 ? mask.substring(0, 10) : mask);

        return this;
    }

    public InventoryScheme masks(String... masks) {
        for (String mask : Objects.requireNonNull(masks)) {
            mask(mask);
        }
        return this;
    }

    public InventoryScheme bindItem(char character, ItemStack item, Consumer<InventoryClickEvent> handler) {
        this.items.put(character, Objects.requireNonNull(item));

        if (handler != null) {
            this.handlers.put(character, handler);
        }
        return this;
    }

    public InventoryScheme bindItem(char character, ItemStack item) {
        return this.bindItem(character, item, null);
    }

    public InventoryScheme unbindItem(char character) {
        this.items.remove(character);
        this.handlers.remove(character);
        return this;
    }

    public void apply(GuiBuilder inv) {
        for (int line = 0; line < this.masks.size(); line++) {
            String mask = this.masks.get(line);

            for (int slot = 0; slot < mask.length(); slot++) {
                char c = mask.charAt(slot);
                ItemStack item = this.items.get(c);
                Consumer<InventoryClickEvent> handler = this.handlers.get(c);

                if (item != null) {
                    inv.setItem(9 * line + slot, item, handler);
                }
            }
        }
    }
}