package com.hp.thermalshockreforged.entity.sanctum;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class SanctumConfig {
    public static final ModConfigSpec SPEC;
    public static final Profile ERRANT, SANCTUM_ERRANT;
    static {
        var builder = new ModConfigSpec.Builder();
        ERRANT = profile(builder, "errant", 62);
        SANCTUM_ERRANT = profile(builder, "sanctum_errant", 40);
        SPEC = builder.build();
    }
    private SanctumConfig() { }
    private static Profile profile(ModConfigSpec.Builder builder, String name, double health) {
        builder.push(name);
        var values = new Profile(builder.defineInRange("maxHealth", health, 1D, 1024D),
                builder.defineInRange("armor", 6D, 0D, 30D), builder.defineInRange("attackDamage", 6D, 0D, 2048D),
                builder.defineInRange("movementSpeed", .3D, 0D, 4D), builder.defineInRange("knockbackResistance", .3D, 0D, 1D),
                builder.defineInRange("attackKnockback", 1D, 0D, 2D), builder.defineInRange("strikeMultiplier", 10D / 6D, 0D, 100D),
                builder.defineInRange("strikeArmorFactor", .4D, 0D, 100D), builder.defineInRange("strikeKnockback", 1D, 0D, 1D));
        builder.pop();
        return values;
    }
    public record Profile(ModConfigSpec.DoubleValue health, ModConfigSpec.DoubleValue armor, ModConfigSpec.DoubleValue damage,
                          ModConfigSpec.DoubleValue speed, ModConfigSpec.DoubleValue resistance, ModConfigSpec.DoubleValue knockback,
                          ModConfigSpec.DoubleValue strikeMultiplier, ModConfigSpec.DoubleValue armorFactor, ModConfigSpec.DoubleValue strikeKnockback) { }
}
