package com.hp.thermalshockreforged.content.dialogue;

import java.util.List;

public enum DialoguePage {
    CIVIL_1("civil_1_tlk", 0, List.of(new Line("gui.thermal_shock_reforged.civil_1_tlk.label_kutty", 9, 112), new Line("gui.thermal_shock_reforged.civil_1_tlk.label_concierge", 5, 93), new Line("gui.thermal_shock_reforged.civil_1_tlk.label_our_snow_is_really_dusty_for_s", 9, 122), new Line("gui.thermal_shock_reforged.civil_1_tlk.label_doesnt_really_stick_together", 9, 132), new Line("gui.thermal_shock_reforged.civil_1_tlk.label_but_if_i_gave_it_water_itd_f", 9, 142), new Line("gui.thermal_shock_reforged.civil_1_tlk.label_it_would_hurt_really_bad", 9, 153))),
    CIVIL_2("civil_2_tlk", 1, List.of(new Line("gui.thermal_shock_reforged.civil_2_tlk.label_kutty", 9, 112), new Line("gui.thermal_shock_reforged.civil_2_tlk.label_concierge", 5, 93), new Line("gui.thermal_shock_reforged.civil_2_tlk.label_our_snow_is_really_dusty_for_s", 9, 122), new Line("gui.thermal_shock_reforged.civil_2_tlk.label_something_like_this_i_think", 9, 133))),
    CIVIL_3("civil_3_tlk", 2, List.of(new Line("gui.thermal_shock_reforged.civil_3_tlk.label_kutty", 9, 112), new Line("gui.thermal_shock_reforged.civil_3_tlk.label_concierge", 5, 93))),
    CIVIL_4("civil_4_tlk", 3, List.of(new Line("gui.thermal_shock_reforged.civil_4_tlk.label_kutty", 9, 112), new Line("gui.thermal_shock_reforged.civil_4_tlk.label_concierge", 5, 93), new Line("gui.thermal_shock_reforged.civil_4_tlk.label_whenever_im_out_in_the_forest", 9, 123), new Line("gui.thermal_shock_reforged.civil_4_tlk.label_about_her", 9, 134))),
    CIVIL_5("civil_5_tlk", 4, List.of(new Line("gui.thermal_shock_reforged.civil_5_tlk.label_kutty", 9, 112), new Line("gui.thermal_shock_reforged.civil_5_tlk.label_concierge", 5, 93), new Line("gui.thermal_shock_reforged.civil_5_tlk.label_whenever_im_out_in_the_forest", 9, 123))),
    CIVIL_6("civil_6_tlk", 5, List.of(new Line("gui.thermal_shock_reforged.civil_6_tlk.label_kutty", 9, 112), new Line("gui.thermal_shock_reforged.civil_6_tlk.label_concierge", 5, 93), new Line("gui.thermal_shock_reforged.civil_6_tlk.label_whenever_im_out_in_the_forest", 9, 123), new Line("gui.thermal_shock_reforged.civil_6_tlk.label_i_think_its_pretty_cozy_in_ther", 9, 134))),
    CIVIL_7("civil_7_tlk", 6, List.of(new Line("gui.thermal_shock_reforged.civil_7_tlk.label_kutty", 9, 112), new Line("gui.thermal_shock_reforged.civil_7_tlk.label_concierge", 5, 93), new Line("gui.thermal_shock_reforged.civil_7_tlk.label_whenever_im_out_in_the_forest", 9, 123))),
    CIVIL_8("civil_8_tlk", 7, List.of(new Line("gui.thermal_shock_reforged.civil_8_tlk.label_kutty", 9, 112), new Line("gui.thermal_shock_reforged.civil_8_tlk.label_concierge", 5, 93))),
    RADIO("rusty_radio_tlk", 8, List.of(new Line("gui.thermal_shock_reforged.rusty_radio_tlk.label_kutty", 9, 112), new Line("gui.thermal_shock_reforged.rusty_radio_tlk.label_concierge", 5, 92), new Line("gui.thermal_shock_reforged.rusty_radio_tlk.label_our_snow_is_really_dusty_for_s", 9, 122), new Line("gui.thermal_shock_reforged.rusty_radio_tlk.label_something_like_this_i_think", 9, 133), new Line("gui.thermal_shock_reforged.rusty_radio_tlk.label_well_more_snow", 8, 144), new Line("gui.thermal_shock_reforged.rusty_radio_tlk.label_also_anyone_living_in_the", 9, 155), new Line("gui.thermal_shock_reforged.rusty_radio_tlk.label_should_prepare_for_also_snow", 8, 165)));
    public record Line(String key, int x, int y) { }
    public final String id;
    public final int variant;
    public final List<Line> lines;
    DialoguePage(String id, int variant, List<Line> lines) { this.id = id; this.variant = variant; this.lines = lines; }
    public static DialoguePage civilian(int variant) { return values()[Math.clamp(variant, 0, 7)]; }
}
