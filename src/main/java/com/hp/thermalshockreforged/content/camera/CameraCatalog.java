package com.hp.thermalshockreforged.content.camera;

import com.hp.thermalshockreforged.ThermalShockReforged;
import java.util.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

final class CameraCatalog {
    private static final List<Entry> ENTRIES = List.of(
        new Entry(Set.of(id("maiden")), "§a\"Maiden.\" §rHaunted device of torture\nType: Minor Machination\n§aWeak to Cloro", false),
        new Entry(Set.of(id("probosker")), "§a\"Probosker.\" §rCuddly forest critter with a taste for fish\nType: Minor Bug", false),
        new Entry(Set.of(id("snobol")), "§a\"Snobol.\" §rWell acclimated bipedal insect\nType: Minor bug", false),
        new Entry(Set.of(id("explosive_snobol")), "§a\"Explosive Snobol.\" §rHOW COULD YOU!!!!!!!!!!!!!!!\nType: Minor bug", false),
        new Entry(Set.of(id("razorwing"), id("ruby_armoured_razorwing"), id("blood_armoured_razorwing")), "§a\"Razorwing,\" §rTall flightless bug. An ideal mount for wayward travellers\nType: Minor Bug", false),
        new Entry(Set.of(id("trilobite")), "§a\"Trilobite.\" §rWingless gliding bug with a tendency to sleep in pots and cauldrons\nType: Minor Bug", false),
        new Entry(Set.of(id("sentrarius")), "§a\"Sentrapede.\" §rDocile horned insect. Scavenges for bones\nType: Minor Bug", false),
        new Entry(Set.of(id("willograde")), "§a\"Roly Poly.\" §rHeavy defensive bug. Loves spice and digging\nType: Minor Bug", false),
        new Entry(Set.of(id("cerulean_jellercel"), id("fuschia_jellercel"), id("veridian_jellercel")), "§a\"Jellercel.\" §rToo many legs\nType: Minor Bug", false),
        new Entry(Set.of(id("aerocel")), "§a\"Aerocel.\" §rSmiles\nType: Medius Bug", false),
        new Entry(Set.of(id("chilloo")), "§a\"Chilloo.\" §rFreezing cold, but very cuddly\nType: Minor Bug", false),
        new Entry(Set.of(id("louse")), "§a\"Louse.\" §rMmm insects haughhghohgghggggh\nType: Minor Bug", false),
        new Entry(Set.of(id("glastrekker"), id("armoured_glastrekker")), "§a\"Glastrekker.\" §rBig Steppa\nType: Medius Fish", false),
        new Entry(Set.of(id("pixie")), "§a\"Pixie.\" §rLuminescent insect from Loqendia's lost asteroid belt\nType: Minor Bug", false),
        new Entry(Set.of(id("pillbud")), "§a\"Pillbud.\" §rPop opera-a-a!\nType: Minor Plant\n§cResistant to Cloro", false),
        new Entry(Set.of(id("gelshooter")), "§a\"Gelshooter.\" §rThe Gravers... are cominggggggg\nType: Minor Plant\n§cResistant to Cloro", false),
        new Entry(Set.of(id("tesla_reed")), "§a\"Tesla Reed.\" §rEnjoys the sound of a piano\nType: Minor Plant\n§cResistant to Cloro", false),
        new Entry(Set.of(id("gardenling")), "§a\"Gardenling.\" §rHelps ancient plants grow in the cold\nType: Minor Plant\n§cResistant to Cloro", false),
        new Entry(Set.of(id("crystal_koi")), "§a\"Crystal Koi.\" §rSparkly fish found all across the garden\nType: Minor Fish", false),
        new Entry(Set.of(id("chromefish")), "§a\"Chromefish.\" §rDon't you look at me with that big ol' eye!\nType: Minor Fish", false),
        new Entry(Set.of(id("siibol")), "§a\"Siibol.\" §rToo wet to be a bug, too dry to be a fish\nType: Minor Siibol", false),
        new Entry(Set.of(id("bonerahna")), "§a\"Bonerahna.\" §rRuthless carnivorous fish\nType: Minor Fish\n§aWeak to Ferro", false),
        new Entry(Set.of(id("scarlet_ray")), "§a\"Scarlet Ray.\" §rDocile bottom feeder\nType: Minor Fish", false),
        new Entry(Set.of(id("pondler")), "§a\"Pondler.\" §rBoing\nType: Minor Fish", false),
        new Entry(Set.of(id("torpedo")), "§a\"Torpedo.\" §rExplosive silver-scaled sea creature\nType: Minor Fish", false),
        new Entry(Set.of(id("springling")), "§a\"Springling.\" §rSpringelatin given life by Helion\nType: Minor Machination", false),
        new Entry(Set.of(id("sola")), "§a\"Sola.\" §rHot potato\nType: Minor Machination\n§aWeak to Cryo", false),
        new Entry(Set.of(id("magnetite")), "§a\"Magnetite.\" §rOrganic cluster of astral minerals\nType: Minor Alien\n§aWeak to Cryo", false),
        new Entry(Set.of(id("lens")), "§a\"Lens.\" §rAll eyes on me!\nType: Minor Plant\n§cResistant to Cloro\n§aWeak to Cryo", false),
        new Entry(Set.of(id("frostilyte")), "§a\"Frostilyte.\" §rLowest ranking moth of Projekt Blizzard\nType: Minor Bug\n§cResistant to Cryo\n§aWeak to Pyro", false),
        new Entry(Set.of(id("frostilyte_soldier")), "§a\"Frostilyte Soldier.\" §rClose quarters combat\nType: Minor Bug\n§cResistant to Cryo\n§aWeak to Pyro", false),
        new Entry(Set.of(id("frostilyte_gunner")), "§a\"Frostilyte Gunner.\" §rNerfed\nType: Minor Bug\n§cResistant to Cryo\n§aWeak to Pyro", false),
        new Entry(Set.of(id("frostilyte_cryomancer")), "§a\"Frostilyte Cryomancer.\" §rApprentice of Projekt Blizzard's Necrotic Division\nType: Minor Bug\n§cResistant to Cryo\n§aWeak to Pyro", false),
        new Entry(Set.of(id("kilolyte")), "§a\"Kilolyte.\" §rHeavy hitter, undefeated in company bowling tournaments\nType: Medius Bug\n§cResistant to Cryo\n§cResistant to Ferro\n§cResistant to Cloro", false),
        new Entry(Set.of(id("icidrone")), "§a\"Icidrone.\" §rWeak single-soul automation\nType: Minor Machination\n§aWeak to Pyro", false),
        new Entry(Set.of(id("icidrogrunt")), "§a\"Icidrogrunt.\" §rCheaply-spawned mindless creation\nType: Minor Machination\n§aWeak to Pyro", false),
        new Entry(Set.of(id("icidrozerk")), "§a\"Icidrozerk.\" §rWeaponized cage of soulsand\nType: Minor Machniation\n§cResistant to Cryo\n§aWeak to Pyro", false),
        new Entry(Set.of(id("protoi_gamma")), "§a\"Protoi Gamma.\" §rAncient terraforming technology\nType: Minor Machniation", false),
        new Entry(Set.of(id("protoi_delta")), "§a\"Protoi Delta.\" §rWandering defender\nType: Minor Machniation", false),
        new Entry(Set.of(id("protoi_sunbeam")), "§a\"Protoi Sunbeam.\" §rDubiously immovable, questionably unstoppable\nType: Medius Machniation", false),
        new Entry(Set.of(id("sootle")), "§a\"Sootle.\" §rThe bugs yearn for the mines\nType: Minor Bug\n§cResistant to Ferro\n§cResistant to Pyro", false),
        new Entry(Set.of(id("sootling")), "§a\"Sootling.\" §rDeadly in numbers\nType: Minor Bug\n§cResistant to Ferro\n§cResistant to Pyro", false),
        new Entry(Set.of(id("beetle_thrasher")), "§a\"Beetle Thrasher.\" §rBlood-worshipping dissenter\nType: Minor Bug\n§cResistant to Ferro\n§cResistant to Pyro", false),
        new Entry(Set.of(id("beetle_crasher")), "§a\"Beetle Crasher.\" §rEating Rocks\nType: Minor Bug\n§cResistant to Ferro\n§cResistant to Pyro", false),
        new Entry(Set.of(id("dormant_beetle_crasher")), "§a\"???.\" §rNothing to describe\nType: Minor Unknown\n§cResistances unknown\n§aWeaknesses unknown", false),
        new Entry(Set.of(id("galvanite")), "§a\"Galvanite.\" §rStolen schematics\nType: Minor Machniation\n§cResistant to Cryo\n§cResistant to Ferro\n§aWeak to Pyro", false),
        new Entry(Set.of(id("forecast")), "§a\"Forecast.\" §rCloudy with a chance of silver\nType: Minor Machniation\n§cResistant to Cryo\n§aWeak to Pyro", false),
        new Entry(Set.of(id("ripple"), id("sanctum_ripple")), "§a\"Ripple.\" §r\"It is Part of Space and Time\"\nType: Minor Machniation\n§cResistant to Cryo\n§cResistant to Ferro\n§aWeak to Pyro", false),
        new Entry(Set.of(id("ribbonblade_galvanite")), "§a\"Ribbonblade Galvanite.\" §rMean lean machine\nType: Minor Machniation\n§cResistant to Cryo\n§cResistant to Ferro\n§aWeak to Pyro", false),
        new Entry(Set.of(id("graver")), "§a\"Graver.\" §rUnremarkable\nType: Minor Undead\n§aWeak to Ferro\n§aWeak to Cloro", false),
        new Entry(Set.of(id("neon_lime_graver")), "§a\"Neon Lime Graver.\" §rI am very green, but I'm mean\nType: Minor Undead", false),
        new Entry(Set.of(id("lanternbearer")), "§a\"Lanternbearer.\" §rKeep your distance\nType: Minor Undead\n§cResistant to Pyro\n§aWeak to Cloro", false),
        new Entry(Set.of(id("raincatcher")), "§a\"Raincatcher.\" §rStill not that interesting\nType: Minor Undead", false),
        new Entry(Set.of(id("skeletitan"), id("frost_titan"), id("mantletitan")), "§a\"Skeletitan.\" §rGiant golem of bone, ivory and mold\nType: Minor Undead\n§aWeak to Ferro\n§aWeak to Cloro", false),
        new Entry(Set.of(id("errant"), id("sanctum_errant")), "§a\"Errant.\" §rSomewhat skilled swordscorpse\nType: Minor Undead\n§aWeak to Ferro", false),
        new Entry(Set.of(id("skullscor")), "§a\"Skullscor.\" §rWears the skull of it's last meal\nType: Minor Bug\n§aWeak to Ferro", false),
        new Entry(Set.of(id("spectre")), "§a\"Whistler.\" §rMelodic apparition\nType: Minor Undead\n§cResistant to Ferro\n§aWeak to Cloro", false),
        new Entry(Set.of(id("spirit")), "§a\"Spirit.\" §rNimble but fragile\nType: Minor Undead\n§aWeak to Cryo\n§aWeak to Cloro", false),
        new Entry(Set.of(id("effigy")), "§a\"Effigy.\" §rIs your refridgerator running?\nType: Minor Machination\n§cResistant to Ferro\n§aWeak to Pyro", false),
        new Entry(Set.of(id("assimilator")), "§a\"Assimilator.\" §rGet down from there, Lamar!\nType: Minor Undead\n§aWeak to Cloro", false),
        new Entry(Set.of(id("assimilated_graver")), "§a\"Assimilated Graver.\" §rYabba, my icing!\nType: Minor Undead\n§aWeak to Cloro", false),
        new Entry(Set.of(id("razoraptor")), "§a\"Razoraptor.\" §rSmarter than most infected\nType: Minor Undead\n§aWeak to Cloro", false),
        new Entry(Set.of(id("monolith")), "§a\"Monolith.\" §rGreat harbinger of The Infection\nType: Medius Undead\n§cResistances unknown\n§aWeaknesses unknown", false),
        new Entry(Set.of(id("dormant_monolith")), "§a\"???.\" §rNothing to describe\nType: Medius Unknown\n§cResistances unknown\n§aWeaknesses unknown", false),
        new Entry(Set.of(id("herald")), "§a\"Herald.\" §rGlorified keychain\nType: Medius Undead\n§cResistances unknown\n§aWeaknesses unknown", false),
        new Entry(Set.of(id("squall")), "§a\"???.\" §rNothing to describe\nType: Medius Unknown\n§cResistances unknown\n§aWeaknesses unknown", false),
        new Entry(Set.of(id("flare")), "§a\"Flare.\" §rPyro-infused creation of The Infection\nType: Minor Undead\n§aWeak to Cloro", false),
        new Entry(Set.of(id("infestophage_seed")), "§a\"???.\" §rNothing to describe\nType: Minor Unknown\n§cResistances unknown\n§aWeaknesses unknown", false),
        new Entry(Set.of(id("infestophage")), "§a\"Infestophage.\" §rStill hungry\nType: Minor Bug\n§aWeak to Cloro", false),
        new Entry(Set.of(id("mangler")), "§a\"Mangler.\" §rWinged creation of The Infection\nType: Minor Undead\n§aWeak to Cloro", false),
        new Entry(Set.of(id("blighted_spore")), "§a\"Spore.\" §rSimplest infection form\nType: Minor Undead\n§aWeak to Cloro", false),
        new Entry(Set.of(id("angry_cavebloomer")), "§a\"Angry Cavebloomer.\" §rFrequent napper with a short temper\nType: Magnus Plant\n§cResistances unknown\n§aWeaknessess unknown", true),
        new Entry(Set.of(id("budding_bloomer")), "§a\"Budding Bloomer.\" §rBig explodey boi\nType: Minor Plant", false),
        new Entry(Set.of(id("dandybloomer")), "§a\"Dandybloomer.\" §rAnnoying\nType: Minor Plant", false),
        new Entry(Set.of(id("icidrogunner")), "§a\"Icidrogunner.\" §rMOUTH COOL, MOUTH COOL\nType: Magnus Machination\n§cResistances unknown\n§aWeaknessess unknown", false),
        new Entry(Set.of(id("icidroburner")), "§a\"Icidroburner.\" §rMOUTH HOT, MOUTH HOT\nType: Magnus Machination\n§cResistances unknown\n§aWeaknessess unknown", false),
        new Entry(Set.of(id("whisper")), "§a\"Whisper.\" §rStranded lunar resident\nType: Minor Alien", false),
        new Entry(Set.of(id("shutterbug")), "§a\"Unette Adamski.\" §rAmateur photographer\nType: Minor Bug", false),
        new Entry(Set.of(id("florist")), "§a\"Umabel.\" §rLook up \"resting bug face\"\nType: Minor Undead", false),
        new Entry(Set.of(id("kofoni")), "§a\"Kofoni.\" §rI don't have a hoarding problem, YOU have a hoarding problem!\nType: Minor Bug", false),
        new Entry(Set.of(id("benzor")), "§a\"Benzor.\" §rCan he play the violin?\nType: Minor Bug", false),
        new Entry(Set.of(id("concierge")), "§a\"The Concierge.\" §rStill uninfected, or just a late bloomer?\nType: Minor Bug", false),
        new Entry(Set.of(id("defector_machinist"), id("lab_machinist")), "§a\"Qo.\" §rFugitive #001\nType: Minor Bug", false),
        new Entry(Set.of(id("huntress"), id("huntress_boss")), "§a\"The Huntress.\" §rOne in a million... or maybe just a couple hundred\nType: Medius Bug", false),
        new Entry(Set.of(id("ly")), "§a\"Ly.\" §rThank you come again\nType: Minor Bug", false),
        new Entry(Set.of(id("laut")), "§a\"Laut.\" §rI have no idea what I'm looking at here\nType: Minor... uh, thing", false));
    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, path); }
    static void describe(ServerPlayer player, Set<ResourceLocation> subjects) {
        for (Entry entry : ENTRIES) {
            if (Collections.disjoint(entry.types, subjects)) continue;
            player.displayClientMessage(Component.literal(entry.text), false);
            if (entry.photoQuest) {
                var advancement = player.server.getAdvancements().get(id("leaf_it_to_me_2"));
                if (advancement == null) { ThermalShockReforged.LOGGER.warn("Camera photo advancement unavailable: leaf_it_to_me_2"); continue; }
                var progress = player.getAdvancements().getOrStartProgress(advancement);
                if (!progress.isDone()) for (String criterion : progress.getRemainingCriteria()) player.getAdvancements().award(advancement, criterion);
            }
        }
    }
    private record Entry(Set<ResourceLocation> types, String text, boolean photoQuest) { }
}
