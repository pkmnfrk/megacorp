package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.client.renderer.GlStateManager;

import javax.annotation.Nonnull;
import java.awt.*;

public class GuiButton
    extends GuiSized
{
    private int id;

    private String label;
    private State state;

    public GuiButton(int id, int x, int y, int width, int height, String label)
    {
        super(x, y, width, height);

        this.id = id;
        this.label = label;
        this.state = State.NORMAL;
    }

    @Override
    public void onMouseUp(int mouseX, int mouseY, int button)
    {
        if(button != 0) return;

        if(GuiUtil.inBounds(mouseX, mouseY, this))
        {
            this.state = State.HOVERED;

            // do action
        }
        else
        {
            this.state = State.NORMAL;
        }
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

        if(label != null)
        {
            int w = this.parent.getFontRenderer().getStringWidth(label);

            this.parent.getFontRenderer().drawString(label, this.x + this.width / 2 - w / 2 + 1, this.y + this.height / 2 - 5 + 1, Color.black.getRGB());
            this.parent.getFontRenderer().drawString(label, this.x + this.width / 2 - w / 2, this.y + this.height / 2 - 5, Color.white.getRGB());
        }
    }

    @Override
    public void onMouseEnter()
    {
        if(this.state == State.NORMAL)
        {
            this.state = State.HOVERED;
        }
    }

    @Override
    public void onMouseExit()
    {
        if(this.state == State.HOVERED)
        {
            this.state = State.NORMAL;
        }
    }

    public void setLabel(@Nonnull String string)
    {
        this.label = string;
    }

    public String getLabel()
    {
        return this.label;
    }

    enum State
    {
        NORMAL,
        HOVERED,
        PRESSED
    }
}
