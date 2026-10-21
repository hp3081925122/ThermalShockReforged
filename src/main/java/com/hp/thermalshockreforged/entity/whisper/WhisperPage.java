package com.hp.thermalshockreforged.entity.whisper;

import java.util.List;

public enum WhisperPage {
    MAIN("whisper_main", "WhisperMain", 276, 182, false,
            List.of(new Line("gui.thermal_shock_reforged.whisper_main.label_concierge", 67, 93, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, -1), new Image("mainflavourboxes", 62, -110, 214, 214, 214, 214, -1), new Image("whisperidleportrait", 0, -14, 128, 128, 128, 1536, 1408)),
            List.of(new Button(0, 203, -6, 65, 22, "maininfobuttona", "INFO"), new Button(1, 203, 18, 65, 22, "tradebuttona1", "MALACHITE"))),
    INFO("whisper_info", "WhisperInfo", 212, 116, false,
            List.of(),
            List.of(new Image("whisperinfobox", -1, 0, 212, 116, 212, 116, -1)),
            List.of(new Button(0, 14, 32, 59, 20, "infowhisper", "SELF"), new Button(1, 76, 32, 59, 20, "infomalachite", "MALACHITE_INFO"))),
    SELF("whisper_self_info", "WhisperSelfInfo", 276, 182, false,
            List.of(new Line("gui.thermal_shock_reforged.whisper_self_info.label_kutty", 9, 112, -1, 10), new Line("gui.thermal_shock_reforged.whisper_self_info.label_concierge", 67, 93, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, -1), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, -1), new Image("whisperidleportrait", 0, -14, 128, 128, 128, 1536, 1408)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona", "INFO"))),
    MALACHITE_INFO("whisper_malachite_info", "WhisperMalachiteInfo", 276, 182, false,
            List.of(new Line("gui.thermal_shock_reforged.whisper_malachite_info.label_kutty", 9, 112, -1, 10), new Line("gui.thermal_shock_reforged.whisper_malachite_info.label_concierge", 67, 93, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, -1), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, -1), new Image("whisperidleportrait", 0, -14, 128, 128, 128, 1536, 1408)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona", "INFO"))),
    MALACHITE("whisper_trade_malachite", "WhisperTradeMalachite", 206, 196, true,
            List.of(new Line("gui.thermal_shock_reforged.whisper_trade_malachite.label_xx", 49, 51, -4124383), new Line("gui.thermal_shock_reforged.whisper_trade_malachite.label_xx1", 69, 51, -15427946), new Line("gui.thermal_shock_reforged.whisper_trade_malachite.label_xx2", 89, 51, -6218527), new Line("gui.thermal_shock_reforged.whisper_trade_malachite.label_xxx", 49, 71, -5577638), new Line("gui.thermal_shock_reforged.whisper_trade_malachite.label_lorem_ipsum", 68, 29, -1)),
            List.of(new Image("whispertrademenu", -20, 0, 246, 180, 246, 180, -1), new Image("malachite", 47, 26, 16, 16, 16, 16, -1)),
            List.of(new Button(0, 84, 73, 38, 20, "tradebuttona", ""), new Button(1, 167, 50, 14, 21, "adamantine_spice", "BISMUTH"), new Button(2, 25, 50, 14, 21, "tabcycleleft", "ZENITH"))),
    BISMUTH("whisper_trade_bismuth", "WhisperTradeBismuth", 206, 196, true,
            List.of(new Line("gui.thermal_shock_reforged.whisper_trade_bismuth.label_xx", 49, 51, -4124383), new Line("gui.thermal_shock_reforged.whisper_trade_bismuth.label_xx1", 69, 51, -15427946), new Line("gui.thermal_shock_reforged.whisper_trade_bismuth.label_xx2", 89, 51, -6218527), new Line("gui.thermal_shock_reforged.whisper_trade_bismuth.label_xxx", 49, 71, -5577638), new Line("gui.thermal_shock_reforged.whisper_trade_bismuth.label_lorem_ipsum", 68, 29, -1)),
            List.of(new Image("whispertrademenu", -20, 0, 246, 180, 246, 180, -1), new Image("bismuth_25", 47, 26, 16, 16, 16, 16, -1)),
            List.of(new Button(0, 84, 73, 38, 20, "tradebuttona", ""), new Button(1, 167, 50, 14, 21, "adamantine_spice", "STARFRUIT"), new Button(2, 25, 50, 14, 21, "tabcycleleft", "MALACHITE"))),
    STARFRUIT("whisper_trade_starfruit_seed", "WhisperTradeStarfruitSeed", 206, 196, true,
            List.of(new Line("gui.thermal_shock_reforged.whisper_trade_starfruit_seed.label_xx", 49, 51, -4124383), new Line("gui.thermal_shock_reforged.whisper_trade_starfruit_seed.label_xx1", 69, 51, -15427946), new Line("gui.thermal_shock_reforged.whisper_trade_starfruit_seed.label_xx2", 89, 51, -6218527), new Line("gui.thermal_shock_reforged.whisper_trade_starfruit_seed.label_xxx", 49, 71, -5577638), new Line("gui.thermal_shock_reforged.whisper_trade_starfruit_seed.label_lorem_ipsum", 68, 29, -1)),
            List.of(new Image("whispertrademenu", -20, 0, 246, 180, 246, 180, -1), new Image("starfruit_seed", 47, 26, 16, 16, 16, 16, -1)),
            List.of(new Button(0, 84, 73, 38, 20, "tradebuttona", ""), new Button(1, 167, 50, 14, 21, "adamantine_spice", "LUNALEMON"), new Button(2, 25, 50, 14, 21, "tabcycleleft", "BISMUTH"))),
    LUNALEMON("whisper_trade_lunalemon_seed", "WhisperTradeLunalemonSeed", 206, 196, true,
            List.of(new Line("gui.thermal_shock_reforged.whisper_trade_lunalemon_seed.label_xx", 49, 51, -4124383), new Line("gui.thermal_shock_reforged.whisper_trade_lunalemon_seed.label_xx1", 69, 51, -15427946), new Line("gui.thermal_shock_reforged.whisper_trade_lunalemon_seed.label_xx2", 89, 51, -6218527), new Line("gui.thermal_shock_reforged.whisper_trade_lunalemon_seed.label_xxx", 49, 71, -5577638), new Line("gui.thermal_shock_reforged.whisper_trade_lunalemon_seed.label_lorem_ipsum", 68, 29, -1)),
            List.of(new Image("whispertrademenu", -20, 0, 246, 180, 246, 180, -1), new Image("lunalemon_seed", 47, 26, 16, 16, 16, 16, -1)),
            List.of(new Button(0, 84, 73, 38, 20, "tradebuttona", ""), new Button(1, 167, 50, 14, 21, "adamantine_spice", "COCOA"), new Button(2, 25, 50, 14, 21, "tabcycleleft", "STARFRUIT"))),
    COCOA("whisper_trade_cocoa", "WhisperTradeCocoa", 206, 196, true,
            List.of(new Line("gui.thermal_shock_reforged.whisper_trade_cocoa.label_xx", 49, 51, -4124383), new Line("gui.thermal_shock_reforged.whisper_trade_cocoa.label_xx1", 69, 51, -15427946), new Line("gui.thermal_shock_reforged.whisper_trade_cocoa.label_xx2", 89, 51, -6218527), new Line("gui.thermal_shock_reforged.whisper_trade_cocoa.label_xxx", 49, 71, -5577638), new Line("gui.thermal_shock_reforged.whisper_trade_cocoa.label_lorem_ipsum", 68, 29, -1)),
            List.of(new Image("whispertrademenu", -20, 0, 246, 180, 246, 180, -1), new Image("cocoa", 47, 26, 16, 16, 16, 16, -1)),
            List.of(new Button(0, 84, 73, 38, 20, "tradebuttona", ""), new Button(1, 167, 50, 14, 21, "adamantine_spice", "PIXIE"), new Button(2, 25, 50, 14, 21, "tabcycleleft", "LUNALEMON"))),
    PIXIE("whisper_trade_pixie_bottle", "WhisperTradePixieBottle", 206, 196, true,
            List.of(new Line("gui.thermal_shock_reforged.whisper_trade_pixie_bottle.label_xx", 49, 51, -4124383), new Line("gui.thermal_shock_reforged.whisper_trade_pixie_bottle.label_xx1", 69, 51, -15427946), new Line("gui.thermal_shock_reforged.whisper_trade_pixie_bottle.label_xx2", 89, 51, -6218527), new Line("gui.thermal_shock_reforged.whisper_trade_pixie_bottle.label_xxx", 49, 71, -5577638), new Line("gui.thermal_shock_reforged.whisper_trade_pixie_bottle.label_lorem_ipsum", 68, 29, -1)),
            List.of(new Image("whispertrademenu", -20, 0, 246, 180, 246, 180, -1), new Image("bottled_pixie", 47, 26, 16, 16, 16, 16, -1)),
            List.of(new Button(0, 84, 73, 38, 20, "tradebuttona", ""), new Button(1, 167, 50, 14, 21, "adamantine_spice", "ZENITH"), new Button(2, 25, 50, 14, 21, "tabcycleleft", "COCOA"))),
    ZENITH("whisper_trade_zenith_template", "WhisperTradeZenithTemplate", 206, 196, true,
            List.of(new Line("gui.thermal_shock_reforged.whisper_trade_zenith_template.label_xx", 49, 51, -4124383), new Line("gui.thermal_shock_reforged.whisper_trade_zenith_template.label_xx1", 69, 51, -15427946), new Line("gui.thermal_shock_reforged.whisper_trade_zenith_template.label_xx2", 89, 51, -6218527), new Line("gui.thermal_shock_reforged.whisper_trade_zenith_template.label_xxx", 49, 71, -5577638), new Line("gui.thermal_shock_reforged.whisper_trade_zenith_template.label_lorem_ipsum", 68, 29, -1)),
            List.of(new Image("whispertrademenu", -20, 0, 246, 180, 246, 180, -1), new Image("zenith_smithing_template", 47, 26, 16, 16, 16, 16, -1)),
            List.of(new Button(0, 84, 73, 38, 20, "tradebuttona", ""), new Button(1, 167, 50, 14, 21, "adamantine_spice", "DISC"), new Button(2, 25, 50, 14, 21, "tabcycleleft", "PIXIE"))),
    DISC("whisper_trade_disc_floor_33", "WhisperTradeDiscFloor33", 206, 196, true,
            List.of(new Line("gui.thermal_shock_reforged.whisper_trade_disc_floor_33.label_xx", 49, 51, -4124383), new Line("gui.thermal_shock_reforged.whisper_trade_disc_floor_33.label_xx1", 69, 51, -15427946), new Line("gui.thermal_shock_reforged.whisper_trade_disc_floor_33.label_xx2", 89, 51, -6218527), new Line("gui.thermal_shock_reforged.whisper_trade_disc_floor_33.label_xxx", 49, 71, -5577638), new Line("gui.thermal_shock_reforged.whisper_trade_disc_floor_33.label_lorem_ipsum", 68, 29, -1)),
            List.of(new Image("whispertrademenuextended", -20, 0, 246, 180, 246, 180, -1), new Image("floor_33", 47, 26, 16, 16, 16, 16, -1)),
            List.of(new Button(0, 84, 73, 38, 20, "tradebuttona", ""), new Button(1, 167, 50, 14, 21, "adamantine_spice", "MALACHITE"), new Button(2, 25, 50, 14, 21, "tabcycleleft", "ZENITH")));
    public final String id, title;
    public final int width, height;
    public final boolean inventory;
    public final List<Line> lines;
    public final List<Image> images;
    public final List<Button> buttons;
    WhisperPage(String id, String title, int width, int height, boolean inventory, List<Line> lines, List<Image> images, List<Button> buttons) {
        this.id = id; this.title = title; this.width = width; this.height = height; this.inventory = inventory; this.lines = lines; this.images = images; this.buttons = buttons;
    }
    public record Line(String key, int x, int y, int color, int spacing) { public Line(String key, int x, int y, int color) { this(key, x, y, color, 0); } }
    public record Image(String texture, int x, int y, int width, int height, int textureWidth, int textureHeight, int frameMax) { }
    public record Button(int action, int x, int y, int width, int height, String texture, String route) { }
}
