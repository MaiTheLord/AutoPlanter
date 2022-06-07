package com.autoplanter;

import com.autoplanter.coordinate.Coordinate;
import com.autoplanter.coordinate.CoordinateArrayDataType;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

class AutoPlanterBlock {
    private final NamespacedKey arrayKey;

    AutoPlanterBlock(@NotNull Plugin plugin) {
        arrayKey = new NamespacedKey(plugin, "autoplanters");
    }

    public @NotNull List<Block> getBlocks() {
        return Bukkit.getWorlds().stream()
                .flatMap(world -> Arrays.stream(world.getLoadedChunks()))
                .flatMap(chunk -> Arrays.stream(getArray(chunk)).map(coordinate -> coordinate.toBlock(chunk))).toList();
    }

    public void addBlock(@NotNull Block block) {
        Coordinate[] originalArray = getArray(block.getChunk());

        Coordinate[] newArray = Arrays.copyOf(originalArray, originalArray.length + 1);

        newArray[newArray.length - 1] = Coordinate.chunkRelativeFromBlock(block);

        setArray(block.getChunk(), newArray);
    }

    public void removeBlock(@NotNull Block block) {
        setArray(block.getChunk(), Arrays.stream(getArray(block.getChunk()))
                .filter(coordinate -> !coordinate.equals(Coordinate.chunkRelativeFromBlock(block)))
                .toArray(Coordinate[]::new));
    }

    public boolean isAutoPlanter(@NotNull Block block) {
        return Arrays.stream(getArray(block.getChunk())).anyMatch(coordinate -> coordinate.equals(Coordinate.chunkRelativeFromBlock(block)));
    }

    private Coordinate @NotNull [] getArray(@NotNull Chunk chunk) {
        Coordinate[] coordinates = chunk.getPersistentDataContainer().get(arrayKey, new CoordinateArrayDataType());

        return coordinates == null ? new Coordinate[0] : coordinates;
    }

    private void setArray(@NotNull Chunk chunk, Coordinate @NotNull [] array) {
        chunk.getPersistentDataContainer().set(arrayKey, new CoordinateArrayDataType(), array);
    }
}
