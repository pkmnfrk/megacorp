package com.mike_caron.megacorp.block;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.network.IGuiUpdater;
import com.mike_caron.megacorp.network.MessageUpdateGui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public abstract class ContainerBase
        extends Container
        implements IGuiUpdater
{
    private Runnable onGuiUpdate;
    private IInventory playerInventory;

    protected boolean changed = false;

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        changed = false;
    }

    public ContainerBase(IInventory player)
    {
        super();

        this.playerInventory = player;
    }

    protected void init()
    {
        addOwnSlots();
        addPlayerSlots(playerInventory);
    }

    @Override
    public void setGuiListener(Runnable onUpdate)
    {
        this.onGuiUpdate = onUpdate;
    }

    protected int playerInventoryX()
    {
        return 11;
    }

    protected int playerInventoryY()
    {
        return 71;
    }

    protected void addPlayerSlots(IInventory playerInventory)
    {
        int px = playerInventoryX();
        int py = playerInventoryY();

        // Slots for the main inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = px + col * 18;
                int y = row * 18 + py;
                this.addSlotToContainer(new Slot(playerInventory, (row + 1) * 9 + col, x, y));
            }
        }

        // Slots for the hotbar
        for (int col = 0; col < 9; ++col) {
            int x = px + col * 18;
            int y = 58 + py;
            this.addSlotToContainer(new Slot(playerInventory, col, x, y));
        }
    }

    protected void addOwnSlots()
    {

    }

    protected int numOwnSlots()
    {
        return 0;
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < numOwnSlots())
            { //transferring from block -> player
                if (!this.mergeItemStack(itemstack1, numOwnSlots(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else
            {
                if (!this.mergeItemStack(itemstack1, 0, numOwnSlots(), false))
                { //transferring from player -> block
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {

                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        onWriteNBT(tag);

        return tag;
    }

    protected void onWriteNBT(NBTTagCompound tag)
    {

    }


    @Override
    public final void deserializeNBT(NBTTagCompound nbtTagCompound)
    {
        if(nbtTagCompound != null)
        {
            onReadNBT(nbtTagCompound);

            if(onGuiUpdate != null)
            {
                onGuiUpdate.run();
            }
        }
    }

    @Override
    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);

        triggerUpdate(listener);
    }

    protected void onReadNBT(NBTTagCompound tag)
    {

    }

    protected void triggerUpdate()
    {
        MessageUpdateGui message = new MessageUpdateGui(this);

        for(IContainerListener listener : this.listeners)
        {
            MegaCorpMod.networkWrapper.sendTo(message, (EntityPlayerMP)listener);
        }
    }

    protected void triggerUpdate(IContainerListener listener)
    {
        MessageUpdateGui message = new MessageUpdateGui(this);
        MegaCorpMod.networkWrapper.sendTo(message, (EntityPlayerMP)listener);

    }
}
