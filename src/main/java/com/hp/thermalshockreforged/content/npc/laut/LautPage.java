package com.hp.thermalshockreforged.content.npc.laut;

import java.util.List;

public enum LautPage {
    NEED("laut_errand_1_epl_1", List.of(new Line("gui.thermal_shock_reforged.laut_errand_1_epl_1.label_kutty", 9, 112, -1, 11), new Line("gui.thermal_shock_reforged.laut_errand_1_epl_1.label_concierge", 67, 93, -1))),
    HAVE("laut_errand_1_epl_2", List.of(new Line("gui.thermal_shock_reforged.laut_errand_1_epl_2.label_kutty", 9, 112, -1, 11), new Line("gui.thermal_shock_reforged.laut_errand_1_epl_2.label_concierge", 67, 93, -1))),
    CONFIRM("laut_errand_1_epl_3", List.of(new Line("gui.thermal_shock_reforged.laut_errand_1_epl_3.label_kutty", 96, 59, -1))),
    ACCEPT("laut_errand_1_accept", List.of(new Line("gui.thermal_shock_reforged.laut_errand_1_accept.label_kutty", 9, 112, -1, 11), new Line("gui.thermal_shock_reforged.laut_errand_1_accept.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.laut_errand_1_accept.label_thank", 61, 145, -1))),
    REJECT("laut_errand_1_reject", List.of(new Line("gui.thermal_shock_reforged.laut_errand_1_reject.label_kutty", 9, 112, -6710887), new Line("gui.thermal_shock_reforged.laut_errand_1_reject.label_concierge", 67, 93, -1)));
    public record Line(String key, int x, int y, int color, int spacing) { public Line(String key, int x, int y, int color) { this(key, x, y, color, 0); } }
    public final String id;
    public final List<Line> lines;
    LautPage(String id, List<Line> lines) { this.id = id; this.lines = lines; }
    public boolean terminal() { return this == ACCEPT || this == REJECT; }
    public boolean portrait() { return this != CONFIRM; }
}
