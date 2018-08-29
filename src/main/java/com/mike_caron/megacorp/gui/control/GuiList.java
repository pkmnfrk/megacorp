package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.List;

public class GuiList
    extends GuiClippedSized
{
    Producer producer;
    int nubY = 0;

    boolean draggingNub = false;
    int draggingStartMouse = 0;
    int draggingStartNub = 0;
    int mouseX = -1, mouseY = -1;

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
            int over = -1;

            over = getItemOver(mouseX,mouseY - 1, itemHeight);

            GL11.glPushMatrix();
            GL11.glTranslatef(1, 1 + numSkipped * itemHeight, 0);
            for(int i = 0; i < numItems && i + numSkipped < this.producer.getNumItems(); i++)
            {
                ListItem item = this.producer.getItem(i + numSkipped);

                item.draw(this.width - 2 - 8, itemHeight, ListItemState.forState(over == i + numSkipped));

                //if(i == 1) GuiUtil.drawDebugFlatRectangle(1, i * itemHeight - scrollX, this.width - 2 - 8, itemHeight);

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
    public void onMouseWheel(int mouseX, int mouseY, int deltaWheel)
    {
        setNubY(nubY - deltaWheel);
    }

    @Override
    public void onMouseMove(int mouseX, int mouseY)
    {
        if(draggingNub)
        {
            int dy = mouseY - draggingStartMouse;

            setNubY(draggingStartNub + dy);
        }
    }

    @Override
    public void onMouseExit()
    {
        this.mouseX = -1;
        this.mouseY = -1;
    }

    @Override
    public void onMouseOver(int mouseX, int mouseY)
    {
        this.mouseX = mouseX - this.x;
        this.mouseY = mouseY - this.y;
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
            setNubY(nubY - 1);
        }
        else if(GuiUtil.inBounds(mouseX - this.x, mouseY - this.y, sX, this.height - 9, 8, 8))
        {
            setNubY(nubY + 1);
        }
        else if(GuiUtil.inBounds(mouseX - this.x, mouseY - this.y, sX, 9, 8, this.height - 18))
        {
            setNubY(mouseY - this.y - 9 - 4);
        }
        else if(GuiUtil.inBounds(mouseX - this.x, mouseY - this.y, 1, 1, this.width - 8 - 1 - 1, this.height - 2))
        {
            if(this.producer != null)
            {
                int over = getItemOver(mouseX - this.x, mouseY - this.y - 1, this.producer.getItemHeight());

                if(over != -1 && over < this.producer.getNumItems())
                {
                    this.producer.onClick(over);
                }
            }
        }
    }

    public void setNubY(int newNubY)
    {
        int trackHeight = this.height - 26;
        this.nubY = Math.max(0, Math.min(trackHeight, newNubY));
        scrollY = maxScrollHeight() * this.nubY / trackHeight;
    }

    private int getItemOver(int mouseX, int mouseY, int itemHeight)
    {
        if(mouseX >= 1 && mouseX < this.width - 9)
        {
            return (mouseY + scrollY) / itemHeight;
        }

        return -1;
    }

    @Nullable
    @Override
    public List<String> getTooltip(int mouseX, int mouseY)
    {
        if(this.producer != null)
        {
            int over = getItemOver(mouseX - this.x, mouseY - this.y - 1, this.producer.getItemHeight());

            if(over != -1 && over < this.producer.getNumItems())
            {
                int itemHeight = this.producer.getItemHeight();

                int adjustedY = mouseY + scrollY - this.y - 1;

                int realY = adjustedY - (itemHeight * (adjustedY / itemHeight));

                return this.producer.getItem(over).getTooltip(mouseX - this.x - 1, realY, this.width - 10);
            }
        }

        return null;
    }

    public interface Producer
    {
        int getNumItems();
        int getItemHeight();
        ListItem getItem(int i);
        void onClick(int i);
    }

    public interface ListItem
    {
        //void draw(int y);
        void draw(int width, int height, ListItemState state);
        default List<String> getTooltip(int mouseX, int mouseY, int width) { return null; };
    }

    public enum ListItemState
    {
        NORMAL(false),
        MOUSE_OVER(true)
        ;

        boolean isOver;
        ListItemState(boolean isOver)
        {
            this.isOver = isOver;
        }

        public boolean isOver()
        {
            return this.isOver;
        }

        public static ListItemState forState(boolean isOver)
        {
            if(isOver)
            {
                return ListItemState.MOUSE_OVER;
            }
            else
            {
                return ListItemState.NORMAL;
            }
        }
    }
}
