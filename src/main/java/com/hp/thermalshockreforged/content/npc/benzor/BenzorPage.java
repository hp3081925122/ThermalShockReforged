package com.hp.thermalshockreforged.content.npc.benzor;

import java.util.List;

public enum BenzorPage {
    BLACKPOWDER("benzor_blackpowder_info", "BenzorBlackpowderInfo", 276, 182, List.of(new Line("gui.thermal_shock_reforged.benzor_blackpowder_info.label_kutty", 9, 112, -1, 11), new Line("gui.thermal_shock_reforged.benzor_blackpowder_info.label_concierge", 67, 93, -1)), List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134), new Image("benzoridlerubiconstatic", 65, -39, 128, 128, 128, 128)), List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    BLIZZARD("benzor_blizzard_info", "BenzorBlizzardInfo", 276, 182, List.of(new Line("gui.thermal_shock_reforged.benzor_blizzard_info.label_kutty", 9, 112, -1, 11), new Line("gui.thermal_shock_reforged.benzor_blizzard_info.label_concierge", 67, 93, -1)), List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134), new Image("benzoridlerubiconstatic", 65, -39, 128, 128, 128, 128)), List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    INFECTION("benzor_infection_info", "BenzorInfectionInfo", 276, 182, List.of(new Line("gui.thermal_shock_reforged.benzor_infection_info.label_kutty", 9, 112, -1, 11), new Line("gui.thermal_shock_reforged.benzor_infection_info.label_concierge", 67, 93, -1)), List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134), new Image("benzoridlerubiconstatic", 65, -39, 128, 128, 128, 128)), List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    INFO("benzor_info", "BenzorInfo", 212, 116, List.of(), List.of(new Image("benzorinfobox", -1, 0, 212, 116, 212, 116)), List.of(new Button(0, 14, 32, 59, 20, "infobenzor"), new Button(1, 76, 32, 59, 20, "infospice"), new Button(2, 138, 32, 59, 20, "infoinfection"), new Button(3, 14, 55, 59, 20, "infoblizzard"), new Button(4, 76, 55, 59, 20, "infoblackpowder"))),
    MAIN("benzor_main", "BenzorMain", 276, 182, List.of(new Line("gui.thermal_shock_reforged.benzor_main.label_concierge", 67, 93, -1)), List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276), new Image("mainflavourboxes", 62, -110, 214, 214, 214, 214), new Image("benzoridlerubiconstatic", 65, -39, 128, 128, 128, 128)), List.of(new Button(0, 203, -6, 65, 22, "maininfobuttona"), new Button(1, 203, 17, 65, 22, "maintradebuttona"))),
    SELF("benzor_self_info", "BenzorSelfInfo", 276, 182, List.of(new Line("gui.thermal_shock_reforged.benzor_self_info.label_kutty", 9, 112, -1, 11), new Line("gui.thermal_shock_reforged.benzor_self_info.label_concierge", 67, 93, -1)), List.of(new Image("mainspeechbox", 2, 103, 276, 276, 276, 276), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134), new Image("benzoridlerubiconstatic", 65, -39, 128, 128, 128, 128)), List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    SPICE_INTRO("benzor_spice_epl", "BenzorSpiceEpl", 276, 182, List.of(new Line("gui.thermal_shock_reforged.benzor_spice_epl.label_kutty", 9, 112, -1, 11), new Line("gui.thermal_shock_reforged.benzor_spice_epl.label_concierge", 67, 93, -1)), List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134), new Image("benzoridlerubiconstatic", 65, -39, 128, 128, 128, 128)), List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    SPICE_INFO("benzor_spice_info", "BenzorSpiceInfo", 276, 182, List.of(new Line("gui.thermal_shock_reforged.benzor_spice_info.label_kutty", 9, 112, -1, 11), new Line("gui.thermal_shock_reforged.benzor_spice_info.label_concierge", 67, 93, -1)), List.of(new Image("mainspeechbox", 0, 103, 276, 276, 276, 276), new Image("mainlabelbox", 62, -30, 134, 134, 134, 134), new Image("benzoridlerubiconstatic", 65, -39, 128, 128, 128, 128)), List.of(new Button(0, 237, 161, 38, 20, "nextbuttona"))),
    SPICE("benzor_spice", "BenzorSpice", 206, 196, List.of(), List.of(new Image("benzorspicemenu", -20, 0, 246, 180, 246, 180)), List.of(new Button(0, 84, 73, 38, 20, "tradebuttona")));
    public final String id;
    public final String title;
    public final int width;
    public final int height;
    public final List<Line> lines;
    public final List<Image> images;
    public final List<Button> buttons;
    BenzorPage(String id, String title, int width, int height, List<Line> lines, List<Image> images, List<Button> buttons) { this.id = id; this.title = title; this.width = width; this.height = height; this.lines = lines; this.images = images; this.buttons = buttons; }
    public record Line(String key, int x, int y, int color, int spacing) { public Line(String key, int x, int y, int color) { this(key, x, y, color, 0); } }
    public record Image(String texture, int x, int y, int width, int height, int textureWidth, int textureHeight) { }
    public record Button(int action, int x, int y, int width, int height, String texture) { }
}
