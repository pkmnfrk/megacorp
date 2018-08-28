package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiList
    extends GuiClippedSized
{
    Producer producer;
    int nubY = 0;

    boolean draggingNub = false;
    int draggingStartMouse = 0;
    int draggingStartNub = 0;

    public GuiList(int x, int y, int width, int height, Producer producer)
    {
        super(x, y, width, height);

        this.producer = producer;
    }

    @Override
    public void draw()
    {
        GuiUtil.setGLColor(Color.WHITE);
        GuiUtil.bindTexture(GuiUtil.MISC_RESOURCES);

        GuiUtil.draw3x3Stretched(scrollX, scrollY, this.width, this.height, 16, 16);
        GuiUtil.drawTexturePart(this.width - 9 + scrollX, 1 + scrollY, 8, 8, 64, 0, 256, 256);
        GuiUtil.drawTexturePart(this.width - 9 + scrollX, this.height - 9 + scrollY, 8, 8, 64, 8, 256, 256);

        int trackHeight = this.height - 18;

        GuiUtil.draw3x3(this.width - 9 + scrollX, 9 + scrollY, 8, trackHeight, 72, 0, 2, 4);

        GuiUtil.drawTexturePart(this.width - 9 + scrollX, 9 + nubY + scrollY, 8, 8, 72, 8, 256, 256);


        setClippingPlane(parent.translateX(this.x) + 1, parent.translateY(this.y) + this.height - 1, this.width - 2, this.height - 2);

        if(this.producer != null)
        {
            int itemHeight = this.producer.getItemHeight();
            int numItems = maxVisibleItems();

            int numSkipped = scrollY / itemHeight;

            GL11.glPushMatrix();
            GL11.glTranslatef(1, 1 + numSkipped * itemHeight, 0);
            for(int i = 0; i < numItems && i + numSkipped < this.producer.getNumItems(); i++)
            {
                ListItem item = this.producer.getItem(i + numSkipped);

                item.draw(this.width - 2 - 8, itemHeight);
                GL11.glTranslatef(0, itemHeight, 0);

            }
            GL11.glPopMatrix();

        }
    }

    private int maxVisibleItems()
    {
        if(this.producer == null) return 0;

        return 1 + this.height / this.producer.getItemHeight();
    }

    private int maxScrollHeight()
    {
        if(this.producer == null) return 0;
        int h = this.producer.getItemHeight();
        return Math.max(0, h * this.producer.getNumItems() - (this.height - 2));
    }

    @Override
    public void onMouseUp(int mouseX, int mouseY, int button)
    {
        draggingNub = false;
    }

    @Override
    public void onMouseOver(int mouseX, int mouseY)
    {
        if(draggingNub)
        {
            int trackHeight = this.height - 26;
            int dy = mouseY - draggingStartMouse;

            nubY = Math.min(trackHeight, Math.max(0, draggingStartNub + dy));
            int representativeValue = maxScrollHeight() * nubY / trackHeight;


            if(representativeValue != scrollY)
            {
                scrollY = representativeValue;
                //draggingStartMouse = mouseY;
            }
        }
    }

    @Override
    public void onMouseDown(int mouseX, int mouseY, int button)
    {
        int sX = this.width - 9;

        if(GuiUtil.inBounds(mouseX - this.x, mouseY - this.y, sX, 9 + nubY, 8, 8))
        {
            draggingNub = true;
            draggingStartMouse = mouseY;
            draggingStartNub = nubY;
        }
        else if(GuiUtil.inBounds(mouseX - this.x, mouseY - this.y, sX, 1, 8, 8))
        {

            nubY = Math.max(0, nubY - 1);
            int trackHeight = this.height - 26;
            scrollY = maxScrollHeight() * nubY / trackHeight;
        }
        else if(GuiUtil.inBounds(mouseX - this.x, mouseY - this.y, sX, this.height - 9, 8, 8))
        {
            int trackHeight = this.height - 26;
            nubY = Math.min(trackHeight, nubY + 1);
            scrollY = maxScrollHeight() * nubY / trackHeight;
        }
    }

    public interface Producer
    {
        int getNumItems();
        int getItemHeight();
        ListItem getItem(int i);
    }

    public interface ListItem
    {
        //void draw(int y);
        void draw(int width, int height);
    }
}
