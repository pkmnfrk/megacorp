package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuiScrollPort
    extends GuiClippedSized
    implements IGuiGroup, GuiScrollBar.ScrollListener
{
    protected final List<GuiControl> controls = new ArrayList<>();

    private boolean enableScrollBar = false;

    protected final GuiScrollBar scrollBar;

    int maxScrollX = 100000;
    int maxScrollY = 100000;


    public GuiScrollPort(int x, int y, int width, int height)
    {
        super(x, y, width, height);

        scrollBar = new GuiScrollBar(this.width - 8, 0, 8, this.height);
        scrollBar.addListener(this);

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
        if(GuiUtil.inBoundsThis(x, y, this))
        {
            if(GuiUtil.inBounds(x, y, marginLeft, marginTop, this.width - marginLeft - marginRight, this.height - marginTop - marginBottom))
            {
                for (GuiControl control : controls)
                {
                    if (control.isVisible())
                    {
                        int transX = x - control.getX() + scrollX;
                        int transY = y - control.getY() + scrollY;

                        GuiControl res = control.hitTest(transX, transY);
                        if (res != null)
                            return res;
                    }
                }
            }
            return this;
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
    public void onMouseOver(int mouseX, int mouseY)
    {
        if(enableScrollBar)
        {
            if(GuiUtil.inBoundsThis(mouseX, mouseY, scrollBar))
            {
                scrollBar.onMouseOver(mouseX - scrollBar.getX(), mouseY - scrollBar.getY());
            }
        }
    }

    @Override
    public void onMouseDown(int mouseX, int mouseY, int button)
    {
        if(enableScrollBar)
        {
            if(GuiUtil.inBounds(mouseX, mouseY, scrollBar))
            {
                scrollBar.onMouseDown(mouseX - scrollBar.getX(), mouseY - scrollBar.getY(), button);
            }
        }
    }

    @Override
    public void onMouseUp(int mouseX, int mouseY, int button)
    {
        if(enableScrollBar)
        {
            scrollBar.onMouseUp(mouseX - scrollBar.getX(), mouseY - scrollBar.getY(), button);
        }
    }

    @Override
    public void onMouseMove(int mouseX, int mouseY)
    {
        if(enableScrollBar)
        {
            scrollBar.onMouseMove(mouseX - scrollBar.getX(), mouseY - scrollBar.getY());
        }
    }

    @Override
    public void onMouseWheel(int mouseX, int mouseY, int deltaWheel)
    {
        scrollBar.setProgress(scrollBar.getProgress() - scrollBar.getOneClick() * deltaWheel);
    }

    public void setEnableScrollBar(boolean enable)
    {
        this.enableScrollBar = enable;
        if(this.enableScrollBar)
        {
            this.marginRight = 8;
        }
        else
        {
            this.marginRight = 0;
        }
    }

    @Override
    public void scrolled(GuiScrollBar.ScrollEvent event)
    {
        this.scrollY = (int)Math.floor(maxScrollY * event.progress);
    }

    @Override
    public void setScrollY(int scrollY)
    {
        super.setScrollY(scrollY);

        if(maxScrollY == 0)
        {
            scrollBar.setProgress(0f);
        }
        else
        {
            scrollBar.setProgress((1f * scrollY) / maxScrollY);
        }
    }

    public void setMaxScrollY(int maxScrollY)
    {
        if(maxScrollY < 0)
        {
            maxScrollY = 0;
        }
        this.maxScrollY = maxScrollY;
        if(maxScrollY != 0)
        {
            this.scrollBar.setOneClick(7f / maxScrollY);
        }
        else
        {
            this.scrollBar.setOneClick(1f);
        }

        if(this.scrollY > maxScrollY)
        {
            this.scrollY = maxScrollY;
        }
    }
}
