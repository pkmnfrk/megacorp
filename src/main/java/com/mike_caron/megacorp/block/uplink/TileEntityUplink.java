package com.mike_caron.megacorp.block.uplink;

import com.mike_caron.megacorp.api.ICorporationManager;
import com.mike_caron.megacorp.block.TileEntityOwnedBase;
import com.mike_caron.megacorp.impl.Corporation;
import com.mike_caron.megacorp.impl.CorporationManager;
import com.mike_caron.megacorp.item.CorporateCard;
import com.mike_caron.megacorp.item.ModItems;
import com.mike_caron.megacorp.storage.LimitedItemStackHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class TileEntityUplink
    extends TileEntityOwnedBase
{
    public final ItemStackHandler cardInventory = new LimitedItemStackHandler(this, 2)
    {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack item)
        {
            if(slot == 1)
                return false;

            return item.getItem() == ModItems.corporateCard;
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            if(this.getStackInSlot(0).getItem() == ModItems.corporateCard
                && this.getStackInSlot(1).isEmpty()
                && owner != null)
            {
                ItemStack newCard = CorporateCard.stackForCorp(owner);

                this.setStackInSlot(0, ItemStack.EMPTY);
                this.setStackInSlot(1, newCard);
            }

            super.onContentsChanged(slot);
        }
    };

    public TileEntityUplink()
    {

    }

    @Override
    public void handleGuiButton(EntityPlayerMP player, int button, String extraData)
    {
        if(button == 1)
        {
            if(owner == null)
            {
                ICorporationManager manager = CorporationManager.get(player.getEntityWorld());
                if(!manager.ownerHasCorporation(player.getPersistentID()))
                {
                    manager.createCorporation(player.getPersistentID());
                }

                setOwner(player.getPersistentID());

            }
        }
    }

    @Override
    public void handleGuiString(EntityPlayerMP player, int element, String string)
    {
        Corporation corp = getCorporation();

        if(corp == null) return;

        if(element == 2)
        {
            if(owner.equals(player.getPersistentID()))
            {
                corp.setName(string);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        if(compound.hasKey("Inventory"))
        {
            cardInventory.deserializeNBT(compound.getCompoundTag("Inventory"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound ret = super.writeToNBT(compound);

        ret.setTag("Inventory", cardInventory.serializeNBT());

        return ret;
    }
}
