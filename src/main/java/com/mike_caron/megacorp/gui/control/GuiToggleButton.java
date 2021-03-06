package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.client.renderer.GlStateManager;

import java.util.EventListener;

public class GuiToggleButton
    extends GuiSized
{
    //protected State state = State.NORMAL;
    protected boolean isMouseOver;
    protected boolean isMouseDown;
    protected boolean pressed;
    protected int id;

    @Override
    public void onMouseEnter()
    {
        isMouseOver = true;
    }

    @Override
    public void onMouseExit()
    {
        isMouseOver = false;
    }

    public GuiToggleButton(int id, int x, int y, int width, int height)
    {
        super(x, y, width, height);
        this.id = id;
    }


    @Override
    public void onMouseUp(int mouseX, int mouseY, int button)
    {
        if(button != 0) return;

        if(GuiUtil.inBoundsThis(mouseX, mouseY, this))
        {
            pressed = !pressed;

            // do action
            this.triggerClicked();
        }

        isMouseDown = false;
    }

    public void setPressed(boolean pressed)
    {
        this.pressed = pressed;
    }

    public boolean getPressed()
    {
        return this.pressed;
    }

    @Override
    public void onMouseDown(int mouseX, int mouseY, int button)
    {
        if(button != 0) return;

        isMouseDown = true;
    }

    private State calcState()
    {
        if(isMouseDown)
        {
            return State.PRESSED;
        }
        else if(isMouseOver && !pressed)
        {
            return State.HOVERED;
        }
        else if(pressed)
        {
            return State.PRESSED;
        }
        else
        {
            return State.NORMAL;
        }
    }

    @Override
    public void draw()
    {
        if(!visible)
            return;

        int sx = 0;
        State state = calcState();

        switch(state)
        {
            case NORMAL:
                sx = 16;
                break;
            case HOVERED:
                sx = 32;
                break;
            case PRESSED:
                sx = 48;
                break;
        }

        GuiUtil.bindTexture(GuiUtil.MISC_RESOURCES);
        GlStateManager.color(1, 1, 1, 1);
        GuiUtil.draw3x3(0, 0, this.width, this.height, sx, 0);

        /*
        if(label != null)
        {
            int w = this.parent.getFontRenderer().getStringWidth(label);

            this.parent.getFontRenderer().drawString(label, this.x + this.width / 2 - w / 2 + 1, this.y + this.height / 2 - 5 + 1, Color.black.getRGB());
            this.parent.getFontRenderer().drawString(label, this.x + this.width / 2 - w / 2, this.y + this.height / 2 - 5, Color.white.getRGB());
        }
        */
    }

    enum State
    {
        NORMAL,
        HOVERED,
        PRESSED
    }

    private void triggerClicked()
    {
        ChangedEvent evt = new ChangedEvent(this, this.id, this.pressed);

        for(EventListener listener : listeners)
        {
            if(listener instanceof ChangedListener)
            {
                ((ChangedListener) listener).changed(evt);
            }
        }
    }

    public interface ChangedListener
        extends EventListener
    {
        void changed(ChangedEvent event);
    }

    public static class ChangedEvent
        extends ControlEvent
    {
        public final boolean newState;
        public final int id;

        public ChangedEvent(GuiControl control, int id, boolean newState)
        {
            super(control);
            this.id = id;
            this.newState = newState;
        }
    }
}
