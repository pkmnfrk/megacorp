package com.mike_caron.megacorp.proxy;

import com.mike_caron.megacorp.block.capital_investor.ContainerCapitalInvestor;
import com.mike_caron.megacorp.block.capital_investor.TileEntityCapitalInvestor;
import com.mike_caron.megacorp.block.profit_condenser.ContainerProfitCondenser;
import com.mike_caron.megacorp.block.profit_condenser.TileEntityProfitCondenser;
import com.mike_caron.megacorp.block.profit_materializer.ContainerProfitMaterializer;
import com.mike_caron.megacorp.block.profit_materializer.TileEntityProfitMaterializer;
import com.mike_caron.megacorp.block.sbs.ContainerSBS;
import com.mike_caron.megacorp.block.sbs.TileEntitySBS;
import com.mike_caron.megacorp.block.shipping_depot.ContainerShippingDepot;
import com.mike_caron.megacorp.block.shipping_depot.TileEntityShippingDepot;
import com.mike_caron.megacorp.block.uplink.ContainerUplink;
import com.mike_caron.megacorp.block.uplink.TileEntityUplink;
import com.mike_caron.megacorp.gui.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiProxy implements IGuiHandler
{
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        BlockPos pos = new BlockPos(x,y,z);
        TileEntity te = world.getTileEntity(pos);


        if(te instanceof TileEntityProfitMaterializer)
        {
            return new ContainerProfitMaterializer(player.inventory, (TileEntityProfitMaterializer)te);
        }
        else if(te instanceof TileEntityUplink)
        {
            return new ContainerUplink(player.inventory, (TileEntityUplink)te, player);
        }
        else if(te instanceof TileEntitySBS)
        {
            return new ContainerSBS(player.inventory, (TileEntitySBS)te);
        }
        else if(te instanceof TileEntityProfitCondenser)
        {
            return new ContainerProfitCondenser(player.inventory, (TileEntityProfitCondenser)te);
        }
        else if(te instanceof TileEntityShippingDepot)
        {
            return new ContainerShippingDepot(player.inventory, (TileEntityShippingDepot)te);
        }
        else if(te instanceof TileEntityCapitalInvestor)
        {
            return new ContainerCapitalInvestor(player.inventory, (TileEntityCapitalInvestor)te);
        }
        /*
        else if(te instanceof TileEntityLiquidShippingDepot)
        {
            return new ContainerLiquidShippingDepot(player.inventory, (TileEntityLiquidShippingDepot)te);
        }
        */

        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if(ID == 100)
        {
            return new GuiGuide();
        }

        BlockPos pos = new BlockPos(x,y,z);
        TileEntity te = world.getTileEntity(pos);

        if(te instanceof TileEntityProfitMaterializer) {
            TileEntityProfitMaterializer tePM = (TileEntityProfitMaterializer) te;
            return new GuiProfitMaterializer(new ContainerProfitMaterializer(player.inventory, tePM));
        }
        else if(te instanceof TileEntityUplink)
        {
            TileEntityUplink teU = (TileEntityUplink)te;
            //return new GuiUplink(new ContainerUplink(player.inventory, teU, player));
            return new GuiTest(new ContainerUplink(player.inventory, teU, player));
        }
        else if(te instanceof TileEntitySBS)
        {
            TileEntitySBS teSBS = (TileEntitySBS)te;
            return new GuiSBS(new ContainerSBS(player.inventory, teSBS));
        }
        else if(te instanceof TileEntityProfitCondenser)
        {
            TileEntityProfitCondenser tePC = (TileEntityProfitCondenser)te;
            return new GuiProfitCondenser(new ContainerProfitCondenser(player.inventory, tePC));
        }
        else if(te instanceof TileEntityShippingDepot)
        {
            TileEntityShippingDepot tePC = (TileEntityShippingDepot)te;
            return new GuiShippingDepot(new ContainerShippingDepot(player.inventory, tePC));
        }
        /*
        else if(te instanceof TileEntityLiquidShippingDepot)
        {
            TileEntityLiquidShippingDepot tePC = (TileEntityLiquidShippingDepot)te;
            return new GuiLiquidShippingDepot(new ContainerLiquidShippingDepot(player.inventory, tePC));
        }
        */
        else if(te instanceof TileEntityCapitalInvestor)
        {
            TileEntityCapitalInvestor teCI = (TileEntityCapitalInvestor)te;
            return new GuiCapitalInvestor(new ContainerCapitalInvestor(player.inventory, teCI));
        }

        return null;
    }
}
