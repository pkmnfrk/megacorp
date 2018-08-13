package com.mike_caron.megacorp.gui.control;

import net.minecraft.client.gui.Gui;

import javax.annotation.Nullable;

public abstract class GuiControl
    extends Gui
{
    protected IGuiGroup parent;
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

    public boolean isEnabled()
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

    public IGuiGroup getParent()
    {
        return this.parent;
    }

    public void setParent(IGuiGroup parent)
    {
        this.parent = parent;
    }

    public boolean canHaveFocus() { return false; }

    public boolean hasFocus() { return false; }

    public void setFocused(boolean focused) { }

    @Nullable
    public GuiControl hitTest(int x, int y) { return null; }

    public void onKeyTyped(char typedChar, int keyCode) { }

    public void onMouseEnter() {}
    public void onMouseExit() {}
    public void onMouseOver(int mouseX, int mouseY) {}
    public void onMouseDown(int mouseX, int mouseY, int button) {}
    public void onMouseUp(int mouseX, int mouseY, int button) {}

    public void update() { }
    public abstract void draw();
}
