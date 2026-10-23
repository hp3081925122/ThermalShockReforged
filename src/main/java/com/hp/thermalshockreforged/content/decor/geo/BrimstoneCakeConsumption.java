package com.hp.thermalshockreforged.content.decor.geo;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.world.timing.*;
import java.util.Calendar;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class BrimstoneCakeConsumption extends Entity implements ServerEndTickTask {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<BrimstoneCakeConsumption>> TYPE = TYPES.register("brimstone_cake_consumption", () -> EntityType.Builder.of(BrimstoneCakeConsumption::new, MobCategory.MISC).noSummon().fireImmune().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(ThermalShockReforged.MOD_ID + ":brimstone_cake_consumption"));
    private static final String TOKEN = "BrimstoneCakeConsumption";
    private UUID owner;
    private int ownerDeaths;
    private BlockPos anchor = BlockPos.ZERO;
    private String dimension = "";
    private int phase = 1;
    private int remaining = 5;
    private boolean armed;
    private boolean consumed;

    public BrimstoneCakeConsumption(EntityType<? extends BrimstoneCakeConsumption> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static void register(IEventBus bus) { TYPES.register(bus); }
    public static boolean begin(ServerLevel level, BlockPos pos, GeoDecorBlockEntity cake, ServerPlayer player) {
        if (!player.isAlive() || cake.kind != GeoDecorKind.BRIMSTONE_CAKE || level.getBlockEntity(pos) != cake || cake.getPersistentData().getDouble("slicecount") == 1) return false;
        var task = TYPE.get().create(level);
        if (task == null) {
            ThermalShockReforged.LOGGER.debug("Brimstone cake task creation failed: position={}", pos);
            return false;
        }
        task.owner = player.getUUID();
        task.ownerDeaths = player.getStats().getValue(Stats.CUSTOM, Stats.DEATHS);
        task.anchor = pos.immutable();
        task.dimension = level.dimension().identifier().toString();
        task.armed = true;
        task.setPos(pos.getX(), pos.getY(), pos.getZ());
        var data = cake.getPersistentData();
        Tag previousSlice = data.get("slicecount"), previousToken = data.get(TOKEN);
        data.putDouble("slicecount", 1);
        data.putUUID(TOKEN, task.getUUID());
        cake.setChanged();
        if (!level.addFreshEntity(task) || task.isRemoved() || level.getEntity(task.getUUID()) != task || level.getBlockEntity(pos) != cake) {
            task.discard();
            if (level.getBlockEntity(pos) == cake && data.hasUUID(TOKEN) && data.getUUID(TOKEN).equals(task.getUUID())) {
                if (previousSlice == null) data.remove("slicecount"); else data.put("slicecount", previousSlice);
                if (previousToken == null) data.remove(TOKEN); else data.put(TOKEN, previousToken);
                cake.setChanged();
            }
            ThermalShockReforged.LOGGER.debug("Brimstone cake task insertion rejected: position={}, task={}", pos, task.getUUID());
            return false;
        }
        level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        level.playSound(null, pos, SoundEvents.HONEY_BLOCK_BREAK, SoundSource.NEUTRAL, 1, 1);
        level.playSound(null, pos, BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "nomnomnom")), SoundSource.NEUTRAL, .8F, 1);
        level.setBlock(pos, level.getBlockState(pos).setValue(BrimstoneCakeBlock.CAKE_ANIMATION, 1), 3);
        feed(player);
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 260, 1));
        ThermalShockReforged.LOGGER.debug("Brimstone cake consumption started: player={}, position={}, dimension={}, task={}", player.getUUID(), pos, task.dimension, task.getUUID());
        return true;
    }
    private static void feed(ServerPlayer player) {
        var food = player.getFoodData();
        food.setSaturation(food.getSaturationLevel() + 3);
        food.setFoodLevel(food.getFoodLevel() + 3);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && !consumed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed || consumed || !dimension.equals(server.dimension().identifier().toString())) { discard(); return; }
            setPos(anchor.getX(), anchor.getY(), anchor.getZ());
            ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || !armed || consumed || !dimension.equals(level.dimension().identifier().toString())) { discard(); return; }
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        int current = phase;
        if (current == 3) { consumed = true; discard(); }
        else { phase++; remaining = 5; ServerEndTickTasks.schedule(level, getUUID()); }
        if (current == 3 && level.hasChunkAt(anchor) && level.getBlockState(anchor).is(GeoDecorContent.block(GeoDecorKind.BRIMSTONE_CAKE)) && level.getBlockEntity(anchor) instanceof GeoDecorBlockEntity cake) {
            var data = cake.getPersistentData();
            if (data.hasUUID(TOKEN) && data.getUUID(TOKEN).equals(getUUID())) level.setBlock(anchor, Blocks.AIR.defaultBlockState(), 3);
        }
        level.playSound(null, anchor, SoundEvents.HONEY_BLOCK_BREAK, SoundSource.NEUTRAL, 1, current + 1);
        if (current == 3) level.playSound(null, anchor, SoundEvents.PLAYER_BURP, SoundSource.NEUTRAL, 1, 1);
        ServerPlayer player = owner == null ? null : level.getServer().getPlayerList().getPlayer(owner);
        if (player == null || !player.isAlive() || player.getStats().getValue(Stats.CUSTOM, Stats.DEATHS) != ownerDeaths) return;
        feed(player);
        if (current == 3 && Calendar.getInstance().get(Calendar.MONTH) == Calendar.FEBRUARY) {
            var advancement = level.getServer().getAdvancements().get(Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "thermofebruary_get"));
            if (advancement != null) {
                var progress = player.getAdvancements().getOrStartProgress(advancement);
                if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
            }
        }
    }
    @Override protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        if (owner != null) output.putString("CakeOwner", owner.toString());
        output.putInt("CakeOwnerDeaths", ownerDeaths);
        output.putLong("CakeAnchor", anchor.asLong());
        output.putString("CakeDimension", dimension);
        output.putInt("CakePhase", phase);
        output.putInt("CakeRemaining", remaining);
        output.putBoolean("CakeArmed", armed);
        output.putBoolean("CakeConsumed", consumed);
    }
    @Override protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        owner = input.getString("CakeOwner").map(java.util.UUID::fromString).orElse(null);
        ownerDeaths = Math.max(0, input.getIntOr("CakeOwnerDeaths", 0));
        anchor = BlockPos.of(input.getLongOr("CakeAnchor", 0L));
        dimension = input.getStringOr("CakeDimension", "");
        phase = Math.clamp(input.getIntOr("CakePhase", 0), 1, 3);
        remaining = Math.clamp(input.getIntOr("CakeRemaining", 0), 1, 5);
        armed = input.getBooleanOr("CakeArmed", false) && owner != null && tag.contains("CakeAnchor", 99) && Identifier.tryParse(dimension) != null;
        consumed = input.getBooleanOr("CakeConsumed", false);
        noPhysics = true;
        setNoGravity(true);
    }
}
