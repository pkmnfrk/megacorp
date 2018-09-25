package com.mike_caron.megacorp.gui.control;

import com.google.gson.*;
import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.gui.GuiUtil;
import com.mike_caron.megacorp.util.DataUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.apache.commons.io.FilenameUtils;

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
            //MegaCorpMod.logger.info("Reflowing due to scroll bar");
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
                //MegaCorpMod.logger.info("Setting yPos of " + control + " to " + yPos + " with height of " + control.getHeight());

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

        JsonArray elements = getElements(src.getAsJsonObject().get("elements"));

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

    private JsonArray getElements(JsonElement elementsEl)
    {
        if(elementsEl == null || elementsEl.isJsonNull() || elementsEl.isJsonPrimitive())
        {
            return new JsonArray();
        }

        if(elementsEl.isJsonArray())
        {
            return elementsEl.getAsJsonArray();
        }

        JsonObject obj = elementsEl.getAsJsonObject();

        if(obj.has("recipe"))
        {
            String itemId = obj.get("recipe").getAsString();

            IRecipe recipe = CraftingManager.getRecipe(new ResourceLocation(itemId));
            JsonArray ret = new JsonArray();
            int numIng = 0;

            JsonObject air = new JsonObject();
            air.addProperty("item", "minecraft:air");

            //protocol is that the ingredients are listed as items 0-8, and the result is item 9

            int recipe_width = 3;
            int recipe_height = 3;

            if(obj.has("recipe_width"))
            {
                recipe_width = obj.get("recipe_width").getAsInt();
            }

            if(obj.has("recipe_height"))
            {
                recipe_height = obj.get("recipe_height").getAsInt();
            }

            if(recipe != null)
            {
                NonNullList<Ingredient> ingredients = recipe.getIngredients();

                numIng = ingredients.size();
                int w = 100;
                int h = 100;
                if(recipe instanceof IShapedRecipe)
                {
                    w = ((IShapedRecipe) recipe).getRecipeWidth();
                    h = ((IShapedRecipe) recipe).getRecipeHeight();
                }

                int x = 0;
                int y = 0;
                int q = 0;
                for (int i = 0; i < 9; i++)
                {
                    if((w != 0 && x >= w) || (h != 0 && y >= h) || q >= ingredients.size())
                    {
                        ret.add(air);
                    }
                    else
                    {
                        Ingredient ing = ingredients.get(q++);

                        ret.add(elementForIngredient(ing));
                    }

                    x += 1;
                    if(x >= recipe_width)
                    {
                        x = 0;
                        y += 1;
                        if(y >= recipe_height)
                            break;
                    }
                }

                ret.add(DataUtils.toJson(recipe.getRecipeOutput()));
            }

            return ret;
        }

        return new JsonArray();
    }

    private JsonElement elementForIngredient(Ingredient ing)
    {
        ItemStack[] stacks = ing.getMatchingStacks();

        if (stacks.length == 1)
        {
            return DataUtils.toJson(stacks[0]);
        }
        else
        {
            JsonObject arrHolder = new JsonObject();
            JsonArray arr = new JsonArray();

            for (int j = 0; j < stacks.length; j++)
            {
                arr.add(DataUtils.toJson(stacks[j]));
            }
            arrHolder.add("imgarray", arr);
            return arrHolder;
        }
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
                //MegaCorpMod.logger.info("Setting yPos of " + label + " to " + yPos + " with height of " + label.getHeight());

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

            // image-types are super diverse, so it's easier to try this first, and try other stuff if it fails
            control = insertImage(obj);

            if(control != null)
            {
                control.setX((this.width - marginRight) / 2 - control.getWidth() / 2);
            }
            else if(obj.has("link"))
            {
                control = insertLink(obj);
                control.setX(2);
                ((GuiSized)control).setWidth(this.width - 4);
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

                    if(image == null)
                        continue;

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

                //MegaCorpMod.logger.info("Setting yPos of " + control + " to " + yPos + " with height of " + control.getHeight());

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
        int spacing = 4;

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

        GuiButton button = new GuiButton(0, 0, 0, this.width - 4, Math.max(14, image != null ? image.getHeight() + 4 : 0) + 4, label, image);
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

    private GuiImage imageForIcon(JsonObject icon)
    {
        //return new GuiImageItemStack(0, 0, new ItemStack(Items.EMERALD, 1));
        int x = 0;
        int y = 0;
        int spacing = 4;
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
        if(icon.has("spacing"))
        {
            spacing = icon.get("spacing").getAsInt();
        }

        GuiImage ret = null;

        if(icon.has("imgarray"))
        {
            JsonArray images = icon.get("imgarray").getAsJsonArray();

            GuiImageFlipper flipper = new GuiImageFlipper(x, y);

            for(JsonElement el : images)
            {
                if(!el.isJsonObject())
                    continue;

                flipper.addImage(imageForIcon(el.getAsJsonObject()));
            }

            ret = flipper;
        } else if(icon.has("bucket"))
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

            ret = image;
        }
        else if(icon.has("item"))
        {
            ItemStack stack = DataUtils.toItemStack(icon);

            ret = new GuiImageItemStack(x, y, stack);
        }

        if(ret == null)
        {
            return null;
        }

        if(link != null)
        {
            ret.extraData.put("link", link);
            ret.addListener(this);
        }

        if(spacing > 0)
        {
            ret.extraData.put("spacing", spacing);
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
