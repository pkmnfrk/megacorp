package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.block.capital_investor.ContainerCapitalInvestor;
import com.mike_caron.megacorp.block.shipping_depot.ContainerShippingDepot;
import com.mike_caron.megacorp.gui.control.GuiButton;
import com.mike_caron.megacorp.gui.control.GuiFluid;
import com.mike_caron.megacorp.gui.control.GuiGroup;
import com.mike_caron.megacorp.gui.control.GuiList;
import com.mike_caron.megacorp.impl.QuestLocalization;
import com.mike_caron.megacorp.impl.RewardManager;
import com.mike_caron.megacorp.network.CtoSMessage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class GuiCapitalInvestor
    extends GuiContainerOwnedBase
    implements GuiButton.ClickedListener, GuiList.Producer
{
    public static final int WIDTH = 176;
    public static final int HEIGHT = 182;

    public static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/capital_investor.png");

    private final ContainerCapitalInvestor container;

    private GuiGroup ownedGroup = new GuiGroup();

    private GuiList rewardList = new GuiList(6, 17, 163, 62, this);
    private GuiButton buyRewardButton = new GuiButton(ContainerShippingDepot.GUI_NEW_QUEST,  124, 82, 45, 14, GuiUtil.translate("tile.megacorp:capital_investor.buy"));
    private GuiFluid fluidGauge = new GuiFluid(7, 83, 113, 12, GuiFluid.Orientation.HORIZONTAL);

    private ContainerCapitalInvestor.RewardData selectedReward = null;

    public GuiCapitalInvestor(ContainerCapitalInvestor container)
    {
        super(container);

        xSize = WIDTH;
        ySize = HEIGHT;

        this.container = container;

        initControls();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        buyRewardButton.setEnabled(selectedReward != null && container.fluidAmount >= selectedReward.nextRankCost);
    }

    @Override
    protected void onContainerRefresh()
    {
        //update fluid
        if(container.owner != null)
        {
            ownedGroup.setVisible(true);
            insertCardLabel.setVisible(false);

            fluidGauge.setFluid(FluidRegistry.getFluid(container.fluid));
            fluidGauge.setAmount(container.fluidAmount);
            fluidGauge.setCapacity(container.fluidCapacity);
        }
        else
        {
            ownedGroup.setVisible(false);
            insertCardLabel.setVisible(true);
        }

    }

    @Override
    public void addControls()
    {
        super.addControls();

        this.addControl(ownedGroup);

        ownedGroup.addControl(buyRewardButton);
        ownedGroup.addControl(rewardList);
        ownedGroup.addControl(fluidGauge);

        buyRewardButton.addClickedListener(this);
    }

    @Override
    protected String getTitleKey()
    {
        return "tile.megacorp:capital_investor.name";
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i1)
    {
        if(container.owner != null)
        {
            GlStateManager.color(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(background);
            drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        }
        else
        {
            drawInsertCardBackground();
        }
    }

    @Override
    public void clicked(GuiButton.ClickedEvent event)
    {
        if(event.id == ContainerCapitalInvestor.GUI_BUY_REWARD)
        {
            if (selectedReward != null)
            {
                CtoSMessage packet = CtoSMessage.forGuiButton(container.getPos(), event.id, selectedReward.id);
                MegaCorpMod.networkWrapper.sendToServer(packet);
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
        return container.rewardList != null ? container.rewardList.size() : 0;
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
    }

    class RewardListItem
        implements GuiList.ListItem
    {
        ContainerCapitalInvestor.RewardData reward;
        QuestLocalization rewardLocalization;

        public RewardListItem(ContainerCapitalInvestor.RewardData reward)
        {
            this.reward = reward;
            this.rewardLocalization = RewardManager.INSTANCE.getLocalizationForCurrent(this.reward.id);
        }

        @Override
        public List<String> getTooltip(int mouseX, int mouseY, int width)
        {
            if(GuiUtil.inBounds(mouseX, mouseY, width - 14, 4, 10, 10))
            {
                ArrayList<String> ret = new ArrayList<>();
                ret.add(String.format(rewardLocalization.description, (Object[])reward.nextRankVariables));
                return ret;
            }
            return null;
        }

        @Override
        public void draw(int width, int height, GuiList.ListItemState state)
        {
            Color color = Color.BLACK;

            if(state.isOver())
            {
                color = Color.GRAY;
            }
            else if(selectedReward == reward)
            {
                color = Color.DARK_GRAY;
            }

            drawGradientRect(0, 0, width, height, color.getRGB(), color.getRGB());

            //drawItemStack(reward.item, 1, 1, "");
            fontRenderer.drawString(rewardLocalization.title, 20, 5, Color.WHITE.getRGB());

            GuiUtil.bindTexture(GuiUtil.MISC_RESOURCES);
            GuiUtil.drawTexturePart(width - 14, 4, 10, 10, 80, 0, 256, 256);
        }
    }
}
