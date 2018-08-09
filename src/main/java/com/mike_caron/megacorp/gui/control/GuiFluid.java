package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class GuiFluid
    extends GuiSized
{
    private int capacity;
    private int amount;
    private Fluid fluid;
    private FluidStack fluidStack;
    private boolean gradEnabled = false;

    public GuiFluid(int x, int y, int width, int height)
    {
        super(x,y, height, width);
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

        int scaledHeight = getScaled(this.height);

        GuiUtil.drawFluid(this.x, this.y + this.height - scaledHeight, fluidStack, this.width, scaledHeight);

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

    private int getScaled(int height)
    {
        int ret = (int)Math.floor(((double)amount) / capacity * height);
        if(ret == 0 && amount > 0)
            return 1;
        return ret;
    }

}
