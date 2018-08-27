package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiList
    extends GuiClippedSized
{
    Producer producer;

    public GuiList(int x, int y, int width, int height, Producer producer)
    {
        super(x, y, width, height);

        this.producer = producer;
    }

    @Override
    public void draw()
    {
        // TODO: implement
        GuiUtil.setGLColor(Color.WHITE);
        GuiUtil.bindTexture(GuiUtil.MISC_RESOURCES);
        //TODO: this is not correct
        GuiUtil.draw3x3Stretched(scrollX, scrollY, this.width, this.height, 16, 16);

        if(this.producer != null)
        {
            int itemHeight = this.producer.getItemHeight();
            int numItems = maxVisibleItems();

            int numSkipped = scrollY / itemHeight;

            GL11.glPushMatrix();
            GL11.glTranslatef(1, 1, 0);
            for(int i = 0; i < numItems && i + numSkipped < this.producer.getNumItems(); i++)
            {
                ListItem item = this.producer.getItem(i + numSkipped);

                item.draw(this.width - 2, itemHeight);
                GL11.glTranslatef(0, itemHeight, 0);

            }
            GL11.glPopMatrix();

        }
    }

    private int maxVisibleItems()
    {
        if(this.producer == null) return 0;

        return 2 + this.height / this.producer.getItemHeight();
    }

    private int maxScrollHeight()
    {
        if(this.producer == null) return 0;
        return Math.max(0, this.producer.getItemHeight() * this.producer.getNumItems() - (this.height - 2));
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
