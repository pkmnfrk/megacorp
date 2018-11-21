package com.mike_caron.megacorp.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mike_caron.megacorp.MegaCorpMod;
import com.mike_caron.megacorp.ModConfig;
import com.mike_caron.megacorp.api.IReward;
import com.mike_caron.megacorp.api.IRewardFactory;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardManager
{
    public static final RewardManager INSTANCE = new RewardManager();

    private final Map<String, IReward> rewards = new HashMap<>();
    private final Map<String, Map<String, QuestLocalization>> localizations = new HashMap<>();

    private RewardManager() {}

    public void loadRewards(File userDirectory)
    {
        rewards.clear();

        JsonParser parser = new JsonParser();

        CraftingHelper.findFiles(Loader.instance().getReversedModObjectList().get(MegaCorpMod.instance), "assets/" + MegaCorpMod.modId + "/rewards", null, (root, url) -> loadRewards(parser, url, true)
        , false, true);

        if(userDirectory != null)
        {
            File[] files = userDirectory.listFiles();
            if(files != null)
            {
                for (File file : files)
                {
                    loadRewards(parser, Paths.get(file.toURI()), false);
                }
            }
        }

    }

    protected Boolean loadRewards(JsonParser parser, Path url, boolean applyBlacklists)
    {
        String extension = FilenameUtils.getExtension(url.toString());

        if("json".equals(extension))
        {
            JsonArray file;
            try (BufferedReader stream = Files.newBufferedReader(url))
            {
                file = parser.parse(stream).getAsJsonArray();
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

            for(JsonElement obj : file)
            {
                try
                {
                    JsonObject json = (JsonObject)obj;

                    String id = json.get("id").getAsString();

                    if(applyBlacklists)
                    {
                        boolean abort = false;

                        for(int i = 0; i < ModConfig.rewardBlacklist.length; i++)
                        {
                            if(ModConfig.rewardBlacklist[i].equals(id))
                            {
                                abort = true;
                                break;
                            }
                        }

                        if(abort)
                        {
                            MegaCorpMod.logger.debug("Skipping reward " + id + " because it is on the blacklist");
                            continue;
                        }
                    }

                    if(rewards.containsKey(id))
                    {
                        IReward reward = rewards.get(id);
                        Class factoryClass = Class.forName(reward.getFactoryClass());
                        IRewardFactory factoryInstance = (IRewardFactory) factoryClass.newInstance();

                        factoryInstance.updateReward(reward, json);

                        MegaCorpMod.logger.debug("Loaded overrides for reward " + id);
                    }
                    else
                    {
                        String factory = "com.mike_caron.megacorp.reward.GenericReward$Factory";

                        if (json.has("class"))
                        {
                            factory = json.get("class").getAsString();
                        }

                        Class factoryClass = Class.forName(factory);

                        IRewardFactory factoryInstance = (IRewardFactory) factoryClass.newInstance();

                        IReward instance = factoryInstance.createReward(id, json);

                        instance.setFactoryClass(factory);

                        rewards.put(id, instance);

                        MegaCorpMod.logger.debug("Loaded reward " + id);
                    }
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
    }

    private void loadLocalization(@Nonnull String locale, @Nonnull List<String> lines)
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

            MegaCorpMod.logger.debug("Loaded localization " + keyParts[1] + " for " + keyParts[0]);

        }
    }

    @Nonnull
    public QuestLocalization getLocalizationFor(@Nonnull String locale,@Nonnull String rewardId)
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
    @Nonnull
    public QuestLocalization getLocalizationForCurrent(@Nonnull String questId)
    {
        return getLocalizationFor(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode(), questId);
    }

    @Nonnull
    public List<IReward> getRewards()
    {
        return new ArrayList<>(rewards.values());
    }

    @Nullable
    public IReward getRewardWithId(@Nonnull String id)
    {
        if(rewards.containsKey(id))
            return rewards.get(id);

        return null;
    }
}
