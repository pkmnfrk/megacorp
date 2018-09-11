package com.mike_caron.megacorp.gui;

import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.gui.control.GuiBase;
import com.mike_caron.megacorp.gui.control.GuiButton;
import com.mike_caron.megacorp.gui.control.GuiGuidePage;
import net.minecraft.util.ResourceLocation;

import java.util.Stack;

public class GuiGuide
    extends GuiBase
    implements GuiButton.ClickedListener,
               GuiGuidePage.NavigationListener
{
    private static final ResourceLocation background = new ResourceLocation(MegaCorpMod.modId, "textures/gui/guide.png");

    private GuiButton backButton = GuiUtil.translatedButton(1, 7, 147, 55, 14, "gui.megacorp:guide.back");

    private GuiGuidePage guidePage = new GuiGuidePage(7, 18, 161, 126);

    private final Stack<String> pageNav = new Stack<>();

    private String currentPage = "index";
    public GuiGuide()
    {
        super(176, 166, background);

        initControls();

        loadPage();
    }

    @Override
    protected String getTitleKey()
    {
        return "gui.megacorp:guide.title";
    }

    @Override
    protected void addControls()
    {
        super.addControls();

        this.addControl(backButton);
        this.addControl(guidePage);
        backButton.setEnabled(false);
        backButton.addListener(this);
        guidePage.addListener(this);

    }


    @Override
    public void clicked(GuiButton.ClickedEvent event)
    {
        if(event.id == 1)
        {
            currentPage = pageNav.pop();
            backButton.setEnabled(!pageNav.isEmpty());

            loadPage();
        }
    }

    private void loadPage()
    {
        guidePage.loadPage(currentPage);
    }

    @Override
    public void navigated(String newUri)
    {
        pageNav.push(currentPage);
        currentPage = newUri;
        backButton.setEnabled(true);
        loadPage();
    }
}
