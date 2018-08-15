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
        ItemStack inHand = player.inventory.getCurrentItem();

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

                if(drained.amount == 0) continue;


                drained = fluidHandler.drain(drained.amount, true);
                filled = container.fill(drained, true);

                player.inventory.setInventorySlotContents(player.inventory.currentItem, container.getContainer());
                return true;
            }
        }

        return false;
    }

    /**
     * If the currently held item of the given player can be filled with the
     * liquid in the given tank's output tank, do so and put the resultant filled
     * container item where it can go. This will also drain the tank and set it to
     * dirty.
     *
     * <p>
     * Cases handled for the the filled container:
     *
     * <ul>
     * <li>If the stacksize of the held item is one, then it will be replaced by
     * the filled container unless the player in in creative.
     * <li>If the filled container is stackable and the player already has a
     * non-maxed stack in the inventory, it is put there.
     * <li>If the player has space in his inventory, it is put there.
     * <li>Otherwise it will be dropped on the ground between the position given
     * as parameter and the player's position.
     * </ul>
     *
     * Copied whole-cloth from EnderCore FluidUtil
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param entityPlayer
     * @param tank
     * @return true if a container was filled, false otherwise
     */
    /*
    public static boolean fillPlayerHandItemFromInternalTank(World world, int x, int y, int z, EntityPlayer entityPlayer, FluidTank subTank) {

        FluidAndStackResult fill = tryFillContainer(entityPlayer.inventory.getCurrentItem(), subTank.getFluid());
        if (fill.result.fluidStack != null) {

            subTank.setFluid(fill.remainder.fluidStack);
            tank.setTanksDirty();
            if (!entityPlayer.capabilities.isCreativeMode) {
                if (fill.remainder.itemStack == null) {
                    entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, fill.result.itemStack);
                    return true;
                } else {
                    entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, fill.remainder.itemStack);
                }

                if (fill.result.itemStack.isStackable()) {
                    for (int i = 0; i < entityPlayer.inventory.mainInventory.length; i++) {
                        ItemStack inventoryItem = entityPlayer.inventory.mainInventory[i];
                        if (ItemUtil.areStackMergable(inventoryItem, fill.result.itemStack) && inventoryItem.stackSize < inventoryItem.getMaxStackSize()) {
                            fill.result.itemStack.stackSize += inventoryItem.stackSize;
                            entityPlayer.inventory.setInventorySlotContents(i, fill.result.itemStack);
                            return true;
                        }
                    }
                }

                for (int i = 0; i < entityPlayer.inventory.mainInventory.length; i++) {
                    if (entityPlayer.inventory.mainInventory[i] == null) {
                        entityPlayer.inventory.setInventorySlotContents(i, fill.result.itemStack);
                        return true;
                    }
                }

                if (!world.isRemote) {
                    double x0 = (x + entityPlayer.posX) / 2.0D;
                    double y0 = (y + entityPlayer.posY) / 2.0D + 0.5D;
                    double z0 = (z + entityPlayer.posZ) / 2.0D;
                    Util.dropItems(world, fill.result.itemStack, x0, y0, z0, true);
                }
            }

            return true;
        }

        return false;
    }

    public static FluidAndStackResult tryFillContainer(ItemStack target, FluidStack source) {
        if (target != null && target.getItem() != null && source != null && source.getFluid() != null && source.amount > 0) {

            if (target.getItem() instanceof IFluidContainerItem) {
                ItemStack resultStack = target.copy();
                resultStack.stackSize = 1;
                int amount = ((IFluidContainerItem) target.getItem()).fill(resultStack, source, true);
                if (amount <= 0) {
                    return new FluidAndStackResult(null, null, target, source);
                }
                FluidStack resultFluid = source.copy();
                resultFluid.amount = amount;
                ItemStack remainderStack = target.copy();
                remainderStack.stackSize--;
                if (remainderStack.stackSize <= 0) {
                    remainderStack = null;
                }
                FluidStack remainderFluid = source.copy();
                remainderFluid.amount -= amount;
                if (remainderFluid.amount <= 0) {
                    remainderFluid = null;
                }
                return new FluidAndStackResult(resultStack, resultFluid, remainderStack, remainderFluid);
            }

            ItemStack resultStack =  FluidContainerRegistry.fillFluidContainer(source.copy(), target);
            if (resultStack != null) {
                FluidStack resultFluid = FluidContainerRegistry.getFluidForFilledItem(resultStack);
                if (resultFluid != null) {
                    ItemStack remainderStack = target.copy();
                    remainderStack.stackSize--;
                    if (remainderStack.stackSize <= 0) {
                        remainderStack = null;
                    }
                    FluidStack remainderFluid = source.copy();
                    remainderFluid.amount -= resultFluid.amount;
                    if (remainderFluid.amount <= 0) {
                        remainderFluid = null;
                    }
                    return new FluidAndStackResult(resultStack, resultFluid, remainderStack, remainderFluid);
                }
            }

        }
        return new FluidAndStackResult(null, null, target, source);
    }
    */
}
