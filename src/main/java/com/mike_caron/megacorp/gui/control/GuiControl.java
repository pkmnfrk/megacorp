package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.client.gui.Gui;

import javax.annotation.Nullable;
import java.util.*;

public abstract class GuiControl
    extends Gui
{
    protected IGuiGroup parent;
    protected int x;
    protected int y;
    protected int zIndex;
    protected boolean enabled = true, visible = true;

    protected final Vector<EventListener> listeners = new Vector<>();

    public final Map<String, Object> extraData = new HashMap<>();
    protected List<String> tooltipText = null;

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

    public int getzIndex() { return this.zIndex; }

    public void setzIndex(int zIndex) {
        this.zIndex = zIndex;
        if(this.parent != null)
            this.parent.sort();
    }

    public abstract int getWidth();
    public abstract int getHeight();

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
    public GuiControl hitTest(int x, int y)
    {
        if(GuiUtil.inBoundsThis(x, y, this))
        {
            return this;
        }

        return null;
    }

    public void onKeyTyped(char typedChar, int keyCode) { }

    public void onMouseEnter() {
        if(parent != null)
        {
            parent.onMouseEnter();
        }
    }

    public void onMouseExit() {
        if(parent != null)
        {
            parent.onMouseEnter();
        }
    }

    public void onMouseOver(int mouseX, int mouseY)
    {
        if(parent != null)
        {
            parent.onMouseOver(mouseX + this.getX(), mouseY + this.getY());
        }
    }

    public void onMouseDown(int mouseX, int mouseY, int button)
    {
        if(parent != null)
        {
            parent.onMouseDown(mouseX + this.getX(), mouseY + this.getY(), button);
        }
    }

    public void onMouseUp(int mouseX, int mouseY, int button)
    {
        if(parent != null)
        {
            parent.onMouseUp(mouseX + this.getX(), mouseY + this.getY(), button);
        }
    }

    public void onMouseMove(int mouseX, int mouseY)
    {
        if(parent != null)
        {
            parent.onMouseMove(mouseX + this.getX(), mouseY + this.getY());
        }
    }

    public void onMouseWheel(int mouseX, int mouseY, int deltaWheel)
    {
        if(parent != null)
        {
            parent.onMouseWheel(mouseX + this.getX(), mouseY + this.getY(), deltaWheel);
        }
    }

    public void update() { }
    public void preDraw() {}
    public abstract void draw();
    public void postDraw() {}

    public void setTooltip(String text)
    {
        this.tooltipText = new ArrayList<>();
        this.tooltipText.add(text);
    }

    public void addListener(EventListener obj)
    {
        this.listeners.add(obj);
    }

    public void removeListener(EventListener obj)
    {
        this.listeners.remove(obj);
    }

    @Nullable
    public List<String> getTooltip(int mouseX, int mouseY)
    {
        return this.tooltipText;
    }

    public void setTooltip(List<String> text)
    {
        this.tooltipText = new ArrayList<>();
        this.tooltipText.addAll(text);
    }
}
