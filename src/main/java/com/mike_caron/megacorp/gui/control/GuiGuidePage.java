package com.mike_caron.megacorp.gui.control;

import com.google.gson.*;
import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.gui.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EventListener;
import java.util.Map;

public class GuiGuidePage
    extends GuiScrollPort
    implements GuiButton.ClickedListener
{
    String title;

    String currentJson = null;

    String currentUri = null;

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

        this.currentUri = uri;

        yPos = 4;
        scrollY = 0;

        ResourceLocation rl = new ResourceLocation(MegaCorpMod.modId, baseFile(uri));

        try
        {
            JsonObject templates = loadResourceAsJson(new ResourceLocation(MegaCorpMod.modId, baseFile("/_templates")));
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

                if(translation != null && translation.has("title"))
                {
                    insertLabel(translation, new JsonPrimitive("title"), true);
                }

                if (body.has("index"))
                {
                    addButtonsForList(body, "index");
                }

                if (body.has("body"))
                {
                    addLabelsForList(body.get("body").getAsJsonArray(), translation, templates);
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
                if(control instanceof GuiButton
                    || control instanceof GuiMultilineLabel)
                {
                    ((GuiSized)control).setWidth(this.width - 4 - marginRight);
                }
                else if(control instanceof GuiImage
                    || control instanceof GuiLabel)
                {
                    control.setX((this.width - marginRight) / 2 - control.getWidth() / 2);
                }
                control.setY(yPos);

                yPos += control.getHeight();

                if(control.extraData.containsKey("spacing"))
                {
                    yPos += (Integer)control.extraData.get("spacing");
                }
            }
        }

        setMaxScrollY(yPos - this.height);
    }

    private void addLabelsForList(JsonArray index, JsonObject translation, JsonObject templates)
    {
        //JsonArray index = body.get(seealso2).getAsJsonArray();

        for (int i = 0; i < index.size(); i++)
        {
            JsonElement p = index.get(i);

            p = resolveTemplate(p, templates);

            insertLabel(translation, p, false);
        }
    }

    private JsonElement resolveTemplate(JsonElement src, JsonObject templates)
    {
        if(!src.isJsonObject())
            return src;

        if(templates == null)
            return src;

        JsonObject obj = src.getAsJsonObject();

        if(!obj.has("template"))
            return src;

        String templateName = obj.get("template").getAsString();
        if(!templates.has(templateName))
            return src;

        JsonArray elements = src.getAsJsonObject().get("elements").getAsJsonArray();

        JsonObject template = templates.get(templateName).getAsJsonObject();
        JsonObject ret = new JsonObject();

        for(Map.Entry<String, JsonElement> entry : template.entrySet())
        {
            ret.add(entry.getKey(), resolveTemplateObject(elements, entry.getValue()));
        }

        for(Map.Entry<String, JsonElement> entry : obj.entrySet())
        {
            if(entry.getKey().equals("template") || entry.getKey().equals("elements"))
                continue;

            ret.add(entry.getKey(), entry.getValue());
        }

        return ret;
    }

    private JsonElement resolveTemplateObject(JsonArray elements, JsonElement entry)
    {
        if(entry.isJsonPrimitive())
            return entry;

        if(entry.isJsonNull())
            return entry;

        if(entry.isJsonArray())
        {
            JsonArray newArray = new JsonArray();

            for(JsonElement el : entry.getAsJsonArray())
            {
                newArray.add(resolveTemplateObject(elements, el));
            }

            return newArray;
        }

        if(entry.isJsonObject())
        {
            JsonObject newObj = entry.getAsJsonObject();

            if (entry.getAsJsonObject().has("placeholder"))
            {
                newObj = new JsonObject();

                for (Map.Entry<String, JsonElement> subentry : entry.getAsJsonObject().entrySet())
                {
                    if (!subentry.getKey().equals("placeholder"))
                    {
                        newObj.add(subentry.getKey(), subentry.getValue());
                        continue;
                    }

                    int placeholderId = subentry.getValue().getAsInt();

                    if (placeholderId < 0 || placeholderId >= elements.size())
                        continue;

                    for (Map.Entry<String, JsonElement> srcEntry : elements.get(placeholderId).getAsJsonObject().entrySet())
                    {
                        newObj.add(srcEntry.getKey(), resolveTemplateObject(elements, srcEntry.getValue()));
                    }
                }
            }
            return newObj;
        }

        throw new RuntimeException("This can't happen");
    }


    private void insertLabel(JsonObject translation, JsonElement l, boolean isTitle)
    {
        if(l.isJsonPrimitive())
        {
            JsonPrimitive p = l.getAsJsonPrimitive();

            if(p.isString())
            {
                String key = l.getAsString();

                if (translation.has(key))
                {
                    key = translation.get(key).getAsString();
                }

                GuiMultilineLabel label;

                if(isTitle)
                {
                    key = TextFormatting.UNDERLINE + key + TextFormatting.RESET;
                }

                label = new GuiMultilineLabel(2, yPos, this.width - 4, key);
                label.setColor(Color.BLACK);

                if(isTitle)
                {
                    label.setAlignment(GuiMultilineLabel.Alignment.CENTER);
                }

                this.addControl(label);

                label.extraData.put("spacing", 6);

                yPos += label.getHeight() + 6;
            }
            else if(p.isNumber())
            {
                int space = p.getAsInt();

                GuiEmpty empty = new GuiEmpty(2, yPos, this.width - 4, space);

                yPos += space;
            }
        }
        else
        {
            GuiControl control = null;
            JsonObject obj = l.getAsJsonObject();
            if(obj.has("link"))
            {
                control = insertLink(obj);
                control.setX(2);
                ((GuiSized)control).setWidth(this.width - 4);
            }
            else if(
                   obj.has("img")
                || obj.has("bucket")
                || obj.has("item")
            )
            {
                control = insertImage(obj);
                control.setX((this.width - marginRight) / 2 - control.getWidth() / 2);
            }
            else if(
                   obj.has("horiz")
                || obj.has("canvas")
            )
            {

                JsonArray elements = null;
                GuiGroup layout = null;

                if(obj.has("horiz"))
                {
                    elements = obj.get("horiz").getAsJsonArray();
                    layout = new GuiHorizontalLayout(2, yPos, this.width - 2 - marginRight, 0, 2);
                }
                else if(obj.has("canvas"))
                {
                    elements = obj.get("canvas").getAsJsonArray();
                    layout = new GuiGroup(2, yPos, 0, height);
                }
                else
                {
                    throw new RuntimeException("This can't happen");
                }

                int height = 0;
                int width = layout.getWidth();
                int z = 0;

                boolean autoHeight = true;
                boolean autoWidth = true;

                if(obj.has("height"))
                {
                    height = obj.get("height").getAsInt();
                    autoHeight = false;
                }

                if(obj.has("width"))
                {
                    width = obj.get("width").getAsInt();
                    autoWidth = false;
                }

                if(obj.has("spacing"))
                {
                    layout.extraData.put("spacing", obj.get("spacing").getAsInt());
                }

                for(int i = 0; i < elements.size(); i++)
                {
                    JsonObject el = elements.get(i).getAsJsonObject();

                    GuiImage image = imageForIcon(el);

                    image.setzIndex(z++);

                    layout.addControl(image);

                    if(autoHeight)
                    {
                        if(height < image.getY() + image.getHeight())
                        {
                            height = image.getY() + image.getHeight();
                        }
                    }

                    if(autoWidth)
                    {
                        if(width < image.getX() + image.getWidth())
                        {
                            width = image.getX() + image.getWidth();
                        }
                    }

                }
                layout.setHeight(height);
                layout.setWidth(width);

                layout.setX(this.width / 2 - layout.getWidth() / 2);

                control = layout;

            }

            if(control != null)
            {
                this.addControl(control);
                control.setY(yPos);

                yPos += control.getHeight();

                if (control.extraData.containsKey("spacing"))
                {
                    yPos += (Integer) control.extraData.get("spacing");
                }
            }
        }
    }

    private void addButtonsForList(JsonObject body, String seealso2)
    {
        JsonArray index = body.get(seealso2).getAsJsonArray();

        for (int i = 0; i < index.size(); i++)
        {
            GuiSized control = insertLink(index.get(i));

            control.setY(yPos);
            control.setX(2);
            control.setWidth(this.width - 4);

            this.addControl(control);

            yPos += control.getHeight();

            if(control.extraData.containsKey("spacing"))
            {
                yPos += (Integer)control.extraData.get("spacing");
            }
        }
    }

    private GuiControl insertImage(JsonObject img)
    {
        return imageForIcon(img);
    }

    private GuiSized insertLink(JsonElement ele)
    {
        String otherUri = null;
        int spacing = 0;

        if(ele.isJsonPrimitive())
        {
            otherUri = resolveUri(currentUri, ele.getAsString());
        }
        else
        {
            JsonObject obj = ele.getAsJsonObject();
            otherUri = resolveUri(currentUri, obj.get("link").getAsString());
            if(obj.has("spacing"))
            {
                spacing = obj.get("spacing").getAsInt();
            }
        }

        GuiImage image = null;

        JsonObject otherPage = loadResourceAsJson(new ResourceLocation(MegaCorpMod.modId, baseFile(otherUri)));
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

        GuiButton button = new GuiButton(0, 0, 0, this.width - 4, Math.max(14, image != null ? image.getHeight() + 4 : 0), label, image);
        button.addListener(this);
        //this.addControl(button);

        button.extraData.put("link", otherUri);

        if(spacing > 0)
        {
            //yPos += spacing;
            button.extraData.put("spacing", spacing);
        }

        //yPos += 14;

        return button;
    }

    public String getTitle()
    {
        return title;
    }

    private String baseFile(String uri)
    {
        return "guide" + uri + ".json";
    }

    private String localeFile(String uri, String locale)
    {
        return "guide" + uri + "." + locale + ".json";
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

    @Nonnull
    private GuiImage imageForIcon(JsonObject icon)
    {
        //return new GuiImageItemStack(0, 0, new ItemStack(Items.EMERALD, 1));
        int x = 0;
        int y = 0;
        String link = null;

        if(icon.has("x"))
        {
            x = icon.get("x").getAsInt();
        }
        if(icon.has("y"))
        {
            y = icon.get("y").getAsInt();
        }
        if(icon.has("link"))
        {
            link = icon.get("link").getAsString();
        }

        GuiImage ret = null;

        if(icon.has("bucket"))
        {
            String fluidId = icon.get("bucket").getAsString();
            Fluid fluid = FluidRegistry.getFluid(fluidId);

            if(fluid != null)
            {
                ItemStack item = FluidUtil.getFilledBucket(new FluidStack(fluid, 1000));
                ret = new GuiImageItemStack(x, y, item);
            }
        }
        else if(icon.has("img"))
        {
            ResourceLocation src;

            if(icon.has("mod"))
            {
                src = new ResourceLocation(icon.get("mod").getAsString(), icon.get("img").getAsString());
            }
            else
            {
                src = new ResourceLocation(MegaCorpMod.modId, icon.get("img").getAsString());
            }

            int width = icon.get("width").getAsInt();
            int height = icon.get("height").getAsInt();
            int sourceX = 0;
            int sourceY = 0;
            int sourceWidth = width;
            int sourceHeight = height;
            int textureWidth = 256;
            int textureHeight = 256;
            int spacing = 0;

            boolean sub = false;

            if (icon.has("sx"))
            {
                sourceX = icon.get("sx").getAsInt();
                sub = true;
            }
            if(icon.has("sy"))
            {
                sourceY = icon.get("sy").getAsInt();
                sub = true;
            }
            if(icon.has("sw"))
            {
                sourceWidth = icon.get("sw").getAsInt();
                sub = true;
            }
            if(icon.has("sh"))
            {
                sourceHeight = icon.get("sh").getAsInt();
                sub = true;
            }
            if(icon.has("tw"))
            {
                textureWidth = icon.get("tw").getAsInt();
                sub = true;
            }
            if(icon.has("th"))
            {
                textureHeight = icon.get("th").getAsInt();
                sub = true;
            }
            if(icon.has("spacing"))
            {
                spacing = icon.get("spacing").getAsInt();
            }

            if(!sub)
            {
                // if they only specified width and height, then it's probably a separate full-sized texture
                textureWidth = width;
                textureHeight = height;
            }

            GuiImage image = new GuiImageTexture(
                x, y,
                width, height,
                sourceX, sourceY,
                sourceWidth, sourceHeight,
                src,
                textureWidth, textureHeight
            );

            if(spacing > 0)
            {
                image.extraData.put("spacing", spacing);
            }

            ret = image;
        }
        else if(icon.has("item"))
        {
            String key = icon.get("item").getAsString();
            int qty = 1;
            int meta = 0;
            if(icon.has("qty"))
            {
                qty = icon.get("qty").getAsInt();
            }
            if(icon.has("meta"))
            {
                meta = icon.get("meta").getAsInt();
            }
            Item item = Item.getByNameOrId(key);
            if(item == null)
            {
                item = Items.AIR;
            }

            ItemStack stack = new ItemStack(item, qty, meta);

            ret = new GuiImageItemStack(x,y, stack);
        }

        if(ret == null)
        {
            ret = new GuiImageItemStack(x, y, new ItemStack(Items.SKULL));
        }

        if(link != null)
        {
            ret.extraData.put("link", link);
            ret.addListener(this);
        }

        return ret;
    }

    private String resolveUri(String currentUri, String newUri)
    {
        if(newUri.startsWith("/"))
            return newUri;

        if(!currentUri.contains("/"))
        {
            currentUri = "/" + currentUri;
        }

        String currentPath = FilenameUtils.getPath(currentUri);

        if(!currentPath.endsWith("/"))
            currentPath += "/";

        if(!currentPath.startsWith("/"))
        {
            currentPath = "/" + currentPath;
        }

        return currentPath + newUri;
    }
}
