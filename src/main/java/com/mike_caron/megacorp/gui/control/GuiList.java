package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.List;

public class GuiList
    extends GuiClippedSized
    implements GuiScrollBar.ScrollListener
{
    Producer producer;

    private GuiScrollBar scrollBar;

    private int lastNumItems = -1;

    int mouseX = -1, mouseY = -1;

    public GuiList(int x, int y, int width, int height, Producer producer)
    {
        super(x, y, width, height);

        this.producer = producer;
        this.scrollBar = new GuiScrollBar(this.width - 9, 1, 8, this.height - 2);
        this.scrollBar.addListener(this);

        lastNumItems = this.producer.getNumItems();
        this.scrollBar.setOneClick(getScrollClick());

        marginTop = 1;
        marginBottom = 1;
        marginLeft = 1;
        marginRight = 1;
    }

    @Override
    public void update()
    {
        if(this.producer.getNumItems() != lastNumItems)
        {
            lastNumItems = this.producer.getNumItems();
            this.scrollBar.setOneClick(getScrollClick());
        }
    }

    @Override
    public void draw()
    {
        int sw = scrollBarWidth();

        GuiUtil.setGLColor(Color.WHITE);
        GuiUtil.bindTexture(GuiUtil.MISC_RESOURCES);

        GuiUtil.draw3x3Stretched(0, 0, this.width, this.height, 16, 16);

        if(sw > 0)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(scrollBar.getX(), scrollBar.getY(), 0);
            scrollBar.draw();
            GlStateManager.popMatrix();
        }

        //GuiUtil.drawTexturePart(this.width - 9 + scrollX, 1 + scrollY, 8, 8, 64, 0, 256, 256);
        //GuiUtil.drawTexturePart(this.width - 9 + scrollX, this.height - 9 + scrollY, 8, 8, 64, 8, 256, 256);

        //int trackHeight = this.height - 18;

        //GuiUtil.draw3x3(this.width - 9 + scrollX, 9 + scrollY, 8, trackHeight, 72, 0, 2, 4);

        //GuiUtil.drawTexturePart(this.width - 9 + scrollX, 9 + nubY + scrollY, 8, 8, 72, 8, 256, 256);


        //setClippingPlane(parent.translateToScreenX(this.x) + 1, parent.translateToScreenY(this.y) + this.height - 1, this.width - 2, this.height - 2);

        if(this.producer != null)
        {
            int itemHeight = this.producer.getItemHeight();
            int numItems = maxVisibleItems();

            int numSkipped = scrollY / itemHeight;
            int over = -1;

            over = getItemOver(mouseX,mouseY - 1, this.width - 2 - scrollBarWidth(), itemHeight);

            start();

            GL11.glPushMatrix();
            GL11.glTranslatef(1, 1 + numSkipped * itemHeight, 0);
            for(int i = 0; i < numItems && i + numSkipped < this.producer.getNumItems(); i++)
            {
                ListItem item = this.producer.getItem(i + numSkipped);

                item.draw(this.width - 2 - sw, itemHeight, ListItemState.forState(over == i + numSkipped));

                //if(i == 1) GuiUtil.drawDebugFlatRectangle(1, i * itemHeight - scrollX, this.width - 2 - 8, itemHeight);

                GL11.glTranslatef(0, itemHeight, 0);
            }
            GL11.glPopMatrix();

            finish();
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
    public void setWidth(int width)
    {
        super.setWidth(width);

        scrollBar.setX(this.width - 9);
    }

    @Override
    public void setHeight(int height)
    {
        super.setHeight(height);

        this.scrollBar.setHeight(this.height - 2);
        this.scrollBar.setOneClick(getScrollClick());
    }

    @Override
    public void onMouseUp(int mouseX, int mouseY, int button)
    {
        scrollBar.onMouseUp(mouseX, mouseY, button);
    }

    @Override
    public void onMouseWheel(int mouseX, int mouseY, int deltaWheel)
    {
        scrollBar.setProgress(scrollBar.getProgress() - scrollBar.getOneClick() * deltaWheel);
    }

    @Override
    public void onMouseMove(int mouseX, int mouseY)
    {
        //if(GuiUtil.inBounds(mouseX, mouseY, scrollBar))
        //{
            scrollBar.onMouseMove(mouseX - scrollBar.getX(), mouseY - scrollBar.getY());
        //}
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
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    @Override
    public void onMouseDown(int mouseX, int mouseY, int button)
    {
        int sX = this.width - 9;

        if(GuiUtil.inBounds(mouseX, mouseY, scrollBar))
        {
            scrollBar.onMouseDown(mouseX - scrollBar.getX(), mouseY - scrollBar.getY(), button);
        }
        else //if(GuiUtil.inBounds(mouseX, mouseY, 1, 1, this.width - scrollBarWidth() - 1 - 1, this.height - 2))
        {
            if(this.producer != null)
            {
                int over = getItemOver(mouseX, mouseY - 1, this.width - 2 - scrollBarWidth(), this.producer.getItemHeight());

                if(over != -1 && over < this.producer.getNumItems())
                {
                    this.producer.onClick(over);
                }
            }
        }
    }

    private float getScrollClick()
    {
        int h = maxScrollHeight();
        if(h == 0) return 1f;

        return 7f / h;
    }

    private int getItemOver(int mouseX, int mouseY, int itemWidth, int itemHeight)
    {
        if(mouseX >= 1 && mouseX < itemWidth + 1)
        {
            return (mouseY + scrollY) / itemHeight;
        }

        return -1;
    }

    private int scrollBarWidth()
    {
        if(producer == null || producer.getNumItems() * producer.getItemHeight() <= this.height - 2) return 0;
        return 8;
    }

    @Nullable
    @Override
    public List<String> getTooltip(int mouseX, int mouseY)
    {
        if(this.producer != null)
        {
            int iw = this.width - 2 - scrollBarWidth();
            int over = getItemOver(mouseX, mouseY - 1, iw, this.producer.getItemHeight());

            if(over >= 0 && over < this.producer.getNumItems())
            {
                int itemHeight = this.producer.getItemHeight();

                int adjustedY = mouseY + scrollY - 1;

                int realY = adjustedY - (itemHeight * (adjustedY / itemHeight));

                return this.producer.getItem(over).getTooltip(mouseX - 1, realY, iw);
            }
        }

        return null;
    }

    @Override
    public void scrolled(GuiScrollBar.ScrollEvent event)
    {
        this.scrollY = (int)Math.floor(maxScrollHeight() * event.progress);
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
