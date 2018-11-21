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
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;

public abstract class ContainerBase
        extends Container
        implements IGuiUpdater
{
    private Runnable onGuiUpdate;
    private IInventory playerInventory;

    protected boolean changed = false;

    protected boolean ownSlotUpdates = true;

    @Override
    public void detectAndSendChanges()
    {
        changed = false;

        if(ownSlotUpdates)
        {
            for (int i = 0; i < inventorySlots.size(); i++)
            {
                ItemStack invStack = inventorySlots.get(i).getStack();
                ItemStack knownStack = this.inventoryItemStacks.get(i);

                if (!ItemStack.areItemStacksEqual(invStack, knownStack) || invStack.getCount() != knownStack.getCount())
                {
                    this.inventoryItemStacks.set(i, invStack);
                    changed = true;
                }
            }
        }
        else
        {
            super.detectAndSendChanges();
        }
    }

    public ContainerBase(IInventory player)
    {
        super();

        this.playerInventory = player;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        MinecraftForge.EVENT_BUS.unregister(this);
    }

    protected void init()
    {
        addOwnSlots();
        addPlayerSlots(playerInventory);

        MinecraftForge.EVENT_BUS.register(this);
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

        //MegaCorpMod.logger.info("serializeNBT:");
        //MegaCorpMod.logger.info(StringUtil.prettyPrintJson(DataUtils.toJson(tag)));

        return tag;
    }

    protected void onWriteNBT(NBTTagCompound tag)
    {
        if(ownSlotUpdates)
        {
            NBTTagCompound slots = new NBTTagCompound();

            for (int i = 0; i < this.inventorySlots.size(); ++i)
            {
                ItemStack itemstack1 = this.inventorySlots.get(i).getStack();
                if (!itemstack1.isEmpty())
                {
                    NBTTagCompound item = itemstack1.serializeNBT();
                    slots.setTag(Integer.toString(i), item);
                }
            }

            tag.setTag("Slots", slots);
        }

    }


    protected void onReadNBT(NBTTagCompound tag)
    {
        if(ownSlotUpdates && tag.hasKey("Slots"))
        {
            NBTTagCompound slots = tag.getCompoundTag("Slots");

            for(int i = 0; i < inventorySlots.size(); i++)
            {
                String key = Integer.toString(i);

                if(slots.hasKey(key))
                {
                    NBTTagCompound nbt = slots.getCompoundTag(key);
                    ItemStack stack = new ItemStack(nbt);
                    if(!ItemStack.areItemStacksEqual(stack, inventorySlots.get(i).getStack()))
                    {
                        inventorySlots.get(i).putStack(stack);
                    }
                }
                else
                {
                    if(inventorySlots.get(i).getHasStack())
                    {
                        inventorySlots.get(i).putStack(ItemStack.EMPTY);
                    }
                }
            }
        }
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

    protected void triggerUpdate()
    {
        MessageUpdateGui message = new MessageUpdateGui(this);

        for(IContainerListener listener : this.listeners)
        {
            if(listener instanceof EntityPlayerMP)
            {
                MegaCorpMod.networkWrapper.sendTo(message, (EntityPlayerMP) listener);
            }
        }
    }

    protected void triggerUpdate(IContainerListener listener)
    {
        MessageUpdateGui message = new MessageUpdateGui(this);
        if(listener instanceof EntityPlayerMP)
        {
            MegaCorpMod.networkWrapper.sendTo(message, (EntityPlayerMP) listener);
        }

    }


}
