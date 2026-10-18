package com.hp.thermalshock;

import com.hp.thermalshock.content.MaterialContent;
import com.hp.thermalshock.content.dependencies.DependencyBlocks;
import com.hp.thermalshock.content.ModSounds;
import com.hp.thermalshock.content.ModBlockEntities;
import com.hp.thermalshock.content.ModEffects;
import com.hp.thermalshock.content.terrain.TerrainContent;
import com.hp.thermalshock.content.tools.ToolsContent;
import com.hp.thermalshock.content.utility.UtilityContent;
import com.hp.thermalshock.content.status.StatusContent;
import com.hp.thermalshock.entity.fauna.FaunaEntities;
import com.hp.thermalshock.entity.infestation.InfestationEntities;
import com.hp.thermalshock.entity.snobol.SnobolContent;
import com.hp.thermalshock.entity.ecosystem.EcosystemContent;
import com.hp.thermalshock.entity.ambient.AmbientContent;
import com.hp.thermalshock.content.lights.LightsContent;
import com.hp.thermalshock.content.plants.PlantContent;
import com.hp.thermalshock.content.plants.SpecialPlantContent;
import com.hp.thermalshock.content.glass.GlassContent;
import com.hp.thermalshock.content.glass.variants.GlassVariantsContent;
import com.hp.thermalshock.content.farming.FarmingContent;
import com.hp.thermalshock.entity.mangler.ManglerContent;
import com.hp.thermalshock.entity.mangler.ManglerCombatConfig;
import com.hp.thermalshock.entity.spore.SporeContent;
import com.hp.thermalshock.entity.sootle.SootleContent;
import com.hp.thermalshock.entity.gardenling.GardenlingContent;
import com.hp.thermalshock.content.plants.tall.TallPlantContent;
import com.hp.thermalshock.content.weapons.WeaponContent;
import com.hp.thermalshock.content.terrain.vermilli.VermilliContent;
import com.hp.thermalshock.content.status.aura.AuraContent;
import com.hp.thermalshock.content.armor.ArmorContent;
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
        container.registerConfig(ModConfig.Type.COMMON, ManglerCombatConfig.SPEC, "thermal_shock-mangler.toml");
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
        LOGGER.info("Thermal Shock migration foundation loaded: {} material blocks and {} items", MaterialContent.blocks().size(), MaterialContent.items().size());
    }
}
