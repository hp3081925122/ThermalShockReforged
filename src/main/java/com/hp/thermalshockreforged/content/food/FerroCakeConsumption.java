package com.hp.thermalshockreforged.content.food;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.decor.geo.*;
import com.hp.thermalshockreforged.world.timing.*;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class FerroCakeConsumption extends Entity implements ServerEndTickTask {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ThermalShockReforged.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<FerroCakeConsumption>> TYPE = TYPES.register("ferro_cake_consumption", () -> EntityType.Builder.of(FerroCakeConsumption::new, MobCategory.MISC).noSummon().fireImmune().sized(.01F, .01F).clientTrackingRange(0).updateInterval(20).build(ThermalShockReforged.MOD_ID + ":ferro_cake_consumption"));
    private static final String TOKEN = "FerroCakeConsumption";
    private UUID owner;
    private int ownerDeaths;
    private BlockPos anchor = BlockPos.ZERO;
    private String dimension = "";
    private int phase = 1;
    private int remaining = 5;
    private boolean armed;
    private boolean consumed;
    public FerroCakeConsumption(EntityType<? extends FerroCakeConsumption> type, Level level) { super(type, level); noPhysics = true; setNoGravity(true); }
    public static void register(IEventBus bus) { TYPES.register(bus); }
    public static boolean begin(ServerLevel level, BlockPos pos, GeoDecorBlockEntity cake, ServerPlayer player) {
        if (!player.isAlive() || cake.kind != GeoDecorKind.FERRO_CAKE || level.getBlockEntity(pos) != cake || cake.getPersistentData().getDouble("slicecount") == 1) return false;
        var task = TYPE.get().create(level);
        if (task == null) return false;
        task.owner = player.getUUID(); task.ownerDeaths = player.getStats().getValue(Stats.CUSTOM, Stats.DEATHS); task.anchor = pos.immutable(); task.dimension = level.dimension().location().toString(); task.armed = true;
        task.setPos(pos.getX(), pos.getY(), pos.getZ());
        var data = cake.getPersistentData();
        Tag previousSlice = data.get("slicecount"), previousToken = data.get(TOKEN);
        data.putDouble("slicecount", 1); data.putUUID(TOKEN, task.getUUID()); cake.setChanged();
        if (!level.addFreshEntity(task) || level.getBlockEntity(pos) != cake) {
            task.discard();
            if (level.getBlockEntity(pos) == cake && data.hasUUID(TOKEN) && data.getUUID(TOKEN).equals(task.getUUID())) {
                if (previousSlice == null) data.remove("slicecount"); else data.put("slicecount", previousSlice);
                if (previousToken == null) data.remove(TOKEN); else data.put(TOKEN, previousToken);
                cake.setChanged();
            }
            return false;
        }
        level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        level.playSound(null, pos, SoundEvents.HONEY_BLOCK_BREAK, SoundSource.NEUTRAL, 1, 1);
        level.playSound(null, pos, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "nomnomnom")), SoundSource.NEUTRAL, .8F, 1);
        level.setBlock(pos, level.getBlockState(pos).setValue(FerroCakeBlock.CAKE_ANIMATION, 1), 3);
        feed(player);
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 260, 1));
        ThermalShockReforged.LOGGER.debug("Ferro cake consumption started: player={}, position={}, dimension={}, task={}", player.getUUID(), pos, task.dimension, task.getUUID());
        return true;
    }
    private static void feed(ServerPlayer player) { player.getFoodData().eat(3, .5F); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public boolean canChangeDimensions(Level from, Level to) { return false; }
    @Override public void onAddedToLevel() { super.onAddedToLevel(); if (armed && !consumed && level() instanceof ServerLevel server) ServerEndTickTasks.schedule(server, getUUID()); }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (!armed || consumed || !dimension.equals(server.dimension().location().toString())) { discard(); return; }
            setPos(anchor.getX(), anchor.getY(), anchor.getZ()); ServerEndTickTasks.schedule(server, getUUID());
        }
    }
    @Override public void endTick(ServerLevel level) {
        if (isRemoved() || !armed || consumed || !dimension.equals(level.dimension().location().toString())) { discard(); return; }
        if (--remaining > 0) { ServerEndTickTasks.schedule(level, getUUID()); return; }
        int current = phase;
        if (current == 3) { consumed = true; discard(); }
        else { phase++; remaining = 5; ServerEndTickTasks.schedule(level, getUUID()); }
        if (current == 3 && level.getBlockState(anchor).is(GeoDecorContent.block(GeoDecorKind.FERRO_CAKE)) && level.getBlockEntity(anchor) instanceof GeoDecorBlockEntity cake) {
            var data = cake.getPersistentData();
            if (data.hasUUID(TOKEN) && data.getUUID(TOKEN).equals(getUUID())) level.setBlock(anchor, Blocks.AIR.defaultBlockState(), 3);
        }
        level.playSound(null, anchor, SoundEvents.HONEY_BLOCK_BREAK, SoundSource.NEUTRAL, 1, current + 1);
        if (current == 3) level.playSound(null, anchor, SoundEvents.PLAYER_BURP, SoundSource.NEUTRAL, 1, 1);
        ServerPlayer player = owner == null ? null : level.getServer().getPlayerList().getPlayer(owner);
        if (player != null && player.isAlive() && player.getStats().getValue(Stats.CUSTOM, Stats.DEATHS) == ownerDeaths) feed(player);
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        if (owner != null) tag.putUUID("CakeOwner", owner);
        tag.putInt("CakeOwnerDeaths", ownerDeaths);
        tag.putLong("CakeAnchor", anchor.asLong()); tag.putString("CakeDimension", dimension); tag.putInt("CakePhase", phase); tag.putInt("CakeRemaining", remaining); tag.putBoolean("CakeArmed", armed); tag.putBoolean("CakeConsumed", consumed);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        owner = tag.hasUUID("CakeOwner") ? tag.getUUID("CakeOwner") : null;
        ownerDeaths = Math.max(0, tag.getInt("CakeOwnerDeaths"));
        anchor = BlockPos.of(tag.getLong("CakeAnchor")); dimension = tag.getString("CakeDimension"); phase = Math.clamp(tag.getInt("CakePhase"), 1, 3); remaining = Math.clamp(tag.getInt("CakeRemaining"), 1, 5);
        armed = tag.getBoolean("CakeArmed") && owner != null && tag.contains("CakeAnchor", 99) && ResourceLocation.tryParse(dimension) != null; consumed = tag.getBoolean("CakeConsumed");
        noPhysics = true; setNoGravity(true);
    }
}
