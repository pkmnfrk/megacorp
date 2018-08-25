package com.mike_caron.megacorp.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mike_caron.megacorp.MegaCorpMod;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class QuestManager
{
    public static final QuestManager INSTANCE = new QuestManager();

    private final List<Quest> quests = new ArrayList<>();
    private final Map<String, Map<String, QuestLocalization>> localizations = new HashMap<>();

    private QuestManager() {}

    public void loadQuests()
    {
        quests.clear();

        JsonParser parser = new JsonParser();

        CraftingHelper.findFiles(Loader.instance().getReversedModObjectList().get(MegaCorpMod.instance), "assets/" + MegaCorpMod.modId + "/quests", null, (root, url) ->
        {
            String extension = FilenameUtils.getExtension(url.toString());

            if("json".equals(extension))
            {
                JsonObject json;
                try (BufferedReader stream = Files.newBufferedReader(url))
                {
                    json = parser.parse(stream).getAsJsonObject();
                }
                catch(RuntimeException | IOException ex)
                {
                    MegaCorpMod.logger.error("Encountered error while reading " + url, ex);
                    return true;
                }

                String mod = json.get("mod").getAsString();

                if(!Loader.isModLoaded(mod))
                {
                    MegaCorpMod.logger.info("Skipping " + mod + " quests, because it's not loaded");
                }

                JsonArray qs = json.getAsJsonArray("quests");

                for(JsonElement obj : qs)
                {
                    try
                    {
                        JsonObject quest = (JsonObject)obj;

                        Quest q = Quest.fromJson(quest);

                        quests.add(q);

                        MegaCorpMod.logger.info("Loaded quest " + q.id);
                    }
                    catch (Exception ex)
                    {
                        MegaCorpMod.logger.error("Error loading quests from " + url, ex);
                    }
                }
            }
            else if("lang".equals(extension))
            {
                String locale = FilenameUtils.getBaseName(url.toString()).toLowerCase();
                if(!localizations.containsKey(locale))
                {
                    localizations.put(locale, new HashMap<>());
                }

                try
                {
                    List<String> lines = Files.readAllLines(url);
                    loadLocalization(locale, lines);
                }
                catch(Exception ex)
                {
                    MegaCorpMod.logger.error("Error loading localization from " + url, ex);
                }
            }
            return true;
        }, false, true);

    }

    private void loadLocalization(String locale, List<String> lines)
    {
        Map<String, QuestLocalization> quests = localizations.get(locale);
        final QuestLocalization EMPTY = new QuestLocalization();

        for(String line : lines)
        {
            if(line.trim().isEmpty()) continue;

            String[] parts = line.split("=");

            if(parts.length != 2)
            {
                MegaCorpMod.logger.warn("Invalid localization line: " + line);
                continue;
            }

            String[] keyParts = parts[0].split("\\.");

            if(keyParts.length != 2)
            {
                MegaCorpMod.logger.warn("Invalid localization line: " + line);
                continue;
            }

            QuestLocalization ql = quests.getOrDefault(keyParts[0], EMPTY);

            if("title".equals(keyParts[1]))
            {
                ql = ql.withTitle(parts[1]);
            }
            else if("desc".equals(keyParts[1]))
            {
                ql = ql.withDescription(parts[1]);
            }

            quests.put(keyParts[0], ql);

            MegaCorpMod.logger.info("Loaded localization " + keyParts[1] + " for " + keyParts[0]);

        }
    }

    public QuestLocalization getLocalizationFor(String locale, String questId)
    {
        if(!localizations.containsKey(locale))
            locale = "en_us";

        if(!localizations.get(locale).containsKey(questId))
        {
            return new QuestLocalization(questId + ".title", questId + ".desc");
        }

        return localizations.get(locale).get(questId);
    }

    @SideOnly(Side.CLIENT)
    public QuestLocalization getLocalizationForCurrent(String questId)
    {
        return getLocalizationFor(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode(), questId);
    }

    public Quest getRandomQuest()
    {
        Random rng = new Random();
        int q = rng.nextInt(quests.size());
        return quests.get(q);
    }
}
