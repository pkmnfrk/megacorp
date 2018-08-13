package com.mike_caron.megacorp.gui.control;

import net.minecraft.client.gui.GuiTextField;

import javax.annotation.Nonnull;
import java.util.EventListener;

public class GuiWrappedTextBox
    extends GuiSized
{
    String currentText = "";

    @Override
    public void update()
    {
        if(textField != null)
        {
            textField.updateCursorCounter();
        }
    }

    @Override
    public void onMouseDown(int mouseX, int mouseY, int button)
    {
        if(!enabled || !visible) return;

        boolean focused = textField.isFocused();

        textField.mouseClicked(mouseX, mouseY, button);

        if(!focused && textField.isFocused())
        {
            if(!this.parent.notifyTakeFocus(this))
            {
                textField.setFocused(false);
            }
        }
        else if(focused && !textField.isFocused())
        {
            String newText = textField.getText();
            if(!newText.equals(currentText))
            {
                currentText = newText;
                this.triggerChangedEvent();
            }
        }
    }

    @Override
    public void onMouseUp(int mouseX, int mouseY, int button)
    {

    }

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
        return isEnabled();
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
            boolean isFocused = textField.isFocused();

            textField.setFocused(focused);

            if(isFocused && !focused)
            {
                String newText = textField.getText();
                if(!newText.equals(currentText))
                {
                    currentText = newText;
                    this.triggerChangedEvent();
                }
            }
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

    private void triggerChangedEvent()
    {
        GuiTextBox.ChangedEvent evt = new GuiTextBox.ChangedEvent(this);

        for(EventListener listener : listeners)
        {
            if(listener instanceof GuiTextBox.TextboxListener)
            {
                ((GuiTextBox.TextboxListener) listener).changed(evt);
            }
        }
    }

    public void addTextboxListener(GuiTextBox.TextboxListener listener)
    {
        this.listeners.add(listener);
    }

    public void removeTextboxListener(GuiTextBox.TextboxListener listener)
    {
        this.listeners.remove(listener);
    }
}
