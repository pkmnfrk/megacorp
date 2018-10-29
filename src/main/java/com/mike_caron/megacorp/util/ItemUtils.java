package com.mike_caron.megacorp.util;

import com.google.common.base.Preconditions;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;

public class ItemUtils
{
    @GameRegistry.ItemStackHolder("minecraft:clock")
    public static ItemStack CLOCK;

    @Nonnull
    public static ItemStack getStackFromTag(String tag)
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

        //return null;
        throw new RuntimeException("I don't understand the item " + tag);
    }
}
