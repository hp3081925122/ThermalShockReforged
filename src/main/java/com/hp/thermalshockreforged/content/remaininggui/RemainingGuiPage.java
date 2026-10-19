package com.hp.thermalshockreforged.content.remaininggui;

import java.util.List;

public enum RemainingGuiPage {
    CLASH_PAGE_1("clash_page_1", 176, 166, List.of(new Image("kgcpage", 0, 0, 176, 166)), List.of(
            new Line("label_no_signal", 9, 43, -16777216),
            new Line("label_take_a_look", 9, 50, -16777216),
            new Line("label_execute_kgcfunc_33", 9, 57, -16764007),
            new Line("label_func33_report_103txt", 9, 64, -6710887),
            new Line("label_they_will_be_arriving_shortly", 9, 71, -16777216),
            new Line("label_shortly", 9, 78, -16777216),
            new Line("label_how_will_i_find_them", 9, 85, -16764007),
            new Line("label_they_will_come_to_you", 9, 92, -16777216),
            new Line("label_they_are_ready_they_will_find", 9, 99, -16777216),
            new Line("label_find_their_way", 9, 106, -16777216),
            new Line("label_goodbye", 9, 113, -16777216),
            new Line("label_wait", 9, 120, -10653287),
            new Line("label_message_not_sent_try_again", 9, 127, -6710887),
            new Line("label_try_again_yn", 9, 134, -6710887)), List.of()),
    CLASH_NO_SIGNAL("clash_no_signal", 176, 166, List.of(new Image("cbt_nosignal", 0, 0, 176, 166)),
            List.of(new Line("label_no_signal", 63, 109, -5920281)), List.of()),
    CHAR_TRADE_TEMPLATE("char_trade_template", 206, 196, List.of(new Image("conciergetrademenu", -20, 0, 246, 180), new Image("heavy_round", 47, 26, 16, 16)), List.of(
            new Line("label_xx", 49, 52, -4124383), new Line("label_xx1", 69, 52, -15427946),
            new Line("label_xx2", 89, 52, -6218527), new Line("label_xxx", 49, 72, -5577638),
            new Line("label_lorem_ipsum", 68, 29, -1)), List.of(
            new Button("tradebuttona", 84, 73, 38, 20, 0),
            new Button("adamantine_spice", 167, 50, 14, 21, -1),
            new Button("tabcycleleft", 25, 50, 14, 21, -1))),
    CIVIL_8_TLK_2("civil_8_tlk_2", 276, 182, List.of(new Image("mainspeechbox", 0, 103, 276, 276), new Image("mainlabelbox", 0, -30, 134, 134)),
            List.of(new Line("label_kutty", 9, 112, -1), new Line("label_concierge", 5, 93, -1)), List.of()),
    LIMBO_VAULT_GUI("limbo_vault_gui", 276, 76, List.of(), List.of(new Line("label_limbo_vault", 110, 4, -1)),
            List.of(new Button("textarrow", 201, 31, 18, 18, 0)));

    public record Image(String texture, int x, int y, int width, int height) { }
    public record Line(String suffix, int x, int y, int color) { }
    public record Button(String texture, int x, int y, int width, int height, int action) { }
    public final String id;
    public final int width;
    public final int height;
    public final List<Image> images;
    public final List<Line> lines;
    public final List<Button> buttons;

    RemainingGuiPage(String id, int width, int height, List<Image> images, List<Line> lines, List<Button> buttons) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.images = images;
        this.lines = lines;
        this.buttons = buttons;
    }

    public String translation(String suffix) { return "gui.thermal_shock_reforged." + id + "." + suffix; }
}
