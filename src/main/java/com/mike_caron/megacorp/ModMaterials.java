package com.mike_caron.megacorp;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModMaterials
{
    public static final Material MONEY = new MaterialLiquid(
            MapColor.GREEN
    );

    /*
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(EntityViewRenderEvent.FogDensity event)
    {
        if (event.getEntity().isInsideOfMaterial(ModMaterials.MONEY))
        {
            event.setDensity(0.5F);
        }
        else
        {
            event.setDensity(0.0001F);
        }

        event.setCanceled(true); // must cancel event for event handler to take effect
    }
    */

    /*
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(EntityViewRenderEvent.FogColors event)
    {
        if (event.getEntity().isInsideOfMaterial(ModMaterials.SLIME))
        {
            Color theColor = Color.GREEN;
            event.setRed(theColor.getRed());
            event.setGreen(theColor.getGreen());
            event.setBlue(theColor.getBlue());
        }
    }
*/
}
