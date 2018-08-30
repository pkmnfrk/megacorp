package com.mike_caron.megacorp.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.api.IReward;
import com.mike_caron.megacorp.api.IRewardFactory;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardManager
{
    public static final RewardManager INSTANCE = new RewardManager();

    private Map<String, IReward> rewards = new HashMap<>();
    private final Map<String, Map<String, QuestLocalization>> localizations = new HashMap<>();

    private RewardManager() {}

    public void loadRewards()
    {
        rewards.clear();

        JsonParser parser = new JsonParser();

        CraftingHelper.findFiles(Loader.instance().getReversedModObjectList().get(MegaCorpMod.instance), "assets/" + MegaCorpMod.modId + "/rewards", null, (root, url) ->
        {
            String extension = FilenameUtils.getExtension(url.toString());

            if("json".equals(extension))
            {
                JsonArray json;
                try (BufferedReader stream = Files.newBufferedReader(url))
                {
                    json = parser.parse(stream).getAsJsonArray();
                }
                catch(RuntimeException | IOException ex)
                {
                    MegaCorpMod.logger.error("Encountered error while reading " + url, ex);
                    return true;
                }

                /*
                String mod = json.get("mod").getAsString();

                if(!Loader.isModLoaded(mod))
                {
                    MegaCorpMod.logger.info("Skipping " + mod + " quests, because it's not loaded");
                }
                */

                for(JsonElement obj : json)
                {
                    try
                    {
                        JsonObject reward = (JsonObject)obj;

                        String factory = "com.mike_caron.megacorp.reward.GenericReward$Factory";

                        if(reward.has("class"))
                        {
                            factory = reward.get("class").getAsString();
                        }

                        Class factoryClass = Class.forName(factory);

                        IRewardFactory factoryInstance = (IRewardFactory)factoryClass.newInstance();

                        IReward instance = factoryInstance.createReward(reward);

                        String id = reward.get("id").getAsString();

                        rewards.put(id, instance);

                        MegaCorpMod.logger.info("Loaded reward " + id);
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

    public QuestLocalization getLocalizationFor(String locale, String rewardId)
    {
        if(!localizations.containsKey(locale))
            locale = "en_us";

        if(!localizations.get(locale).containsKey(rewardId))
        {
            localizations.get(locale).put(rewardId, new QuestLocalization(rewardId + ".title", rewardId + ".desc"));
        }

        return localizations.get(locale).get(rewardId);
    }

    @SideOnly(Side.CLIENT)
    public QuestLocalization getLocalizationForCurrent(String questId)
    {
        return getLocalizationFor(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode(), questId);
    }
}
