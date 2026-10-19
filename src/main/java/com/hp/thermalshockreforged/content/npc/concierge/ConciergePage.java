package com.hp.thermalshockreforged.content.npc.concierge;

import java.util.List;

public enum ConciergePage {
    ERRAND_1("conc_errand_1_epl_1", "ConcErrand1Epl1", 276, 182, null,
            List.of(new Line("gui.thermal_shock_reforged.conc_errand_1_epl_1.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_1.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_1.label_especially_if_you_have_xp", 9, 122, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_1.label_i_know_a_way_to_pacify_them_b", 9, 132, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_1.label_calls_for_a_rather_special_ing", 9, 142, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_1.label_id_do_it_myself_of_course", 9, 152, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_1.label_to_be_missing", 9, 162, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("concem1portrait", -32, -20, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    ERRAND_2("conc_errand_1_epl_2", "ConcErrand1Epl2", 276, 182, null,
            List.of(new Line("gui.thermal_shock_reforged.conc_errand_1_epl_2.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_2.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_2.label_especially_if_you_have_xp", 9, 122, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_2.label_i_know_a_way_to_pacify_them_b", 9, 132, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_2.label_calls_for_a_rather_special_ing", 9, 142, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_2.label_sins_of", 9, 153, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("concem2portrait", -32, -20, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    ERRAND_3("conc_errand_1_epl_3", "ConcErrand1Epl3", 276, 182, null,
            List.of(new Line("gui.thermal_shock_reforged.conc_errand_1_epl_3.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_3.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_3.label_especially_if_you_have_xp", 9, 122, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_3.label_his_primordial_blood", 9, 132, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_3.label_i_suspect_he_too_has_been_tarn", 9, 142, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_3.label_the_pure_primordial_blood_may", 9, 152, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_3.label_extracted_from_his_core", 9, 162, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("concem1portrait", -32, -20, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"), new Button(-1, 233, 29, 16, 16, "primordial_blood"))),
    FINISH("conc_errand_1_epl_f", "ConcErrand1EplF", 276, 182, null,
            List.of(new Line("gui.thermal_shock_reforged.conc_errand_1_epl_f.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_f.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_f.label_especially_if_you_have_xp", 9, 123, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_f.label_his_primordial_blood", 9, 134, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_f.label_floorboards_this_whole_time", 9, 144, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_f.label_regardless", 9, 154, -1), new Line("gui.thermal_shock_reforged.conc_errand_1_epl_f.label_want_to_let_all_your_work_go_to", 9, 164, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("concem2portrait", -32, -20, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    NONE("concierge_errand_none", "ConciergeErrandNone", 276, 182, null,
            List.of(new Line("gui.thermal_shock_reforged.concierge_errand_none.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.concierge_errand_none.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.concierge_errand_none.label_especially_if_you_have_xp", 9, 123, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("concrubiconidleportrait", -32, -20, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    INFO("concierge_info", "ConciergeInfo", 212, 116, null,
            List.of(),
            List.of(new Image("conciergeinfobox", -1, 0, 212, 116, 212, 116, false)),
            List.of(new Button(0, 76, 32, 59, 20, "infoinfection"), new Button(1, 138, 32, 59, 20, "infoblizzard"), new Button(2, 14, 32, 59, 20, "infoconcierge"), new Button(3, 76, 55, 59, 20, "infovilebath"), new Button(4, 14, 55, 59, 20, "infoumabel"))),
    MAIN("concierge_main", "ConciergeMain", 276, 182, null,
            List.of(new Line("gui.thermal_shock_reforged.concierge_main.label_concierge", 67, 93, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainflavourboxes", 62, -110, 214, 214, 214, 214, false), new Image("concrubiconidleportrait", -32, -20, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 203, -6, 65, 22, "maininfobuttona"), new Button(1, 203, 18, 65, 22, "maintradebuttona"), new Button(2, 203, 42, 65, 22, "mainerrandsbuttona"))),
    TRADE_INTRO("concierge_trade_epl", "ConciergeTradeEpl", 276, 182, null,
            List.of(new Line("gui.thermal_shock_reforged.concierge_trade_epl.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.concierge_trade_epl.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.concierge_trade_epl.label_especially_if_you_have_xp", 9, 123, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("concem2portrait", -32, -20, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    BLIZZARD("conc_info_blizzard", "ConcInfoBlizzard", 276, 182, null,
            List.of(new Line("gui.thermal_shock_reforged.conc_info_blizzard.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.conc_info_blizzard.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.conc_info_blizzard.label_especially_if_you_have_xp", 9, 123, -1), new Line("gui.thermal_shock_reforged.conc_info_blizzard.label_now_i_remain_here_i_dare_not", 9, 134, -1), new Line("gui.thermal_shock_reforged.conc_info_blizzard.label_the_things_they_do_now", 9, 145, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("concem3portrait", -32, -20, 128, 128, 128, 128, false)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    FLORIST("conc_info_florist", "ConcInfoFlorist", 276, 182, null,
            List.of(new Line("gui.thermal_shock_reforged.conc_info_florist.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.conc_info_florist.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.conc_info_florist.label_especially_if_you_have_xp", 9, 123, -1), new Line("gui.thermal_shock_reforged.conc_info_florist.label_now_i_remain_here_i_dare_not", 9, 134, -1), new Line("gui.thermal_shock_reforged.conc_info_florist.label_lest_i_risk_being_taken_by_that", 9, 144, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("concem2portrait", -32, -20, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    INFECTION("conc_info_infection", "ConcInfoInfection", 276, 182, null,
            List.of(new Line("gui.thermal_shock_reforged.conc_info_infection.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.conc_info_infection.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.conc_info_infection.label_especially_if_you_have_xp", 9, 123, -1), new Line("gui.thermal_shock_reforged.conc_info_infection.label_now_i_remain_here_i_dare_not", 9, 134, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("concem1portrait", -32, -20, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    SELF("conc_info_self", "ConcInfoSelf", 276, 182, null,
            List.of(new Line("gui.thermal_shock_reforged.conc_info_self.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.conc_info_self.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.conc_info_self.label_especially_if_you_have_xp", 9, 123, -1), new Line("gui.thermal_shock_reforged.conc_info_self.label_now_i_remain_here_i_dare_not", 9, 134, -1), new Line("gui.thermal_shock_reforged.conc_info_self.label_lest_i_risk_being_taken_by_that", 9, 144, -1), new Line("gui.thermal_shock_reforged.conc_info_self.label_i_havent_had_much_trouble_sin", 9, 155, -1), new Line("gui.thermal_shock_reforged.conc_info_self.label_i_think_ill_stay_a_little_longe", 9, 166, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("concrubiconidleportrait", -32, -20, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    VILEBATH("conc_info_vilebath", "ConcInfoVilebath", 276, 182, null,
            List.of(new Line("gui.thermal_shock_reforged.conc_info_vilebath.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.conc_info_vilebath.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.conc_info_vilebath.label_especially_if_you_have_xp", 9, 123, -1), new Line("gui.thermal_shock_reforged.conc_info_vilebath.label_now_i_remain_here_i_dare_not", 9, 134, -1), new Line("gui.thermal_shock_reforged.conc_info_vilebath.label_they_wanted_independence_but", 9, 145, -1), new Line("gui.thermal_shock_reforged.conc_info_vilebath.label_gardeners_rarely_saw_eye_to_eye", 9, 155, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("concrubiconidleportrait", -32, -20, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    HEAVY("conc_trade_heavy_round", "ConcTradeHeavyRound", 206, 196, new ConciergeTrade("heavy_round", 4, 1, 4),
            List.of(new Line("gui.thermal_shock_reforged.conc_trade_heavy_round.label_xx", 49, 52, -4124383), new Line("gui.thermal_shock_reforged.conc_trade_heavy_round.label_xx1", 69, 52, -15427946), new Line("gui.thermal_shock_reforged.conc_trade_heavy_round.label_xx2", 89, 52, -6218527), new Line("gui.thermal_shock_reforged.conc_trade_heavy_round.label_xxx", 49, 72, -5577638), new Line("gui.thermal_shock_reforged.conc_trade_heavy_round.label_lorem_ipsum", 68, 29, -1)),
            List.of(new Image("conciergetrademenu", -20, 0, 246, 180, 246, 180, false), new Image("heavy_round", 46, 25, 16, 16, 16, 16, false)),
            List.of(new Button(0, 84, 73, 38, 20, "tradebuttona"), new Button(1, 167, 50, 14, 21, "adamantine_spice"), new Button(2, 25, 50, 14, 21, "tabcycleleft"))),
    LARGE_LILY("conc_trade_large_spider_lily", "ConcTradeLargeSpiderLily", 206, 196, new ConciergeTrade("large_spider_lily", 1, 0, 4),
            List.of(new Line("gui.thermal_shock_reforged.conc_trade_large_spider_lily.label_xx", 49, 52, -4124383), new Line("gui.thermal_shock_reforged.conc_trade_large_spider_lily.label_xx1", 69, 52, -15427946), new Line("gui.thermal_shock_reforged.conc_trade_large_spider_lily.label_xx2", 89, 52, -6218527), new Line("gui.thermal_shock_reforged.conc_trade_large_spider_lily.label_xxx", 49, 72, -5577638), new Line("gui.thermal_shock_reforged.conc_trade_large_spider_lily.label_lorem_ipsum", 68, 29, -1)),
            List.of(new Image("conciergetrademenu", -20, 0, 246, 180, 246, 180, false), new Image("large_spider_lily_item", 45, 24, 20, 20, 20, 20, false)),
            List.of(new Button(0, 84, 73, 38, 20, "tradebuttona"), new Button(1, 167, 50, 14, 21, "adamantine_spice"), new Button(2, 25, 50, 14, 21, "tabcycleleft"))),
    LIGHT("conc_trade_light_round", "ConcTradeLightRound", 206, 196, new ConciergeTrade("light_round", 4, 1, 3),
            List.of(new Line("gui.thermal_shock_reforged.conc_trade_light_round.label_xx", 49, 52, -4124383), new Line("gui.thermal_shock_reforged.conc_trade_light_round.label_xx1", 69, 52, -15427946), new Line("gui.thermal_shock_reforged.conc_trade_light_round.label_xx2", 89, 52, -6218527), new Line("gui.thermal_shock_reforged.conc_trade_light_round.label_xxx", 49, 72, -5577638), new Line("gui.thermal_shock_reforged.conc_trade_light_round.label_lorem_ipsum", 68, 29, -1)),
            List.of(new Image("conciergetrademenu", -20, 0, 246, 180, 246, 180, false), new Image("light_round", 46, 25, 16, 16, 16, 16, false)),
            List.of(new Button(0, 84, 73, 38, 20, "tradebuttona"), new Button(1, 167, 50, 14, 21, "adamantine_spice"), new Button(2, 25, 50, 14, 21, "tabcycleleft"))),
    PANACEA("conc_trade_panacea", "ConcTradePanacea", 206, 196, new ConciergeTrade("panacea", 1, 8, 12),
            List.of(new Line("gui.thermal_shock_reforged.conc_trade_panacea.label_xx", 49, 52, -4124383), new Line("gui.thermal_shock_reforged.conc_trade_panacea.label_xx1", 69, 52, -15427946), new Line("gui.thermal_shock_reforged.conc_trade_panacea.label_xx2", 89, 52, -6218527), new Line("gui.thermal_shock_reforged.conc_trade_panacea.label_xxx", 49, 72, -5577638), new Line("gui.thermal_shock_reforged.conc_trade_panacea.label_lorem_ipsum", 68, 29, -1)),
            List.of(new Image("conciergetrademenu", -20, 0, 246, 180, 246, 180, false), new Image("panacea", 47, 26, 16, 16, 16, 16, false)),
            List.of(new Button(0, 84, 73, 38, 20, "tradebuttona"), new Button(1, 167, 50, 14, 21, "adamantine_spice"), new Button(2, 25, 50, 14, 21, "tabcycleleft"))),
    SPECIAL("conc_trade_spec_round", "ConcTradeSpecRound", 206, 196, new ConciergeTrade("specialized_round", 4, 3, 3),
            List.of(new Line("gui.thermal_shock_reforged.conc_trade_spec_round.label_xx", 49, 52, -4124383), new Line("gui.thermal_shock_reforged.conc_trade_spec_round.label_xx1", 69, 52, -15427946), new Line("gui.thermal_shock_reforged.conc_trade_spec_round.label_xx2", 89, 52, -6218527), new Line("gui.thermal_shock_reforged.conc_trade_spec_round.label_xxx", 49, 72, -5577638), new Line("gui.thermal_shock_reforged.conc_trade_spec_round.label_lorem_ipsum", 68, 29, -1)),
            List.of(new Image("conciergetrademenuextend", -20, 0, 246, 180, 246, 180, false), new Image("magnetic_round", 46, 25, 16, 16, 16, 16, false)),
            List.of(new Button(0, 84, 73, 38, 20, "tradebuttona"), new Button(1, 167, 50, 14, 21, "adamantine_spice"), new Button(2, 25, 50, 14, 21, "tabcycleleft"))),
    LILIES("conc_trade_spider_lilies", "ConcTradeSpiderLilies", 206, 196, new ConciergeTrade("spider_lilies", 8, 0, 2),
            List.of(new Line("gui.thermal_shock_reforged.conc_trade_spider_lilies.label_xx", 49, 52, -4124383), new Line("gui.thermal_shock_reforged.conc_trade_spider_lilies.label_xx1", 69, 52, -15427946), new Line("gui.thermal_shock_reforged.conc_trade_spider_lilies.label_xx2", 89, 52, -6218527), new Line("gui.thermal_shock_reforged.conc_trade_spider_lilies.label_xxx", 49, 72, -5577638), new Line("gui.thermal_shock_reforged.conc_trade_spider_lilies.label_lorem_ipsum", 68, 29, -1)),
            List.of(new Image("conciergetrademenu", -20, 0, 246, 180, 246, 180, false), new Image("spider_lily_item", 45, 24, 20, 20, 20, 20, false)),
            List.of(new Button(0, 84, 73, 38, 20, "tradebuttona"), new Button(1, 167, 50, 14, 21, "adamantine_spice"), new Button(2, 25, 50, 14, 21, "tabcycleleft"))),
    WIDE("conc_trade_wide_angle", "ConcTradeWideAngle", 206, 196, new ConciergeTrade("wide_angle", 1, 5, 5),
            List.of(new Line("gui.thermal_shock_reforged.conc_trade_wide_angle.label_xx", 49, 52, -4124383), new Line("gui.thermal_shock_reforged.conc_trade_wide_angle.label_xx1", 69, 52, -15427946), new Line("gui.thermal_shock_reforged.conc_trade_wide_angle.label_xx2", 89, 52, -6218527), new Line("gui.thermal_shock_reforged.conc_trade_wide_angle.label_xxx", 49, 72, -5577638), new Line("gui.thermal_shock_reforged.conc_trade_wide_angle.label_lorem_ipsum", 68, 29, -1)),
            List.of(new Image("conciergetrademenu", -20, 0, 246, 180, 246, 180, false), new Image("wide_angle", 47, 26, 16, 16, 16, 16, false)),
            List.of(new Button(0, 84, 73, 38, 20, "tradebuttona"), new Button(1, 167, 50, 14, 21, "adamantine_spice"), new Button(2, 25, 50, 14, 21, "tabcycleleft")));

    public final String id;
    public final String title;
    public final int width;
    public final int height;
    public final ConciergeTrade trade;
    public final List<Line> lines;
    public final List<Image> images;
    public final List<Button> buttons;
    ConciergePage(String id, String title, int width, int height, ConciergeTrade trade, List<Line> lines, List<Image> images, List<Button> buttons) {
        this.id = id; this.title = title; this.width = width; this.height = height; this.trade = trade; this.lines = lines; this.images = images; this.buttons = buttons;
    }
    public record Line(String key, int x, int y, int color) { }
    public record Image(String texture, int x, int y, int width, int height, int textureWidth, int textureHeight, boolean animated) { }
    public record Button(int action, int x, int y, int width, int height, String texture) { }
}
