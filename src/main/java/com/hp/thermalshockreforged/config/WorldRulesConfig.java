package com.hp.thermalshockreforged.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class WorldRulesConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue DROP_HEART_CRYSTALS;
    public static final ModConfigSpec.BooleanValue PYLON_CORES_EXPLODE;
    public static final ModConfigSpec.BooleanValue FIRE_EXTINGUISHES;
    public static final ModConfigSpec.BooleanValue MAGMA_FREEZES_UNDERWATER;

    static {
        var builder = new ModConfigSpec.Builder();
        DROP_HEART_CRYSTALS = builder.comment("是否允许相关生物掉落生命水晶 / Whether related mobs can drop heart crystals").define("dropHeartCrystals", true);
        PYLON_CORES_EXPLODE = builder.comment("不安全放置塔尖核心时是否爆炸 / Whether unsafe Pylon Cores explode").define("pylonCoresExplode", true);
        FIRE_EXTINGUISHES = builder.comment("洛肯迪亚内放置的火是否自动熄灭 / Whether placed fire extinguishes in Loqendia").define("fireExtinguishes", true);
        MAGMA_FREEZES_UNDERWATER = builder.comment("洛肯迪亚内水下岩浆块是否转为下界岩 / Whether submerged magma blocks become netherrack in Loqendia").define("magmaFreezesUnderwater", true);
        SPEC = builder.build();
    }

    private WorldRulesConfig() { }
}
