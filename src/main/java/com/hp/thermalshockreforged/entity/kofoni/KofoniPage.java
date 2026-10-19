package com.hp.thermalshockreforged.entity.kofoni;

import java.util.List;

public enum KofoniPage {
    BENZOR("kofoni_benzor_info", "KofoniBenzorInfo", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.kofoni_benzor_info.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.kofoni_benzor_info.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.kofoni_benzor_info.label_old_relics_n_stuff", 9, 123, -1), new Line("gui.thermal_shock_reforged.kofoni_benzor_info.label_but_you_know_everything_went", 9, 134, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("kofoniidleportraitalt", -32, -15, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    BLIZZARD("kofoni_blizzard_info", "KofoniBlizzardInfo", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.kofoni_blizzard_info.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.kofoni_blizzard_info.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.kofoni_blizzard_info.label_old_relics_n_stuff", 9, 123, -1), new Line("gui.thermal_shock_reforged.kofoni_blizzard_info.label_but_you_know_everything_went", 9, 134, -1), new Line("gui.thermal_shock_reforged.kofoni_blizzard_info.label_house_my_collection", 9, 145, -1), new Line("gui.thermal_shock_reforged.kofoni_blizzard_info.label_big_ones_too_felt_like_an_ea", 9, 156, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("kofoniidleportraitalt", -32, -15, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    INFO("kofoni_info", "KofoniInfo", 212, 116,
            List.of(),
            List.of(new Image("kofoniinfobox", -1, 0, 212, 116, 212, 116, false)),
            List.of(new Button(0, 14, 32, 59, 20, "infokofoni"), new Button(1, 76, 32, 59, 20, "infoqo"), new Button(2, 76, 78, 59, 20, "infoblizzard"), new Button(3, 138, 55, 59, 20, "infomalachite"), new Button(4, 14, 78, 59, 20, "infopylons"), new Button(5, 138, 32, 59, 20, "infobenzor"), new Button(6, 14, 55, 59, 20, "infoshalespring"), new Button(7, 76, 55, 59, 20, "inforuby"))),
    MAIN("kofoni_main", "KofoniMain", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.kofoni_main.label_concierge", 67, 93, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainflavourboxes", 62, -110, 214, 214, 214, 214, false), new Image("kofoniidleportraitalt", -32, -15, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 203, -6, 65, 22, "maininfobuttona"), new Button(1, 203, 18, 65, 22, "maintabletsbuttona"))),
    MALACHITE("kofoni_malachite_info", "KofoniMalachiteInfo", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.kofoni_malachite_info.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.kofoni_malachite_info.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.kofoni_malachite_info.label_old_relics_n_stuff", 9, 123, -1), new Line("gui.thermal_shock_reforged.kofoni_malachite_info.label_but_you_know_everything_went", 9, 134, -1), new Line("gui.thermal_shock_reforged.kofoni_malachite_info.label_maybe_you_should_ask_whisper", 9, 145, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("kofoniidleportraitalt", -32, -15, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    PYLONS("kofoni_pylons_info", "KofoniPylonsInfo", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.kofoni_pylons_info.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.kofoni_pylons_info.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.kofoni_pylons_info.label_old_relics_n_stuff", 9, 123, -1), new Line("gui.thermal_shock_reforged.kofoni_pylons_info.label_but_you_know_everything_went", 9, 134, -1), new Line("gui.thermal_shock_reforged.kofoni_pylons_info.label_supposedly_theyre_like_supe", 9, 145, -1), new Line("gui.thermal_shock_reforged.kofoni_pylons_info.label_snow_wiped_those_out", 9, 156, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("kofoniidleportraitalt", -32, -15, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    QO("kofoni_qo_info", "KofoniQoInfo", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.kofoni_qo_info.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.kofoni_qo_info.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.kofoni_qo_info.label_old_relics_n_stuff", 9, 123, -1), new Line("gui.thermal_shock_reforged.kofoni_qo_info.label_but_you_know_everything_went", 9, 134, -1), new Line("gui.thermal_shock_reforged.kofoni_qo_info.label_house_my_collection", 9, 145, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("kofoniidleportraitalt", -32, -15, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    RUBY("kofoni_ruby_info", "KofoniRubyInfo", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.kofoni_ruby_info.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.kofoni_ruby_info.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.kofoni_ruby_info.label_old_relics_n_stuff", 9, 123, -1), new Line("gui.thermal_shock_reforged.kofoni_ruby_info.label_but_you_know_everything_went", 9, 134, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("kofoniidleportraitalt", -32, -15, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    SELF("kofoni_self_info", "KofoniSelfInfo", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.kofoni_self_info.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.kofoni_self_info.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.kofoni_self_info.label_old_relics_n_stuff", 9, 123, -1), new Line("gui.thermal_shock_reforged.kofoni_self_info.label_but_you_know_everything_went", 9, 134, -1), new Line("gui.thermal_shock_reforged.kofoni_self_info.label_house_my_collection", 9, 145, -1), new Line("gui.thermal_shock_reforged.kofoni_self_info.label_eh_who_cares_i_have_a_new_co", 9, 156, -1), new Line("gui.thermal_shock_reforged.kofoni_self_info.label_digging_up_muck_and_here_i_am_n", 9, 167, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("kofoniidleportraitalt", -32, -15, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    SHALESPRING("kofoni_shalespring_info", "KofoniShalespringInfo", 276, 182,
            List.of(new Line("gui.thermal_shock_reforged.kofoni_shalespring_info.label_kutty", 9, 112, -1), new Line("gui.thermal_shock_reforged.kofoni_shalespring_info.label_concierge", 67, 93, -1), new Line("gui.thermal_shock_reforged.kofoni_shalespring_info.label_old_relics_n_stuff", 9, 123, -1), new Line("gui.thermal_shock_reforged.kofoni_shalespring_info.label_but_you_know_everything_went", 9, 134, -1)),
            List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276, false), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134, false), new Image("kofoniidleportraitalt", -32, -15, 128, 128, 128, 1536, true)),
            List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    TABLETS("kofoni_tablets", "KofoniTablets", 206, 196,
            List.of(new Line("gui.thermal_shock_reforged.kofoni_tablets.label_diskettes", 81, 25, -1)),
            List.of(new Image("kofonittradebox", -20, 0, 246, 180, 246, 180, false)),
            List.of(new Button(0, 84, 60, 38, 20, "tradebuttona")));

    public final String id;
    public final String title;
    public final int width;
    public final int height;
    public final List<Line> lines;
    public final List<Image> images;
    public final List<Button> buttons;
    KofoniPage(String id, String title, int width, int height, List<Line> lines, List<Image> images, List<Button> buttons) {
        this.id = id; this.title = title; this.width = width; this.height = height; this.lines = lines; this.images = images; this.buttons = buttons;
    }
    public record Line(String key, int x, int y, int color) { }
    public record Image(String texture, int x, int y, int width, int height, int textureWidth, int textureHeight, boolean animated) { }
    public record Button(int action, int x, int y, int width, int height, String texture) { }
}
