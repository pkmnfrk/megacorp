package com.mike_caron.megacorp.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.reward.BaseReward;
import com.mike_caron.megacorp.util.ItemUtils;
import net.minecraft.item.ItemStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.SortedSet;
import java.util.TreeSet;

public class VendingManager
{
    public static final VendingManager INSTANCE = new VendingManager();

    private final SortedSet<VendingItem> items = new TreeSet<>(new VendingItem.Comparator());

    private VendingManager(){}

    public boolean loadVendingItems(File configDirectory)
    {
        items.clear();

        File vendingFile = new File(configDirectory.getPath(), "vending.json");

        if(!vendingFile.exists())
        {
            return true;
        }

        JsonParser parser = new JsonParser();

        JsonObject json;
        try (BufferedReader stream = Files.newBufferedReader(vendingFile.toPath()))
        {
            json = parser.parse(stream).getAsJsonObject();
        }
        catch(RuntimeException | IOException ex)
        {
            MegaCorpMod.logger.error("Encountered error while reading " + vendingFile.getPath(), ex);
            return false;
        }

        try
        {
            if (json.has("items"))
            {
                JsonArray itemlist = json.get("items").getAsJsonArray();

                for(JsonElement item : itemlist)
                {
                    loadItem(item.getAsJsonObject());
                }
            }
        }
        catch (IllegalStateException ex)
        {
            MegaCorpMod.logger.error("Encountered error while reading " + vendingFile.getPath(), ex);
            return false;
        }

        return true;
    }

    public SortedSet<VendingItem> getItems()
    {
        return items;
    }

    public VendingItem getItem(String id)
    {
        for(VendingItem item : items)
        {
            if(item.id.equals(id))
                return item;
        }

        return null;
    }

    private void loadItem(JsonObject item)
    {
        ItemStack itemStack = ItemUtils.getStackFromTag(item.get("item").getAsString());

        int amount = 1;
        int cost = item.get("cost").getAsInt();
        BaseReward.CurrencyType currency = BaseReward.CurrencyType.MONEY;

        if(item.has("amount"))
        {
            amount = item.get("amount").getAsInt();
        }

        if(item.has("cost_type"))
        {
            String k = item.get("cost_type").getAsString();
            if(k.equals("dense_money"))
            {
                currency = BaseReward.CurrencyType.DENSE_MONEY;
            }
        }

        itemStack.setCount(amount);

        items.add(new VendingItem(itemStack, cost, currency));
    }


}
