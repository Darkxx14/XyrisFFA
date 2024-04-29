package dev.darkxx.ffa.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides a builder pattern for creating ItemStacks with specific attributes.
 */
public class ItemBuilder {

    private Material material;
    private int amount = 1;
    private short data = -1;
    private String name;
    private List<String> lore;
    private Map<Enchantment, Integer> enchantments;
    private ItemFlag[] itemflags;
    private Integer customModelData;

    /**
     * Constructs a new ItemBuilder instance.
     */
    public ItemBuilder() {
    }

    /**
     * Constructs a new ItemBuilder instance with the specified material.
     *
     * @param material the material of the item
     */
    public ItemBuilder(Material material) {
        this.material = material;
    }

    /**
     * Sets the material of the item.
     *
     * @param material the material of the item
     * @return the ItemBuilder instance
     */
    public ItemBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    /**
     * Sets the amount of the item.
     *
     * @param amount the amount of the item
     * @return the ItemBuilder instance
     */
    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    /**
     * Sets the data value of the item.
     *
     * @param data the data value of the item
     * @return the ItemBuilder instance
     */
    public ItemBuilder setData(short data) {
        this.data = data;
        return this;
    }

    /**
     * Sets the display name of the item.
     *
     * @param name the display name of the item
     * @return the ItemBuilder instance
     */
    public ItemBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the lore of the item.
     *
     * @param lore the lore of the item
     * @return the ItemBuilder instance
     */
    public ItemBuilder setLore(String... lore) {
        this.lore = Arrays.asList(lore);
        return this;
    }

    /**
     * Adds an enchantment to the item.
     *
     * @param enchantment the enchantment to add
     * @param level       the level of the enchantment
     * @return the ItemBuilder instance
     */
    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        if (enchantments == null)
            enchantments = new HashMap<>();
        enchantments.put(enchantment, level);
        return this;
    }

    /**
     * Sets the item flags of the item.
     *
     * @param flags the item flags to set
     * @return the ItemBuilder instance
     */
    public ItemBuilder setFlags(ItemFlag... flags) {
        itemflags = flags;
        return this;
    }

    /**
     * Sets the custom model data of the item.
     *
     * @param customModelData the custom model data of the item
     * @return the ItemBuilder instance
     */
    public ItemBuilder setCustomModelData(Integer customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    /**
     * Sets minimal attributes for the item (empty name, lore, and all item flags).
     *
     * @return the ItemBuilder instance
     */
    public ItemBuilder minimal() {
        setName("");
        setLore("");
        setFlags(ItemFlag.values());
        return this;
    }

    /**
     * Builds the ItemStack with the specified attributes.
     *
     * @return the constructed ItemStack
     */
    public ItemStack build() {
        ItemStack is = new ItemStack(material, amount);
        if (data != -1)
            is.setDurability(data);
        ItemMeta meta = is.getItemMeta();
        if (name != null)
            meta.setDisplayName(name);
        if (lore != null)
            meta.setLore(lore);
        if (enchantments != null)
            enchantments.forEach((enchantment, level) -> meta.addEnchant(enchantment, level, true));
        if (itemflags != null)
            meta.addItemFlags(itemflags);
        if (customModelData != null)
            meta.setCustomModelData(customModelData);
        is.setItemMeta(meta);
        return is;
    }
}
