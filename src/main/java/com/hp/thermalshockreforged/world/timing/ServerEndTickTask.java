package com.hp.thermalshockreforged.world.timing;

import net.minecraft.server.level.ServerLevel;

public interface ServerEndTickTask {
    void endTick(ServerLevel level);
}
