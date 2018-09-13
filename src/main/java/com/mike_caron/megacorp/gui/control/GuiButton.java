package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.client.renderer.GlStateManager;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.EventListener;

public class GuiButton
    extends GuiSized
{
    private int id;

    private String label;
    private State state;
    private GuiImage image;

    public GuiButton(int id, int x, int y, int width, int height, String label)
    {
        super(x, y, width, height);

        this.id = id;
        this.label = label;
        this.state = State.NORMAL;
    }

    @Override
    public void setParent(IGuiGroup parent)
    {
        super.setParent(parent);
        if(image != null)
        {
            image.setParent(parent);
        }
    }

    public GuiButton(int id, int x, int y, int width, int height, String label, GuiImage image)
    {
        super(x, y, width, height);

        this.id = id;
        this.label = label;
        this.state = State.NORMAL;
        this.image = image;
    }

    @Override
    public void onMouseUp(int mouseX, int mouseY, int button)
    {
        if(!enabled) return;
        if(button != 0) return;

        if(GuiUtil.inBoundsThis(mouseX, mouseY, this))
        {
            this.state = State.HOVERED;

            // do action
            this.triggerClicked();
        }
        else
        {
            this.state = State.NORMAL;
        }
    }

    @Override
    public void onMouseDown(int mouseX, int mouseY, int button)
    {
        if(!enabled) return;
        if(button != 0) return;

        this.state = State.PRESSED;
    }

    @Override
    public void draw()
    {
        if(!visible)
            return;

        Color fore = Color.WHITE;

        if(!enabled)
        {
            fore = Color.LIGHT_GRAY;
            state = State.NORMAL;
        }

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
        GuiUtil.draw3x3(0, 0, this.width, this.height, sx, 0);

        int imageX = this.width / 2;
        int labelX = this.width / 2;
        int labelWidth = 0;

        if(label != null)
        {
            labelWidth = this.parent.getFontRenderer().getStringWidth(label);
            labelX -= labelWidth / 2;
        }

        if(image != null)
        {
            imageX -= image.getWidth() / 2;
        }



        if(image != null && label != null)
        {
            imageX -= labelWidth / 2 + 2;
            labelX += image.getWidth() / 2 + 2;
        }

        if(label != null)
        {
            drawShadowedText(label, labelX, this.height / 2 - 5, fore, Color.BLACK);
        }
        if(image != null)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(imageX, this.height / 2 - image.getHeight() / 2, 0);
            image.draw();
            GlStateManager.popMatrix();
        }
    }
    private void drawShadowedText(String label, int x, int y, Color fore, Color shadow)
    {
        this.parent.getFontRenderer().drawString(label, x + 1, y + 1, shadow.getRGB());
        this.parent.getFontRenderer().drawString(label, x, y, fore.getRGB());
    }

    @Override
    public void onMouseEnter()
    {
        if(!this.enabled)
            return;

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

    private void triggerClicked()
    {
        if(!this.enabled)
            return;

        ClickedEvent evt = new ClickedEvent(this,id);

        for(EventListener listener : listeners)
        {
            if(listener instanceof ClickedListener)
            {
                ((ClickedListener) listener).clicked(evt);
            }
        }
    }

    enum State
    {
        NORMAL,
        HOVERED,
        PRESSED
    }

    public interface ClickedListener
        extends EventListener
    {
        void clicked(ClickedEvent event);
    }

    public static class ClickedEvent
        extends ControlEvent
    {
        public final int id;

        public ClickedEvent(GuiControl control, int id)
        {
            super(control);
            this.id = id;
        }
    }
}
