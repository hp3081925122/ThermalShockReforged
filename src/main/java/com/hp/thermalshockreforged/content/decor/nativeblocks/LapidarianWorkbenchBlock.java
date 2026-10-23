package com.hp.thermalshockreforged.content.decor.nativeblocks;
import net.minecraft.world.InteractionResult;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.recipe.LapidaryRecipe;
import com.hp.thermalshockreforged.entity.ecosystem.EcosystemContent;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.*;
import net.minecraft.world.phys.*;

final class LapidarianWorkbenchBlock extends FacingDecorBlock {
    LapidarianWorkbenchBlock() {
        super(Properties.of().mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS)
                        .sound(SoundType.CHISELED_BOOKSHELF).strength(1, 5),
                new Traits(15, false, false, false, 0, 0, false, false), Outline.FULL,
                "block.thermal_shock_reforged.lapidarian_workbench.description_0");
    }
    private boolean process(Level level, BlockPos pos, Player player) {
        var input = player.getMainHandItem();
        var recipe = LapidaryRecipe.find(level, input);
        if (recipe == null) return false;
        if (!(level instanceof ServerLevel server)) return true;
        LootTable table = null;
        if (recipe.lootTable().isPresent()) table = server.getServer().reloadableRegistries().getLootTable(recipe.lootTable().get());
        AdvancementHolder advancement = null;
        if (recipe.advancement().isPresent()) advancement = server.getServer().getAdvancements().get(recipe.advancement().get());
        if (table == LootTable.EMPTY || recipe.advancement().isPresent() && advancement == null) {
            ThermalShockReforged.LOGGER.warn("Lapidarian processing dependency unavailable; keeping input: input={}, table={}, advancement={}", BuiltInRegistries.ITEM.getKey(input.getItem()), recipe.lootTable(), recipe.advancement());
            return true;
        }
        var inputId = BuiltInRegistries.ITEM.getKey(input.getItem());
        input.shrink(1);
        server.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1, 2);
        boolean springling = recipe.springlingChance() > 0;
        if (!springling) recipe.sound().play(server, pos, SoundSource.BLOCKS);
        Vec3 origin = new Vec3(pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5);
        if (table != null) {
            spawnLoot(server, origin, table);
            if (springling) recipe.sound().play(server, pos, SoundSource.BLOCKS);
            if (Math.random() < recipe.extraRollChance()) spawnLoot(server, origin, table);
        } else {
            var drop = new ItemEntity(server, origin.x, origin.y, origin.z, recipe.result().get().copy());
            drop.setPickUpDelay(10); server.addFreshEntity(drop);
        }
        if (advancement != null && player instanceof ServerPlayer owner) {
            var progress = owner.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) owner.getAdvancements().award(advancement, criterion);
        }
        if (springling && Math.random() < recipe.springlingChance()) {
            var child = EcosystemContent.SPRINGLING.get().spawn(server, BlockPos.containing(origin), EntitySpawnReason.MOB_SUMMONED);
            if (child != null) child.setDeltaMovement(0, .1, 0);
        }
        ThermalShockReforged.LOGGER.debug("Lapidarian processing completed: player={}, position={}, input={}", player.getUUID(), pos, inputId);
        return true;
    }
    private static void spawnLoot(ServerLevel level, Vec3 origin, LootTable table) {
        var params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, origin).create(LootContextParamSets.CHEST);
        for (var stack : table.getRandomItems(params)) {
            var drop = new ItemEntity(level, origin.x, origin.y, origin.z, stack);
            drop.setDefaultPickUpDelay(); level.addFreshEntity(drop);
        }
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { return process(level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { return process(level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS; }
}
