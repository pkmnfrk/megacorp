package com.mike_caron.megacorp.gui.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EventListener;

public class GuiGuidePage
    extends GuiScrollPort
    implements GuiButton.ClickedListener
{
    String title;

    String currentJson = null;

    int yPos = 0;

    public GuiGuidePage(int x, int y, int width, int height)
    {
        super(x, y, width, height);

        //scrollX = -20;
    }

    public void loadPage(String uri)
    {
        this.clearControls();
        setEnableScrollBar(false);

        yPos = 4;
        scrollY = 0;

        ResourceLocation rl = new ResourceLocation(MegaCorpMod.modId, baseFile(uri));

        try
        {
            JsonObject body = loadResourceAsJson(rl);
            if(body == null)
            {
                String message = new TextComponentTranslation("gui.megacorp:guide.pagenotfound", currentJson).getFormattedText();

                GuiMultilineLabel label = new GuiMultilineLabel(2, 0, this.width - 4 - 8, message);
                this.addControl(label);

                yPos += label.getHeight();
            }
            else
            {
                JsonObject translation = loadLocalizedResourceAsJson(uri);

                if (body.has("index"))
                {
                    addButtonsForList(body, "index");
                }

                if (body.has("body"))
                {
                    addLabelsForList(body, translation, "body");
                }

                if (body.has("seealso"))
                {
                    GuiLabel seealso = GuiUtil.staticLabelFromTranslationKey(2, yPos, "gui.megacorp:guide.seealso");
                    yPos += 10;

                    addButtonsForList(body, "seealso");
                }
            }

        }
        catch(Exception ex)
        {
            String message = "Error while parsing " + currentJson + "\r\n" + ex.getMessage();

            GuiMultilineLabel label = new GuiMultilineLabel(2, 0, this.width - 4 - 8, message);
            this.addControl(label);

            yPos += label.getHeight();

            ex.printStackTrace();
        }

        if(yPos > this.height)
        {
            //enable scroll bar
            setEnableScrollBar(true);

            //reflow everything

            yPos = 4;
            for(GuiControl control : controls)
            {
                GuiSized sized = (GuiSized)control;
                if(control instanceof GuiLabel
                    || control instanceof GuiButton
                    || control instanceof GuiMultilineLabel)
                {
                    sized.setWidth(this.width - 4 - marginRight);
                }
                else if(control instanceof GuiImage)
                {
                    sized.setX((this.width - marginRight) / 2 - sized.getWidth() / 2);
                }
                sized.setY(yPos);

                yPos += sized.getHeight();

                if(control.extraData.containsKey("spacing"))
                {
                    yPos += (Integer)control.extraData.get("spacing");
                }
            }
        }

        setMaxScrollY(yPos - this.height);
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

                label.extraData.put("spacing", 10);

                yPos += label.getHeight() + 10;
            }
            else
            {
                JsonObject obj = p.getAsJsonObject();
                if(obj.has("link"))
                {
                    insertLink(obj);
                }
                else if(obj.has("img"))
                {
                    insertImage(obj);
                }
            }
        }
    }

    private void addButtonsForList(JsonObject body, String seealso2)
    {
        JsonArray index = body.get(seealso2).getAsJsonArray();

        for (int i = 0; i < index.size(); i++)
        {
            insertLink(index.get(i));
        }
    }

    private void insertImage(JsonObject img)
    {
        ResourceLocation src;

        if(img.has("mod"))
        {
            src = new ResourceLocation(img.get("mod").getAsString(), img.get("img").getAsString());
        }
        else
        {
            src = new ResourceLocation(MegaCorpMod.modId, img.get("img").getAsString());
        }

        int width = img.get("width").getAsInt();
        int height = img.get("height").getAsInt();
        int sourceX = 0;
        int sourceY = 0;
        int sourceWidth = width;
        int sourceHeight = height;
        int textureWidth = 256;
        int textureHeight = 256;
        int spacing = 0;

        boolean sub = false;

        if (img.has("sx"))
        {
            sourceX = img.get("sx").getAsInt();
            sub = true;
        }
        if(img.has("sy"))
        {
            sourceY = img.get("sy").getAsInt();
            sub = true;
        }
        if(img.has("sw"))
        {
            sourceWidth = img.get("sw").getAsInt();
            sub = true;
        }
        if(img.has("sh"))
        {
            sourceHeight = img.get("sh").getAsInt();
            sub = true;
        }
        if(img.has("tw"))
        {
            textureWidth = img.get("tw").getAsInt();
            sub = true;
        }
        if(img.has("th"))
        {
            textureHeight = img.get("th").getAsInt();
            sub = true;
        }
        if(img.has("spacing"))
        {
            spacing = img.get("spacing").getAsInt();
        }

        if(!sub)
        {
            // if they only specified width and height, then it's probably a separate full-sized texture
            textureWidth = width;
            textureHeight = height;
        }

        int x = (this.width - marginRight) / 2 - width / 2;

        GuiImage image = new GuiImageTexture(
            x, yPos,
            width, height,
            sourceX, sourceY,
            sourceWidth, sourceHeight,
            src,
            textureWidth, textureHeight
        );

        this.addControl(image);

        if(spacing > 0)
        {
            image.extraData.put("spacing", spacing);
        }

        yPos += height + spacing;
    }

    private void insertLink(JsonElement ele)
    {
        String otherUri = null;
        int spacing = 0;

        if(ele.isJsonPrimitive())
        {
            otherUri = ele.getAsString();
        }
        else
        {
            JsonObject obj = ele.getAsJsonObject();
            otherUri = obj.get("link").getAsString();
            if(obj.has("spacing"))
            {
                spacing = obj.get("spacing").getAsInt();
            }
        }

        GuiImage image = null;

        JsonObject otherPage = loadResourceAsJson(new ResourceLocation(MegaCorpMod.modId, otherUri));
        JsonObject otherTranslation = loadLocalizedResourceAsJson(otherUri);

        if(otherPage != null)
        {
            if(otherPage.has("icon"))
            {
                image = imageForIcon(otherPage.get("icon").getAsJsonObject());
            }
        }

        String label = otherUri;
        if (otherTranslation != null)
        {
            label = otherTranslation.get("title").getAsString();
        }

        GuiButton button = new GuiButton(0, 2, yPos, this.width - 4, 14, label);
        button.addListener(this);
        this.addControl(button);

        button.extraData.put("link", otherUri);

        if(spacing > 0)
        {
            yPos += spacing;
            button.extraData.put("spacing", spacing);
        }

        yPos += 14;
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
    {
        currentJson = res.getPath();

        try
        {
            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(res);
            JsonParser parser = new JsonParser();
            JsonObject ret;

            InputStreamReader is = new InputStreamReader(resource.getInputStream());
            ret = parser.parse(is).getAsJsonObject();
            is.close();

            return ret;
        }
        catch(IOException ex)
        {
            return null;
        }
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
            if(!locale.equals("en_us"))
            {
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
            else
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
        triggerNavigated((String)event.control.extraData.get("link"));
    }

    public interface NavigationListener
        extends EventListener
    {
        void navigated(String newUri);
    }

    private GuiImage imageForIcon(JsonObject icon)
    {
        return null;
    }
}
