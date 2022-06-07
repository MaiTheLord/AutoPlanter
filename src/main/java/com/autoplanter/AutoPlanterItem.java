package com.autoplanter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

final class AutoPlanterItem {
    private final NamespacedKey key;

    AutoPlanterItem(@NotNull Plugin plugin) {
        key = new NamespacedKey(plugin, "autoplanter");
    }

    public boolean isAutoPlanter(@NotNull ItemStack itemStack) {
        return Objects.requireNonNull(itemStack.getItemMeta()).getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }

    public @NotNull ItemStack getItem() {
        ItemStack itemStack = new ItemStack(Material.HOPPER);

        ItemMeta autoPlanterMeta = Objects.requireNonNull(itemStack.getItemMeta());

        autoPlanterMeta.setDisplayName(ChatColor.RESET + "Auto Planter");

        autoPlanterMeta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

        itemStack.setItemMeta(autoPlanterMeta);

        return itemStack;
    }
}
