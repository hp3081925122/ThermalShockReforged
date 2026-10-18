package com.hp.thermalshockreforged.content.decor.nativeblocks;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.entity.ecosystem.EcosystemContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.MobSpawnType;
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
    private record Processing(String table, String gem, SoundEvent sound, float volume, boolean springling, boolean extraRoll) { }
    LapidarianWorkbenchBlock() {
        super(Properties.of().mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS)
                        .sound(SoundType.CHISELED_BOOKSHELF).strength(1, 5),
                new Traits(15, false, false, false, 0, 0, false, false), Outline.FULL,
                "block.thermal_shock_reforged.lapidarian_workbench.description_0");
    }
    private static Processing processing(ItemStack stack) {
        var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id.equals(ResourceLocation.withDefaultNamespace("gravel"))) return new Processing("gravel_lapidary", null, SoundEvents.GRAVEL_BREAK, .6F, false, false);
        if (!id.getNamespace().equals(ThermalShockReforged.MOD_ID)) return null;
        return switch (id.getPath()) {
            case "muck" -> new Processing("blocks/muck_loot", null, SoundEvents.MUD_BREAK, 1, false, false);
            case "sparkly_muck" -> new Processing("sparkly_muck_loot", null, SoundEvents.SLIME_BLOCK_BREAK, 1, true, false);
            case "rough_ruby" -> new Processing(null, "ruby", SoundEvents.GLASS_BREAK, .35F, false, false);
            case "rough_bloodstone" -> new Processing(null, "bloodstone", SoundEvents.GLASS_BREAK, .35F, false, false);
            case "pathiolite_scrap" -> new Processing("gameplay/pathiolite_lapidary", null, SoundEvents.GILDED_BLACKSTONE_BREAK, .6F, false, false);
            case "thermal_geyser" -> new Processing("thermal_vent_lapidary", null, SoundEvents.NETHERRACK_BREAK, .6F, false, false);
            case "glittery_shale" -> new Processing("gameplay/glittery_shale_lapidary", null, SoundEvents.NETHERRACK_BREAK, .6F, false, false);
            case "metallic_debris" -> new Processing("gameplay/metallic_debris_lapidary", null, SoundEvents.ANCIENT_DEBRIS_BREAK, .6F, false, false);
            case "modest_archive_cabinet" -> new Processing("gameplay/modest_archive_lapidary", null, SoundEvents.GLASS_BREAK, .6F, false, false);
            case "crammed_archive_cabinet" -> new Processing("gameplay/modest_archive_lapidary", null, SoundEvents.GLASS_BREAK, .6F, false, true);
            default -> null;
        };
    }
    private boolean process(Level level, BlockPos pos, Player player) {
        var input = player.getMainHandItem();
        var recipe = processing(input);
        if (recipe == null) return false;
        if (!(level instanceof ServerLevel server)) return true;
        LootTable table = recipe.table() == null ? null : server.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, recipe.table())));
        var advancement = recipe.gem() == null ? null : server.getServer().getAdvancements().get(ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "lapidary_get"));
        var gemId = recipe.gem() == null ? null : ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, recipe.gem());
        if (table == LootTable.EMPTY || gemId != null && (!BuiltInRegistries.ITEM.containsKey(gemId) || advancement == null)) {
            ThermalShockReforged.LOGGER.warn("Lapidarian processing dependency unavailable; keeping input: input={}, table={}, gem={}", BuiltInRegistries.ITEM.getKey(input.getItem()), recipe.table(), gemId);
            return true;
        }
        var inputId = BuiltInRegistries.ITEM.getKey(input.getItem());
        input.shrink(1);
        server.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1, 2);
        if (!recipe.springling()) server.playSound(null, pos, recipe.sound(), SoundSource.BLOCKS, recipe.volume(), 1);
        Vec3 origin = new Vec3(pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5);
        if (table != null) {
            spawnLoot(server, origin, table);
            if (recipe.springling()) server.playSound(null, pos, recipe.sound(), SoundSource.BLOCKS, recipe.volume(), 1);
            if (recipe.extraRoll() && Math.random() < .5) spawnLoot(server, origin, table);
        } else {
            var drop = new ItemEntity(server, origin.x, origin.y, origin.z, new ItemStack(BuiltInRegistries.ITEM.get(gemId)));
            drop.setPickUpDelay(10); server.addFreshEntity(drop);
            if (player instanceof ServerPlayer owner) {
                var progress = owner.getAdvancements().getOrStartProgress(advancement);
                if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) owner.getAdvancements().award(advancement, criterion);
            }
        }
        if (recipe.springling() && Math.random() < .01) {
            var child = EcosystemContent.SPRINGLING.get().spawn(server, BlockPos.containing(origin), MobSpawnType.MOB_SUMMONED);
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
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { return process(level, pos, player) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { return process(level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS; }
}
