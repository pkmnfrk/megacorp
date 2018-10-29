package com.mike_caron.megacorp.impl;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mike_caron.megacorp.MegaCorpMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;

public class Quest
{
    public final String id;
    private final List<ItemStack> item;
    private final String oreDict;
    public final float baseQty;
    public final float multQty;
    public final float randomFactor;
    public final float levelScale;
    public final float baseProfit;
    public final String completionCommand;
    public final Map<String, Object> extraData = new HashMap<>();

    public Quest(String id, ItemStack item, float baseQty, float multQty, float randomFactor, float levelScale, float baseProfit)
    {
        this(id, item, baseQty, multQty, randomFactor, levelScale, baseProfit, null);
    }

    public Quest(String id, ItemStack item, float baseQty, float multQty, float randomFactor, float levelScale, float baseProfit, String completionCommand)
    {
        this.id = id;
        this.item = new ArrayList<>();
        this.item.add(item);
        this.oreDict = null;
        this.baseQty = baseQty;
        this.multQty = multQty;
        this.randomFactor = randomFactor;
        this.levelScale = levelScale;
        this.baseProfit = baseProfit;
        this.completionCommand = completionCommand;
    }

    public Quest(String id, Collection<ItemStack> item, float baseQty, float multQty, float randomFactor, float levelScale, float baseProfit)
    {
        this(id, item, baseQty, multQty, randomFactor, levelScale, baseProfit, null);
    }

    public Quest(String id, Collection<ItemStack> item, float baseQty, float multQty, float randomFactor, float levelScale, float baseProfit, String completionCommand)
    {
        this.id = id;
        this.item = new ArrayList<>(item);
        this.oreDict = null;
        this.baseQty = baseQty;
        this.multQty = multQty;
        this.randomFactor = randomFactor;
        this.levelScale = levelScale;
        this.baseProfit = baseProfit;
        this.completionCommand = completionCommand;
    }

    public Quest(String id, String oreDict, float baseQty, float multQty, float randomFactor, float levelScale, float baseProfit)
    {
        this(id, oreDict, baseQty, multQty, randomFactor, levelScale, baseProfit, null);
    }

    public Quest(String id, String oreDict, float baseQty, float multQty, float randomFactor, float levelScale, float baseProfit, String completionCommand)
    {
        this.id = id;
        this.item = null;
        this.oreDict = oreDict;
        this.baseQty = baseQty;
        this.multQty = multQty;
        this.randomFactor = randomFactor;
        this.levelScale = levelScale;
        this.baseProfit = baseProfit;
        this.completionCommand = completionCommand;
    }

    public static Quest fromJson(JsonObject obj)
    {
        String id = obj.get("id").getAsString();
        List<ItemStack> item = new ArrayList<>();
        String oreDict = null;
        String oreDictFallback = null;

        if(obj.has("item")){
            ItemStack is = getStackFromTag(obj.get("item").getAsString());
            if(is == null)
                return null;
            item.add(is);
        }
        else if(obj.has("oredict"))
        {
            oreDict = obj.get("oredict").getAsString();
            if(obj.has("fallback"))
            {
                oreDictFallback = obj.get("fallback").getAsString();
            }
        }
        else if(obj.has("items"))
        {
            for(JsonElement i : obj.get("items").getAsJsonArray())
            {
                ItemStack is = getStackFromTag(i.getAsString());
                if(is == null)
                    continue;
                item.add(is);
            }
        }

        float baseQty = obj.get("baseqty").getAsFloat();
        float multQty = 1.5f;
        if(obj.has("multqty"))
        {
            obj.get("multqty").getAsFloat();
        }

        float randomFactor = 0;
        float levelScale = 1;
        float baseProfit = 1;
        String completionCommand = null;

        if(obj.has("rand"))
        {
            randomFactor = obj.get("rand").getAsFloat();
        }
        if(obj.has("levelscale"))
        {
            levelScale = obj.get("levelscale").getAsFloat();
        }
        if(obj.has("baseprofit"))
        {
            baseProfit = obj.get("baseprofit").getAsFloat();
        }
        if(obj.has("command"))
        {
            completionCommand = obj.get("command").getAsString();
        }

        if(oreDictFallback != null && !OreDictionary.doesOreNameExist(oreDict))
        {
            oreDict = null;
            item.add(getStackFromTag(oreDictFallback));
        }

        if(!item.isEmpty())
        {
            return new Quest(id, item, baseQty, multQty, randomFactor, levelScale, baseProfit, completionCommand);
        }
        else
        {
            return new Quest(id, oreDict, baseQty, multQty, randomFactor, levelScale, baseProfit, completionCommand);
        }
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

        return null;
        //throw new RuntimeException("I don't understand the item " + tag);
    }

    public int getCountForLevel(int level)
    {
        double qty = baseQty * Math.pow(multQty, level * levelScale);

        if(randomFactor > 0)
        {
            Random rng = new Random();
            float low = 1 - randomFactor;
            float high = 1 + randomFactor;
            float fin = (high - low) * rng.nextFloat() + low;

            if(fin < low || fin > high)
            {
                MegaCorpMod.logger.warn("RNG out of range: " + low + " .. " + fin + " .. " + high);
            }

            qty *= fin;
        }

        return (int)Math.max(1, Math.floor(qty));
    }

    public int getProfit(int totalQty, int level)
    {
        float actualProfit = totalQty * baseProfit * (float)Math.pow(1.1, level);

        return (int)actualProfit;
    }

    public NonNullList<ItemStack> possibleItems()
    {
        if(this.oreDict != null)
            return OreDictionary.getOres(this.oreDict);

        NonNullList<ItemStack> ret = NonNullList.create();
        ret.addAll(this.item);
        return ret;
    }
}
