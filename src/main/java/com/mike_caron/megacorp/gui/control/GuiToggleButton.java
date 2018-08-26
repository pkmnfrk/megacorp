package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.client.renderer.GlStateManager;

import java.util.EventListener;

public class GuiToggleButton
    extends GuiSized
{
    protected State state = State.NORMAL;
    protected boolean pressed;
    protected int id;

    @Override
    public void onMouseEnter()
    {
        if(!pressed)
        {
            state = State.HOVERED;
        }
    }

    @Override
    public void onMouseExit()
    {
        if(!pressed)
        {
            state = State.NORMAL;
        }
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

        if(GuiUtil.inBounds(mouseX, mouseY, this))
        {
            if(pressed)
            {
                pressed = false;
                this.state = State.HOVERED;
            }
            else
            {
                pressed = true;
                this.state = State.PRESSED;
            }

            // do action
            // this.triggerClicked();
        }
        else
        {
            this.state = pressed ? State.PRESSED : State.NORMAL;
        }
    }

    public void setPressed(boolean pressed)
    {
        if(this.state != State.PRESSED || !pressed)
        {
            this.state = pressed ? State.PRESSED : State.NORMAL;
        }
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

        this.state = State.PRESSED;
    }

    @Override
    public void draw()
    {
        if(!visible)
            return;

        int sx = 0;
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
        GuiUtil.draw3x3(this.x, this.y, this.width, this.height, sx, 0);

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

    public void addListener(ChangedListener listener)
    {
        this.listeners.add(listener);
    }

    public void removeListener(ChangedListener listener)
    {
        this.listeners.remove(listener);
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
