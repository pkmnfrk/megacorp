package com.mike_caron.megacorp.gui.control;

import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuiScrollPort
    extends GuiClippedSized
    implements IGuiGroup
{
    private final List<GuiControl> controls = new ArrayList<>();

    @Override
    public void addControl(GuiControl control)
    {
        this.controls.add(control);
        control.setParent(this);
        this.sort();
    }

    @Override
    public void removeControl(GuiControl control)
    {
        this.controls.remove(control);
    }

    @Override
    public void clearControls()
    {
        this.controls.clear();
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
                if(!control.isEnabled()) continue;

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

    @Override
    public GuiControl hitTest(int x, int y)
    {
        for(GuiControl control : controls)
        {
            if(control.isVisible())
            {
                GuiControl res = control.hitTest(x, y);
                if (res != null)
                    return res;
            }
        }

        return null;
    }

    @Override
    public boolean notifyTakeFocus(GuiControl taker)
    {
        if(this.parent != null)
        {
            if (!this.parent.notifyTakeFocus(this))
            {
                return false;
            }
        }

        for(GuiControl control : controls)
        {
            if(control != taker && control.hasFocus())
            {
                control.setFocused(false);
            }
        }

        return true;
    }

    @Override
    public void sort()
    {
        this.controls.sort(Comparator.comparingInt(a -> a.zIndex));
    }

    @Override
    public int translateX(int x)
    {
        return this.x + x - scrollX;
    }

    @Override
    public int translateY(int y)
    {
        return this.y + y - scrollY;
    }

    public GuiScrollPort(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    @Override
    public void draw()
    {
        if(!this.visible)
            return;

        for(GuiControl control : controls)
        {
            if(control.isVisible())
            {
                control.preDraw();
                control.draw();
                control.postDraw();
            }
        }
    }
}
