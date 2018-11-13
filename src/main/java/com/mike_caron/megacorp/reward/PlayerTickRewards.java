package com.mike_caron.megacorp.reward;

import com.mike_caron.megacorp.impl.Corporation;
import com.mike_caron.megacorp.impl.CorporationManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;

public class PlayerTickRewards
{
    public static void handle(EntityPlayer player)
    {
        if(player.world.isRemote) return;

        CorporationManager manager = CorporationManager.get(player.world);
        if(manager.ownerHasCorporation(player.getUniqueID()))
        {
            Corporation corp = manager.getCorporationForOwnerInternal(player.getUniqueID());
            IPlayerRewards rewards = player.getCapability(PlayerRewardsProvider.REWARDS, null);

            if(rewards != null)
            {
                if (player.hurtTime != 0)
                {
                    rewards.setDamageTimer(60);
                } else if(rewards.getDamageTimer() > 0)
                {
                    rewards.setDamageTimer(rewards.getDamageTimer() - 1);
                }

                handleHunger(player, corp, rewards);
                handleHealth(player, corp, rewards);
            }
        }

    }

    private static void handleHunger(EntityPlayer player, Corporation corp, IPlayerRewards rewards)
    {
        int rank = corp.getRankInReward("hunger_restoration");

        if(rank == 0) return;

        int ticks = (int)(20 * 32 * Math.pow(0.5, rank - 1));

        int hungerTicks = rewards.getHungerRestore();

        if(rewards.getDamageTimer() == 0)
        {
            hungerTicks += 1;
        }

        if(hungerTicks >= ticks)
        {
            hungerTicks = 0;

            //MegaCorpMod.logger.info("Feeding after {} ticks", ticks);

            FoodStats food = player.getFoodStats();

            food.addStats(1, 0.5f);
        }

        rewards.setHungerRestore(hungerTicks);
    }

    private static void handleHealth(EntityPlayer player, Corporation corp, IPlayerRewards rewards)
    {
        int rank = corp.getRankInReward("health_restoration");

        if(rank == 0) return;

        int ticks = (int)(20 * 32 * Math.pow(0.5, rank - 1));

        int healthTicks = rewards.getHealthRestore();

        healthTicks += 1;

        if(healthTicks >= ticks)
        {
            healthTicks = 0;

            //MegaCorpMod.logger.info("Healing after {} ticks", ticks);

            if(player.getHealth() < player.getMaxHealth())
            {
                player.setHealth(player.getHealth() + 1);
            }
        }

        rewards.setHealthRestore(healthTicks);
    }

}
