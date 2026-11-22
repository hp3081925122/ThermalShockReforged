package com.hp.thermalshockreforged.gametest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.client.weapons.ZapGauntletArmPose;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

@EventBusSubscriber(modid = ThermalShockReforged.MOD_ID, value = Dist.CLIENT)
public final class ClientPoseProbe {
    private static final List<String> ARM_POSE_ITEMS = List.of(
            "zap_gauntlet", "flintsteel_rifle", "hailfall", "magnetic_crossbow", "zenith_crossbow", "volcano",
            "cobalt_thermoblaster", "zenith_thermoblaster", "buzzsaw", "liteprism_laser", "thistle_bow",
            "core_eject_flare_gun", "carbor_gl", "sylvia_rifle", "net_launcher", "the_judge");
    private static final List<String> EXPLICIT_EMPTY_POSE_ITEMS = List.of(
            "jelly_umbrella", "legendary_jelly_umbrella", "piston_gauntlet", "titanium_mace");
    private static JsonArray RESULTS = new JsonArray();
    private static boolean running;
    private static boolean finished;
    private static boolean screenshotPending;
    private static boolean screenshotDone;
    private static int state;
    private static int sceneIndex;
    private static int stableTicks;
    private static int stableRenderFrames;
    private static long nextCommandTick;
    private static long deadlineNanos;
    private static String session;
    private static String autoStartedSession;
    private static String capturePath;
    private static long extractSequence;
    private static long consumedSequence;
    private static long activeFrameSequence;
    private static long latestFrameSequence;
    private static Throwable failure;
    private static CameraType originalCameraType;
    private static JsonObject prepared;
    private static JsonObject current;
    private static JsonObject latestObservation;

    private ClientPoseProbe() {
    }

    public static List<String> scenarios() {
        return ClientPoseServer.scenarios();
    }

    public static boolean finished() {
        return finished;
    }

    public static JsonObject result() {
        var result = new JsonObject();
        result.addProperty("status", finished ? failure == null ? "passed" : "failed" : running ? "running" : "idle");
        result.addProperty("session", session);
        result.addProperty("expectedScenarios", scenarios().size());
        result.addProperty("completedScenarios", RESULTS.size());
        result.add("scenarios", RESULTS.deepCopy());
        if (failure != null) result.addProperty("error", failure.toString());
        return result;
    }

    private static boolean enabled() {
        return ClientIntegrationData.enabled() && Boolean.getBoolean("tsr.clientIntegrationPose");
    }

    public static void begin() {
        Minecraft mc = Minecraft.getInstance();
        ClientIntegrationData.require(enabled(), "Client pose probe is not enabled");
        ClientIntegrationData.require(!running, "Client pose probe is already running");
        ClientIntegrationData.require(mc.player != null && mc.level != null && mc.getConnection() != null, "Pose probe requires a logged-in client");
        ClientIntegrationData.require(!mc.getConnection().getConnection().isMemoryConnection(), "Pose probe requires the real TCP client");
        ClientIntegrationData.require(mc.screen == null, "Pose probe requires an unobstructed in-world view");
        JsonObject ready = ClientIntegrationData.read("server-ready.json");
        ClientIntegrationData.require("ready".equals(ready.get("status").getAsString()), "TCP integration server is not ready");
        session = ready.get("session").getAsString();
        originalCameraType = mc.options.getCameraType();
        state = 1;
        sceneIndex = 0;
        stableTicks = 0;
        stableRenderFrames = 0;
        nextCommandTick = clock() + 30L;
        deadlineNanos = System.nanoTime() + 3_600_000_000_000L;
        running = true;
        finished = false;
        screenshotPending = false;
        screenshotDone = false;
        failure = null;
        prepared = null;
        current = null;
        latestObservation = null;
        extractSequence = 0L;
        consumedSequence = 0L;
        activeFrameSequence = 0L;
        latestFrameSequence = -1L;
        RESULTS = new JsonArray();
        writeResult("running", null);
        ThermalShockReforged.LOGGER.info("Client pose probe armed: session={}, scenarios={}, transport=TCP", session, scenarios().size());
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        if (!enabled() || finished) return;
        Minecraft mc = Minecraft.getInstance();
        try {
            if (failure != null) throw new IllegalStateException("Pose extraction or screenshot failed", failure);
            if (running && System.nanoTime() > deadlineNanos) throw new IllegalStateException("Client pose probe timed out at scene " + scene());
            if (!running) {
                Path baselinePath = ClientIntegrationData.directory().resolve("client-results.json");
                if (!Files.exists(baselinePath)) return;
                JsonObject baseline = ClientIntegrationData.read("client-results.json");
                JsonObject ready = ClientIntegrationData.read("server-ready.json");
                if (!"passed".equals(baseline.get("status").getAsString()) || baseline.get("completedScenarios").getAsInt() != 7 || !"ready".equals(ready.get("status").getAsString())) return;
                String baselineSession = baseline.get("session").getAsString();
                if (!baselineSession.equals(ready.get("session").getAsString()) || baselineSession.equals(autoStartedSession)) return;
                if (mc.player == null || mc.level == null || mc.getConnection() == null || mc.getConnection().getConnection().isMemoryConnection() || mc.screen != null) return;
                autoStartedSession = baselineSession;
                begin();
                ThermalShockReforged.LOGGER.info("Client pose probe auto-started after baseline seven passed: session={}", baselineSession);
            }
            advance(mc);
        } catch (Throwable problem) {
            fail(mc, problem);
        }
    }

    private static void advance(Minecraft mc) throws java.io.IOException {
        if (state == 1) {
            if (sceneIndex >= scenarios().size()) {
                restoreClient(mc);
                running = false;
                finished = true;
                writeResult("passed", null);
                ThermalShockReforged.LOGGER.info("Client pose probe passed: scenes={}, screenshots={}", RESULTS.size(), RESULTS.size());
                return;
            }
            if (clock() < nextCommandTick) return;
            if (mc.screen != null) throw new IllegalStateException("Close the screen before pose capture");
            prepared = null;
            latestObservation = null;
            stableTicks = 0;
            stableRenderFrames = 0;
            consumedSequence = extractSequence;
            latestFrameSequence = -1L;
            screenshotPending = false;
            screenshotDone = false;
            current = new JsonObject();
            current.addProperty("scene", scene());
            current.addProperty("session", session);
            Path evidencePath = ClientIntegrationData.directory().resolve("pose-" + scene() + "-prepared.json");
            Files.deleteIfExists(evidencePath);
            mc.player.connection.sendCommand("tsr_pose prepare " + scene());
            state = 2;
            deadlineNanos = System.nanoTime() + 120_000_000_000L;
            return;
        }
        if (state == 2) {
            Path evidencePath = ClientIntegrationData.directory().resolve("pose-" + scene() + "-prepared.json");
            if (!Files.exists(evidencePath)) return;
            prepared = ClientIntegrationData.read(evidencePath.getFileName().toString());
            ClientIntegrationData.require("passed".equals(prepared.get("status").getAsString()), "Pose server preparation failed: " + prepared);
            ClientIntegrationData.require(session.equals(prepared.get("session").getAsString()), "Pose fixture belongs to another server session");
            ClientIntegrationData.require(scene().equals(prepared.get("scene").getAsString()), "Pose fixture scenario does not match the requested scene");
            current.add("serverPrepared", prepared.deepCopy());
            current.addProperty("capture", "real game renderer screenshot after synchronized target state remains stable for at least eight client ticks and eight render frames");
            current.addProperty("renderStateCapture", "ExtractLevelRenderStateEvent deep-copy during extraction; RenderFrameEvent.Post only consumes the immutable JSON snapshot");
            nextCommandTick = prepared.get("serverTick").getAsLong() + 30L;
            state = 3;
            deadlineNanos = System.nanoTime() + 120_000_000_000L;
        }
        if (state == 3) {
            configureViewAndUse(mc);
            if (stableTargetState(mc)) stableTicks++;
            else {
                stableTicks = 0;
                stableRenderFrames = 0;
            }
            if (screenshotDone) completeScene(mc);
        } else if (state == 4 && screenshotDone) {
            completeScene(mc);
        }
    }

    private static void configureViewAndUse(Minecraft mc) {
        boolean firstPerson = "first_person".equals(prepared.get("view").getAsString());
        mc.options.setCameraType(firstPerson ? CameraType.FIRST_PERSON : CameraType.THIRD_PERSON_FRONT);
        if (prepared.get("sceneKind").getAsString().equals("single_armor_piece_third_person_front")) {
            mc.player.stopUsingItem();
            return;
        }
        String itemId = prepared.get("itemId").getAsString();
        InteractionHand hand = prepared.get("hand").getAsString().equals("main") ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack target = mc.player.getItemInHand(hand);
        if (!itemId.equals(itemId(target))) return;
        if (prepared.get("useState").getAsString().equals("using")) {
            if (!mc.player.isUsingItem() || mc.player.getUsedItemHand() != hand || mc.player.getUseItem() != target) {
                mc.player.stopUsingItem();
                mc.player.startUsingItem(hand);
            }
        } else if (mc.player.isUsingItem()) {
            mc.player.stopUsingItem();
        }
    }

    private static boolean stableTargetState(Minecraft mc) {
        if (mc.player == null || mc.level == null || mc.getConnection() == null || mc.screen != null) return false;
        if (!mc.level.dimension().identifier().toString().equals(prepared.get("dimension").getAsString())) return false;
        if (mc.player.blockPosition().distSqr(new net.minecraft.core.BlockPos(prepared.get("x").getAsInt(), prepared.get("y").getAsInt(), prepared.get("z").getAsInt())) > 64.0D) return false;
        if (mc.options.getCameraType() != (prepared.get("view").getAsString().equals("first_person") ? CameraType.FIRST_PERSON : CameraType.THIRD_PERSON_FRONT)) return false;
        if (prepared.get("sceneKind").getAsString().equals("single_armor_piece_third_person_front")) {
            return expectedArmorOnClient(mc) && !mc.player.isUsingItem();
        }
        String expectedId = prepared.get("itemId").getAsString();
        InteractionHand hand = prepared.get("hand").getAsString().equals("main") ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack target = mc.player.getItemInHand(hand);
        if (!expectedId.equals(itemId(target))) return false;
        boolean using = prepared.get("useState").getAsString().equals("using");
        return mc.player.isUsingItem() == using && (!using || mc.player.getUsedItemHand() == hand && mc.player.getUseItem() == target);
    }

    private static boolean expectedArmorOnClient(Minecraft mc) {
        String itemId = prepared.get("itemId").getAsString();
        for (EquipmentSlot slot : List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) {
            ItemStack stack = mc.player.getItemBySlot(slot);
            if (itemId.equals(itemId(stack))) {
                for (EquipmentSlot other : List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) {
                    if (other != slot && !mc.player.getItemBySlot(other).isEmpty()) return false;
                }
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void extract(ExtractLevelRenderStateEvent event) {
        if (!running || finished || state != 3 || prepared == null || event.getLevel() != Minecraft.getInstance().level) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;
        try {
            AvatarRenderState avatar = event.getRenderState().entityRenderStates.stream()
                    .filter(AvatarRenderState.class::isInstance)
                    .map(AvatarRenderState.class::cast)
                    .filter(candidate -> candidate.id == mc.player.getId())
                    .findFirst().orElse(null);
            latestObservation = observe(mc, avatar);
            latestObservation.addProperty("snapshotSequence", ++extractSequence);
            latestFrameSequence = activeFrameSequence + 1L;
            latestObservation.addProperty("renderFrameSequence", latestFrameSequence);
        } catch (Throwable problem) {
            failure = problem;
        }
    }

    @SubscribeEvent
    public static void framePre(RenderFrameEvent.Pre event) {
        if (running && !finished && state == 3) activeFrameSequence++;
    }

    @SubscribeEvent
    public static void frame(RenderFrameEvent.Post event) {
        if (!running || finished || state != 3 || latestObservation == null || latestFrameSequence != activeFrameSequence || screenshotPending || screenshotDone || latestObservation.get("snapshotSequence").getAsLong() <= consumedSequence) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;
        consumedSequence = latestObservation.get("snapshotSequence").getAsLong();
        if (!matchesActualRenderState(latestObservation)) {
            stableRenderFrames = 0;
            return;
        }
        stableRenderFrames++;
        if (stableTicks < 8 || stableRenderFrames < 8) return;
        current.add("capturedRenderState", latestObservation.deepCopy());
        current.addProperty("pairedRenderFrameSequence", activeFrameSequence);
        current.addProperty("pairedRenderSnapshotSequence", consumedSequence);
        current.addProperty("stableTargetTicks", stableTicks);
        current.addProperty("stableMatchingRenderFrames", stableRenderFrames);
        current.addProperty("tcp", !mc.getConnection().getConnection().isMemoryConnection());
        current.addProperty("zenithCrossbowBoneBranchCaptured", latestObservation.get("zenithCrossbowFirstPersonBranchConditions").getAsBoolean());
        String scenario = scene();
        capturePath = ClientIntegrationData.directory().resolve("pose-" + scenario + ".png").toString();
        current.addProperty("screenshot", capturePath);
        screenshotPending = true;
        state = 4;
        Path path = Path.of(capturePath);
        Screenshot.takeScreenshot(mc.getMainRenderTarget(), image -> {
            try (image) {
                image.writeToFile(path);
                mc.execute(() -> {
                    screenshotPending = false;
                    screenshotDone = true;
                });
            } catch (Throwable problem) {
                mc.execute(() -> failure = problem);
            }
        });
    }

    private static boolean matchesActualRenderState(JsonObject observation) {
        if (!"none".equals(observation.get("screen").getAsString())) return false;
        if (!prepared.get("dimension").getAsString().equals(observation.get("dimension").getAsString())) return false;
        String expectedCamera = prepared.get("view").getAsString().equals("first_person") ? "FIRST_PERSON" : "THIRD_PERSON_FRONT";
        if (!expectedCamera.equals(observation.get("cameraType").getAsString())) return false;
        boolean thirdPerson = prepared.get("view").getAsString().equals("third_person_front");
        if (!thirdPerson) {
            if (prepared.get("sceneKind").getAsString().equals("single_armor_piece_third_person_front")) return false;
            if (!prepared.get("itemId").getAsString().equals(observation.get("hands").getAsJsonObject().get(prepared.get("hand").getAsString()).getAsString())) return false;
            boolean using = prepared.get("useState").getAsString().equals("using");
            if (observation.get("isUsingItem").getAsBoolean() != using
                    || using && !observation.get("usedItemHand").getAsString().equals(prepared.get("hand").getAsString())) return false;
            return !prepared.get("itemId").getAsString().equals("thermal_shock_reforged:zenith_crossbow")
                    || !observation.get("cameraType").getAsString().equals("FIRST_PERSON")
                    || observation.get("zenithCrossbowFirstPersonBranchConditions").getAsBoolean();
        }
        if (!observation.get("avatarStateFound").getAsBoolean()) return false;
        String itemId = prepared.get("itemId").getAsString();
        if (prepared.get("sceneKind").getAsString().equals("single_armor_piece_third_person_front")) {
            return itemId.equals(observation.get("armorEquipment").getAsJsonObject().get(prepared.get("equipmentSlot").getAsString()).getAsString());
        }
        String arm = observation.get("targetArm").getAsString();
        if (!itemId.equals(observation.get("heldItems").getAsJsonObject().get(arm).getAsString())) return false;
        boolean using = prepared.get("useState").getAsString().equals("using");
        if (observation.get("avatarIsUsingItem").getAsBoolean() != using) return false;
        if (using && !observation.get("avatarUseItemHand").getAsString().equals(prepared.get("hand").getAsString())) return false;
        String path = itemId.substring(itemId.indexOf(':') + 1);
        if (ARM_POSE_ITEMS.contains(path)) {
            boolean shouldUseCustomPose = observation.get("avatarUseItemHand").getAsString().equals(prepared.get("hand").getAsString());
            String pose = observation.get("armPoses").getAsJsonObject().get(arm).getAsString();
            if (shouldUseCustomPose) return pose.equals(observation.get("customArmPoseName").getAsString());
            return "EMPTY".equals(pose);
        }
        if (EXPLICIT_EMPTY_POSE_ITEMS.contains(path)) return "EMPTY".equals(observation.get("armPoses").getAsJsonObject().get(arm).getAsString());
        return true;
    }

    private static JsonObject observe(Minecraft mc, AvatarRenderState avatar) {
        var result = new JsonObject();
        result.addProperty("dimension", mc.level.dimension().identifier().toString());
        result.addProperty("screen", mc.screen == null ? "none" : mc.screen.getClass().getName());
        result.addProperty("cameraType", mc.options.getCameraType().name());
        result.addProperty("playerId", mc.player.getId());
        result.addProperty("playerX", mc.player.getX());
        result.addProperty("playerY", mc.player.getY());
        result.addProperty("playerZ", mc.player.getZ());
        result.addProperty("playerMainArm", mc.player.getMainArm().name());
        result.addProperty("mainHand", itemId(mc.player.getMainHandItem()));
        result.addProperty("offHand", itemId(mc.player.getOffhandItem()));
        result.addProperty("isUsingItem", mc.player.isUsingItem());
        result.addProperty("usedItemHand", mc.player.getUsedItemHand() == InteractionHand.MAIN_HAND ? "main" : "off");
        result.addProperty("useItem", itemId(mc.player.getUseItem()));
        result.addProperty("useItemRemainingTicks", mc.player.getUseItemRemainingTicks());
        result.addProperty("useItemConstruction", "LocalPlayer.startUsingItem or stopUsingItem explicitly drives this client render fixture; no item use packet or natural gameplay success is claimed");
        boolean armorScene = prepared.get("sceneKind").getAsString().equals("single_armor_piece_third_person_front");
        InteractionHand hand = prepared.get("hand").getAsString().equals("main") ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        HumanoidArm targetArm = hand == InteractionHand.MAIN_HAND ? mc.player.getMainArm() : mc.player.getMainArm().getOpposite();
        result.addProperty("targetArm", armorScene ? "not_applicable" : targetArm == HumanoidArm.RIGHT ? "right" : "left");
        result.addProperty("itemRenderContext", armorScene ? "not_applicable" : itemRenderContext(mc, hand));
        result.addProperty("firstPersonUseOffsetCondition", !armorScene && mc.player.getUseItem() == mc.player.getItemInHand(hand));
        var clientHands = new JsonObject();
        clientHands.addProperty("main", itemId(mc.player.getMainHandItem()));
        clientHands.addProperty("off", itemId(mc.player.getOffhandItem()));
        result.add("hands", clientHands);
        boolean zenith = prepared.get("itemId").getAsString().equals("thermal_shock_reforged:zenith_crossbow");
        result.addProperty("zenithCrossbowFirstPersonBranchConditions", !armorScene && zenith && mc.options.getCameraType().isFirstPerson()
                && (itemRenderContext(mc, hand).equals("FIRST_PERSON_RIGHT_HAND") || itemRenderContext(mc, hand).equals("FIRST_PERSON_LEFT_HAND")));
        result.addProperty("zenithCrossbowExpectedBoneScale", !armorScene && zenith && mc.options.getCameraType().isFirstPerson() ? 0.75F : 1.0F);
        result.addProperty("zenithBoneEvidenceLimit", "Captures the actual first-person render while the production item and ItemDisplayContext conditions are true; no production hook or direct GeoBone scale introspection was added");
        String targetItemPath = prepared.get("itemId").getAsString().substring(prepared.get("itemId").getAsString().indexOf(':') + 1);
        result.addProperty("targetItemPoseBranch", ARM_POSE_ITEMS.contains(targetItemPath) ? "custom_when_used_item_hand_matches" : EXPLICIT_EMPTY_POSE_ITEMS.contains(targetItemPath) ? "extension_returns_empty" : "no_mod_custom_arm_pose_branch");
        result.addProperty("avatarStateFound", avatar != null);
        if (avatar != null) {
            result.addProperty("avatarStateId", avatar.id);
            result.addProperty("avatarIsUsingItem", avatar.isUsingItem);
            result.addProperty("avatarUseItemHand", avatar.useItemHand == InteractionHand.MAIN_HAND ? "main" : "off");
            result.addProperty("avatarMainArm", avatar.mainArm.name());
            result.addProperty("rightArmPose", avatar.rightArmPose.name());
            result.addProperty("leftArmPose", avatar.leftArmPose.name());
            result.addProperty("customArmPoseName", ZapGauntletArmPose.POSE.getValue().name());
            var poses = new JsonObject();
            poses.addProperty("right", avatar.rightArmPose.name());
            poses.addProperty("left", avatar.leftArmPose.name());
            result.add("armPoses", poses);
            var hands = new JsonObject();
            hands.addProperty("right", itemId(avatar.rightHandItemStack));
            hands.addProperty("left", itemId(avatar.leftHandItemStack));
            result.add("heldItems", hands);
            var armor = new JsonObject();
            armor.addProperty("head", itemId(avatar.headEquipment));
            armor.addProperty("chest", itemId(avatar.chestEquipment));
            armor.addProperty("legs", itemId(avatar.legsEquipment));
            armor.addProperty("feet", itemId(avatar.feetEquipment));
            result.add("armorEquipment", armor);
        }
        result.addProperty("captureSource", "actual AvatarRenderState from ExtractLevelRenderStateEvent; values copied into JSON before LevelRenderState reset");
        result.addProperty("poseTriggerInterpretation", "Idle means isUsingItem=false; getUsedItemHand is captured separately because its actual value selects the registered IClientItemExtensions pose and may retain a hand value");
        return result;
    }

    private static String itemRenderContext(Minecraft mc, InteractionHand hand) {
        HumanoidArm arm = hand == InteractionHand.MAIN_HAND ? mc.player.getMainArm() : mc.player.getMainArm().getOpposite();
        return arm == HumanoidArm.RIGHT ? "FIRST_PERSON_RIGHT_HAND" : "FIRST_PERSON_LEFT_HAND";
    }

    private static String itemId(ItemStack stack) {
        return stack == null || stack.isEmpty() ? "empty" : BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    private static void completeScene(Minecraft mc) {
        current.addProperty("status", "passed");
        current.addProperty("scenario", scene());
        current.addProperty("serverTickObserved", clock());
        RESULTS.add(current.deepCopy());
        ClientIntegrationData.write("pose-" + scene() + "-verified.json", current.deepCopy());
        writeResult("running", null);
        sceneIndex++;
        prepared = null;
        current = null;
        latestObservation = null;
        screenshotPending = false;
        screenshotDone = false;
        stableTicks = 0;
        stableRenderFrames = 0;
        state = 1;
        nextCommandTick = clock() + 30L;
        deadlineNanos = System.nanoTime() + 3_600_000_000_000L;
    }

    private static void restoreClient(Minecraft mc) {
        if (mc.player != null) mc.player.stopUsingItem();
        if (originalCameraType != null) mc.options.setCameraType(originalCameraType);
    }

    private static void fail(Minecraft mc, Throwable problem) {
        failure = problem;
        finished = true;
        running = false;
        restoreClient(mc);
        writeResult("failed", problem);
        ThermalShockReforged.LOGGER.error("Client pose probe failed: state={}, scenario={}", state, sceneIndex, problem);
    }

    private static long clock() {
        JsonObject clock = ClientIntegrationData.read("server-clock.json");
        ClientIntegrationData.require(session == null || session.equals(clock.get("session").getAsString()), "Pose server clock belongs to another session");
        return clock.get("serverTick").getAsLong();
    }

    private static String scene() {
        return sceneIndex < scenarios().size() ? scenarios().get(sceneIndex) : "finished";
    }

    private static void writeResult(String status, Throwable problem) {
        var result = new JsonObject();
        result.addProperty("status", status);
        result.addProperty("session", session);
        result.addProperty("expectedScenarios", scenarios().size());
        result.addProperty("completedScenarios", RESULTS.size());
        result.addProperty("heldItemIds", ClientPoseServer.scenarios().stream().filter(value -> value.startsWith("held_")).map(value -> value.replaceFirst("_(main|off)_(idle|using)_(first|third_front)$", "")).distinct().count());
        result.addProperty("armorItemIds", ClientPoseServer.scenarios().stream().filter(value -> value.startsWith("armor_")).count());
        result.addProperty("transport", "Real TCP client, server-synchronized stack/equipment, actual first/third-person renderer screenshots");
        result.addProperty("usingStateLimit", "using cases force LocalPlayer render state only and do not prove natural use action, ammo, cooldown, fire, or release behavior");
        result.addProperty("armorAnimationLimit", "armor scenes verify 31 one-piece default equipped renderings; geckoAnim has no production writer and is not claimed as natural trigger");
        result.addProperty("state", state);
        result.add("scenarios", RESULTS.deepCopy());
        if (problem != null) {
            result.addProperty("error", problem.toString());
            if (current != null) result.add("currentScenario", current.deepCopy());
            if (latestObservation != null) result.add("lastRenderState", latestObservation.deepCopy());
        }
        ClientIntegrationData.write("pose-probe-result.json", result);
    }
}
