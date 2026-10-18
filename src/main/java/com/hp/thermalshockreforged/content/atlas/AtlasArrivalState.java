package com.hp.thermalshockreforged.content.atlas;

public interface AtlasArrivalState {
    int arrivalProtectionTicks();
    boolean hasArrivalProtectionVisual();
    void beginArrivalProtection(int ticks);
    void tickArrivalProtection();
    void receiveArrivalProtectionData(net.minecraft.network.syncher.EntityDataAccessor<?> key);
}
