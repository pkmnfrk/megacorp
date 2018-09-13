package com.mike_caron.megacorp.gui.control;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuiHorizontalLayout
    extends GuiSized
    implements IGuiGroup
{
    private int margin;

    private final List<GuiControl> controls = new ArrayList<>();

    public GuiHorizontalLayout(int x, int y, int width, int height, int margin)
    {
        super(x, y, width, height);

        this.margin = margin;
    }

    @Override
    public void setWidth(int width)
    {
        super.setWidth(width);

        doLayout();
    }

    @Override
    public void setHeight(int height)
    {
        super.setHeight(height);

        doLayout();
    }

    @Override
    public int translateToScreenX(int x)
    {
        return parent.translateToScreenX(x + this.x);
    }

    @Override
    public int translateToScreenY(int y)
    {
        return parent.translateToScreenY(y + this.y);
    }

    @Override
    public int translateFromScreenX(int x)
    {
        return parent.translateFromScreenX(x) - this.x;
    }

    @Override
    public int translateFromScreenY(int y)
    {
        return parent.translateFromScreenX(y) - this.y;
    }

    @Override
    public void sort()
    {
        this.controls.sort(Comparator.comparingInt(a -> a.zIndex));
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
                GlStateManager.pushMatrix();
                GlStateManager.translate(control.getX(), control.getY(), 0);
                control.preDraw();
                control.draw();
                control.postDraw();
                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public void update()
    {
        for(GuiControl control : controls)
        {
            control.update();
        }
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
    public GuiControl hitTest(int x, int y)
    {
        for(GuiControl control : controls)
        {
            if(control.isVisible())
            {
                GuiControl res = control.hitTest(x - control.getX(), y - control.getY());
                if (res != null)
                    return res;
            }
        }

        return null;
    }

    @Override
    public void addControl(GuiControl control)
    {
        this.controls.add(control);
        control.setParent(this);
        this.sort();
        this.doLayout();
    }

    @Override
    public void removeControl(GuiControl control)
    {
        this.controls.remove(control);
        this.doLayout();
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

    private void doLayout()
    {
        if(controls.size() < 1) return;

        int totalWidth = 0;

        totalWidth += margin * (controls.size() - 1);

        for(GuiControl control : controls)
        {
            totalWidth += control.getWidth();
        }

        int left = this.width / 2 - totalWidth / 2;

        for(GuiControl control : controls)
        {
            control.setY(this.height / 2 - control.getHeight() / 2);
            control.setX(left);

            left += control.getWidth() + margin;
        }
    }
}
