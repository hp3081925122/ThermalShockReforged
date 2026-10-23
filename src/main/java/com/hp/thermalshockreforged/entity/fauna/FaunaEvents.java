package com.hp.thermalshockreforged.entity.fauna;

import com.hp.thermalshockreforged.combat.AttackAttemptEvent;
import com.hp.thermalshockreforged.entity.legacy.SolaEntity;

final class FaunaEvents {
    private FaunaEvents() { }

    static void incomingDamage(AttackAttemptEvent event) {
        if (!event.getEntity().level().isClientSide() && (event.getSource().getEntity() instanceof FlareEntity || event.getSource().getEntity() instanceof SolaEntity)) event.getEntity().igniteForSeconds(4);
    }
}
