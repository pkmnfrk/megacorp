package com.mike_caron.megacorp.integrations.jei;

import com.mike_caron.megacorp.gui.control.GuiContainerBase;
import com.mike_caron.megacorp.gui.control.GuiControl;
import com.mike_caron.megacorp.gui.control.GuiFluid;
import mezz.jei.api.gui.IAdvancedGuiHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GuiHandler
    implements IAdvancedGuiHandler<GuiContainerBase>
{
    private final Class actualGui;

    public GuiHandler(Class<? extends GuiContainerBase> clz)
    {
        this.actualGui = clz;
    }

    @Override
    @Nonnull
    public Class<GuiContainerBase> getGuiContainerClass()
    {
        return actualGui;
    }

    @Nullable
    @Override
    public Object getIngredientUnderMouse(GuiContainerBase guiContainer, int mouseX, int mouseY)
    {
        GuiControl control = guiContainer.hitTest(mouseX - guiContainer.getGuiLeft(), mouseY - guiContainer.getGuiTop());

        if(control instanceof GuiFluid)
        {
            return ((GuiFluid) control).getFluidStack();
        }

        return null;
    }
}
