package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;

import javax.annotation.Nonnull;
import java.awt.*;

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

        GuiUtil.draw3x3Stretched(this.x, this.y, this.width, this.height, 16, 16);

        String beforeCursor = currentString.substring(0, cursorPosition);

        int cursorX = this.parent.getFontRenderer().getStringWidth(beforeCursor);

        this.parent.getFontRenderer().drawString(currentString, this.x + 1, this.y + 1, foreColor);

    }
}
