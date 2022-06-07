package com.autoplanter;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

final class AutoPlanterListener implements Listener {
    private final Plugin plugin;
    private final AutoPlanterItem autoPlanterItem;
    private final AutoPlanterBlock autoPlanterBlock;

    AutoPlanterListener(@NotNull Plugin plugin, @NotNull AutoPlanterItem autoPlanterItem, @NotNull AutoPlanterBlock autoPlanterBlock) {
        this.plugin = plugin;
        this.autoPlanterItem = autoPlanterItem;
        this.autoPlanterBlock = autoPlanterBlock;
    }

    void register() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        if (!autoPlanterItem.isAutoPlanter(event.getItemInHand())) return;

        autoPlanterBlock.addBlock(event.getBlockPlaced());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        if (!autoPlanterBlock.isAutoPlanter(event.getBlock())) return;

        autoPlanterBlock.removeBlock(event.getBlock());

        if (event.isDropItems() && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), autoPlanterItem.getItem());
        }

        event.setDropItems(false);
    }
}
