package com.mike_caron.megacorp.gui.control;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuiScrollPort
    extends GuiClippedSized
    implements IGuiGroup
{
    protected final List<GuiControl> controls = new ArrayList<>();

    private boolean enableScrollBar = false;

    private GuiScrollBar scrollBar;

    public GuiScrollPort(int x, int y, int width, int height)
    {
        super(x, y, width, height);

        scrollBar = new GuiScrollBar(this.width - 8, 0, 8, this.height);
    }

    @Override
    public void setWidth(int width)
    {
        super.setWidth(width);

        scrollBar.setX(this.width - 8);
    }

    @Override
    public void setHeight(int height)
    {
        super.setHeight(height);

        scrollBar.setHeight(this.height);
    }

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
                int transX = x - control.getX();
                int transY = y - control.getY();

                GuiControl res = control.hitTest(transX, transY);
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
    public int translateToScreenX(int x)
    {
        return parent.translateToScreenX(this.x + x) - scrollX;
    }

    @Override
    public int translateToScreenY(int y)
    {
        return parent.translateFromScreenY(this.y + y) - scrollY;
    }

    @Override
    public int translateFromScreenX(int x)
    {
        return parent.translateFromScreenX(x + scrollX) - this.x;
    }

    @Override
    public int translateFromScreenY(int y)
    {
        return parent.translateFromScreenY(y + scrollY) - this.y;
    }

    @Override
    public void draw()
    {
        if(!this.visible)
            return;

        if(this.enableScrollBar)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.scrollBar.getX(), this.scrollBar.getY(), 0);
            this.scrollBar.draw();
            GlStateManager.popMatrix();
        }

        this.start();

        //GlStateManager.pushMatrix();
        //GlStateManager.translate(this.x, this.y, 0);

        for(GuiControl control : controls)
        {
            if(control.isVisible())
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(control.getX(), control.getY(), 0);
                control.preDraw();
                assertClippingPlane();
                control.draw();
                control.postDraw();
                GlStateManager.popMatrix();
            }
        }
        //GlStateManager.popMatrix();

        this.finish();
    }

    @Override
    protected int getRightMargin()
    {
        return enableScrollBar ? 8 : 0;
    }

    public void setEnableScrollBar(boolean enable)
    {
        this.enableScrollBar = enable;
    }
}
