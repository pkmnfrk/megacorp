package com.mike_caron.megacorp.block.uplink;

import com.mike_caron.megacorp.api.ICorporation;
import com.mike_caron.megacorp.api.ICorporationManager;
import com.mike_caron.megacorp.block.TEContainerBase;
import com.mike_caron.megacorp.impl.CorporationManager;
import com.mike_caron.megacorp.storage.SlotOutputOnly;
import com.mike_caron.megacorp.util.StringUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.SlotItemHandler;

import java.util.UUID;

public class ContainerUplink
        extends TEContainerBase
{

    public UUID opener;
    public boolean hasCorp = false;
    public String corpName = null;
    public int corpNameCounter = 0;
    public long profit = 0;

    Slot cardSlotInput, cardSlotOutput;

    public ContainerUplink(IInventory playerInventory, TileEntityUplink te, EntityPlayer player)
    {
        super(playerInventory, te);

        opener = player.getPersistentID();
    }

    private TileEntityUplink getTE()
    {
        return (TileEntityUplink)this.te;
    }

    @Override
    protected int playerInventoryY()
    {
        return 84;
    }

    @Override
    protected int playerInventoryX()
    {
        return 8;
    }

    @Override
    protected void addOwnSlots()
    {
        cardSlotInput = new SlotItemHandler(getTE().cardInventory, 0, 62, 58);
        cardSlotOutput = new SlotOutputOnly(getTE().cardInventory, 1, 98, 58);

        this.addSlotToContainer(cardSlotInput);
        this.addSlotToContainer(cardSlotOutput);
    }

    @Override
    protected int numOwnSlots()
    {
        return 2;
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        TileEntityUplink te = getTE();

        if(te.getWorld().isRemote) return;

        ICorporationManager manager = CorporationManager.get(te.getWorld());

        if(manager.ownerHasCorporation(opener) != hasCorp)
        {
            hasCorp = manager.ownerHasCorporation(opener);
            changed = true;
        }

        if(owner != null)
        {
            ICorporation corp = manager.getCorporationForOwner(owner);
            if (!corp.getName().equals(corpName))
            {
                corpName = corp.getName();
                changed = true;
            }
            if (profit != corp.getTotalProfit())
            {
                profit = corp.getTotalProfit();
                changed = true;
            }

        }

        if(changed)
        {
            triggerUpdate();
        }
    }

    @Override
    protected void onReadNBT(NBTTagCompound tag)
    {
        super.onReadNBT(tag);

        hasCorp = tag.getBoolean("HasCorp");
        String newCorpName;
        if(tag.hasKey("CorpName"))
        {
            newCorpName = tag.getString("CorpName");
        }
        else
        {
            newCorpName = null;
        }
        if(!StringUtil.areEqual(newCorpName, corpName))
        {
            corpNameCounter++;
        }
        corpName = newCorpName;
        profit = tag.getLong("Profit");
    }

    @Override
    protected void onWriteNBT(NBTTagCompound tag)
    {
        super.onWriteNBT(tag);

        tag.setBoolean("HasCorp", hasCorp);
        if(corpName != null)
        {
            tag.setString("CorpName", corpName);
        }
        tag.setLong("Profit", profit);
    }

    @Override
    public int getId()
    {
        return 2;
    }
}
