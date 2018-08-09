package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.block.ContainerBase;
import com.mike_caron.megacorp.gui.control.GuiControl;
import com.mike_caron.megacorp.gui.control.GuiMultilineLabel;
import com.mike_caron.megacorp.gui.control.GuiTranslatedLabel;
import com.mike_caron.megacorp.gui.control.IGuiGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import org.lwjgl.input.Keyboard;

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
    }

    protected void onContainerRefresh()
    {

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
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        for (Gui control : this.controls) {
            if(control instanceof GuiButton)
            {
                if(state == 0)
                {
                    ((GuiButton) control).mouseReleased(mouseX, mouseY);
                }
            }

        }

        super.mouseReleased(mouseX, mouseY, state);
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
                    this.textFieldLostFocus(textField);
                }
            }
            else if(control instanceof GuiButton)
            {
                if(mouseButton == 0)
                {
                    if(((GuiButton) control).mousePressed(this.mc, mouseX, mouseY))
                    {
                        ((GuiButton)control).playPressSound(this.mc.getSoundHandler());
                        this.actionPerformed((GuiButton)control);

                    }
                }
            }

        }

        Slot slot = this.getSlotUnderMouse();

        if(slot != null)
        {

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
                        this.textFieldLostFocus(textField);
                    }

                    break;
                }
            }
        }

        if (!textFocused)
            super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);
        /*this.mc = mc;
        this.itemRender = mc.getRenderItem();
        this.fontRenderer = mc.fontRenderer;
        this.width = width;
        this.height = height;

        if (!MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Pre(this, this.buttonList))) {
            this.buttonList.clear();
            //this.initGui();
        }

        MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post(this, this.buttonList));
        */
    }

    @Override
    public final void initGui()
    {
        super.initGui();

        //this.controls.clear();

        //addControls();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        /*String title = getTitleKey();
        if(title != null)
        {
            this.fontRenderer.drawString(title, 6, 6, GuiUtil.FONT_COLOUR);
        }*/

        GlStateManager.pushMatrix();
        //GlStateManager.translate(-guiLeft, -guiTop, 0);

        for (Gui control : this.controls) {
            if (control instanceof GuiTextField) {
                ((GuiTextField)control).drawTextBox();
            }
            else if (control instanceof GuiButton)
            {
                GlStateManager.color(1, 1, 1, 1);
                ((GuiButton)control).drawButton(this.mc, mouseX, mouseY, 0f);
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

    protected void textFieldLostFocus(GuiTextField textField)
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
}
