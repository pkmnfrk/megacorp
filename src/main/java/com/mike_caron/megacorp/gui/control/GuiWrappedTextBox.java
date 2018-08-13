package com.mike_caron.megacorp.gui.control;

import net.minecraft.client.gui.GuiTextField;

import javax.annotation.Nonnull;

public class GuiWrappedTextBox
    extends GuiSized
{
    private GuiTextField textField;
    private String tempText = null;
    private int tempCursorPosition = -1;

    public GuiWrappedTextBox(int x, int y, int width, int height)
    {
        super(x, y, width, height);

    }

    @Override
    public void draw()
    {
        if(!visible)
            return;

        if(textField == null)
        {
            createTextField();
        }

        textField.drawTextBox();
    }

    public int getCursorPosition()
    {
        if(textField != null)
        {
            return textField.getCursorPosition();
        }

        return tempCursorPosition;
    }

    public void setCursorPosition(int pos)
    {
        if(textField != null)
        {
            textField.setCursorPosition(pos);
        }
        else
        {
            tempCursorPosition = pos;
        }
    }

    public void setText(@Nonnull String text)
    {
        if(textField != null)
        {
            textField.setText(text);
        }
        else
        {
            tempText = text;
        }
    }

    public String getText()
    {
        if(textField != null)
        {
            return textField.getText();
        }
        else
        {
            return tempText;
        }
    }

    @Override
    public boolean canHaveFocus()
    {
        return getEnabled();
    }

    @Override
    public boolean hasFocus()
    {
        if(textField != null)
        {
            return textField.isFocused();
        }
        return false;
    }

    @Override
    public void setFocused(boolean focused)
    {
        if(textField != null)
        {
            textField.setFocused(focused);
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode)
    {
        if(textField == null)
        {
            createTextField();
        }

        textField.textboxKeyTyped(typedChar, keyCode);
    }

    private void createTextField()
    {
        if(textField != null)
        {
            tempText = textField.getText();
        }

        textField = new GuiTextField(1, this.parent.getFontRenderer(), this.x, this.y, this.width, this.height);

        textField.setCanLoseFocus(true);

        if(tempText != null)
        {
            textField.setText(tempText);
            tempText = null;
        }

        if(tempCursorPosition != -1)
        {
            textField.setCursorPosition(tempCursorPosition);
            tempCursorPosition = -1;
        }
    }
}
