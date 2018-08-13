package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.client.renderer.GlStateManager;

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
        this.state = State.HOVERED;
    }

    @Override
    public void onMouseExit()
    {
        this.state = State.NORMAL;
    }

    enum State
    {
        NORMAL,
        HOVERED,
        PRESSED
    }
}
