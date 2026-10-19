package com.hp.thermalshockreforged.content.npc.huntress;

import java.util.List;

public enum HuntressPage {
    MAIN("huntress_main", "HuntressMain", List.of(new Line("gui.thermal_shock_reforged.huntress_main.label_concierge", 67, 92, -1)), List.of(new Button(-1, 203, -6, 65, 22, "maininfobuttona"), new Button(-1, 203, 18, 65, 22, "maintradebuttona"), new Button(-1, 203, 42, 65, 22, "mainerrandsbuttona"))),
    SELF("huntress_self_info", "HuntressSelfInfo", List.of(new Line("gui.thermal_shock_reforged.huntress_self_info.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.huntress_self_info.label_concierge", 67, 92, -1), new Line("gui.thermal_shock_reforged.huntress_self_info.label_suitable_land_to_expand_out_terr", 9, 121, -1), new Line("gui.thermal_shock_reforged.huntress_self_info.label_we_build_our_nests_in", 9, 130, -1), new Line("gui.thermal_shock_reforged.huntress_self_info.label_birth_to_hunt_survive_and_defe", 9, 139, -1), new Line("gui.thermal_shock_reforged.huntress_self_info.label_fight_against_the_infection", 9, 149, -1)), List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    VILEBATH("huntress_vilebath_info", "HuntressVilebathInfo", List.of(new Line("gui.thermal_shock_reforged.huntress_vilebath_info.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.huntress_vilebath_info.label_concierge", 67, 92, -1), new Line("gui.thermal_shock_reforged.huntress_vilebath_info.label_suitable_land_to_expand_out_terr", 9, 121, -1), new Line("gui.thermal_shock_reforged.huntress_vilebath_info.label_we_build_our_nests_in", 9, 130, -1), new Line("gui.thermal_shock_reforged.huntress_vilebath_info.label_birth_to_hunt_survive_and_defe", 9, 140, -1), new Line("gui.thermal_shock_reforged.huntress_vilebath_info.label_fight_against_the_infection", 9, 149, -1), new Line("gui.thermal_shock_reforged.huntress_vilebath_info.label_i_missed_their_prime", 9, 158, -1)), List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    INTRO("huntress_intro", "HuntressIntro", List.of(new Line("gui.thermal_shock_reforged.huntress_intro.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.huntress_intro.label_concierge", 67, 92, -1), new Line("gui.thermal_shock_reforged.huntress_intro.label_suitable_land_to_expand_out_terr", 9, 121, -1), new Line("gui.thermal_shock_reforged.huntress_intro.label_we_build_our_nests_in", 9, 130, -1), new Line("gui.thermal_shock_reforged.huntress_intro.label_birth_to_hunt_survive_and_defe", 9, 140, -1), new Line("gui.thermal_shock_reforged.huntress_intro.label_fight_against_the_infection", 9, 149, -1)), List.of(new Button(0, 237, 161, 38, 20, "nextbuttona")));
    public final String id;
    public final String title;
    public final List<Line> lines;
    public final List<Button> buttons;
    HuntressPage(String id, String title, List<Line> lines, List<Button> buttons) { this.id = id; this.title = title; this.lines = lines; this.buttons = buttons; }
    public record Line(String key, int x, int y, int color) { }
    public record Button(int action, int x, int y, int width, int height, String texture) { }
}
