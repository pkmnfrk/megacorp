package com.mike_caron.megacorp.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FluidUtils
{
    public static boolean fillPlayerHandWithFluid(World world, BlockPos pos, EntityPlayer player, IFluidHandler fluidHandler)
    {
        ItemStack inHand = player.inventory.getCurrentItem().copy();
        ItemStack theRest = null;

        if(inHand.getCount() > 1)
        {
            //first, check to see if there's more room for this stack
            if(player.inventory.getFirstEmptyStack() != -1)
            {
                theRest = inHand.splitStack(inHand.getCount() - 1);
            }
            // else, if we can't split, then hope we can fill a whole stack!
        }

        if(inHand.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
        {
            IFluidHandlerItem container = inHand.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

            for(IFluidTankProperties prop : fluidHandler.getTankProperties())
            {
                if(!prop.canDrain()) continue;

                boolean good = false;
                for(IFluidTankProperties cprop : container.getTankProperties())
                {
                    if(cprop.canFillFluidType(prop.getContents()))
                    {
                        good = true;
                        break;
                    }

                }
                if(!good) continue;

                int filled = container.fill(prop.getContents(), false);
                if(filled == 0) continue;

                FluidStack drained = fluidHandler.drain(filled, false);

                if(drained == null || drained.amount == 0) continue;


                drained = fluidHandler.drain(drained.amount, true);
                filled = container.fill(drained, true);


                if(theRest != null)
                {
                    //the stack stays in your hand, while the new container goes elsewhere.
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, theRest);
                    player.inventory.addItemStackToInventory(container.getContainer());
                }
                else
                {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, container.getContainer());
                }
                return true;
            }
        }

        return false;
    }

    public static boolean drainPlayerHandOfFluid(World world, BlockPos pos, EntityPlayer player, IFluidHandler fluidHandler)
    {
        ItemStack inHand = player.inventory.getCurrentItem();

        if(inHand.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
        {
            IFluidHandlerItem container = inHand.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

            for(IFluidTankProperties prop : fluidHandler.getTankProperties())
            {
                if(!prop.canFill()) continue;

                boolean good = false;
                for(IFluidTankProperties cprop : container.getTankProperties())
                {
                    //if(cprop.canDrainFluidType(prop.getContents()))
                    if(prop.canFillFluidType(cprop.getContents()))
                    {
                        int filled = fluidHandler.fill(cprop.getContents(), false);
                        if(filled == 0) continue;

                        FluidStack drained = container.drain(filled, false);

                        if(drained == null || drained.amount == 0) continue;


                        drained = container.drain(drained.amount, true);
                        filled = fluidHandler.fill(drained, true);

                        player.inventory.setInventorySlotContents(player.inventory.currentItem, container.getContainer());
                        return true;
                    }

                }
            }
        }

        return false;
    }
}
