package com.mike_caron.megacorp.proxy;

import com.mike_caron.megacorp.block.profit_materializer.ContainerProfitMaterializer;
import com.mike_caron.megacorp.block.profit_materializer.TileEntityProfitMaterializer;
import com.mike_caron.megacorp.gui.GuiProfitMaterializer;
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


        if(te instanceof TileEntityProfitMaterializer) {
            return new ContainerProfitMaterializer(player.inventory, (TileEntityProfitMaterializer)te);
        }
        /*
        else if(te instanceof TransmutationGeneratorTileEntity)
        {
            return new TransmutationGeneratorContainer(player.inventory, (TransmutationGeneratorTileEntity)te);
        }
        */
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
        /*
        else if(te instanceof TransmutationGeneratorTileEntity)
        {
            TransmutationGeneratorTileEntity TGte = (TransmutationGeneratorTileEntity)te;
            return new TransmutationGeneratorContainerGui(TGte, new TransmutationGeneratorContainer(player.inventory, TGte));
        }
        */

        return null;
    }
}
