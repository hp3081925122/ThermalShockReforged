package com.hp.thermalshock.content.farming;

public enum CropKind {
    SKORIO("skorio", false, false, false),
    BARBUBBLE("barbubble", false, false, false),
    ANCIENT_SKORIO("ancient_skorio", true, true, false),
    ANCIENT_BARBUBBLE("ancient_barbubble", true, true, false),
    DEWLEAF("dewleaf", true, true, false),
    HEARTHFLY_BRANCH("hearthfly_branch", true, true, true),
    FROST_LOTUS("frost_lotus", true, true, true),
    NEOFLORM("neoflorm", true, false, true),
    VOLATINE("volatine", true, false, false);

    public final String id;
    public final boolean geo;
    public final boolean gardenling;
    public final boolean luminous;
    CropKind(String id, boolean geo, boolean gardenling, boolean luminous) {
        this.id = id;
        this.geo = geo;
        this.gardenling = gardenling;
        this.luminous = luminous;
    }
    public String model() { return this == FROST_LOTUS ? "small_spire_blossom" : id; }
}
