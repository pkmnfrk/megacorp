package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.EventListener;

public class GuiTextBox
    extends GuiSized
{
    private String currentString;
    private int foreColor;

    private int cursorPosition = 0;
    private int selectStart = 0;
    private int selectEnd = 0;

    public GuiTextBox(int x, int y, int width, int height)
    {
        this(x, y, width, height, Color.WHITE.getRGB());
    }

    public GuiTextBox(int x, int y, int width, int height, int foreColor)
    {
        super(x, y, width, height);

        this.foreColor = foreColor;
        currentString = "";
    }

    public int getCursorPosition()
    {
        return cursorPosition;
    }

    public void setCursorPosition(int pos)
    {
        if(pos > currentString.length())
        {
            pos = currentString.length();
        }
        else if(pos < 0)
        {
            pos = 0;
        }

        cursorPosition = pos;
    }

    public void setText(@Nonnull String text)
    {
        this.currentString = text;
        this.cursorPosition = this.currentString.length();
    }

    public String getText()
    {
        return currentString;
    }

    @Override
    public void draw()
    {
        if(!visible)
            return;

        GuiUtil.draw3x3Stretched(0,0, this.width, this.height, 16, 16);

        String beforeCursor = currentString.substring(0, cursorPosition);

        int cursorX = this.parent.getFontRenderer().getStringWidth(beforeCursor);

        this.parent.getFontRenderer().drawString(currentString, 1, 1, foreColor);

    }

    private void triggerChangedEvent()
    {
        ChangedEvent evt = new ChangedEvent(this);

        for(EventListener listener : listeners)
        {
            if(listener instanceof TextboxListener)
            {
                ((TextboxListener) listener).changed(evt);
            }
        }
    }

    public void addTextboxListener(TextboxListener listener)
    {
        this.listeners.add(listener);
    }

    public void removeTextboxListener(TextboxListener listener)
    {
        this.listeners.remove(listener);
    }

    public interface TextboxListener
        extends EventListener
    {
        void changed(ChangedEvent event);
    }

    public static class ChangedEvent
        extends ControlEvent
    {
        public ChangedEvent(GuiControl control)
        {
            super(control);
        }
    }
}
