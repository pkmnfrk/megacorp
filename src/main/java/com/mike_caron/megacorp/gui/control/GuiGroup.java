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
        control.setParent(this);
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

    @Override
    public boolean hasFocus()
    {
        for(GuiControl control : controls)
        {
            if(control.hasFocus()) return true;
        }
        return false;
    }

    @Override
    public boolean canHaveFocus()
    {
        return controls.stream().anyMatch(GuiControl::canHaveFocus);
    }

    @Override
    public void setFocused(boolean focused)
    {
        if(!focused)
        {
            for(GuiControl control : controls)
            {
                if(control.hasFocus())
                {
                    control.setFocused(false);
                    break;
                }
            }
        }
        else
        {
            for(GuiControl control : controls)
            {
                if(control.canHaveFocus())
                {
                    control.setFocused(true);
                    break;
                }
            }
        }

    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode)
    {
        for(GuiControl control : controls)
        {
            if(control.hasFocus())
            {
                control.onKeyTyped(typedChar, keyCode);
                break;
            }
        }
    }
}
