package com.hp.thermalshockreforged.entity.fauna;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

final class FaunaSwimMoveControl extends MoveControl {
    private final net.minecraft.world.entity.PathfinderMob fish;

    FaunaSwimMoveControl(net.minecraft.world.entity.PathfinderMob fish) {
        super(fish);
        this.fish = fish;
    }

    @Override
    public void tick() {
        if (fish.isInWater()) fish.setDeltaMovement(fish.getDeltaMovement().add(0, 0.005, 0));
        if (operation != Operation.MOVE_TO || fish.getNavigation().isDone()) {
            fish.setSpeed(0);
            fish.setYya(0);
            fish.setZza(0);
            return;
        }
        double dx = wantedX - fish.getX();
        double dy = wantedY - fish.getY();
        double dz = wantedZ - fish.getZ();
        float yaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90;
        float speed = (float) (speedModifier * fish.getAttributeValue(Attributes.MOVEMENT_SPEED));
        fish.setYRot(rotlerp(fish.getYRot(), yaw, 10));
        fish.yBodyRot = fish.getYRot();
        fish.yHeadRot = fish.getYRot();
        if (fish.isInWater()) {
            fish.setSpeed((float) fish.getAttributeValue(Attributes.MOVEMENT_SPEED));
            float pitch = -(float) (Mth.atan2(dy, (float) Math.sqrt(dx * dx + dz * dz)) * Mth.RAD_TO_DEG);
            pitch = Mth.clamp(Mth.wrapDegrees(pitch), -85, 85);
            fish.setXRot(rotlerp(fish.getXRot(), pitch, 5));
            fish.setZza(Mth.cos(fish.getXRot() * Mth.DEG_TO_RAD) * speed);
            fish.setYya((float) (speed * dy));
        } else {
            fish.setSpeed(speed * 0.05F);
        }
    }
}
