package com.mike_caron.megacorp.gui.control;

import com.google.gson.*;
import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class GuiGuidePage
    extends GuiScrollPort
    implements GuiButton.ClickedListener
{
    String title;

    String currentJson = null;

    private final List<String> navigationDirectory = new ArrayList<>();

    int yPos = 0;
    int nextId = 0;

    public GuiGuidePage(int x, int y, int width, int height)
    {
        super(x, y, width, height);

        //scrollX = -20;
    }

    public void loadPage(String uri)
    {
        this.clearControls();
        navigationDirectory.clear();
        setEnableScrollBar(false);

        yPos = 0;
        nextId = 0;

        ResourceLocation rl = new ResourceLocation(MegaCorpMod.modId, baseFile(uri));

        try
        {
            JsonObject body = loadResourceAsJson(rl);
            JsonObject translation = loadLocalizedResourceAsJson(uri);

            if(body.has("index"))
            {
                addButtonsForList(body, "index");
            }

            if(body.has("body"))
            {
                addLabelsForList(body, translation, "body");
            }

            if(body.has("seealso"))
            {
                GuiLabel seealso = GuiUtil.staticLabelFromTranslationKey(2, yPos, "gui.megacorp:guide.seealso");
                yPos += 10;

                addButtonsForList(body, "seealso");
            }

        }
        catch(IOException ex)
        {
            String message = new TextComponentTranslation("gui.megacorp:guide.pagenotfound", currentJson).getFormattedText();

            GuiMultilineLabel label = new GuiMultilineLabel(2, 0, this.width - 4 - 8, message);
            this.addControl(label);

            yPos += label.getHeight();
        }
        catch(JsonParseException ex)
        {
            String message = "Error while parsing " + currentJson + "\r\n" + ex.getMessage();

            GuiMultilineLabel label = new GuiMultilineLabel(2, 0, this.width - 4 - 8, message);
            this.addControl(label);

            yPos += label.getHeight();
        }

        if(yPos > this.height)
        {
            //enable scroll bar
            setEnableScrollBar(true);

            //reflow everything

            yPos = 0;
            for(GuiControl control : controls)
            {
                GuiSized sized = (GuiSized)control;
                sized.setWidth(this.width - 4 - 8);
                sized.setY(yPos);

                yPos += sized.getHeight();

                if(control instanceof GuiMultilineLabel)
                {
                    yPos += 10;
                }
            }
        }
    }

    private void addLabelsForList(JsonObject body, JsonObject translation, String seealso2)
    {
        JsonArray index = body.get(seealso2).getAsJsonArray();

        for (int i = 0; i < index.size(); i++)
        {
            JsonElement p = index.get(i);

            if(p.isJsonPrimitive())
            {
                String key = p.getAsString();

                if(translation.has(key))
                {
                    key = translation.get(key).getAsString();
                }

                GuiMultilineLabel label = new GuiMultilineLabel(2, yPos, this.width - 4, key);

                this.addControl(label);

                yPos += label.getHeight() + 10;
            }
            else
            {

            }
        }
    }

    private void addButtonsForList(JsonObject body, String seealso2)
    {
        JsonArray index = body.get(seealso2).getAsJsonArray();

        for (int i = 0; i < index.size(); i++)
        {
            String otherUri = index.get(i).getAsString();

            JsonObject otherTranslation = loadLocalizedResourceAsJson(otherUri);
            String label = otherUri;
            if (otherTranslation != null)
            {
                label = otherTranslation.get("title").getAsString();
            }

            GuiButton button = new GuiButton(nextId, 2, yPos, this.width - 4, 14, label);
            button.addListener(this);
            this.addControl(button);

            navigationDirectory.add(otherUri);

            yPos += 14;
            nextId += 1;
        }
    }

    public String getTitle()
    {
        return title;
    }

    private String baseFile(String uri)
    {
        return "guide/" + uri + ".json";
    }

    private String localeFile(String uri, String locale)
    {
        return "guide/" + uri + "." + locale + ".json";
    }

    private JsonObject loadResourceAsJson(ResourceLocation res)
        throws IOException
    {
        currentJson = res.getPath();

        IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(res);
        JsonParser parser = new JsonParser();
        JsonObject ret;

        InputStreamReader is = new InputStreamReader(resource.getInputStream());
        ret = parser.parse(is).getAsJsonObject();
        is.close();

        return ret;
    }

    private JsonObject loadLocalizedResourceAsJson(String uri)
    {
        String locale = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();

        InputStream stream = null;

        try
        {
            ResourceLocation loc = new ResourceLocation(MegaCorpMod.modId, localeFile(uri, locale));
            currentJson = loc.getPath();

            IResource res = Minecraft.getMinecraft().getResourceManager().getResource(loc);
            stream = res.getInputStream();
        }
        catch(IOException ex)
        {
            //try US

            try
            {
                ResourceLocation loc = new ResourceLocation(MegaCorpMod.modId, localeFile(uri, "en_us"));
                currentJson = loc.getPath();

                IResource res = Minecraft.getMinecraft().getResourceManager().getResource(loc);
                stream = res.getInputStream();
            }
            catch (IOException ex2)
            {
                return null;
            }
        }

        try{
            JsonParser parser = new JsonParser();
            InputStreamReader is = new InputStreamReader(stream);
            JsonObject ret = parser.parse(is).getAsJsonObject();
            is.close();

            return ret;
        }
        catch (IOException ex)
        {
            return null;
        }
    }

    private void triggerNavigated(String newUri)
    {
        if(!this.enabled)
            return;

        for(EventListener listener : listeners)
        {
            if(listener instanceof NavigationListener)
            {
                ((NavigationListener) listener).navigated(newUri);
            }
        }
    }

    @Override
    public void clicked(GuiButton.ClickedEvent event)
    {
        triggerNavigated(navigationDirectory.get(event.id));
    }

    public interface NavigationListener
        extends EventListener
    {
        void navigated(String newUri);
    }
}
