package com.mike_caron.megacorp.util;

import javax.annotation.Nonnull;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DataUtils
{
    private DataUtils() {}

    @Nonnull
    public static Integer[] box(@Nonnull int[] array)
    {
        return IntStream.of(array).boxed().toArray(Integer[]::new);
    }

    @Nonnull
    public static int[] unbox(@Nonnull Integer[] array)
    {
        return Stream.of(array).mapToInt(Integer::intValue).toArray();
    }
}
