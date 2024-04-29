package dev.darkxx.ffa.utils.gui;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ItemBuilderGUI {

    private final ItemStack item;

    public static ItemBuilderGUI copyOf(ItemStack item) {
        return new ItemBuilderGUI(item.clone());
    }

    public ItemBuilderGUI(Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilderGUI(ItemStack item) {
        this.item = Objects.requireNonNull(item, "item");
    }

    public ItemBuilderGUI edit(Consumer<ItemStack> function) {
        function.accept(this.item);
        return this;
    }

    public ItemBuilderGUI meta(Consumer<ItemMeta> metaConsumer) {
        return edit(item -> {
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                metaConsumer.accept(meta);
                item.setItemMeta(meta);
            }
        });
    }

    public <T extends ItemMeta> ItemBuilderGUI meta(Class<T> metaClass, Consumer<T> metaConsumer) {
        return meta(meta -> {
            if (metaClass.isInstance(meta)) {
                metaConsumer.accept(metaClass.cast(meta));
            }
        });
    }

    public ItemBuilderGUI type(Material material) {
        return edit(item -> item.setType(material));
    }

    public ItemBuilderGUI data(int data) {
        return durability((short) data);
    }

    @SuppressWarnings("deprecation")
    public ItemBuilderGUI durability(short durability) {
        return edit(item -> item.setDurability(durability));
    }

    public ItemBuilderGUI amount(int amount) {
        return edit(item -> item.setAmount(amount));
    }

    public ItemBuilderGUI enchant(Enchantment enchantment) {
        return enchant(enchantment, 1);
    }

    public ItemBuilderGUI enchant(Enchantment enchantment, int level) {
        return meta(meta -> meta.addEnchant(enchantment, level, true));
    }

    public ItemBuilderGUI removeEnchant(Enchantment enchantment) {
        return meta(meta -> meta.removeEnchant(enchantment));
    }

    public ItemBuilderGUI removeEnchants() {
        return meta(m -> m.getEnchants().keySet().forEach(m::removeEnchant));
    }

    public ItemBuilderGUI name(String name) {
        return meta(meta -> meta.setDisplayName(name));
    }

    public ItemBuilderGUI lore(String lore) {
        return lore(Collections.singletonList(lore));
    }

    public ItemBuilderGUI lore(String... lore) {
        return lore(Arrays.asList(lore));
    }

    public ItemBuilderGUI lore(List<String> lore) {
        return meta(meta -> meta.setLore(lore));
    }

    public ItemBuilderGUI addLore(String line) {
        return meta(meta -> {
            List<String> lore = meta.getLore();

            if (lore == null) {
                meta.setLore(Collections.singletonList(line));
                return;
            }

            lore.add(line);
            meta.setLore(lore);
        });
    }

    public ItemBuilderGUI addLore(String... lines) {
        return addLore(Arrays.asList(lines));
    }

    public ItemBuilderGUI addLore(List<String> lines) {
        return meta(meta -> {
            List<String> lore = meta.getLore();

            if (lore == null) {
                meta.setLore(lines);
                return;
            }

            lore.addAll(lines);
            meta.setLore(lore);
        });
    }

    public ItemBuilderGUI flags(ItemFlag... flags) {
        return meta(meta -> meta.addItemFlags(flags));
    }

    public ItemBuilderGUI flags() {
        return flags(ItemFlag.values());
    }

    public ItemBuilderGUI removeFlags(ItemFlag... flags) {
        return meta(meta -> meta.removeItemFlags(flags));
    }

    public ItemBuilderGUI removeFlags() {
        return removeFlags(ItemFlag.values());
    }

    public ItemBuilderGUI armorColor(Color color) {
        return meta(LeatherArmorMeta.class, meta -> meta.setColor(color));
    }

    public ItemStack build() {
        return this.item;
    }
}
