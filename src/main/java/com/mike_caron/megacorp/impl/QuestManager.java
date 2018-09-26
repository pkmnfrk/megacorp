package com.mike_caron.megacorp.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.api.IQuestFactory;
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

    private final Map<String, Quest> quests = new HashMap<>();
    private final Map<String, Map<String, QuestLocalization>> localizations = new HashMap<>();
    private final Map<Quest, IQuestFactory> questFactories = new HashMap<>();

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

                String mod = "minecraft";

                if(json.has("mod"))
                    mod = json.get("mod").getAsString();

                if(!Loader.isModLoaded(mod))
                {
                    MegaCorpMod.logger.info("Skipping " + mod + " quests, because it's not loaded");
                }

                if(json.has("quests"))
                {
                    JsonArray qs = json.getAsJsonArray("quests");

                    for (JsonElement obj : qs)
                    {
                        try
                        {
                            JsonObject quest = (JsonObject) obj;

                            Quest q = Quest.fromJson(quest);

                            if(q == null) continue;

                            if(q.possibleItems().isEmpty())
                            {
                                MegaCorpMod.logger.info("Skipping quest " + q.id + " because no items exist");
                                continue;
                            }

                            if(quests.containsKey(q.id))
                            {
                                MegaCorpMod.logger.warn("Overwriting quest " + q.id);
                            }

                            quests.put(q.id, q);

                            MegaCorpMod.logger.info("Loaded quest " + q.id);
                        }
                        catch (Exception ex)
                        {
                            MegaCorpMod.logger.error("Error loading quests from " + url, ex);
                        }
                    }
                }

                if(json.has("factories"))
                {
                    JsonArray factories = json.get("factories").getAsJsonArray();

                    for(JsonElement factory : factories)
                    {
                        String className;
                        if(factory.isJsonPrimitive())
                        {
                            className = factory.getAsString();
                        }
                        else
                        {
                            JsonObject ob = factory.getAsJsonObject();
                            className = ob.get("class").getAsString();
                        }

                        try
                        {
                            Class clazz = Class.forName(className);
                            IQuestFactory fact = (IQuestFactory)clazz.newInstance();

                            List<Quest> qs = fact.createQuests();

                            MegaCorpMod.logger.info("Loading " + qs.size() + " from " + className);

                            for(Quest q : qs)
                            {
                                if(q.possibleItems().isEmpty())
                                {
                                    MegaCorpMod.logger.info("Skipping quest " + q.id + " because no items exist");
                                    continue;
                                }

                                if(quests.containsKey(q.id))
                                {
                                    MegaCorpMod.logger.warn("Overwriting quest " + q.id);
                                }

                                quests.put(q.id, q);
                                questFactories.put(q, fact);

                                MegaCorpMod.logger.info("Loaded quest " + q.id + " from " + className);
                            }
                        }
                        catch(Exception ex)
                        {
                            MegaCorpMod.logger.error("Error loading quests from " + url, ex);
                        }


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

    QuestLocalization getLocalizationFor(String locale, String questId)
    {
        if(!localizations.containsKey(locale))
            locale = "en_us";

        if(!localizations.get(locale).containsKey(questId))
        {
            localizations.get(locale).put(questId, new QuestLocalization(questId + ".title", questId + ".desc"));
        }

        return localizations.get(locale).get(questId);
    }

    /**
     * Determines whether a string exists. It does _not_ determine whether the string has been localized, only whether
     * it exists at all. Intended to be used by dynamic quests (eg, so you can have a generic localization that can be
     * overridden for specific instances)
     * @param locale the locale in question (eg, en_us)
     * @param questId the string to look up
     */

    boolean localizationExists(String locale, String questId)
    {
        if(!localizations.containsKey(locale))
            locale = "en_us";

        if(localizations.get(locale).containsKey(questId))
            return true;

        if(!locale.equals("en_us"))
            return localizations.get("en_us").containsKey(locale);

        return false;
    }

    public QuestLocalization getLocalizationFor(String locale, Quest quest)
    {
        if(questFactories.containsKey(quest))
        {
            return questFactories.get(quest).localize(locale, quest);
        }

        return getLocalizationFor(locale, quest.id);
    }

    @SideOnly(Side.CLIENT)
    public QuestLocalization getLocalizationForCurrent(String questId)
    {
        return getLocalizationForCurrent(quests.get(questId));
    }

    @SideOnly(Side.CLIENT)
    public QuestLocalization getLocalizationForCurrent(Quest quest)
    {
        return getLocalizationFor(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode(), quest);
    }

    public Quest getRandomQuest()
    {
        Random rng = new Random();
        int q = rng.nextInt(quests.size());
        return quests.values().stream().skip(q).findFirst().get();
    }

    public Quest getSpecificQuest(String questId)
    {
        if(quests.containsKey(questId))
            return quests.get(questId);
        return null;
    }

    public int getNumQuests()
    {
        return quests.size();
    }

    public List<Quest> getQuests()
    {
        return new ArrayList<>(quests.values());
    }
}
