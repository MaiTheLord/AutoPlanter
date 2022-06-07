package com.autoplanter;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

final class AutoPlanterRecipe implements Listener {
    private final Plugin plugin;

    private final NamespacedKey key;
    private final ItemStack result;

    AutoPlanterRecipe(@NotNull Plugin plugin, @NotNull AutoPlanterItem autoPlanterItem) {
        this.plugin = plugin;

        this.key = new NamespacedKey(plugin, "autoplanter");
        this.result = autoPlanterItem.getItem();
    }

    void register() {
        Bukkit.getServer().addRecipe(new ShapedRecipe(key, result)
                .shape(
                        "I I",
                        "IWI",
                        " R ")
                .setIngredient('I', Material.IRON_INGOT)
                .setIngredient('W', Material.WATER_BUCKET)
                .setIngredient('R', Material.REDSTONE)
        );

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    void addToPlayer(@NotNull Player player) {
        player.discoverRecipe(key);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player && event.getItem().getItemStack().getType() == Material.IRON_INGOT) {
            addToPlayer(player);
        }
    }
}
