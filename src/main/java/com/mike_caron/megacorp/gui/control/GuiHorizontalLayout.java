package com.mike_caron.megacorp.gui.control;

public class GuiHorizontalLayout
    extends GuiGroup
    implements IGuiGroup
{
    private int margin;

    public GuiHorizontalLayout(int x, int y, int width, int height, int margin)
    {
        super(x, y, width, height);

        this.margin = margin;
    }

    @Override
    public void setWidth(int width)
    {
        super.setWidth(width);

        doLayout();
    }

    @Override
    public void setHeight(int height)
    {
        super.setHeight(height);

        doLayout();
    }

    @Override
    public void addControl(GuiControl control)
    {
        super.addControl(control);

        doLayout();
    }

    @Override
    public void removeControl(GuiControl control)
    {
        super.removeControl(control);

        doLayout();
    }

    private void doLayout()
    {
        if(this.controls.size() < 1) return;

        int totalWidth = 0;

        totalWidth += margin * (this.controls.size() - 1);

        for(GuiControl control : this.controls)
        {
            totalWidth += control.getWidth();
        }

        int left = this.width / 2 - totalWidth / 2;

        for(GuiControl control : this.controls)
        {
            control.setY(this.height / 2 - control.getHeight() / 2);
            control.setX(left);

            left += control.getWidth() + margin;
        }
    }
}
