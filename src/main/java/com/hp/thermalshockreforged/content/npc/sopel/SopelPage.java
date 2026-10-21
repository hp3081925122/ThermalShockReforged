package com.hp.thermalshockreforged.content.npc.sopel;

import java.util.List;

public enum SopelPage {
    INTRO_1("sopel_intro_epl_1", List.of(new Line("gui.thermal_shock_reforged.sopel_intro_epl_1.label_kutty", 9, 112, -65536, 10), new Line("gui.thermal_shock_reforged.sopel_intro_epl_1.label_concierge", 67, 94, -65536))),
    INTRO_2("sopel_intro_epl_2", List.of(new Line("gui.thermal_shock_reforged.sopel_intro_epl_2.label_kutty", 9, 112, -65536, 10), new Line("gui.thermal_shock_reforged.sopel_intro_epl_2.label_concierge", 67, 94, -65536))),
    INTRO_3("sopel_intro_epl_3", List.of(new Line("gui.thermal_shock_reforged.sopel_intro_epl_3.label_kutty", 9, 112, -65536, 10), new Line("gui.thermal_shock_reforged.sopel_intro_epl_3.label_concierge", 67, 94, -65536))),
    INTRO_4("sopel_intro_epl_4", List.of(new Line("gui.thermal_shock_reforged.sopel_intro_epl_4.label_kutty", 9, 112, -65536, 10), new Line("gui.thermal_shock_reforged.sopel_intro_epl_4.label_concierge", 67, 94, -65536))),
    CHOICE("sopel_intro_epl_choice_1", List.of(new Line("gui.thermal_shock_reforged.sopel_intro_epl_choice_1.label_kutty", 90, 59, -1))),
    YES("sopel_intro_epl_5", List.of(new Line("gui.thermal_shock_reforged.sopel_intro_epl_5.label_kutty", 9, 112, -65536, 10), new Line("gui.thermal_shock_reforged.sopel_intro_epl_5.label_concierge", 67, 94, -65536))),
    NO("sopel_intro_epl_5b", List.of(new Line("gui.thermal_shock_reforged.sopel_intro_epl_5b.label_kutty", 9, 112, -65536, 10), new Line("gui.thermal_shock_reforged.sopel_intro_epl_5b.label_concierge", 67, 94, -65536)));
    public final String id;
    public final String title;
    public final List<Line> lines;
    SopelPage(String id, List<Line> lines) {
        this.id = id; this.lines = lines;
        var text = new StringBuilder(); for (String part : id.split("_")) text.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)); title = text.toString();
    }
    public SopelPage next(int button) {
        return switch (this) {
            case INTRO_1 -> INTRO_2;
            case INTRO_2 -> INTRO_3;
            case INTRO_3 -> INTRO_4;
            case INTRO_4 -> CHOICE;
            case CHOICE -> button == 0 ? YES : NO;
            case YES, NO -> null;
        };
    }
    public record Line(String key, int x, int y, int color, int spacing) { public Line(String key, int x, int y, int color) { this(key, x, y, color, 0); } }
}
