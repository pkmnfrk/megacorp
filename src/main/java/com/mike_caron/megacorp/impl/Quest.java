package com.mike_caron.megacorp.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.util.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.*;

public final class Quest
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
            ItemStack is = ItemUtils.getStackFromTag(obj.get("item").getAsString());
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
                ItemStack is = ItemUtils.getStackFromTag(i.getAsString());
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
            item.add(ItemUtils.getStackFromTag(oreDictFallback));
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

    public void loadOverrides(@Nonnull JsonObject json)
    {

    }
}
