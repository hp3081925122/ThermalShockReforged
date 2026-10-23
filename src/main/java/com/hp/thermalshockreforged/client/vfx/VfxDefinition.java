package com.hp.thermalshockreforged.client.vfx;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;

public final class VfxDefinition {
    static final int DRAW_NONE = 0;
    static final int DRAW_SPRITE = 2;
    static final int DRAW_RING = 4;
    static final int MOTION_FIXED = 0;
    static final int MOTION_PVA = 1;
    static final int MOTION_EASING = 2;
    static final int MOTION_FCURVE = 3;
    static final int MOTION_SINGLE_PVA = 4;
    static final int MOTION_SINGLE_EASING = 5;
    static final int BILLBOARD_FACING = 0;
    static final int BILLBOARD_Y_AXIS = 1;
    static final int BILLBOARD_FIXED = 2;
    static final int BILLBOARD_ROTATED = 3;

    final Node[] nodes;

    private VfxDefinition(Node[] nodes) { this.nodes = nodes; }

    static VfxDefinition parse(JsonObject json) { return new VfxDefinition(nodes(json.getAsJsonArray("nodes"))); }

    private static Node[] nodes(JsonArray array) {
        Node[] result = new Node[array.size()];
        for (int index = 0; index < result.length; index++) result[index] = new Node(array.get(index).getAsJsonObject());
        return result;
    }

    static float[] floats(JsonElement element) {
        JsonArray array = element.getAsJsonArray();
        float[] result = new float[array.size()];
        for (int index = 0; index < result.length; index++) result[index] = array.get(index).getAsFloat();
        return result;
    }

    static float[][] range(JsonElement element) {
        JsonArray array = element.getAsJsonArray();
        if (array.get(0).isJsonArray()) return new float[][] { floats(array.get(0)), floats(array.get(1)) };
        return new float[][] { { array.get(0).getAsFloat() }, { array.get(1).getAsFloat() } };
    }

    static float sample(float[] range, RandomSource random) { return range[0] + (range[1] - range[0]) * random.nextFloat(); }

    static float[] sample(float[][] range, RandomSource random) {
        float[] result = new float[range[0].length];
        for (int index = 0; index < result.length; index++) result[index] = range[0][index] + (range[1][index] - range[0][index]) * random.nextFloat();
        return result;
    }

    static float cubic(float[] params, float t) { return params[0] * t * t * t + params[1] * t * t + params[2] * t; }

    static float clamp01(float value) { return Math.max(0, Math.min(1, value)); }

    static float ease(int type, float[] params, float t) {
        switch (type) {
            case 0: return cubic(params, t);
            case 1: return t;
            case 10: return t * t;
            case 11: return 1 - (1 - t) * (1 - t);
            case 12: return inOut(t, 2);
            case 20: return t * t * t;
            case 21: return 1 - (float) Math.pow(1 - t, 3);
            case 22: return inOut(t, 3);
            case 30: return (float) Math.pow(t, 4);
            case 31: return 1 - (float) Math.pow(1 - t, 4);
            case 32: return inOut(t, 4);
            case 40: return (float) Math.pow(t, 5);
            case 41: return 1 - (float) Math.pow(1 - t, 5);
            case 42: return inOut(t, 5);
            case 50: return back(t);
            case 51: return 1 - back(1 - t);
            case 52: {
                if (t <= .5F) return back(t * 2) * .5F;
                return 1 - back((1 - t) * 2) * .5F;
            }
            case 60: return 1 - bounceOut(1 - t);
            case 61: return bounceOut(t);
            case 62: {
                if (t <= .5F) return (1 - bounceOut(1 - t * 2)) * .5F;
                return bounceOut((t - .5F) * 2) * .5F + .5F;
            }
            default: return t;
        }
    }

    private static float inOut(float t, int power) {
        if (t <= .5F) return (float) Math.pow(t * 2, power) * .5F;
        return 1 - (float) Math.pow((1 - t) * 2, power) * .5F;
    }

    private static float back(float t) { return 2.8F * t * t * t - 1.8F * t * t; }

    private static float bounceOut(float t) {
        if (t < 4F / 11F) {
            float v = t / 4F * 11F;
            return v * v;
        }
        if (t < 8F / 11F) {
            float v = t - 4F / 11F - 2F / 11F;
            return 1 + (v * v - (2F / 11F) * (2F / 11F)) * 8;
        }
        if (t < 10F / 11F) {
            float v = t - 8F / 11F - 1F / 11F;
            return 1 + (v * v - (1F / 11F) * (1F / 11F)) * 8;
        }
        float v = t - 10F / 11F - .5F / 11F;
        return 1 + (v * v - (.5F / 11F) * (.5F / 11F)) * 8;
    }

    static float spline3(float start, float middle, float end, boolean sameStart, boolean sameEnd, float rate, float t) {
        float eps = .000001F;
        if (t > rate) t = 1 + (t - rate) / Math.max(eps, 1 - rate);
        else t = t / Math.max(eps, rate);
        t = Math.max(0, Math.min(2, t));
        float c1 = (start - 2 * middle + end) * 3 / 4F;
        float d0 = c1 / 3;
        float b0 = middle - start - d0;
        float d1 = -c1 / 3;
        float b1 = end - middle - c1 - d1;
        int segment = (int) Math.floor(t);
        if (segment < 0) segment = 0;
        if (segment > 2) segment = 2;
        float dt = t - segment;
        if (segment == 0) {
            if (sameStart) return start;
            return start + (b0 + d0 * dt * dt) * dt;
        }
        if (segment == 1) {
            if (sameEnd) return middle;
            return middle + (b1 + (c1 + d1 * dt) * dt) * dt;
        }
        return end;
    }

    static final class Node {
        final String name;
        final int draw;
        final float[] life;
        final int maxGeneration;
        final boolean infinite;
        final float[] generationTime;
        final float[] generationOffset;
        final int bindTranslation;
        final int bindRotation;
        final int bindScale;
        final boolean removeWhenExtinct;
        final Motion location;
        final Motion rotation;
        final Motion scale;
        final Identifier texture;
        final int blend;
        final boolean linear;
        final boolean depthTest;
        final float[] uv;
        final Fade fadeIn;
        final Fade fadeOut;
        final int fadeOutType;
        final int colorInherit;
        final ColorSource color;
        final int billboard;
        final float[][] cornerColors;
        final float[][] positions;
        final Ring ring;
        final Node[] children;

        Node(JsonObject json) {
            name = json.get("name").getAsString();
            draw = json.get("draw").getAsInt();
            life = floats(json.get("life"));
            JsonObject gen = json.getAsJsonObject("gen");
            maxGeneration = gen.get("max").getAsInt();
            infinite = gen.get("inf").getAsBoolean();
            generationTime = floats(gen.get("time"));
            generationOffset = floats(gen.get("offset"));
            float[] bind = floats(json.get("bind"));
            bindTranslation = (int) bind[0];
            bindRotation = (int) bind[1];
            bindScale = (int) bind[2];
            removeWhenExtinct = json.get("removeExtinct").getAsBoolean();
            location = new Motion(json.getAsJsonObject("loc"), false);
            rotation = new Motion(json.getAsJsonObject("rot"), true);
            scale = new Motion(json.getAsJsonObject("scale"), false);
            if (json.has("tex") && !json.get("tex").isJsonNull()) texture = Identifier.parse(json.get("tex").getAsString());
            else texture = null;
            blend = json.get("blend").getAsInt();
            linear = json.get("linear").getAsBoolean();
            depthTest = json.get("ztest").getAsBoolean();
            if (json.has("uv")) uv = floats(json.get("uv"));
            else uv = new float[] { 0, 0, 1, 1 };
            if (json.has("fadeIn")) fadeIn = new Fade(json.getAsJsonObject("fadeIn"));
            else fadeIn = null;
            if (json.has("fadeOut")) {
                fadeOut = new Fade(json.getAsJsonObject("fadeOut"));
                fadeOutType = json.getAsJsonObject("fadeOut").get("type").getAsInt();
            } else {
                fadeOut = null;
                fadeOutType = 0;
            }
            colorInherit = json.get("colorInherit").getAsInt();
            if (json.has("color")) color = new ColorSource(json.getAsJsonObject("color"));
            else color = null;
            if (json.has("sprite")) {
                JsonObject sprite = json.getAsJsonObject("sprite");
                billboard = sprite.get("bb").getAsInt();
                cornerColors = matrix(sprite, "corners");
                positions = matrix(sprite, "pos");
            } else {
                billboard = BILLBOARD_FIXED;
                cornerColors = null;
                positions = null;
            }
            if (json.has("ring")) ring = new Ring(json.getAsJsonObject("ring"));
            else ring = null;
            children = nodes(json.getAsJsonArray("children"));
        }

        private static float[][] matrix(JsonObject json, String key) {
            if (!json.has(key)) return null;
            JsonArray array = json.getAsJsonArray(key);
            float[][] result = new float[array.size()][];
            for (int index = 0; index < result.length; index++) result[index] = floats(array.get(index));
            return result;
        }
    }

    static final class Motion {
        final int kind;
        final float[] fixed;
        final float[][] position;
        final float[][] velocity;
        final float[][] acceleration;
        final Easing easing;
        final Curve[] curves;

        Motion(JsonObject json, boolean angular) {
            String type = json.get("k").getAsString();
            float factor = 1;
            if (angular) factor = (float) (Math.PI / 180);
            float[] fixedValue = null;
            float[][] p = null;
            float[][] v = null;
            float[][] a = null;
            Easing e = null;
            Curve[] c = null;
            switch (type) {
                case "fixed" -> {
                    kind = MOTION_FIXED;
                    fixedValue = scaled(floats(json.get("v")), factor);
                }
                case "pva" -> {
                    kind = MOTION_PVA;
                    p = scaled(range(json.get("p")), factor);
                    v = scaled(range(json.get("v")), factor);
                    a = scaled(range(json.get("a")), factor);
                }
                case "single_pva" -> {
                    kind = MOTION_SINGLE_PVA;
                    p = range(json.get("p"));
                    v = range(json.get("v"));
                    a = range(json.get("a"));
                }
                case "easing" -> {
                    kind = MOTION_EASING;
                    e = new Easing(json, factor);
                }
                case "single_easing" -> {
                    kind = MOTION_SINGLE_EASING;
                    e = new Easing(json, 1);
                }
                case "fcurve" -> {
                    kind = MOTION_FCURVE;
                    JsonArray array = json.getAsJsonArray("c");
                    c = new Curve[array.size()];
                    for (int index = 0; index < c.length; index++) c[index] = new Curve(array.get(index).getAsJsonObject(), factor);
                }
                default -> throw new IllegalArgumentException("Unknown motion type " + type);
            }
            fixed = fixedValue;
            position = p;
            velocity = v;
            acceleration = a;
            easing = e;
            curves = c;
        }

        private static float[] scaled(float[] values, float factor) {
            for (int index = 0; index < values.length; index++) values[index] *= factor;
            return values;
        }

        private static float[][] scaled(float[][] values, float factor) {
            scaled(values[0], factor);
            scaled(values[1], factor);
            return values;
        }
    }

    static final class Easing {
        final float[][] start;
        final float[][] end;
        final float[][] middle;
        final int type;
        final float[] params;

        Easing(JsonObject json, float factor) {
            start = Motion.scaled(range(json.get("s")), factor);
            end = Motion.scaled(range(json.get("e")), factor);
            if (json.has("m")) middle = Motion.scaled(range(json.get("m")), factor);
            else middle = null;
            if (json.has("t")) type = json.get("t").getAsInt();
            else type = 0;
            params = floats(json.get("p"));
        }
    }

    static final class Curve {
        final int startEdge;
        final int endEdge;
        final float[] offset;
        final int first;
        final int length;
        final int frequency;
        final float[] values;

        Curve(JsonObject json, float factor) {
            startEdge = json.get("start").getAsInt();
            endEdge = json.get("end").getAsInt();
            offset = Motion.scaled(floats(json.get("off")), factor);
            first = json.get("first").getAsInt();
            length = json.get("len").getAsInt();
            frequency = Math.max(1, json.get("freq").getAsInt());
            values = Motion.scaled(floats(json.get("v")), factor);
        }

        float evaluate(float living) {
            if (values.length == 0) return 0;
            if (length == 0) return values[0];
            float frame = living - first;
            float span = length;
            if (frame < 0) {
                if (startEdge == 0) return values[0];
                if (startEdge == 1) frame = span - (-frame % span);
                else frame = -frame % span;
            }
            if (span < frame) {
                if (endEdge == 0) return values[values.length - 1];
                if (endEdge == 1) frame = (frame - span) % span;
                else frame = span - (frame - span) % span;
            }
            if (Math.abs(frame - span) < .0001F) return values[values.length - 1];
            int index = (int) (frame / frequency);
            if (index >= values.length - 1) return values[values.length - 1];
            float step = frequency;
            if (index == values.length - 2) step = length - index * frequency;
            return values[index] + (values[index + 1] - values[index]) / step * (frame - index * frequency);
        }
    }

    static final class Fade {
        final float frames;
        final float[] params;

        Fade(JsonObject json) {
            frames = json.get("f").getAsFloat();
            params = floats(json.get("p"));
        }
    }

    static final class ColorSource {
        final int kind;
        final float[] fixed;
        final float[][] random;
        final float[][] start;
        final float[][] end;
        final float[] params;

        ColorSource(JsonObject json) {
            kind = json.get("k").getAsInt();
            if (json.has("c")) fixed = floats(json.get("c"));
            else fixed = null;
            if (json.has("r")) random = range(json.get("r"));
            else random = null;
            if (json.has("s")) start = range(json.get("s"));
            else start = null;
            if (json.has("e")) end = range(json.get("e"));
            else end = null;
            if (json.has("p")) params = floats(json.get("p"));
            else params = null;
        }
    }

    static final class RingValue {
        final int kind;
        final float[] fixed;
        final float[][] position;
        final float[][] velocity;
        final float[][] acceleration;
        final float[][] start;
        final float[][] end;
        final float[] params;

        RingValue(JsonObject json) {
            kind = json.get("k").getAsInt();
            float[] f = null;
            float[][] p = null;
            float[][] v = null;
            float[][] a = null;
            float[][] s = null;
            float[][] e = null;
            float[] pr = null;
            if (kind == 0) {
                JsonElement value = json.get("v");
                if (value.isJsonArray()) f = floats(value);
                else f = new float[] { value.getAsFloat() };
            } else if (kind == 1) {
                if (json.has("r")) {
                    p = range(json.get("r"));
                    v = new float[][] { { 0 }, { 0 } };
                    a = new float[][] { { 0 }, { 0 } };
                } else {
                    p = range(json.get("p"));
                    v = range(json.get("v"));
                    a = range(json.get("a"));
                }
            } else {
                s = range(json.get("s"));
                e = range(json.get("e"));
                pr = floats(json.get("p"));
            }
            fixed = f;
            position = p;
            velocity = v;
            acceleration = a;
            start = s;
            end = e;
            params = pr;
        }
    }

    static final class Ring {
        final int billboard;
        final int vertexCount;
        final float startAngle;
        final float endAngle;
        final float startFade;
        final float endFade;
        final RingValue outer;
        final RingValue inner;
        final RingValue ratio;
        final ColorSource outerColor;
        final ColorSource centerColor;
        final ColorSource innerColor;

        Ring(JsonObject json) {
            billboard = json.get("bb").getAsInt();
            vertexCount = Math.max(3, json.get("n").getAsInt());
            startAngle = json.get("sa").getAsFloat();
            endAngle = json.get("ea").getAsFloat();
            startFade = json.get("sf").getAsFloat();
            endFade = json.get("ef").getAsFloat();
            outer = new RingValue(json.getAsJsonObject("outer"));
            inner = new RingValue(json.getAsJsonObject("inner"));
            ratio = new RingValue(json.getAsJsonObject("ratio"));
            outerColor = new ColorSource(json.getAsJsonObject("oc"));
            centerColor = new ColorSource(json.getAsJsonObject("cc"));
            innerColor = new ColorSource(json.getAsJsonObject("ic"));
        }
    }
}
