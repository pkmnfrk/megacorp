package com.mike_caron.megacorp.gui.control;

import com.mike_caron.megacorp.block.ContainerBase;
import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class GuiContainerBase
        extends GuiContainer
    implements IGuiGroup
{
    protected final List<Gui> controls = new ArrayList<>();
    private GuiTranslatedLabel titleLabel;
    protected GuiMultilineLabel insertCardLabel;

    private GuiControl mouseOverControl = null;
    private boolean leftDown = false, rightDown = false;
    private List<List<GuiControl>> waitingForButton = new ArrayList<List<GuiControl>>();

    public GuiContainerBase(ContainerBase inventorySlotsIn)
    {
        super(inventorySlotsIn);

        inventorySlotsIn.setGuiListener(new Runnable()
        {
            @Override
            public void run()
            {
                onContainerRefresh();
            }
        });

        waitingForButton.add(new ArrayList<>());
        waitingForButton.add(new ArrayList<>());
    }

    protected void onContainerRefresh()
    {

    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();

        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth - guiLeft;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1 - guiTop;

        GuiControl newMouseControl = null;

        for(Gui control : this.controls)
        {
            if(control instanceof GuiControl)
            {
                GuiControl ctrl = (GuiControl)control;

                if(!ctrl.isVisible() || !ctrl.isEnabled())
                    continue;

                GuiControl result = ctrl.hitTest(mouseX, mouseY);

                if(result != null)
                {
                    newMouseControl = result;
                }
            }
        }

        if(mouseOverControl != newMouseControl)
        {
            if(mouseOverControl != null)
            {
                mouseOverControl.onMouseExit();
            }
            mouseOverControl = newMouseControl;
            if(mouseOverControl != null)
            {
                mouseOverControl.onMouseEnter();
            }
        }

        if(mouseOverControl != null)
        {
            mouseOverControl.onMouseOver(mouseX, mouseY);
        }


        int button = Mouse.getEventButton();
        if(button >= 0 && button <= 1)
        {
            boolean newState = Mouse.getEventButtonState();

            if(getStateForButton(button) && !newState)
            {
                setStateForButton(button, false);

                //mouseOverControl.onMouseUp(mouseX, mouseY, button);
                for(GuiControl waiting : waitingForButton.get(button))
                {
                    waiting.onMouseUp(mouseX, mouseY, button);
                }
                waitingForButton.get(button).clear();
            }
            else if(!getStateForButton(button) && newState)
            {
                setStateForButton(button, true);
                if(mouseOverControl != null)
                {
                    mouseOverControl.onMouseDown(mouseX, mouseY, button);
                    waitingForButton.get(button).add(mouseOverControl);
                }
            }
        }
    }

    private boolean getStateForButton(int button)
    {
        if(button == 0) return leftDown;
        if(button == 1) return rightDown;
        return false;
    }

    private void setStateForButton(int button, boolean state)
    {
        if(button == 0) leftDown = state;
        if(button == 1) rightDown = state;
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        for(Gui control : this.controls)
        {
            if(control instanceof GuiTextField)
            {
                ((GuiTextField)control).updateCursorCounter();
            }
            else if(control instanceof GuiControl)
            {
                ((GuiControl)control).update();
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        for (Gui control : this.controls) {
            if (control instanceof GuiTextField) {
                GuiTextField textField = (GuiTextField)control;
                boolean wasFocused = textField.isFocused();
                textField.mouseClicked(mouseX, mouseY, mouseButton);
                if(wasFocused && !textField.isFocused())
                {
                    this.controlLostFocus(textField);
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        boolean textFocused = false;

        for (Gui control : this.controls) {
            if (control instanceof GuiTextField) {
                GuiTextField textField = (GuiTextField) control;
                if (textField.isFocused()) {
                    textFocused = true;
                    textField.textboxKeyTyped(typedChar, keyCode);

                    if(keyCode == Keyboard.KEY_ESCAPE)
                    {
                        textField.setFocused(false);
                        this.controlLostFocus(textField);
                    }

                    break;
                }
            }
            else if(control instanceof GuiControl)
            {
                if(((GuiControl) control).hasFocus())
                {
                    textFocused = true;
                    ((GuiControl) control).onKeyTyped(typedChar, keyCode);

                    if(keyCode == Keyboard.KEY_ESCAPE)
                    {
                        ((GuiControl)control).setFocused(false);
                        this.controlLostFocus(control);
                    }

                    break;
                }

            }
        }

        if (!textFocused)
            super.keyTyped(typedChar, keyCode);
    }

    @Override
    public final void initGui()
    {
        super.initGui();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {

        GlStateManager.pushMatrix();

        for (Gui control : this.controls) {
            if (control instanceof GuiTextField) {
                ((GuiTextField)control).drawTextBox();
            }
            else if (control instanceof net.minecraft.client.gui.GuiButton)
            {
                GlStateManager.color(1, 1, 1, 1);
                ((net.minecraft.client.gui.GuiButton)control).drawButton(this.mc, mouseX, mouseY, 0f);
            }
            else if(control instanceof GuiControl)
            {
                ((GuiControl)control).draw();
            }
        }

        GlStateManager.popMatrix();

        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    public void addControl(Gui control)
    {
        this.controls.add(control);
        if(control instanceof GuiControl)
        {
            ((GuiControl)control).setParent(this);
        }
    }

    public void removeControl(Gui control)
    {
        this.controls.remove(control);
    }

    protected void drawInsertCardBackground()
    {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(GuiUtil.EMPTY_GUI);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    /*
    protected void drawInsertCardForeground()
    {
        String message = new TextComponentTranslation("tile.megacorp:misc.insertcard").getUnformattedText();

        drawCenteredWrappedString(message, 88, 45, 154);
    }
    */

    protected String getTitleKey()
    {
        return null;
    }

    protected void drawCenteredWrappedString(String message, int x, int y, int wrapWidth)
    {
        List<String> lines = this.fontRenderer.listFormattedStringToWidth(message, wrapWidth);
        int height = 10 * lines.size();
        int ty = y - height / 2;

        for(int i = 0; i < lines.size(); i++)
        {
            int w = this.fontRenderer.getStringWidth(lines.get(i));
            this.fontRenderer.drawString(lines.get(i), x - w / 2, ty, GuiUtil.FONT_COLOUR);
            ty += 10;
        }
    }

    protected void controlLostFocus(Gui textField)
    {

    }

    protected void initControls()
    {
        addControls();
    }

    protected void addControls()
    {
        titleLabel = new GuiTranslatedLabel(6, 6, GuiUtil.FONT_COLOUR, getTitleKey());
        this.addControl(titleLabel);

        insertCardLabel = GuiUtil.staticMultilineLabelFromTranslationKey(8, 16, 160, 53,"tile.megacorp:misc.insertcard");
        insertCardLabel.setAlignment(GuiMultilineLabel.Alignment.CENTER);
        insertCardLabel.setVisible(false);
        this.addControl(insertCardLabel);
    }

    public FontRenderer getFontRenderer()
    {
        return this.fontRenderer;
    }

    @Override
    public void addControl(GuiControl control)
    {
        this.addControl((Gui)control);
    }

    @Override
    public void removeControl(GuiControl control)
    {
        this.removeControl((Gui)control);
    }

    protected void onActionPerformed(GuiControl control)
    {

    }

    @Override
    public boolean notifyTakeFocus(GuiControl taker)
    {
        //first, look for objections
        /*
        for(Gui control : controls)
        {
            if(control instanceof GuiTextField)
            {
                //hum, how to determine if a text field can lose focus?

                //if(((GuiTextField)control).get) {
                //    return false;
                //}
            }
        }*/

        //no objections, lady
        for(Gui control : controls)
        {
            if(control instanceof GuiControl)
            {
                if (control != taker && ((GuiControl)control).hasFocus())
                {
                    ((GuiControl)control).setFocused(false);
                }
            }
            else if(control instanceof GuiTextField)
            {
                ((GuiTextField) control).setFocused(false);
            }
        }

        return true;
    }
}
