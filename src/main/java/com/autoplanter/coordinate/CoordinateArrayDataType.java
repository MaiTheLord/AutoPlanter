package com.autoplanter.coordinate;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CoordinateArrayDataType implements PersistentDataType<int[], Coordinate[]> {

    @Override
    public @NotNull Class<int[]> getPrimitiveType() {
        return int[].class;
    }

    @Override
    public @NotNull Class<Coordinate[]> getComplexType() {
        return Coordinate[].class;
    }

    @Override
    public int @NotNull [] toPrimitive(Coordinate @NotNull [] complex, @NotNull PersistentDataAdapterContext context) {
        return Arrays.stream(complex)
                .flatMapToInt(coordinate -> Arrays.stream(coordinate.toArray()))
                .toArray();
    }

    @Override
    public Coordinate @NotNull [] fromPrimitive(int @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        List<Coordinate> coordinates = new ArrayList<>();

        List<Integer> primitiveCoordinate = new ArrayList<>();

        for (int i : primitive) {
            primitiveCoordinate.add(i);

            if (primitiveCoordinate.size() == 3) {
                coordinates.add(Coordinate.fromList(primitiveCoordinate));
                primitiveCoordinate.clear();
            }
        }

        return coordinates.toArray(Coordinate[]::new);
    }
}
