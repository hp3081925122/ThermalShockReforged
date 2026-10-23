package com.hp.thermalshockreforged.compat.jade;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.ModGameRules;
import com.hp.thermalshockreforged.content.decor.geo.AltarBlock;
import com.hp.thermalshockreforged.content.decor.geo.DecorPartBlock;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorBlock;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorContent;
import com.hp.thermalshockreforged.content.decor.geo.GeoDecorKind;
import com.hp.thermalshockreforged.content.farming.FarmSeedBlock;
import com.hp.thermalshockreforged.content.growth.GrowthSeedBlock;
import com.hp.thermalshockreforged.content.decor.geo.SuspiciousCavebloomerBlock;
import com.hp.thermalshockreforged.entity.mangler.HeraldEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin(ThermalShockReforged.MOD_ID)
public final class ThermalShockJadePlugin implements IWailaPlugin {
    private static final Identifier ALTAR = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "altar");
    private static final String STATUS = "AltarStatus";
    private static final String KEY = "jade." + ThermalShockReforged.MOD_ID + ".altar.";
    private static final Identifier SEED = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "seed");
    private static final String SEED_KEY = "jade." + ThermalShockReforged.MOD_ID + ".seed.";
    private static final String THIRSTY = "SeedThirsty";
    private static final String WATERINGS = "SeedWaterings";
    private static final int MAX_WATERINGS = 3;
    private static final Identifier HERALD = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "herald");
    private static final String HERALD_KEY = "jade." + ThermalShockReforged.MOD_ID + ".herald.";
    private static final String HERALD_KILLS = "HeraldKills";
    private static final Identifier SUSPICIOUS_CAVEBLOOMER = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "suspicious_cavebloomer");
    private static final String SUSPICIOUS_CAVEBLOOMER_KEY = "jade." + ThermalShockReforged.MOD_ID + ".suspicious_cavebloomer.";
    private static final Identifier PHOBOTITE = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "phobotite");
    private static final String PHOBOTITE_STATUS = "PhobotiteStatus";
    private static final String PHOBOTITE_KEY = "jade." + ThermalShockReforged.MOD_ID + ".phobotite.";

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(AltarProvider.INSTANCE, AltarBlock.class);
        registration.registerBlockDataProvider(PhobotiteProvider.INSTANCE, GeoDecorBlock.class);
        registration.registerBlockDataProvider(SeedProvider.INSTANCE, FarmSeedBlock.class);
        registration.registerBlockDataProvider(SeedProvider.INSTANCE, GrowthSeedBlock.class);
        registration.registerEntityDataProvider(HeraldProvider.INSTANCE, HeraldEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addRayTraceCallback((hit, accessor, original) -> {
            if (!(accessor instanceof BlockAccessor block) || !(block.getHitResult() instanceof BlockHitResult blockHit)) return accessor;
            var main = DecorPartBlock.main(block.getLevel(), block.getPosition(), block.getBlockState());
            if (main == null) return accessor;
            var level = block.getLevel();
            return registration.blockAccessor().from(block).hit(blockHit.withPosition(main)).blockState(level.getBlockState(main)).blockEntity(level.getBlockEntity(main)).build();
        });
        registration.registerBlockComponent(AltarProvider.INSTANCE, AltarBlock.class);
        registration.registerBlockComponent(PhobotiteProvider.INSTANCE, GeoDecorBlock.class);
        registration.registerBlockComponent(SeedProvider.INSTANCE, FarmSeedBlock.class);
        registration.registerBlockComponent(SeedProvider.INSTANCE, GrowthSeedBlock.class);
        registration.registerBlockComponent(SuspiciousCavebloomerProvider.INSTANCE, SuspiciousCavebloomerBlock.class);
        registration.registerEntityComponent(HeraldProvider.INSTANCE, HeraldEntity.class);
    }

    private enum HeraldProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
        INSTANCE;

        @Override
        public void appendServerData(CompoundTag data, EntityAccessor accessor) {
            if (accessor.getEntity() instanceof HeraldEntity herald) data.putInt(HERALD_KILLS, herald.killCount());
        }

        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
            var data = accessor.getServerData();
            if (data.contains(HERALD_KILLS)) tooltip.add(Component.translatable(HERALD_KEY + "kills", Math.min(data.getInt(HERALD_KILLS), 10)));
        }

        @Override
        public Identifier getUid() { return HERALD; }
    }

    private enum AltarProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
        INSTANCE;

        @Override
        public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            if (!(accessor.getPlayer() instanceof ServerPlayer player)) return;
            int status = 0;
            if (!((net.minecraft.server.level.ServerLevel) player.level()).getGameRules().getBoolean(ModGameRules.LOQENDIA_ENFORCE_PROGRESSION)) status = 2;
            else if (AltarBlock.unlocked(player)) status = 1;
            data.putInt(STATUS, status);
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            var data = accessor.getServerData();
            if (!data.contains(STATUS)) return;
            int status = data.getInt(STATUS);
            if (status == 2) tooltip.add(Component.translatable(KEY + "not_required"));
            if (status == 1) tooltip.add(Component.translatable(KEY + "unlocked"));
            if (status == 0) {
                tooltip.add(Component.translatable(KEY + "locked"));
                tooltip.add(Component.translatable(KEY + "how"));
            }
            if (status != 0) tooltip.add(Component.translatable(KEY + "use"));
        }

        @Override
        public Identifier getUid() { return ALTAR; }
    }

    private enum PhobotiteProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
        INSTANCE;

        private static boolean isPhobotite(BlockAccessor accessor) {
            var state = accessor.getBlockState();
            return state.is(GeoDecorContent.block(GeoDecorKind.PHOBOTITE_OUTCROP)) || state.is(GeoDecorContent.block(GeoDecorKind.PHOBOTITE_MANTLE_OUTCROP));
        }

        @Override
        public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            if (!isPhobotite(accessor) || !(accessor.getPlayer() instanceof ServerPlayer player)
                    || !((net.minecraft.server.level.ServerLevel) player.level()).getGameRules().getBoolean(ModGameRules.LOQENDIA_ENFORCE_PROGRESSION)
                    || AltarBlock.unlocked(player)) return;
            data.putBoolean(PHOBOTITE_STATUS, true);
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (accessor.getServerData().getBoolean(PHOBOTITE_STATUS)) tooltip.add(Component.translatable(PHOBOTITE_KEY + "locked"));
        }

        @Override
        public Identifier getUid() { return PHOBOTITE; }
    }

    private enum SeedProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
        INSTANCE;

        @Override
        public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            var entity = accessor.getBlockEntity();
            if (entity == null) return;
            var seed = entity.getPersistentData();
            data.putBoolean(THIRSTY, seed.getBoolean("thirsty"));
            data.putInt(WATERINGS, (int) seed.getDouble("waterings"));
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            var data = accessor.getServerData();
            if (!data.contains(WATERINGS)) return;
            String answer = "no";
            if (data.getBoolean(THIRSTY)) answer = "yes";
            tooltip.add(Component.translatable(SEED_KEY + "thirsty", Component.translatable(SEED_KEY + answer)));
            tooltip.add(Component.translatable(SEED_KEY + "waterings", Math.min(data.getInt(WATERINGS), MAX_WATERINGS), MAX_WATERINGS));
        }

        @Override
        public Identifier getUid() { return SEED; }
    }

    private enum SuspiciousCavebloomerProvider implements IBlockComponentProvider {
        INSTANCE;

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            tooltip.add(Component.translatable(SUSPICIOUS_CAVEBLOOMER_KEY + "budding"));
            tooltip.add(Component.translatable(SUSPICIOUS_CAVEBLOOMER_KEY + "boss"));
        }

        @Override
        public Identifier getUid() { return SUSPICIOUS_CAVEBLOOMER; }
    }
}
