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
            }
        }

    }

    private static void handleHunger(EntityPlayer player, Corporation corp, IPlayerRewards rewards)
    {
        int rank = corp.getRankInReward("hunger_restoration");

        if(rank == 0) return;

        int ticks = (int)(20 * 32 * Math.pow(0.5, rank));

        int hungerTicks = rewards.getHungerRestore();

        if(rewards.getDamageTimer() == 0)
        {
            hungerTicks += 1;
        }

        if(hungerTicks >= ticks)
        {
            hungerTicks = 0;

            FoodStats food = player.getFoodStats();
            if(food.needFood()){
                food.setFoodLevel(food.getFoodLevel() + 1);
            }
            else if(food.getSaturationLevel() < 20)
            {
                food.setFoodSaturationLevel(food.getSaturationLevel() + 1);
            }
        }

        rewards.setHungerRestore(hungerTicks);
    }

}
