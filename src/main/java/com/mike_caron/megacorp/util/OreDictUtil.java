package com.mike_caron.megacorp.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class OreDictUtil
{
    private OreDictUtil(){}

    /*
    private static List<NonNullList<ItemStack>> oreRegistry;
    private static List<String> idToName;

    static {
        try
        {
            Field f = OreDictionary.class.getDeclaredField("idToStack");
            f.setAccessible(true);
            oreRegistry = (List<NonNullList<ItemStack>>) f.get(null);

            f = OreDictionary.class.getDeclaredField("idToName");
            f.setAccessible(true);
            idToName = (List<String>)f.get(null);
        }
        catch(Exception ex)
        {
            MegaCorpMod.logger.error("Error getting ore dict data", ex);
        }
    }
    */

    public static List<String> getDictsForItem(ItemStack stack)
    {
        List<String> ret = new ArrayList<>();

        int[] relevantIds = OreDictionary.getOreIDs(stack);

        if(relevantIds.length > 0)
        {
            for (int id : relevantIds)
            {
                ret.add(OreDictionary.getOreName(id));
                //ret.add(idToName.get(id));
            }
        }


        return ret;
    }

    public static List<String> getDictsWithWildcards(String... names)
    {
        List<String> ret = new ArrayList<>();
        String[] dicts = OreDictionary.getOreNames();

        for(String dict : dicts)
        {
            for(String name : names)
            {
                if(dict.startsWith(name))
                {
                    ret.add(dict);
                    continue;
                }
            }
        }

        return ret;
    }
}
