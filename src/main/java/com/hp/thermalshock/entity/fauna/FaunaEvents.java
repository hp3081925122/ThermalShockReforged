package com.hp.thermalshock.entity.fauna;

import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

final class FaunaEvents {
    private FaunaEvents() { }

    static void incomingDamage(LivingIncomingDamageEvent event) {
        if (!event.getEntity().level().isClientSide && event.getSource().getEntity() instanceof FlareEntity) event.getEntity().igniteForSeconds(4);
    }
}
