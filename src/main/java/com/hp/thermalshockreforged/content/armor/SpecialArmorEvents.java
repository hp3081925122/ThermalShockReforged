package com.hp.thermalshockreforged.content.armor;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.combat.PreArmorDamageEvent;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

final class SpecialArmorEvents {
    private SpecialArmorEvents() { }
    static void hurt(PreArmorDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || !(player.level() instanceof ServerLevel server) || !Float.isFinite(event.getAmount()) || event.getAmount() < 0) return;
        var chest = player.getItemBySlot(EquipmentSlot.CHEST);
        var id = BuiltInRegistries.ITEM.getKey(chest.getItem());
        boolean silenced = id.getNamespace().equals(ThermalShockReforged.MOD_ID) && id.getPath().equals("silenced_armor_chestplate");
        if (!chest.is(ArmorContent.DEV_SCARF.get()) && !silenced || Math.random() >= .5) return;
        var origin = player.position();
        server.playSound(null, player.blockPosition(), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.NEUTRAL, 1, ThreadLocalRandom.current().nextInt(1, 3));
        server.sendParticles(ArmorContent.PURPLE_SPARKLE.get(), origin.x, origin.y + 1.2, origin.z, 15, 1, .1, 1, 0);
        var mobs = server.getEntitiesOfClass(Mob.class, new AABB(origin, origin).inflate(2));
        mobs.sort(Comparator.comparingDouble(mob -> mob.distanceToSqr(origin)));
        for (Mob mob : mobs) mob.igniteForSeconds(2);
    }
}
