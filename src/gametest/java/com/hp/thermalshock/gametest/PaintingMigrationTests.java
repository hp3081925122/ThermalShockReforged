package com.hp.thermalshock.gametest;

import com.hp.thermalshock.ThermalShock;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ThermalShock.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PaintingMigrationTests {
    private record Expected(String id, int width, int height) {
    }

    @GameTest(template = "migration_empty")
    public static void paintingDimensionsAndPlaceableTagsLoad(GameTestHelper helper) {
        var registry = helper.getLevel().registryAccess().registryOrThrow(Registries.PAINTING_VARIANT);
        Expected[] expected = {
            new Expected("pylon_painting", 4, 2),
            new Expected("showcase_painting", 4, 2),
            new Expected("therm_painting", 2, 2),
            new Expected("cd_painting", 8, 4),
            new Expected("snowy_night_painting", 4, 2),
            new Expected("loqendian_gothic", 2, 3),
            new Expected("flowergirl", 7, 9),
            new Expected("starfall_valley", 3, 2),
            new Expected("cable_canopy", 3, 2),
            new Expected("cryonic_exile", 3, 2),
            new Expected("solarium", 3, 2),
            new Expected("sulphur_tundra", 3, 2),
            new Expected("loqendias_deep_dark", 4, 3),
            new Expected("valley_of_gears", 3, 2),
            new Expected("megacomplex_waterways", 3, 2),
            new Expected("primordialis_painting", 8, 4),
            new Expected("kiosk_sign", 3, 1)
        };
        for (var item : expected) {
            var id = ResourceLocation.fromNamespaceAndPath(ThermalShock.MOD_ID, item.id());
            var holder = registry.getHolder(id).orElseThrow();
            var value = holder.value();
            helper.assertTrue(value.width() == item.width() && value.height() == item.height(), "Painting dimensions must convert pixels to blocks without resizing: " + item.id());
            helper.assertTrue(value.assetId().equals(id), "Painting must reference its original texture: " + item.id());
            helper.assertTrue(holder.is(PaintingVariantTags.PLACEABLE), "Painting must remain available to placement: " + item.id());
        }
        helper.succeed();
    }

    @GameTest(template = "migration_empty")
    public static void largePaintingKeepsVariantAndPhysicalSizeOnSave(GameTestHelper helper) {
        var registry = helper.getLevel().registryAccess().registryOrThrow(Registries.PAINTING_VARIANT);
        var variant = registry.getHolder(ResourceLocation.fromNamespaceAndPath(ThermalShock.MOD_ID, "flowergirl")).orElseThrow();
        var painting = new Painting(helper.getLevel(), helper.absolutePos(new net.minecraft.core.BlockPos(3, 2, 3)), Direction.SOUTH, variant);
        helper.assertTrue(Math.abs(painting.getBoundingBox().getXsize() - 7) < 0.001 && Math.abs(painting.getBoundingBox().getYsize() - 9) < 0.001, "The original 112 by 144 pixel painting must occupy 7 by 9 blocks");
        var saved = new CompoundTag();
        painting.saveWithoutId(saved);
        var loaded = new Painting(EntityType.PAINTING, helper.getLevel());
        loaded.load(saved);
        helper.assertTrue(loaded.getPos().equals(painting.getPos()), "Painting anchor position must persist through the complete entity save flow");
        helper.assertTrue(loaded.getVariant().is(variant.unwrapKey().orElseThrow()), "Painting variant must persist across save and load");
        helper.assertTrue(Math.abs(loaded.getBoundingBox().getYsize() - 9) < 0.001, "Painting physical size must persist");
        helper.succeed();
    }
}
