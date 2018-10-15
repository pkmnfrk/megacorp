package com.mike_caron.megacorp.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.math.Fraction;

import javax.annotation.Nonnull;
import java.util.Map;
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
    public static Float[] box(@Nonnull float[] array)
    {
        Float[] ret = new Float[array.length];
        for(int i = 0; i < array.length; i++)
        {
            ret[i] = array[i];
        }
        return ret;
    }

    @Nonnull
    public static int[] unbox(@Nonnull Integer[] array)
    {
        return Stream.of(array).mapToInt(Integer::intValue).toArray();
    }

    @Nonnull
    public static float[] unbox(@Nonnull Float[] array)
    {
        float[] ret = new float[array.length];
        for(int i = 0; i < array.length; i++)
        {
            ret[i] = array[i];
        }
        return ret;
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

    @Nonnull
    public static JsonElement toJson(@Nonnull NBTBase nbt)
    {
        if(nbt instanceof NBTTagCompound)
        {
            JsonObject ret = new JsonObject();

            for(String key : ((NBTTagCompound) nbt).getKeySet())
            {
                ret.add(key, toJson(((NBTTagCompound) nbt).getTag(key)));
            }

            return ret;
        }
        else if(nbt instanceof NBTTagList)
        {
            JsonArray ret = new JsonArray();

            for(int i = 0; i < ((NBTTagList)nbt).tagCount(); i++)
            {
                ret.add(toJson(((NBTTagList) nbt).get(i)));
            }

            return ret;
        }
        else if (nbt instanceof NBTTagInt)
        {
            return new JsonPrimitive(((NBTTagInt) nbt).getInt());
        }
        else if (nbt instanceof NBTTagShort)
        {
            return new JsonPrimitive(((NBTTagShort) nbt).getInt());
        }
        else if (nbt instanceof NBTTagByte)
        {
            return new JsonPrimitive(((NBTTagByte) nbt).getInt());
        }
        else if (nbt instanceof NBTTagDouble)
        {
            return new JsonPrimitive(((NBTTagDouble) nbt).getDouble());
        }
        else if (nbt instanceof NBTTagFloat)
        {
            return new JsonPrimitive(((NBTTagFloat) nbt).getFloat());
        }
        else if(nbt instanceof NBTTagString)
        {
            return new JsonPrimitive(((NBTTagString) nbt).getString());
        }
        else
        {
            throw new RuntimeException("I don't know how to convert NBT " + nbt.toString() + " to Json");
        }
    }

    public static NBTBase toNBT(JsonElement el)
    {
        if(el == null || el.isJsonNull())
        {
            throw new RuntimeException("NBT Tags may not be null");
        }

        if(el.isJsonPrimitive())
        {
            JsonPrimitive prim = el.getAsJsonPrimitive();

            if(prim.isString())
            {
                return new NBTTagString(prim.getAsString());
            }
            if(prim.isNumber())
            {
                if(prim.getAsDouble() != Math.floor(prim.getAsDouble()))
                {
                    return new NBTTagDouble(prim.getAsDouble());
                }
                else
                {
                    return new NBTTagInt(prim.getAsInt());
                }
            }
            if(prim.isBoolean())
            {
                return new NBTTagByte((byte)(prim.getAsBoolean() ? 1 : 0));
            }

            throw new RuntimeException("Unknown type of Json primitive");
        }
        if(el.isJsonObject())
        {
            NBTTagCompound ret = new NBTTagCompound();

            for(Map.Entry<String, JsonElement> kvp : el.getAsJsonObject().entrySet())
            {
                ret.setTag(kvp.getKey(), toNBT(kvp.getValue()));
            }

            return ret;
        }
        if(el.isJsonArray())
        {
            NBTTagList ret = new NBTTagList();

            el.getAsJsonArray().forEach(itm -> ret.appendTag(toNBT(itm)));

            return ret;
        }

        throw new RuntimeException("Unknown type of Json");
    }

    public static NBTTagCompound toNBT(JsonObject el)
    {
        return (NBTTagCompound)toNBT((JsonElement) el);
    }

    public static NBTTagList toNBT(JsonArray el)
    {
        return (NBTTagList)toNBT((JsonElement) el);
    }


    @Nonnull
    public static JsonObject toJson(@Nonnull ItemStack stack)
    {
        NBTTagCompound tag = new NBTTagCompound();
        stack.writeToNBT(tag);

        JsonObject ret = new JsonObject();

        ret.addProperty("item", tag.getString("id"));
        ret.addProperty("qty", tag.getByte("Count"));
        ret.addProperty("meta", tag.getShort("Damage"));

        if(tag.hasKey("tag"))
        {
            ret.add("tag", toJson(tag.getTag("tag")));
        }

        if(tag.hasKey("ForgeCaps"))
        {
            ret.add("forge", toJson(tag.getTag("ForgeCaps")));
        }

        return ret;
    }

    public static ItemStack toItemStack(@Nonnull JsonObject obj)
    {
        Item item = Item.REGISTRY.getObject(new ResourceLocation(obj.get("item").getAsString()));

        if(item == null) return ItemStack.EMPTY;

        ItemStack ret = new ItemStack(item, 1, 0, null);
        if(obj.has("qty"))
        {
            ret.setCount(obj.get("qty").getAsInt());
        }
        if(obj.has("meta"))
        {
            ret.setItemDamage(obj.get("meta").getAsInt());
        }
        if(obj.has("tag"))
        {
            ret.setTagCompound(toNBT(obj.get("tag").getAsJsonObject()));
        }

        return ret;
    }
}
