package com.example.singleworldmod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Дозволяє удар лише тоді, коли з моменту попереднього успішного удару
 * пройшло не менше ModConfig.cooldownTicks тіків. Усі кліки до цього моменту
 * скасовуються повністю (шкоди немає взагалі).
 */
public class CombatCooldownHandler {

    private static final Map<UUID, Long> lastAttackTick = new HashMap<>();

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        EntityPlayer player = event.getEntityPlayer();

        // Рахуємо тільки на серверній стороні (у синглплеєрі це внутрішній сервер)
        if (player.world.isRemote) {
            return;
        }

        long currentTick = player.world.getTotalWorldTime();
        Long last = lastAttackTick.get(player.getUniqueID());

        if (last != null && currentTick - last < ModConfig.cooldownTicks) {
            event.setCanceled(true);
            return;
        }

        lastAttackTick.put(player.getUniqueID(), currentTick);
    }
}
