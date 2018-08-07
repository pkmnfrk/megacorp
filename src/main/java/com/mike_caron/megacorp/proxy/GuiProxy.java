package com.mike_caron.megacorp.proxy;

import com.mike_caron.megacorp.block.profit_materializer.ContainerProfitMaterializer;
import com.mike_caron.megacorp.block.profit_materializer.TileEntityProfitMaterializer;
import com.mike_caron.megacorp.block.uplink.ContainerUplink;
import com.mike_caron.megacorp.block.uplink.TileEntityUplink;
import com.mike_caron.megacorp.gui.GuiProfitMaterializer;
import com.mike_caron.megacorp.gui.GuiUplink;
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
            return new ContainerUplink(player.inventory, (TileEntityUplink)te);
        }

        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        BlockPos pos = new BlockPos(x,y,z);
        TileEntity te = world.getTileEntity(pos);


        if(te instanceof TileEntityProfitMaterializer) {
            TileEntityProfitMaterializer TCte = (TileEntityProfitMaterializer) te;
            return new GuiProfitMaterializer(TCte, new ContainerProfitMaterializer(player.inventory, TCte));
        }
        else if(te instanceof TileEntityUplink)
        {
            TileEntityUplink TCte = (TileEntityUplink)te;
            return new GuiUplink(TCte, new ContainerUplink(player.inventory, TCte));
        }

        return null;
    }
}
