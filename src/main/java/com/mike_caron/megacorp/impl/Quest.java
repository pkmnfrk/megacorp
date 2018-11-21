package com.mike_caron.megacorp.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.util.DataUtils;
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
    private float randomFactor = 0.25f;
    private float levelScale = 1f;
    private float baseProfit = 1f;
    private String completionCommand;
    private String[][] gameStages = null;
    private String langKey = null;

    public final Map<String, Object> extraData = new HashMap<>();

    public Quest(String id, ItemStack item, float baseQty, float multQty, float randomFactor, float levelScale, float baseProfit)
    {
        this(id, null, item, baseQty, multQty, randomFactor, levelScale, baseProfit, null);
    }

    public Quest(@Nonnull String id, @Nullable String langKey, @Nonnull ItemStack item, float baseQty, float multQty, float randomFactor, float levelScale, float baseProfit, @Nullable String completionCommand)
    {
        this.id = id;
        this.langKey = null;
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

    public Quest(@Nonnull String id, @Nonnull Collection<ItemStack> item, float baseQty, float multQty, float randomFactor, float levelScale, float baseProfit)
    {
        this(id, null, item, baseQty, multQty, randomFactor, levelScale, baseProfit, null);
    }

    public Quest(@Nonnull String id, @Nullable String langKey, @Nonnull Collection<ItemStack> item, float baseQty, float multQty, float randomFactor, float levelScale, float baseProfit, @Nonnull String completionCommand)
    {
        this.id = id;
        this.langKey = langKey;
        this.item = new ArrayList<>(item);
        this.oreDict = null;
        this.baseQty = baseQty;
        this.multQty = multQty;
        this.randomFactor = randomFactor;
        this.levelScale = levelScale;
        this.baseProfit = baseProfit;
        this.completionCommand = completionCommand;
    }

    public Quest(@Nonnull String id, @Nonnull String oreDict, float baseQty, float multQty, float randomFactor, float levelScale, float baseProfit)
    {
        this(id, null, oreDict, baseQty, multQty, randomFactor, levelScale, baseProfit, null);
    }

    public Quest(@Nonnull String id, @Nullable String langKey, @Nonnull String oreDict, float baseQty, float multQty, float randomFactor, float levelScale, float baseProfit, @Nonnull String completionCommand)
    {
        this.id = id;
        this.langKey = langKey;
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

        String oreDictFallback = null;

        if(obj.has("item")){
            try
            {
                ItemStack is = ItemUtils.getStackFromTag(obj.get("item").getAsString());

                this.oreDict = null;
                this.item = new ArrayList<>();
                this.item.add(is);
            }
            catch(RuntimeException ex)
            {
                return false;
            }
        }
        else if(obj.has("oredict"))
        {
            this.oreDict = obj.get("oredict").getAsString();
            this.item = null;
            if(obj.has("fallback"))
            {
                oreDictFallback = obj.get("fallback").getAsString();
            }
        }
        else if(obj.has("items"))
        {
            this.oreDict = null;
            //this.item.clear();
            List<ItemStack> oldItems = this.item;
            this.item = new ArrayList<>();

            for(JsonElement i : obj.get("items").getAsJsonArray())
            {
                try
                {
                    ItemStack is = ItemUtils.getStackFromTag(i.getAsString());
                    this.item.add(is);
                }
                catch(RuntimeException ignore)
                {

                }
            }

            if(this.item.isEmpty())
            {
                this.item = oldItems;
                return false;
            }
        }
        else if(this.item == null && this.oreDict == null)
        {
            throw new RuntimeException("Missing property 'item' or 'oredict' or 'items'");
        }

        if(oreDictFallback != null && !OreDictionary.doesOreNameExist(oreDict))
        {
            this.oreDict = null;
            this.item = new ArrayList<>();
            this.item.add(ItemUtils.getStackFromTag(oreDictFallback));
        }

        if(this.baseQty == 0 && !obj.has("baseqty"))
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
        if(obj.has("game_stages"))
        {
            this.gameStages = DataUtils.loadJsonNestedArray(obj.get("game_stages"));
        }
        if(obj.has("langkey"))
        {
            this.langKey = obj.get("langkey").getAsString();
        }


        return true;
    }

    public JsonObject toJson()
    {
        JsonObject ret = new JsonObject();

        ret.addProperty("id", this.id);
        if(this.oreDict != null)
        {
            ret.addProperty("oredict", this.oreDict);
        }
        else
        {
            if(this.item.size() == 1)
            {
                ret.addProperty("item", ItemUtils.getTagFromStack(this.item.get(0)));
            }
            else
            {
                JsonArray array = new JsonArray();

                for(ItemStack itemStack : this.item)
                {
                    array.add(ItemUtils.getTagFromStack(itemStack));
                }

                ret.add("items", array);
            }
        }
        ret.addProperty("baseqty", this.baseQty);
        ret.addProperty("multqty", this.multQty);
        ret.addProperty("rand", this.randomFactor);
        ret.addProperty("levelscale", this.levelScale);
        ret.addProperty("baseprofit", this.baseProfit);
        ret.addProperty("command", this.completionCommand);
        ret.addProperty("langkey", this.langKey);
        ret.add("game_stages", DataUtils.serializeJson(this.gameStages));

        return ret;
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

    public String[][] getGameStages()
    {
        return gameStages;
    }

    public String getLangKey()
    {
        if(langKey != null)
            return langKey;

        return id;
    }
}
