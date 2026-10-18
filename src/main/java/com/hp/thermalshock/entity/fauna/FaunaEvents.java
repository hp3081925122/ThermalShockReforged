package com.hp.thermalshock.entity.fauna;

import com.hp.thermalshock.combat.AttackAttemptEvent;

final class FaunaEvents {
    private FaunaEvents() { }

    static void incomingDamage(AttackAttemptEvent event) {
        if (!event.getEntity().level().isClientSide && event.getSource().getEntity() instanceof FlareEntity) event.getEntity().igniteForSeconds(4);
    }
}
