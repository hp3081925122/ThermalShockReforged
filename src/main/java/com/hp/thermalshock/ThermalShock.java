package com.hp.thermalshock;

import com.hp.thermalshock.content.MaterialContent;
import com.hp.thermalshock.content.ModSounds;
import com.hp.thermalshock.content.ModBlockEntities;
import com.hp.thermalshock.content.ModEffects;
import com.hp.thermalshock.entity.ModEntities;
import com.hp.thermalshock.config.CombatConfig;
import net.neoforged.fml.config.ModConfig;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(ThermalShock.MOD_ID)
public final class ThermalShock {
    public static final String MOD_ID = "thermal_shock";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ThermalShock(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, CombatConfig.SPEC);
        ModEntities.register(bus);
        MaterialContent.register(bus);
        ModBlockEntities.register(bus);
        ModEffects.register(bus);
        ModSounds.register(bus);
        LOGGER.info("Thermal Shock migration foundation loaded: {} material blocks and {} items", MaterialContent.blocks().size(), MaterialContent.items().size());
    }
}
