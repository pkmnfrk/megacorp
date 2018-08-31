package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class GuiFluid
    extends GuiSized
{
    private int capacity;
    private int amount;
    private Fluid fluid;
    private FluidStack fluidStack;
    private boolean gradEnabled = false;
    private Orientation orientation;

    public GuiFluid(int x, int y, int width, int height, Orientation orientation)
    {
        super(x,y, width, height);

        this.orientation = orientation;
    }

    public GuiFluid(int x, int y, int width, int height)
    {
        this(x, y, width, height, Orientation.VERTICAL);
    }

    @Nullable
    @Override
    public List<String> getTooltip(int mouseX, int mouseY)
    {
        List<String> items = new ArrayList<>();

        if(this.fluid != null)
        {
            items.add(this.fluid.getLocalizedName(null));

            StringBuilder sb = new StringBuilder();
            sb.append(NumberFormat.getIntegerInstance().format(this.amount));
            sb.append("/");
            sb.append(NumberFormat.getIntegerInstance().format(this.capacity));
            sb.append("mb");
            items.add(sb.toString());
        }
        else
        {
            items.add(new TextComponentTranslation("tile.megacorp:misc.empty").getFormattedText());
        }

        return items;
    }

    public void setCapacity(int capacity)
    {
        this.capacity = capacity;
    }

    public void setAmount (int amount)
    {
        this.amount = amount;
    }

    public void setFluid(Fluid fluid)
    {
        this.fluid = fluid;
        if(fluid != null)
        {
            this.fluidStack = new FluidStack(fluid, 1);
        }
        else
        {
            this.fluidStack = null;
        }
    }

    public Fluid getFluid()
    {
        return this.fluid;
    }

    public FluidStack getFluidStack()
    {
        return this.fluidStack;
    }

    public boolean isGradEnabled()
    {
        return gradEnabled;
    }

    public void setGradEnabled(boolean gradEnabled)
    {
        this.gradEnabled = gradEnabled;
    }

    @Override
    public void draw()
    {
        if(!this.visible) return;

        int drawWidth = this.width;
        int drawHeight = this.height;

        if(orientation == Orientation.VERTICAL)
        {
            drawHeight = getScaled(drawHeight);
        }
        else if(orientation == Orientation.HORIZONTAL)
        {
            drawWidth = getScaled(drawWidth);
        }

        GuiUtil.drawFluid(this.x, this.y + this.height - drawHeight, fluidStack, drawWidth, drawHeight);

        if(gradEnabled)
        {
            GuiUtil.bindTexture(GuiUtil.MISC_RESOURCES);
            GlStateManager.color(1, 1, 1, 1);
            //drawTexturedModalRect(this.x + this.width - 18, this.y + 30, 0, 1, 18, 46);
            //drawModalRectWithCustomSizedTexture(this.x + this.width - 18, this.y + 1, 0, 1/128f, this.width / 3, this.height - 2, 128f, 128f);
            //GuiUtil.drawTiledTexture(this.parent, this.x + this.width - 16, this.y, GuiUtil.getTexture(GuiUtil.MISC_RESOURCES), 16, this.height);
            GuiUtil.drawTiledTexturePart(this.x + this.width - 16, this.y, 16, this.height, 0, 0, 16, 16, 256, 256);
        }
    }

    private int getScaled(int i)
    {
        int ret = (int)Math.floor(((double)amount) / capacity * i);
        if(ret == 0 && amount > 0)
            return 1;
        return ret;
    }

    public enum Orientation
    {
        VERTICAL,
        HORIZONTAL
    }

}
