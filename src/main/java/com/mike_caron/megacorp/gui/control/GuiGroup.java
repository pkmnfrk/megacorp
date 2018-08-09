package com.mike_caron.megacorp.gui.control;

import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.List;

public class GuiGroup
    extends GuiControl
    implements IGuiGroup
{
    private final List<GuiControl> controls = new ArrayList<>();

    public GuiGroup()
    {
        super(0, 0);
    }

    @Override
    public void draw()
    {
        if(!this.visible)
            return;

        for(GuiControl control : controls)
        {
            control.draw();
        }
    }

    @Override
    public void addControl(GuiControl control)
    {
        this.controls.add(control);
        if(control instanceof GuiControl)
        {
            ((GuiControl)control).setParent(this);
        }
    }

    @Override
    public void removeControl(GuiControl control)
    {
        this.controls.remove(control);
    }

    @Override
    public FontRenderer getFontRenderer()
    {
        return parent.getFontRenderer();
    }
}
