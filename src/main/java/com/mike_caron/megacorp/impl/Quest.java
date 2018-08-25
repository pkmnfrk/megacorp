package com.mike_caron.megacorp.impl;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.mike_caron.megacorp.MegaCorpMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class Quest
{
    public final String id;
    public final ItemStack item;
    public final float baseQty;
    public final float multQty;
    public final float randomFactor;
    public final float levelScale;


    public Quest(String id, ItemStack item, float baseQty, float multQty, float randomFactor, float levelScale)
    {
        this.id = id;
        this.item = item;
        this.baseQty = baseQty;
        this.multQty = multQty;
        this.randomFactor = randomFactor;
        this.levelScale = levelScale;
    }

    public static Quest fromJson(JsonObject obj)
    {
        String id = obj.get("id").getAsString();
        ItemStack item = getStackFromTag(obj.get("item").getAsString());
        float baseQty = obj.get("baseqty").getAsFloat();
        float multQty = obj.get("multqty").getAsFloat();
        float randomFactor = 0;
        float levelScale = 1;
        if(obj.has("rand"))
        {
            randomFactor = obj.get("rand").getAsFloat();
        }
        if(obj.has("levelscale"))
        {
            levelScale = obj.get("levelscale").getAsFloat();
        }

        return new Quest(id, item, baseQty, multQty, randomFactor, levelScale);
    }

    private static ItemStack getStackFromTag(String tag)
    {
        try
        {
            String[] parts = tag.split(":");
            if (parts.length == 1)
            {
                //assume minecraft:item:0
                Item item = Item.getByNameOrId(parts[0]);
                Preconditions.checkNotNull(item);
                return new ItemStack(item, 1);
            }
            else if (parts.length == 2)
            {
                // this can either be mod:item:0 or minecraft:item:meta

                Item item = Item.getByNameOrId(tag);

                if (item != null)
                {
                    return new ItemStack(item, 1);
                }

                // try minecraft:item:meta
                int meta = Integer.parseInt(parts[1]);
                item = Item.getByNameOrId("minecraft:" + parts[0]);

                Preconditions.checkNotNull(item);

                return new ItemStack(item, 1, meta);

            }
            else if (parts.length == 3)
            {
                //this has to be mod:item:meta
                int meta = Integer.parseInt(parts[2]);
                Item item = Item.getByNameOrId(parts[0] + ":" + parts[1]);

                Preconditions.checkNotNull(item, "Can't locate the item " + tag);
                return new ItemStack(item, 1, meta);

            }
        }
        catch (NullPointerException ex)
        {
            //handled below
        }
        catch (NumberFormatException ex)
        {
            throw new RuntimeException("Can't locate the item " + tag, ex);
        }

        throw new RuntimeException("I don't understand the item " + tag);
    }

    public ItemStack getItemForLevel(int level)
    {
        ItemStack ret = item.copy();

        double qty = baseQty * Math.pow(multQty, level * levelScale);

        if(randomFactor > 0)
        {
            Random rng = new Random();
            float low = 1 - randomFactor;
            float high = 1 + randomFactor;
            float fin = (high - low) * rng.nextFloat() + low;

            MegaCorpMod.logger.warn("RNG: " + low + " .. " + fin + " .. " + high);

            qty *= fin;
        }

        ret.setCount((int)qty);

        return ret;
    }
}
