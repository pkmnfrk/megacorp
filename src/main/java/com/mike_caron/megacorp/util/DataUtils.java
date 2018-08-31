package com.mike_caron.megacorp.util;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.math.Fraction;

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

    public static NBTBase toNBT(Fraction fraction)
    {
        NBTTagCompound ret = new NBTTagCompound();
        ret.setInteger("N", fraction.getNumerator());
        ret.setInteger("D", fraction.getDenominator());
        return ret;
    }

    public static Fraction fraction(NBTBase tag)
    {
        NBTTagCompound data = (NBTTagCompound) tag;
        return Fraction.getFraction(data.getInteger("N"), data.getInteger("D"));
    }

    public static boolean areEqual(Fraction a, Fraction b)
    {
        if(a == null && b == null) return true;
        if((a == null) != (b == null)) return false;
        return a.equals(b);
    }
}
