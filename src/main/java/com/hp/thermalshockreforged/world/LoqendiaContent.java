package com.hp.thermalshockreforged.world;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.loqendiaportal.LoqendiaPortalBlock;
import java.util.Set;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LoqendiaContent {
    public static final net.minecraft.resources.Identifier LOQENDIA_ID = net.minecraft.resources.Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "loqendia");
    public static final net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> DIMENSION = net.minecraft.resources.ResourceKey.create(Registries.DIMENSION, LOQENDIA_ID);
    private static final DeferredRegister<Block> BLOCKS = com.hp.thermalshockreforged.ModRegistration.blocks(ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<Block, LoqendiaPortalBlock> PORTAL = BLOCKS.register("loqendia_portal", () -> new LoqendiaPortalBlock());
    private static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<PoiType, PoiType> PORTAL_POI = POI_TYPES.register("loqendia_portal", () -> new PoiType(Set.copyOf(PORTAL.get().getStateDefinition().getPossibleStates()), 0, 1));
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GOLD_GLITTER = PARTICLES.register("gold_glitter", () -> new SimpleParticleType(false));
    private static final DeferredRegister<StructureProcessorType<?>> PROCESSORS = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<WaterloggingFixProcessor>> WATERLOGGING_FIX = PROCESSORS.register("waterlogging_fix", () -> () -> WaterloggingFixProcessor.CODEC);
    private LoqendiaContent() { }
    private static final DeferredRegister<net.minecraft.server.level.TicketType> TICKETS = DeferredRegister.create(Registries.TICKET_TYPE, ThermalShockReforged.MOD_ID);
    public static final net.neoforged.neoforge.registries.DeferredHolder<net.minecraft.server.level.TicketType, net.minecraft.server.level.TicketType> PORTAL_TICKET = TICKETS.register("loqendia_portal", () -> new net.minecraft.server.level.TicketType(300L, net.minecraft.server.level.TicketType.FLAG_LOADING | net.minecraft.server.level.TicketType.FLAG_SIMULATION | net.minecraft.server.level.TicketType.FLAG_KEEP_DIMENSION_ACTIVE));

    public static void register(IEventBus bus) { BLOCKS.register(bus); TICKETS.register(bus); POI_TYPES.register(bus); PARTICLES.register(bus); PROCESSORS.register(bus); LoqendiaRules.register(); }
}
