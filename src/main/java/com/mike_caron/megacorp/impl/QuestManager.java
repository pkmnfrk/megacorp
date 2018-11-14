package com.mike_caron.megacorp.impl;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.ModConfig;
import com.mike_caron.megacorp.api.IQuestFactory;
import com.mike_caron.megacorp.util.DataUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class QuestManager
{
    public static final QuestManager INSTANCE = new QuestManager();

    private final Map<String, Quest> quests = new HashMap<>();
    private final Map<String, Map<String, QuestLocalization>> localizations = new HashMap<>();
    private final Map<Quest, IQuestFactory> questFactories = new HashMap<>();

    private final List<JsonObject> files = new ArrayList<>();
    private final Map<String, JsonElement> constants = new HashMap<>();

    private QuestManager() {}

    public void loadQuests(File userDirectory)
    {
        quests.clear();

        Loader.instance().getActiveModList().forEach(this::loadModJsonFiles);

        if(userDirectory != null)
        {
            File[] files = userDirectory.listFiles();
            if(files != null)
            {
                for (File file : files)
                {
                    loadJsonFile(Paths.get(file.toURI()), false);
                }
            }
        }

        for(JsonObject file : files)
        {
            this.loadConstants(file);
        }

        for(JsonObject file : files)
        {
            JsonObject json = DataUtils.resolveConstants(file, constants).getAsJsonObject();
            this.loadJsonData(json);
        }

        files.clear();
        constants.clear();

    }

    private void logNonError(String message, boolean userGenerated)
    {
        if(userGenerated)
        {
            MegaCorpMod.logger.info(message);
        }
    }

    private void loadModJsonFiles(ModContainer mod)
    {
        CraftingHelper.findFiles(mod, "assets/" + MegaCorpMod.modId + "/quests", null, (root, url) -> this.loadJsonFile(url, true), false, true);
    }

    private boolean loadJsonFile(Path url, boolean applyBlacklists)
    {
        JsonParser parser = new JsonParser();

        String extension = FilenameUtils.getExtension(url.toString());
        String baseName = FilenameUtils.getBaseName(url.toString());

        if("json".equals(extension))
        {
            //first check the blacklist
            if(applyBlacklists)
            {
                for(int i = 0; i < ModConfig.workorderBlacklist.length; i++)
                {
                    if(ModConfig.workorderFileBlacklist[i].equals(baseName))
                    {
                        //peace
                        logNonError("Skipping quest file " + url.toString() + " because it is on the blacklist", !applyBlacklists);
                        return true;
                    }
                }
            }

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
                logNonError("Skipping " + mod + " quests, because it's not loaded", !applyBlacklists);
                return true;
            }

            json.addProperty("_applyBlacklists", applyBlacklists);
            json.addProperty("_url", url.toString());

            files.add(json);
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
    }

    private void loadJsonData(JsonObject json)
    {
        boolean applyBlacklists = json.get("_applyBlacklists").getAsBoolean();
        String url = json.get("_url").getAsString();

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

                    if(applyBlacklists)
                    {
                        boolean abort = false;

                        for(int i = 0; i < ModConfig.workorderBlacklist.length; i++)
                        {
                            if(ModConfig.workorderBlacklist[i].equals(q.id))
                            {
                                abort = true;
                                break;
                            }
                        }

                        if(abort)
                        {
                            logNonError("Skipping quest " + q.id + " because it is on the blacklist", !applyBlacklists);
                            continue;
                        }
                    }

                    if(q.possibleItems().isEmpty())
                    {
                        logNonError("Skipping quest " + q.id + " because no items exist", !applyBlacklists);
                        continue;
                    }

                    if(quests.containsKey(q.id))
                    {
                        MegaCorpMod.logger.warn("Overwriting quest " + q.id);
                    }

                    quests.put(q.id, q);

                    logNonError("Loaded quest " + q.id, !applyBlacklists);
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
                JsonObject ob = null;
                if(factory.isJsonPrimitive())
                {
                    className = factory.getAsString();
                }
                else
                {
                    ob = factory.getAsJsonObject();
                    className = ob.get("class").getAsString();
                }

                try
                {
                    Class clazz = Class.forName(className);
                    IQuestFactory fact = (IQuestFactory)clazz.newInstance();

                    List<Quest> qs = fact.createQuests(ob);

                    logNonError("Loading " + qs.size() + " from " + className, !applyBlacklists);

                    for(Quest q : qs)
                    {
                        if(applyBlacklists)
                        {
                            boolean abort = false;

                            for(int i = 0; i < ModConfig.workorderBlacklist.length; i++)
                            {
                                if(ModConfig.workorderBlacklist[i].equals(q.id))
                                {
                                    abort = true;
                                    break;
                                }
                            }

                            if(abort)
                            {
                                logNonError("Skipping quest " + q.id + " because it is on the blacklist", !applyBlacklists);
                                continue;
                            }
                        }

                        if(q.possibleItems().isEmpty())
                        {
                            logNonError("Skipping quest " + q.id + " because no items exist", !applyBlacklists);
                            continue;
                        }

                        if(quests.containsKey(q.id))
                        {
                            MegaCorpMod.logger.warn("Overwriting quest " + q.id);
                        }

                        quests.put(q.id, q);
                        questFactories.put(q, fact);

                        logNonError("Loaded quest " + q.id + " from " + className, !applyBlacklists);
                    }
                }
                catch(Exception ex)
                {
                    MegaCorpMod.logger.error("Error loading quests from " + url, ex);
                }


            }

        }
    }

    private void loadConstants(JsonObject json)
    {
        if(json.has("constants"))
        {
            for(Map.Entry<String, JsonElement> kvp : json.get("constants").getAsJsonObject().entrySet())
            {
                if(constants.containsKey(kvp.getKey()))
                {
                    constants.put(kvp.getKey(), DataUtils.mergeJson(kvp.getValue(), constants.get(kvp.getKey())));

                    logNonError("Merged constant " + kvp.getKey(), !json.get("_applyBlacklists").getAsBoolean());
                }
                else
                {
                    constants.put(kvp.getKey(), kvp.getValue());

                    logNonError("Loaded constant " + kvp.getKey(), !json.get("_applyBlacklists").getAsBoolean());
                }
            }
        }
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

            logNonError("Loaded localization " + keyParts[1] + " for " + keyParts[0], false);

        }
    }

    public QuestLocalization getLocalizationFor(String locale, String questId)
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

    public boolean localizationExists(String locale, String questId)
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

    public Quest getQuestByNum(int num)
    {
        Preconditions.checkArgument(num >= 0 && num < quests.size());

        return quests.values().stream().skip(num).findFirst().get();
    }

    public List<Quest> getQuests()
    {
        return new ArrayList<>(quests.values());
    }
}
