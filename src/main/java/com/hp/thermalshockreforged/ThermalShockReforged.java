package com.hp.thermalshockreforged;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.content.dependencies.DependencyBlocks;
import com.hp.thermalshockreforged.content.ModSounds;
import com.hp.thermalshockreforged.content.ModBlockEntities;
import com.hp.thermalshockreforged.content.ModEffects;
import com.hp.thermalshockreforged.content.terrain.TerrainContent;
import com.hp.thermalshockreforged.content.tools.ToolsContent;
import com.hp.thermalshockreforged.content.utility.UtilityContent;
import com.hp.thermalshockreforged.content.status.StatusContent;
import com.hp.thermalshockreforged.entity.fauna.FaunaEntities;
import com.hp.thermalshockreforged.entity.infestation.InfestationEntities;
import com.hp.thermalshockreforged.entity.snobol.SnobolContent;
import com.hp.thermalshockreforged.entity.ecosystem.EcosystemContent;
import com.hp.thermalshockreforged.entity.ambient.AmbientContent;
import com.hp.thermalshockreforged.content.lights.LightsContent;
import com.hp.thermalshockreforged.content.plants.PlantContent;
import com.hp.thermalshockreforged.content.plants.SpecialPlantContent;
import com.hp.thermalshockreforged.content.glass.GlassContent;
import com.hp.thermalshockreforged.content.glass.variants.GlassVariantsContent;
import com.hp.thermalshockreforged.content.farming.FarmingContent;
import com.hp.thermalshockreforged.entity.mangler.ManglerContent;
import com.hp.thermalshockreforged.entity.mangler.ManglerCombatConfig;
import com.hp.thermalshockreforged.entity.spore.SporeContent;
import com.hp.thermalshockreforged.entity.sootle.SootleContent;
import com.hp.thermalshockreforged.entity.gardenling.GardenlingContent;
import com.hp.thermalshockreforged.content.plants.tall.TallPlantContent;
import com.hp.thermalshockreforged.content.weapons.WeaponContent;
import com.hp.thermalshockreforged.content.terrain.vermilli.VermilliContent;
import com.hp.thermalshockreforged.content.status.aura.AuraContent;
import com.hp.thermalshockreforged.content.armor.ArmorContent;
import com.hp.thermalshockreforged.entity.ModEntities;
import com.hp.thermalshockreforged.config.CombatConfig;
import net.neoforged.fml.config.ModConfig;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(ThermalShockReforged.MOD_ID)
public final class ThermalShockReforged {
    public static final String MOD_ID = "thermal_shock_reforged";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ThermalShockReforged(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, CombatConfig.SPEC);
        container.registerConfig(ModConfig.Type.COMMON, ManglerCombatConfig.SPEC, "thermal_shock_reforged-mangler.toml");
        ModEntities.register(bus);
        MaterialContent.register(bus);
        DependencyBlocks.register(bus);
        ModBlockEntities.register(bus);
        ModEffects.register(bus);
        TerrainContent.register(bus);
        ToolsContent.register(bus);
        UtilityContent.register(bus);
        StatusContent.register(bus);
        FaunaEntities.register(bus);
        InfestationEntities.register(bus);
        SnobolContent.register(bus);
        EcosystemContent.register(bus);
        AmbientContent.register(bus);
        LightsContent.register(bus);
        PlantContent.register(bus);
        SpecialPlantContent.register(bus);
        GlassContent.register(bus);
        GlassVariantsContent.register(bus);
        FarmingContent.register(bus);
        ManglerContent.register(bus);
        SporeContent.register(bus);
        SootleContent.register(bus);
        GardenlingContent.register(bus);
        TallPlantContent.register(bus);
        WeaponContent.register(bus);
        VermilliContent.register(bus);
        AuraContent.register(bus);
        ArmorContent.register(bus);
        ModSounds.register(bus);
        LOGGER.info("Thermal Shock Reforged migration foundation loaded: {} material blocks and {} items", MaterialContent.blocks().size(), MaterialContent.items().size());
    }
}
