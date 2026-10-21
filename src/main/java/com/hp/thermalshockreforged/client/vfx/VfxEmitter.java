package com.hp.thermalshockreforged.client.vfx;

import com.hp.thermalshockreforged.client.vfx.VfxDefinition.ColorSource;
import com.hp.thermalshockreforged.client.vfx.VfxDefinition.Motion;
import com.hp.thermalshockreforged.client.vfx.VfxDefinition.Node;
import com.hp.thermalshockreforged.client.vfx.VfxDefinition.RingValue;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

final class VfxEmitter {
    private static final int ACTIVE = 0;
    private static final int REMOVING = 1;
    private static final int DEAD = 2;
    private static final float MAX_AGE = 3600;

    final double x;
    final double y;
    final double z;
    private final RandomSource random = RandomSource.create();
    private final Instance root;
    private float age;

    VfxEmitter(VfxDefinition definition, double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        root = new Instance(null, null);
        root.groups = new Group[definition.nodes.length];
        for (int index = 0; index < definition.nodes.length; index++) root.groups[index] = new Group(definition.nodes[index], random);
    }

    boolean update(float delta) {
        age += delta;
        root.living += delta;
        boolean alive = false;
        for (Group group : root.groups) {
            if (tickGroup(root, group, delta)) alive = true;
        }
        return alive && age < MAX_AGE;
    }

    void render(VfxBatch batch) {
        for (Group group : root.groups) renderGroup(group, batch);
    }

    private void renderGroup(Group group, VfxBatch batch) {
        if (group.node.draw != VfxDefinition.DRAW_NONE && group.node.texture != null) {
            for (Instance instance : group.instances) {
                if (instance.state == DEAD) continue;
                if (group.node.draw == VfxDefinition.DRAW_SPRITE) batch.sprite(group.node, instance.global, instance.color);
                else if (group.node.draw == VfxDefinition.DRAW_RING) batch.ring(group.node, instance.global, instance.ringValues(), instance.outerColor, instance.color, instance.innerColor);
            }
        }
        for (int index = 0; index < group.node.children.length; index++) {
            for (Instance instance : group.instances) renderGroup(instance.groups[index], batch);
        }
    }

    private boolean tickGroup(Instance parent, Group group, float delta) {
        boolean alive = false;
        for (int index = group.instances.size() - 1; index >= 0; index--) {
            Instance instance = group.instances.get(index);
            if (tickInstance(instance, delta)) alive = true;
            else group.instances.remove(index);
        }
        if (parent.state == ACTIVE) {
            while ((group.node.infinite || group.generated < group.node.maxGeneration) && parent.living >= group.next) {
                Instance child = new Instance(group.node, parent);
                child.living = parent.living - group.next;
                child.initialize(random);
                group.instances.add(child);
                group.generated++;
                group.next += Math.max(.00001F, VfxDefinition.sample(group.node.generationTime, random));
                tickInstance(child, 0);
                alive = true;
            }
            if (group.node.infinite || group.generated < group.node.maxGeneration) alive = true;
        }
        return alive;
    }

    private boolean tickInstance(Instance instance, float delta) {
        boolean alive = false;
        if (instance.state != DEAD) {
            instance.living += delta;
            instance.updateTransform();
            instance.updateColor();
            if (instance.state == ACTIVE && instance.node.removeWhenExtinct && instance.living > instance.lived) {
                if (instance.node.fadeOutType == 2) instance.state = REMOVING;
                else instance.state = DEAD;
            } else if (instance.state == REMOVING) {
                instance.removing += delta;
                if (instance.node.fadeOut == null || instance.removing > instance.node.fadeOut.frames) instance.state = DEAD;
            }
            if (instance.state != DEAD) alive = true;
        }
        for (Group group : instance.groups) {
            if (tickGroup(instance, group, delta)) alive = true;
        }
        return alive;
    }

    private static final class Group {
        final Node node;
        final List<Instance> instances = new ArrayList<>();
        float next;
        int generated;

        Group(Node node, RandomSource random) {
            this.node = node;
            next = VfxDefinition.sample(node.generationOffset, random);
        }
    }

    private static final class MotionState {
        float[] a;
        float[] b;
        float[] c;
        float rate;
        boolean sameStart;
        boolean sameEnd;
    }

    private final class Instance {
        final Node node;
        final Instance parent;
        Group[] groups = new Group[0];
        float living;
        float lived = Float.MAX_VALUE;
        float removing;
        int state = ACTIVE;
        final Matrix4f global = new Matrix4f();
        final Matrix4f local = new Matrix4f();
        Matrix4f captured;
        final float[] color = { 255, 255, 255, 255 };
        final float[] outerColor = { 255, 255, 255, 255 };
        final float[] innerColor = { 255, 255, 255, 255 };
        float[] parentColor = { 255, 255, 255, 255 };
        MotionState location;
        MotionState rotation;
        MotionState scale;
        float[][] colorState;
        float[][] outerColorState;
        float[][] centerColorState;
        float[][] innerColorState;
        MotionState ringOuter;
        MotionState ringInner;
        MotionState ringRatio;
        final float[] ringValues = new float[5];

        Instance(Node node, Instance parent) {
            this.node = node;
            this.parent = parent;
        }

        void initialize(RandomSource random) {
            lived = VfxDefinition.sample(node.life, random);
            groups = new Group[node.children.length];
            for (int index = 0; index < groups.length; index++) groups[index] = new Group(node.children[index], random);
            location = motionState(node.location, random);
            rotation = motionState(node.rotation, random);
            scale = motionState(node.scale, random);
            if (node.bindTranslation != 2 || node.bindRotation != 2 || node.bindScale != 2) captured = new Matrix4f(parent.global);
            if (node.colorInherit != 0) parentColor = parent.color.clone();
            if (node.color != null) colorState = colorState(node.color, random);
            if (node.ring != null) {
                outerColorState = colorState(node.ring.outerColor, random);
                centerColorState = colorState(node.ring.centerColor, random);
                innerColorState = colorState(node.ring.innerColor, random);
                ringOuter = ringState(node.ring.outer, random);
                ringInner = ringState(node.ring.inner, random);
                ringRatio = ringState(node.ring.ratio, random);
            }
        }

        private MotionState motionState(Motion motion, RandomSource random) {
            MotionState state = new MotionState();
            switch (motion.kind) {
                case VfxDefinition.MOTION_FIXED -> state.a = motion.fixed;
                case VfxDefinition.MOTION_PVA, VfxDefinition.MOTION_SINGLE_PVA -> {
                    state.a = VfxDefinition.sample(motion.position, random);
                    state.b = VfxDefinition.sample(motion.velocity, random);
                    state.c = VfxDefinition.sample(motion.acceleration, random);
                }
                case VfxDefinition.MOTION_EASING, VfxDefinition.MOTION_SINGLE_EASING -> easingState(state, motion.easing.start, motion.easing.end, motion.easing.middle, random);
                case VfxDefinition.MOTION_FCURVE -> {
                    state.a = new float[motion.curves.length];
                    for (int index = 0; index < state.a.length; index++) state.a[index] = VfxDefinition.sample(motion.curves[index].offset, random);
                }
                default -> { }
            }
            return state;
        }

        private void easingState(MotionState state, float[][] start, float[][] end, float[][] middle, RandomSource random) {
            state.a = VfxDefinition.sample(start, random);
            state.b = VfxDefinition.sample(end, random);
            if (middle == null) return;
            state.c = VfxDefinition.sample(middle, random);
            float first = 0;
            float second = 0;
            boolean sameStart = true;
            boolean sameEnd = true;
            for (int index = 0; index < state.a.length; index++) {
                first += (state.c[index] - state.a[index]) * (state.c[index] - state.a[index]);
                second += (state.b[index] - state.c[index]) * (state.b[index] - state.c[index]);
                if (state.c[index] != state.a[index]) sameStart = false;
                if (state.b[index] != state.c[index]) sameEnd = false;
            }
            first = (float) Math.sqrt(first);
            second = (float) Math.sqrt(second);
            if (first + second > .000001F) state.rate = first / (first + second);
            state.sameStart = sameStart;
            state.sameEnd = sameEnd;
        }

        private MotionState ringState(RingValue value, RandomSource random) {
            MotionState state = new MotionState();
            if (value.kind == 0) state.a = value.fixed;
            else if (value.kind == 1) {
                state.a = VfxDefinition.sample(value.position, random);
                state.b = VfxDefinition.sample(value.velocity, random);
                state.c = VfxDefinition.sample(value.acceleration, random);
            } else {
                state.a = VfxDefinition.sample(value.start, random);
                state.b = VfxDefinition.sample(value.end, random);
            }
            return state;
        }

        private float[][] colorState(ColorSource source, RandomSource random) {
            if (source.kind == 0) return new float[][] { source.fixed };
            if (source.kind == 1) return new float[][] { VfxDefinition.sample(source.random, random) };
            return new float[][] { VfxDefinition.sample(source.start, random), VfxDefinition.sample(source.end, random) };
        }

        private float progress() {
            if (lived <= 0) return 1;
            return VfxDefinition.clamp01(living / lived);
        }

        private float[] evaluate(Motion motion, MotionState state, float[] out) {
            switch (motion.kind) {
                case VfxDefinition.MOTION_FIXED -> System.arraycopy(state.a, 0, out, 0, 3);
                case VfxDefinition.MOTION_PVA -> {
                    for (int index = 0; index < 3; index++) out[index] = state.a[index] + state.b[index] * living + state.c[index] * living * living * .5F;
                }
                case VfxDefinition.MOTION_SINGLE_PVA -> {
                    float value = state.a[0] + state.b[0] * living + state.c[0] * living * living * .5F;
                    out[0] = value;
                    out[1] = value;
                    out[2] = value;
                }
                case VfxDefinition.MOTION_EASING -> {
                    float t = VfxDefinition.ease(motion.easing.type, motion.easing.params, progress());
                    for (int index = 0; index < 3; index++) out[index] = eased(state, index, t);
                }
                case VfxDefinition.MOTION_SINGLE_EASING -> {
                    float t = VfxDefinition.ease(motion.easing.type, motion.easing.params, progress());
                    float value = eased(state, 0, t);
                    out[0] = value;
                    out[1] = value;
                    out[2] = value;
                }
                case VfxDefinition.MOTION_FCURVE -> {
                    for (int index = 0; index < 3; index++) out[index] = motion.curves[index].evaluate(living) + state.a[index];
                }
                default -> { }
            }
            return out;
        }

        private float eased(MotionState state, int index, float t) {
            if (state.c != null) return VfxDefinition.spline3(state.a[index], state.c[index], state.b[index], state.sameStart, state.sameEnd, state.rate, t);
            return state.a[index] + (state.b[index] - state.a[index]) * t;
        }

        void updateTransform() {
            float[] p = evaluate(node.location, location, new float[3]);
            float[] r = evaluate(node.rotation, rotation, new float[3]);
            float[] s = evaluate(node.scale, scale, new float[3]);
            local.identity().translate(p[0], p[1], p[2]).rotateY(r[1]).rotateX(r[0]).rotateZ(r[2]).scale(s[0], s[1], s[2]);
            global.set(parentMatrix()).mul(local);
        }

        private Matrix4f parentMatrix() {
            if (captured == null) return parent.global;
            if (node.bindTranslation != 2 && node.bindRotation != 2 && node.bindScale != 2) return captured;
            Vector3f translation = new Vector3f();
            Quaternionf rotationPart = new Quaternionf();
            Vector3f scalePart = new Vector3f();
            Matrix4f translationSource = parent.global;
            if (node.bindTranslation != 2) translationSource = captured;
            Matrix4f rotationSource = parent.global;
            if (node.bindRotation != 2) rotationSource = captured;
            Matrix4f scaleSource = parent.global;
            if (node.bindScale != 2) scaleSource = captured;
            translationSource.getTranslation(translation);
            rotationSource.getNormalizedRotation(rotationPart);
            scaleSource.getScale(scalePart);
            return new Matrix4f().translation(translation).rotate(rotationPart).scale(scalePart);
        }

        void updateColor() {
            float fade = fadeAlpha();
            if (node.color != null) {
                applyColor(node.color, colorState, color, fade);
            } else if (node.ring != null) {
                applyColor(node.ring.outerColor, outerColorState, outerColor, fade);
                applyColor(node.ring.centerColor, centerColorState, color, fade);
                applyColor(node.ring.innerColor, innerColorState, innerColor, fade);
                updateRing();
            } else {
                color[0] = 255;
                color[1] = 255;
                color[2] = 255;
                color[3] = 255 * fade;
            }
            if (node.colorInherit != 0) {
                if (node.colorInherit == 2) parentColor = parent.color;
                multiply(color, parentColor);
                if (node.ring != null) {
                    multiply(outerColor, parentColor);
                    multiply(innerColor, parentColor);
                }
            }
        }

        private void multiply(float[] target, float[] other) {
            for (int index = 0; index < 4; index++) target[index] = target[index] * other[index] / 255F;
        }

        private void applyColor(ColorSource source, float[][] state, float[] out, float fade) {
            if (source.kind == 2) {
                float t = VfxDefinition.cubic(source.params, progress());
                for (int index = 0; index < 4; index++) out[index] = Math.max(0, Math.min(255, state[0][index] + (state[1][index] - state[0][index]) * t));
            } else {
                System.arraycopy(state[0], 0, out, 0, 4);
            }
            out[3] = (float) Math.floor(out[3] * fade);
        }

        private float fadeAlpha() {
            float alpha = 1;
            if (node.fadeIn != null && living < node.fadeIn.frames) {
                alpha *= VfxDefinition.cubic(node.fadeIn.params, living / Math.max(.0001F, node.fadeIn.frames));
            }
            if (node.fadeOut != null) {
                float frames = Math.max(.0001F, node.fadeOut.frames);
                if (node.fadeOutType == 1 && living + node.fadeOut.frames > lived) {
                    alpha *= 1 - VfxDefinition.cubic(node.fadeOut.params, (living + frames - lived) / frames);
                } else if (node.fadeOutType == 2 && state == REMOVING) {
                    alpha *= 1 - VfxDefinition.cubic(node.fadeOut.params, removing / frames);
                }
            }
            return VfxDefinition.clamp01(alpha);
        }

        private void updateRing() {
            ringValue(node.ring.outer, ringOuter, 0);
            ringValue(node.ring.inner, ringInner, 2);
            ringValue(node.ring.ratio, ringRatio, 4);
        }

        private void ringValue(RingValue value, MotionState state, int offset) {
            for (int index = 0; index < state.a.length; index++) {
                float result;
                if (value.kind == 0) result = state.a[index];
                else if (value.kind == 1) result = state.a[index] + state.b[index] * living + state.c[index] * living * living * .5F;
                else result = state.a[index] + (state.b[index] - state.a[index]) * VfxDefinition.cubic(value.params, progress());
                ringValues[offset + index] = result;
            }
        }

        float[] ringValues() { return ringValues; }
    }
}
