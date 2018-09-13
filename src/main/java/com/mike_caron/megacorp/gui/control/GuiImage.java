package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;

import java.util.EventListener;

public abstract class GuiImage
    extends GuiControl
{
    public GuiImage(int x, int y)
    {
        super(x,y);
    }

    @Override
    public void onMouseUp(int mouseX, int mouseY, int button)
    {
        if(button == 0 && GuiUtil.inBoundsThis(mouseX, mouseY, this))
        {
            triggerClicked();
        }
    }

    private int getId()
    {
        if(extraData.containsKey("id"))
            return (Integer)extraData.get("id");

        return 0;
    }

    private void triggerClicked()
    {
        for(EventListener listener : this.listeners)
        {
            GuiButton.ClickedEvent evt = new GuiButton.ClickedEvent(this, getId());

            if(listener instanceof GuiButton.ClickedListener)
            {
                ((GuiButton.ClickedListener) listener).clicked(evt);
            }
        }
    }
}
