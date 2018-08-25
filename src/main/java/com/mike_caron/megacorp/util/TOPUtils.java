package com.mike_caron.megacorp.util;

import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;

import java.awt.*;

public class TOPUtils
{

    private TOPUtils() {}

    public static void addFluidTank(IProbeInfo probeInfo, IFluidTank tank)
    {
        addFluid(probeInfo, tank.getFluid(), tank.getCapacity());
    }

    public static void addFluid(IProbeInfo probeInfo, FluidStack fluid, int capacity)
    {
        if(fluid == null) return;

        int fluidColor = Color.BLUE.getRGB();

        if(fluid.getFluid() != null)
        {
            fluidColor = fluid.getFluid().getColor();
        }

        probeInfo
            .horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
            .item(FluidUtil.getFilledBucket(new FluidStack(fluid.getFluid(), 1000)))
            .vertical()
            .text(fluid.getLocalizedName())
            .progress(fluid.amount, capacity, new ProgressStyle()
                .filledColor(fluidColor)
                .suffix("mB")
                .numberFormat(NumberFormat.COMMAS)
            )
        ;
    }

    public static void addProgress(IProbeInfo probeInfo, float progress)
    {
        if(progress == 0f)
        {
            probeInfo
                .horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                .item(ItemUtils.CLOCK)
                .text(new TextComponentTranslation("tile.megacorp:misc.idle").getFormattedText());
            ;
        }
        else
        {
            probeInfo
                .horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                .item(ItemUtils.CLOCK)
                .progress((int) (progress * 100), 100, new ProgressStyle().suffix("%"))
            ;
        }
    }
}
