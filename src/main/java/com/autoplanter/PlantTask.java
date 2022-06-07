package com.autoplanter;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

final class PlantTask extends BukkitRunnable {
    private static final double RANGE = 4.4;
    private static final int INITIAL_DELAY_TICKS = 64;
    private static final int COOLDOWN_TICKS = 18;
    private static final String COOLDOWN_KEY = "cooldown";

    private final Plugin plugin;
    private final AutoPlanterBlock autoPlanterBlock;

    PlantTask(@NotNull Plugin plugin, @NotNull AutoPlanterBlock autoPlanterBlock) {
        this.plugin = plugin;
        this.autoPlanterBlock = autoPlanterBlock;
    }

    void begin() {
        this.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public void run() {
        autoPlanterBlock.getBlocks().forEach(planter -> {
            List<Block> targets = getTargetsSorted(planter);

            if (!handleCooldown(planter, targets.isEmpty())) return;

            ItemStack[] contents = ((Hopper) planter.getState()).getInventory().getContents();

            Block target = targets.stream().filter(block -> getSupportedTargetTypes(contents).contains(block.getType())).findFirst().orElse(null);

            plant(target, contents);
        });
    }

    private @NotNull List<Block> getTargetsSorted(@NotNull Block planter) {
        Map<Block, Double> targetsWithDistance = new HashMap<>();

        int flooredRange = (int) Math.floor(RANGE);

        for (int x = -flooredRange; x <= flooredRange; x++) {
            for (int z = -flooredRange; z <= flooredRange; z++) {
                double distance = Math.sqrt(x * x + z * z);
                if (distance > RANGE) continue;

                Block target = planter.getRelative(x, -1, z);

                final List<Material> supportedGrounds = List.of(Material.FARMLAND, Material.SOUL_SAND);
                if (!supportedGrounds.contains(target.getType())) continue;

                if (target.getRelative(0, 1, 0).getType() != Material.AIR) continue;

                targetsWithDistance.put(target, distance);
            }
        }

        return targetsWithDistance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * @param shouldHold pass true if the planter doesn't have any empty blocks to plant on
     * @return true if the planter is ready to plant
     */
    private boolean handleCooldown(@NotNull Block planter, boolean shouldHold) {
        List<MetadataValue> cooldownMetadata = planter.getMetadata(COOLDOWN_KEY);

        if (shouldHold) {
            planter.setMetadata(COOLDOWN_KEY, new LazyMetadataValue(plugin, () -> 0));

        } else if (cooldownMetadata.isEmpty() || cooldownMetadata.get(0).asInt() == 0) {
            planter.setMetadata(COOLDOWN_KEY, new LazyMetadataValue(plugin, () -> INITIAL_DELAY_TICKS));

        } else if (cooldownMetadata.get(0).asInt() > 1) {
            planter.setMetadata(COOLDOWN_KEY, new LazyMetadataValue(plugin, () -> cooldownMetadata.get(0).asInt() - 1));

        } else {
            planter.setMetadata(COOLDOWN_KEY, new LazyMetadataValue(plugin, () -> COOLDOWN_TICKS));
            return true;
        }

        return false;
    }

    private @NotNull Set<Material> getSupportedTargetTypes(ItemStack @NotNull [] contents) {
        Set<Material> result = new HashSet<>();

        final Set<Material> farmlandPlantables = EnumSet.of(Material.WHEAT_SEEDS, Material.BEETROOT_SEEDS, Material.POTATO, Material.CARROT);

        for (ItemStack stack : contents) {
            if (stack == null) continue;

            if (farmlandPlantables.contains(stack.getType())) result.add(Material.FARMLAND);
            if (stack.getType() == Material.NETHER_WART) result.add(Material.SOUL_SAND);
        }

        return result;
    }

    private void plant(@Nullable Block target, ItemStack @NotNull [] planterContents) {
        if (target == null) return;

        Block air = target.getRelative(0, 1, 0);

        for (ItemStack stack : planterContents) {
            if (stack == null) continue;

            if (target.getType() == Material.FARMLAND) {
                switch (stack.getType()) {
                    case WHEAT_SEEDS -> air.setType(Material.WHEAT);
                    case BEETROOT_SEEDS -> air.setType(Material.BEETROOTS);
                    case POTATO -> air.setType(Material.POTATOES);
                    case CARROT -> air.setType(Material.CARROTS);
                    default -> { continue; }
                }
            } else if (target.getType() == Material.SOUL_SAND) {
                if (stack.getType() == Material.NETHER_WART) {
                    air.setType(Material.NETHER_WART);
                } else continue;
            }

            stack.setAmount(stack.getAmount() - 1);
            break;
        }
    }
}
