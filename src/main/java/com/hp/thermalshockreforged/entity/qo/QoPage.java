package com.hp.thermalshockreforged.entity.qo;

import java.util.List;

public enum QoPage {
    INTRO_1("qo_thermoid_epl_1", "QoThermoidEpl1", "qo_em2_speak", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.qo_thermoid_epl_1.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.qo_thermoid_epl_1.label_concierge", 67, 93, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("qoidleportrait", -15, -24, 128, 128, 128, 1536, true)),
            List.of()),
    INTRO_2("qo_thermoid_epl_2", "QoThermoidEpl2", "qo_main_speak", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.qo_thermoid_epl_2.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.qo_thermoid_epl_2.label_concierge", 67, 93, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("qoidleportrait", -15, -24, 128, 128, 128, 1536, true)),
            List.of()),
    INTRO_3("qo_thermoid_epl_3", "QoThermoidEpl3", "qo_main_speak", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.qo_thermoid_epl_3.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.qo_thermoid_epl_3.label_concierge", 67, 93, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("qoem1portrait", -15, -24, 128, 128, 128, 1536, true)),
            List.of()),
    INTRO_4("qo_thermoid_epl_4", "QoThermoidEpl4", "qo_em1_speak", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.qo_thermoid_epl_4.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.qo_thermoid_epl_4.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.qo_thermoid_epl_4.label_that_badly", 9, 123, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("qoem3portrait", -15, -24, 128, 128, 128, 1536, true)),
            List.of()),
    INTRO_5("qo_thermoid_epl_5", "QoThermoidEpl5", "qo_main_speak", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.qo_thermoid_epl_5.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.qo_thermoid_epl_5.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.qo_thermoid_epl_5.label_that_badly", 9, 123, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("qoidleportrait", -15, -24, 128, 128, 128, 1536, true)),
            List.of()),
    EXHAUST("qo_exhaust", "QoExhaust", "qo_em2_speak", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.qo_exhaust.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.qo_exhaust.label_concierge", 67, 93, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("qoem1portrait", -15, -24, 128, 128, 128, 1536, true)),
            List.of()),
    PHOBOTITE("q_phobotite_1", "QPhobotite1", "qo_main_speak", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.q_phobotite_1.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.q_phobotite_1.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.q_phobotite_1.label_are_you_trying_to_blow_us_up", 9, 122, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("qoidleportrait", -15, -24, 128, 128, 128, 1536, true)),
            List.of()),
    CRYO_ALLOY("qo_cryo_alloy_1", "QoCryoAlloy1", "qo_main_speak", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.qo_cryo_alloy_1.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.qo_cryo_alloy_1.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.qo_cryo_alloy_1.label_are_you_trying_to_blow_us_up", 9, 122, -1), new Line("gui.thermal_shock_reforged.qo_cryo_alloy_1.label_i_could_actually_do_with_s", 9, 132, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("qoem2portrait", -15, -24, 128, 128, 128, 1536, true)),
            List.of()),
    CRYO_ENERGY("qo_cryo_energy_1", "QoCryoEnergy1", "qo_em2_speak", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.qo_cryo_energy_1.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.qo_cryo_energy_1.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.qo_cryo_energy_1.label_are_you_trying_to_blow_us_up", 9, 122, -1), new Line("gui.thermal_shock_reforged.qo_cryo_energy_1.label_i_could_actually_do_with_s", 9, 132, -1), new Line("gui.thermal_shock_reforged.qo_cryo_energy_1.label_my_stockpiles_been_running_low", 9, 142, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("qoidleportrait", -15, -24, 128, 128, 128, 1536, true)),
            List.of()),
    NEURAL_CHIP("qo_neural_chip_1", "QoNeuralChip1", "qo_em1_speak", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.qo_neural_chip_1.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.qo_neural_chip_1.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.qo_neural_chip_1.label_are_you_trying_to_blow_us_up", 9, 122, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("qoem3portrait", -15, -24, 128, 128, 128, 1536, true)),
            List.of()),
    STELLAR_SCRAP("qo_stellar_scrap_1", "QoStellarScrap1", "qo_em2_speak", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.qo_stellar_scrap_1.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.qo_stellar_scrap_1.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.qo_stellar_scrap_1.label_are_you_trying_to_blow_us_up", 9, 122, -1), new Line("gui.thermal_shock_reforged.qo_stellar_scrap_1.label_i_could_actually_do_with_s", 9, 132, -1), new Line("gui.thermal_shock_reforged.qo_stellar_scrap_1.label_if_youre_into_that_stuff", 9, 142, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("qoidleportrait", -15, -24, 128, 128, 128, 1536, true)),
            List.of()),
    ALTAR_KEY("qo_altar_key_1", "QoAltarKey1", "qo_main_speak", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.qo_altar_key_1.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.qo_altar_key_1.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.qo_altar_key_1.label_are_you_trying_to_blow_us_up", 9, 122, -1), new Line("gui.thermal_shock_reforged.qo_altar_key_1.label_i_could_actually_do_with_s", 9, 132, -1), new Line("gui.thermal_shock_reforged.qo_altar_key_1.label_go_dissolve_some_pylon_cores_i", 9, 142, -1), new Line("gui.thermal_shock_reforged.qo_altar_key_1.label_proud_of_yourself", 9, 152, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("qoidleportrait", -15, -24, 128, 128, 128, 1536, true)),
            List.of()),
    STRATUS_1("qo_stratus_alloy_1", "QoStratusAlloy1", "qo_em1_speak", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.qo_stratus_alloy_1.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.qo_stratus_alloy_1.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.qo_stratus_alloy_1.label_are_you_trying_to_blow_us_up", 9, 122, -1), new Line("gui.thermal_shock_reforged.qo_stratus_alloy_1.label_i_could_actually_do_with_s", 9, 132, -1), new Line("gui.thermal_shock_reforged.qo_stratus_alloy_1.label_if_youre_into_that_stuff", 9, 142, -1), new Line("gui.thermal_shock_reforged.qo_stratus_alloy_1.label_please_give_it_to_me_ill_mak", 9, 152, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("qoem2portrait", -15, -24, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    STRATUS_2("qo_stratus_alloy_2", "QoStratusAlloy2", "qo_main_speak", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.qo_stratus_alloy_2.label_kutty", 79, 59, -1)),
            List.of(new Image("mainspeechbox", 0, 50, 276, 276, 276, 276, false)),
            List.of(new Button(0, 107, 87, 20, 20, "yesbuttonb"), new Button(1, 147, 87, 20, 20, "nobuttonb"))),
    STRATUS_REJECT("qo_stratus_alloy_3a", "QoStratusAlloy3a", "qo_em2_speak", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.qo_stratus_alloy_3a.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.qo_stratus_alloy_3a.label_concierge", 67, 93, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("qoem4portrait", -5, -24, 128, 128, 128, 128, true)),
            List.of()),
    STRATUS_ACCEPT("qo_stratus_alloy_3b", "QoStratusAlloy3b", "qo_main_speak", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.qo_stratus_alloy_3b.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.qo_stratus_alloy_3b.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.qo_stratus_alloy_3b.label_oh_and_you_can_have_this_ehh", 9, 123, -1), new Line("gui.thermal_shock_reforged.qo_stratus_alloy_3b.label_why_are_you_looking_at_me", 9, 134, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("qoem2portrait", -15, -24, 128, 128, 128, 1536, true)),
            List.of());

    public final String id;
    public final String title;
    public final String sound;
    public final int width;
    public final int height;
    public final List<Line> lines;
    public final List<Image> images;
    public final List<Button> buttons;
    QoPage(String id, String title, String sound, int width, int height, List<Line> lines, List<Image> images, List<Button> buttons) {
        this.id = id; this.title = title; this.sound = sound; this.width = width; this.height = height; this.lines = lines; this.images = images; this.buttons = buttons;
    }
    public record Line(String key, int x, int y, int color) { }
    public record Image(String texture, int x, int y, int width, int height, int textureWidth, int textureHeight, boolean animated) { }
    public record Button(int action, int x, int y, int width, int height, String texture) { }
}
