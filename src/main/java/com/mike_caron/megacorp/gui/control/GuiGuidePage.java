package com.mike_caron.megacorp.gui.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mike_caron.megacorp.MegaCorpMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GuiGuidePage
    extends GuiScrollPort
{
    String title;

    public GuiGuidePage(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    public void loadPage(String uri)
    {
        this.clearControls();

        ResourceLocation rl = new ResourceLocation(MegaCorpMod.modId, baseFile(uri));

        try
        {
            JsonObject body = loadResourceAsJson(rl);
            JsonObject translation = loadLocalizedResourceAsJson(uri);

            int yPos = 0;

            if(body.has("index"))
            {
                JsonArray index = body.get("index").getAsJsonArray();

                for(int i = 0; i < index.size(); i++)
                {
                    String otherUri = index.get(i).getAsString();

                    JsonObject otherTranslation = loadLocalizedResourceAsJson(otherUri);
                    String label = otherUri;
                    if(otherTranslation != null)
                    {
                        label = otherTranslation.get("title").getAsString();
                    }

                    GuiButton button = new GuiButton(i, 2, yPos, this.width - 4, 14, label);
                    this.addControl(button);

                    yPos += 14;
                }
            }

            if(body.has("body"))
            {

            }

            if(body.has("seealso"))
            {

            }

        }
        catch(IOException ex)
        {

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
            IResource res = Minecraft.getMinecraft().getResourceManager().getResource(loc);
            stream = res.getInputStream();
        }
        catch(IOException ex)
        {
            //try US

            try
            {
                ResourceLocation loc = new ResourceLocation(MegaCorpMod.modId, localeFile(uri, "en-us"));
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
}
