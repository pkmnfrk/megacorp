package com.mike_caron.megacorp.gui;

import net.minecraft.client.gui.Gui;

public abstract class GuiControl
    extends Gui
{
    protected GuiContainerBase parent;
    protected int x;
    protected int y;
    protected boolean enabled = true, visible = true;

    public GuiControl(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public boolean isVisible()
    {
        return this.visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public GuiContainerBase getParent()
    {
        return this.parent;
    }

    public void setParent(GuiContainerBase parent)
    {
        this.parent = parent;
    }

    public abstract void draw();
}
