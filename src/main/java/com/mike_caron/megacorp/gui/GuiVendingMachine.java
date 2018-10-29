package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.capital_investor.ContainerCapitalInvestor;
import com.mike_caron.megacorp.block.vending_machine.ContainerVendingMachine;
import com.mike_caron.megacorp.gui.control.*;
import com.mike_caron.megacorp.network.CtoSMessage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class GuiVendingMachine
    extends GuiContainerBase
    implements GuiButton.ClickedListener, GuiList.Producer
{
    public static final int WIDTH = 176;
    public static final int HEIGHT = 236;

    public static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/vending_machine.png");

    private final ContainerVendingMachine container;

    private GuiGroup ownedGroup = new GuiGroup();

    private GuiList rewardList = new GuiList(6, 17, 163, 114, this);
    private GuiButton buyRewardButton = new GuiButton(ContainerVendingMachine.GUI_BUY_REWARD,  124, 135, 45, 14, GuiUtil.translate("tile.megacorp:capital_investor.buy"));
    private GuiFluid fluidGauge = new GuiFluid(7, 136, 113, 12, GuiFluid.Orientation.HORIZONTAL);

    private ContainerVendingMachine.VendingData selectedReward = null;
    private int selectedIndex = -1;

    public GuiVendingMachine(ContainerVendingMachine container)
    {
        super(container, WIDTH, HEIGHT);

        this.container = container;

        initControls();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        buyRewardButton.setEnabled(selectedReward != null && selectedReward.available);
    }

    @Override
    protected void onContainerRefresh()
    {
        //update fluid
        if(selectedReward != null)
        {
            selectedReward = container.rewardList.get(selectedIndex);
        }

        ownedGroup.setVisible(true);

        fluidGauge.setFluid(FluidRegistry.getFluid(container.fluid));
        fluidGauge.setAmount(container.fluidAmount);
        fluidGauge.setCapacity(container.fluidCapacity);

    }

    @Override
    public void addControls()
    {
        super.addControls();

        this.addControl(ownedGroup);

        ownedGroup.addControl(buyRewardButton);
        ownedGroup.addControl(rewardList);
        ownedGroup.addControl(fluidGauge);

        buyRewardButton.addListener(this);
    }

    @Override
    protected String getTitleKey()
    {
        return "tile.megacorp:vending_machine.name";
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i1)
    {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    public void clicked(GuiButton.ClickedEvent event)
    {
        if(event.id == ContainerCapitalInvestor.GUI_BUY_REWARD)
        {
            if (selectedReward != null)
            {
                //CtoSMessage packet = CtoSMessage.forGuiButton(container.getPos(), event.id, selectedReward.id);
                //MegaCorpMod.networkWrapper.sendToServer(packet);
            }
        }
        else
        {
            CtoSMessage packet = CtoSMessage.forGuiButton(container.getPos(), event.id);
            MegaCorpMod.networkWrapper.sendToServer(packet);
        }
    }

    @Override
    public int getNumItems()
    {
        return (container != null && container.rewardList != null) ? container.rewardList.size() : 0;
    }

    @Override
    public int getItemHeight()
    {
        return 18;
    }

    @Override
    public GuiList.ListItem getItem(int i)
    {
        return new RewardListItem(container.rewardList.get(i));
    }

    @Override
    public void onClick(int i)
    {
        selectedReward = container.rewardList.get(i);
        selectedIndex = i;
    }

    class RewardListItem
        implements GuiList.ListItem
    {
        ContainerVendingMachine.VendingData reward;
        List<String> toolTip = new ArrayList<>();

        public RewardListItem(ContainerVendingMachine.VendingData reward)
        {
            this.reward = reward;


            String tmp;

            tmp = NumberFormat.getIntegerInstance().format(reward.cost) + "mB";
            if(reward.cost > container.fluidAmount)
            {
                tmp = TextFormatting.RED + tmp;
            }

            tmp = GuiUtil.i18n("tile.megacorp:capital_investor.cost", tmp);

            toolTip.add(tmp);

            Fluid fluid = FluidRegistry.getFluid(reward.currencyType.name().toLowerCase());
            tmp = fluid.getLocalizedName(null);
            if(!fluid.getName().equals(container.fluid))
            {
                tmp = TextFormatting.RED + tmp;
            }
            tmp = GuiUtil.i18n("tile.megacorp:capital_investor.currency", tmp);

            toolTip.add(tmp);
        }


        @Override
        public List<String> getTooltip(int mouseX, int mouseY, int width)
        {
            if(GuiUtil.inBounds(mouseX, mouseY, width - 14, 4, 10, 10))
            {
               return toolTip;
            }
            return null;
        }

        @Override
        public void draw(int width, int height, GuiList.ListItemState state)
        {
            Color backColor = Color.BLACK;
            Color foreColor = Color.WHITE;

            if(state.isOver())
            {
                backColor = Color.GRAY;
            }
            else if(selectedReward == reward)
            {
                backColor = Color.DARK_GRAY;
            }

            if(!reward.available)
            {
                foreColor = Color.RED;
            }

            drawGradientRect(0, 0, width, height, backColor.getRGB(), backColor.getRGB());


            GuiUtil.drawItemStack(reward.itemStack, 1, 1, itemRender, getFontRenderer());
            fontRenderer.drawString(reward.itemStack.getDisplayName(), 20, 5, foreColor.getRGB());

            GuiUtil.setGLColor(Color.WHITE);
            GuiUtil.bindTexture(GuiUtil.MISC_RESOURCES);
            GuiUtil.drawTexturePart(width - 14, 4, 10, 10, 80, 0, 256, 256);
        }
    }
}
