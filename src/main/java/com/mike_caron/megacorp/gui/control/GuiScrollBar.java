package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;

import java.awt.Color;
import java.util.EventListener;

public class GuiScrollBar
    extends GuiSized
{
    int nubY = 0;

    boolean draggingNub = false;
    int draggingStartMouse = 0;
    int draggingStartNub = 0;

    float progress = 0f;
    float oneClick = 0.1f;

    public GuiScrollBar(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    @Override
    public void onMouseUp(int mouseX, int mouseY, int button)
    {
        draggingNub = false;
    }

    @Override
    public void onMouseDown(int mouseX, int mouseY, int button)
    {
        if(GuiUtil.inBounds(mouseX, mouseY, 0, this.width + nubY, this.width, this.width))
        {
            draggingNub = true;
            draggingStartMouse = mouseY;
            draggingStartNub = nubY;
        }
        else if(GuiUtil.inBounds(mouseX, mouseY, 0, 0, this.width, this.width))
        {
            setNubY(nubY - 1);
        }
        else if(GuiUtil.inBounds(mouseX, mouseY, 0, this.height - 8, this.width, this.width))
        {
            setNubY(nubY + 1);
        }
        else if(GuiUtil.inBounds(mouseX, mouseY, 0, this.width, this.width, this.height - this.width * 2))
        {
            setNubY(mouseY - this.width - this.width / 2);
        }
    }

    @Override
    public void onMouseMove(int mouseX, int mouseY)
    {
        if(draggingNub)
        {
            int dy = mouseY - draggingStartMouse;

            setNubY(draggingStartNub + dy);
        }
    }

    @Override
    public void draw()
    {
        GuiUtil.setGLColor(Color.WHITE);
        GuiUtil.bindTexture(GuiUtil.MISC_RESOURCES);
        GuiUtil.draw3x3(0, 0, this.width, this.height, 72, 0, 2, 4);
        GuiUtil.drawStretchedTexturePart(0, 0, this.width, this.width, 64, 0, 8, 8, 256, 256);
        GuiUtil.drawStretchedTexturePart(0, this.height - this.width, this.width, this.width, 64, 8, 8, 8, 256, 256);
        GuiUtil.drawTexturePart(0, this.width + nubY, 8, 8, 72, 8, 256, 256);
    }

    private void setNubY(int nubY)
    {
        int minY = 0;
        int maxY = this.height - this.width * 3;

        if(nubY < minY)
        {
            nubY = minY;
        }
        else if(nubY > maxY)
        {
            nubY = maxY;
        }

        this.nubY = nubY;

        progress =  ((1f * nubY) - minY) / (maxY - minY);

        triggerScrollEvent();
    }

    public float getProgress()
    {
        return progress;
    }

    public void setProgress(float newProgress)
    {
        progress = Math.max(0f, Math.min(1f, newProgress));

        nubY = (int)Math.floor((this.height - this.width * 3) * progress);

        triggerScrollEvent();
    }

    public void setOneClick(float newOneClick)
    {
        this.oneClick = newOneClick;
    }

    public float getOneClick()
    {
        return this.oneClick;
    }

    void triggerScrollEvent()
    {
        ScrollEvent evt = new ScrollEvent(this, progress);

        for(EventListener listener : this.listeners)
        {
            if(listener instanceof ScrollListener)
            {
                ((ScrollListener) listener).scrolled(evt);
            }
        }
    }

    public interface ScrollListener
        extends EventListener
    {
        void scrolled(ScrollEvent event);
    }

    public static class ScrollEvent
        extends ControlEvent
    {
        public final float progress;

        public ScrollEvent(GuiControl control, float progress)
        {
            super(control);

            this.progress = progress;
        }
    }
}
