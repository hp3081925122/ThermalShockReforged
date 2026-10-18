package com.hp.thermalshockreforged.content.npc.laut;

import java.util.List;

public enum LautPage {
    NEED("laut_errand_1_epl_1", List.of(new Line("gui.thermal_shock_reforged.laut_errand_1_epl_1.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.laut_errand_1_epl_1.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.laut_errand_1_epl_1.label_especially_if_you_have_xp", 9, 123, -1), new Line("gui.thermal_shock_reforged.laut_errand_1_epl_1.label_were_you_down_there_for_a_while", 9, 134, -1), new Line("gui.thermal_shock_reforged.laut_errand_1_epl_1.label_second_and_all_gone", 9, 145, -1), new Line("gui.thermal_shock_reforged.laut_errand_1_epl_1.label_how_will_i_ever_how_will_i", 9, 156, -1), new Line("gui.thermal_shock_reforged.laut_errand_1_epl_1.label_willi_ever", 9, 166, -1))),
    HAVE("laut_errand_1_epl_2", List.of(new Line("gui.thermal_shock_reforged.laut_errand_1_epl_2.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.laut_errand_1_epl_2.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.laut_errand_1_epl_2.label_especially_if_you_have_xp", 9, 123, -1), new Line("gui.thermal_shock_reforged.laut_errand_1_epl_2.label_were_you_down_there_for_a_while", 9, 134, -1), new Line("gui.thermal_shock_reforged.laut_errand_1_epl_2.label_second_and_all_gone", 9, 145, -1))),
    CONFIRM("laut_errand_1_epl_3", List.of(new Line("gui.thermal_shock_reforged.laut_errand_1_epl_3.label_kutty", 96, 59, -1))),
    ACCEPT("laut_errand_1_accept", List.of(new Line("gui.thermal_shock_reforged.laut_errand_1_accept.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.laut_errand_1_accept.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.laut_errand_1_accept.label_especially_if_you_have_xp", 9, 123, -1), new Line("gui.thermal_shock_reforged.laut_errand_1_accept.label_were_you_down_there_for_a_while", 9, 134, -1), new Line("gui.thermal_shock_reforged.laut_errand_1_accept.label_thank", 61, 145, -1))),
    REJECT("laut_errand_1_reject", List.of(new Line("gui.thermal_shock_reforged.laut_errand_1_reject.label_kutty", 9, 112, -6710887), new Line("gui.thermal_shock_reforged.laut_errand_1_reject.label_concierge", 67, 93, -1)));
    public record Line(String key, int x, int y, int color) { }
    public final String id;
    public final List<Line> lines;
    LautPage(String id, List<Line> lines) { this.id = id; this.lines = lines; }
    public boolean terminal() { return this == ACCEPT || this == REJECT; }
    public boolean portrait() { return this == NEED || this == HAVE; }
}
