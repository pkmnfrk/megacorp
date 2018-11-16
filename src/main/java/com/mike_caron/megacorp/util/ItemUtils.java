package com.mike_caron.megacorp.util;

import com.google.common.base.Preconditions;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    public static String getTagFromStack(ItemStack stack)
    {
        StringBuilder ret = new StringBuilder();

        ret.append(stack.getItem().getRegistryName().toString());

        if(stack.getMetadata() != 0)
        {
            ret.append(":");
            ret.append(stack.getMetadata());
        }

        return ret.toString();
    }

    public static boolean areEqual(@Nullable NonNullList<ItemStack> a, @Nullable NonNullList<ItemStack> b)
    {
        if(a == b)
            return true;

        if(a != null && b == null)
            return false;

        if(a == null) //b is implied not null here
            return false;

        if(a.size() != b.size())
            return false;

        for(int i = 0; i < a.size(); i++)
        {
            ItemStack ai = a.get(i);
            ItemStack bi = b.get(i);

            if(!ai.isItemEqual(bi))
                return false;
        }

        return true;
    }

    public static void giveToPlayerOrDrop(@Nonnull ItemStack itemStack, @Nonnull EntityPlayer player)
    {
        ItemStack newStack = itemStack.copy();

        if(!player.inventory.addItemStackToInventory(newStack))
        {
            EntityItem item = new EntityItem(player.getEntityWorld(), player.posX, player.posY, player.posZ, newStack);
            player.getEntityWorld().spawnEntity(item);
        }
    }
}
