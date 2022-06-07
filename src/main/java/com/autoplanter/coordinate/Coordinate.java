package com.autoplanter.coordinate;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Coordinate(int x, int y, int z) {
    public int @NotNull [] toArray() {
        return new int[] {x, y, z};
    }

    public @NotNull Block toBlock(@NotNull Chunk chunk) {
        return chunk.getBlock(x, y, z);
    }

    public static @NotNull Coordinate fromList(@NotNull List<Integer> list) {
        return new Coordinate(list.get(0), list.get(1), list.get(2));
    }

    public static @NotNull Coordinate chunkRelativeFromBlock(@NotNull Block block) {
        return new Coordinate(block.getX() & 0xF, block.getY(), block.getZ() & 0xF);
    }
}
