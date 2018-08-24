package com.mike_caron.megacorp.item;

import com.mike_caron.megacorp.impl.WorkOrder;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class WorkVoucher extends ItemBase
{
    public WorkVoucher()
    {
        super();

        setRegistryName("work_voucher");
        setUnlocalizedName("megacorp:work_voucher");
        setMaxStackSize(1);

        //intentionally not set:
        //setCreativeTab(MegaCorpMod.creativeTab);

    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        WorkOrder workOrder = getWorkOrder(stack);
        String message;

        if(workOrder == null)
        {
            tooltip.add(I18n.format("item.megacorp:work_voucher.tooltip_blank"));
        }
        else
        {
            tooltip.add(I18n.format("item.megacorp:work_order.tooltip", workOrder.getDesiredItem().getDisplayName(), workOrder.getDesiredItem().getCount(), workOrder.getProgress()));
        }


    }

    public static ItemStack stackForWorkOrder(WorkOrder workOrder)
    {
        if(workOrder == null)
        {
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(ModItems.work_voucher, 1);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("WorkOrder", workOrder.serializeNBT());
        stack.setTagCompound(tag);

        return stack;
    }

    public static WorkOrder getWorkOrder(ItemStack stack)
    {
        if(stack.getItem() != ModItems.work_voucher || !stack.hasTagCompound())
            return null;

        return WorkOrder.fromNBT(stack.getTagCompound().getCompoundTag("WorkOrder"));
    }

    public static UUID getOwner(ItemStack stack)
    {
        if(stack.getItem() != ModItems.corporateCard || !stack.hasTagCompound() || !stack.getTagCompound().hasKey("Owner"))
            return null;

        return UUID.fromString(stack.getTagCompound().getString("Owner"));
    }
}
