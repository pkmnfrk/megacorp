package com.mike_caron.megacorp.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.util.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public final class Quest
{
    private String id;
    private List<ItemStack> item;
    private String oreDict;
    private float baseQty;
    private float multQty = 1.5f;
    private float randomFactor;
    private float levelScale;
    private float baseProfit;
    private String completionCommand;
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

    private Quest()
    {

    }

    @Nullable
    public static Quest fromJson(@Nonnull JsonObject obj)
    {
        Quest q = new Quest();

        if(!q.loadFromJson(obj))
        {
            return null;
        }

        return q;
    }

    public boolean loadFromJson(@Nonnull JsonObject obj)
    {
        if(this.id == null)
        {
            this.id = obj.get("id").getAsString();
        }

        List<ItemStack> item = new ArrayList<>();
        String oreDict = null;
        String oreDictFallback = null;

        if(obj.has("item")){
            ItemStack is = ItemUtils.getStackFromTag(obj.get("item").getAsString());
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
                try
                {
                    ItemStack is = ItemUtils.getStackFromTag(i.getAsString());
                    item.add(is);
                }
                catch(RuntimeException ignore)
                {

                }
            }
        }

        if(item.isEmpty() && oreDict == null && this.item == null && this.oreDict == null)
        {
            throw new RuntimeException("Missing property 'item' or 'oredict' or 'items'");
        }

        if(oreDictFallback != null && !OreDictionary.doesOreNameExist(oreDict))
        {
            oreDict = null;
            item.add(ItemUtils.getStackFromTag(oreDictFallback));
        }

        if(oreDict == null)
        {
            this.oreDict = oreDict;
        }
        else if(!item.isEmpty())
        {
            this.item = item;
        }
        else
        {
            return false;
        }

        if(this.baseQty == 0 && !obj.has("multqty"))
        {
            throw new RuntimeException("Missing property 'baseqty'");
        }

        this.baseQty = obj.get("baseqty").getAsFloat();

        if(obj.has("multqty"))
        {
            this.multQty = obj.get("multqty").getAsFloat();
        }

        if(obj.has("rand"))
        {
            this.randomFactor = obj.get("rand").getAsFloat();
        }
        if(obj.has("levelscale"))
        {
            this.levelScale = obj.get("levelscale").getAsFloat();
        }
        if(obj.has("baseprofit"))
        {
            this.baseProfit = obj.get("baseprofit").getAsFloat();
        }
        if(obj.has("command"))
        {
            this.completionCommand = obj.get("command").getAsString();
        }

        return true;
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

    public String getId()
    {
        return id;
    }

    public List<ItemStack> getItem()
    {
        return item;
    }

    public String getOreDict()
    {
        return oreDict;
    }

    public float getBaseQty()
    {
        return baseQty;
    }

    public float getMultQty()
    {
        return multQty;
    }

    public float getRandomFactor()
    {
        return randomFactor;
    }

    public float getLevelScale()
    {
        return levelScale;
    }

    public float getBaseProfit()
    {
        return baseProfit;
    }

    public String getCompletionCommand()
    {
        return completionCommand;
    }
}
