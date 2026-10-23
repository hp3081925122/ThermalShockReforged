package com.hp.thermalshockreforged.client.vfx;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.hp.thermalshockreforged.client.vfx.VfxDefinition.Node;
import com.hp.thermalshockreforged.client.vfx.VfxDefinition.Ring;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL14;

final class VfxBatch {
    private static final float[][] DEFAULT_POSITIONS = { { -.5F, -.5F }, { .5F, -.5F }, { -.5F, .5F }, { .5F, .5F } };
    private static final float[] WHITE = { 255, 255, 255, 255 };

    private final Vector3f front = new Vector3f();
    private float offsetX;
    private float offsetY;
    private float offsetZ;
    private BufferBuilder builder;
    private Identifier texture;
    private int blend = -1;
    private boolean linear;
    private boolean depthTest;

    void begin(Vector3f cameraLook) {
        front.set(cameraLook).negate().normalize();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.depthFunc(515);
    }

    void origin(double x, double y, double z) {
        offsetX = (float) x;
        offsetY = (float) y;
        offsetZ = (float) z;
    }

    void end() {
        flush();
        RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        texture = null;
        blend = -1;
    }

    private void state(Node node) {
        if (builder != null && node.texture.equals(texture) && node.blend == blend && node.linear == linear && node.depthTest == depthTest) return;
        flush();
        texture = node.texture;
        blend = node.blend;
        linear = node.linear;
        depthTest = node.depthTest;
        Minecraft.getInstance().getTextureManager().getTexture(texture).setFilter(linear, false);
        RenderSystem.setShaderTexture(0, texture);
        if (depthTest) RenderSystem.enableDepthTest();
        else RenderSystem.disableDepthTest();
        RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
        switch (blend) {
            case 0 -> RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            case 2 -> RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            case 3 -> {
                RenderSystem.blendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            }
            case 4 -> RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            default -> RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }
        builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
    }

    private void flush() {
        if (builder == null) return;
        MeshData mesh = builder.build();
        builder = null;
        if (mesh != null) net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture).draw(mesh);
    }

    private void vertex(Vector3f position, float u, float v, float[] color, float[] tint) {
        builder.addVertex(position.x + offsetX, position.y + offsetY, position.z + offsetZ).setUv(u, v)
                .setColor(channel(color[0] * tint[0]), channel(color[1] * tint[1]), channel(color[2] * tint[2]), channel(color[3] * tint[3]));
    }

    private static int channel(float value) { return Math.max(0, Math.min(255, Math.round(value / 255F))); }

    void sprite(Node node, Matrix4f matrix, float[] color) {
        if (color[3] <= 0) return;
        state(node);
        float[][] positions = node.positions;
        if (positions == null) positions = DEFAULT_POSITIONS;
        float u = node.uv[0];
        float v = node.uv[1];
        float w = node.uv[2];
        float h = node.uv[3];
        float[][] uvs = { { u, v + h }, { u + w, v + h }, { u, v }, { u + w, v } };
        Vector3f[] corners = new Vector3f[4];
        if (node.billboard == VfxDefinition.BILLBOARD_FIXED) {
            for (int index = 0; index < 4; index++) corners[index] = matrix.transformPosition(positions[index][0], positions[index][1], 0, new Vector3f());
        } else {
            Vector3f[] axes = billboard(node.billboard, matrix);
            Vector3f translation = matrix.getTranslation(new Vector3f());
            Vector3f scale = matrix.getScale(new Vector3f());
            for (int index = 0; index < 4; index++) {
                corners[index] = new Vector3f(translation)
                        .add(new Vector3f(axes[0]).mul(positions[index][0] * scale.x))
                        .add(new Vector3f(axes[1]).mul(positions[index][1] * scale.y));
            }
        }
        int[] order = { 0, 1, 3, 2 };
        for (int index : order) {
            float[] tint = WHITE;
            if (node.cornerColors != null) tint = node.cornerColors[index];
            vertex(corners[index], uvs[index][0], uvs[index][1], color, tint);
        }
    }

    private Vector3f[] billboard(int type, Matrix4f matrix) {
        Vector3f column0 = matrix.getColumn(0, new Vector3f());
        Vector3f column1 = matrix.getColumn(1, new Vector3f());
        Vector3f column2 = matrix.getColumn(2, new Vector3f());
        normalize(column0);
        normalize(column1);
        normalize(column2);
        Vector3f up = new Vector3f(0, 1, 0);
        Vector3f right;
        Vector3f upAxis;
        if (type == VfxDefinition.BILLBOARD_Y_AXIS) {
            upAxis = new Vector3f(column1);
            right = normalize(new Vector3f(upAxis).cross(front));
            return new Vector3f[] { right, upAxis };
        }
        right = normalize(new Vector3f(up).cross(front));
        upAxis = normalize(new Vector3f(front).cross(right));
        if (type == VfxDefinition.BILLBOARD_ROTATED) {
            float r10 = column0.y;
            float r11 = column1.y;
            float length = (float) Math.sqrt(Math.max(0, r10 * r10 + r11 * r11));
            float sine = 0;
            float cosine = 1;
            if (Math.abs(length) > .001F) {
                sine = r10 / length;
                cosine = r11 / length;
            }
            Vector3f rotatedRight = new Vector3f(right).mul(cosine).add(new Vector3f(upAxis).mul(sine));
            Vector3f rotatedUp = new Vector3f(upAxis).mul(cosine).sub(new Vector3f(right).mul(sine));
            return new Vector3f[] { rotatedRight, rotatedUp };
        }
        return new Vector3f[] { right, upAxis };
    }

    private static Vector3f normalize(Vector3f vector) {
        if (vector.lengthSquared() > 1.0E-12F) vector.normalize();
        return vector;
    }

    void ring(Node node, Matrix4f matrix, float[] values, float[] outerColor, float[] centerColor, float[] innerColor) {
        if (outerColor[3] <= 0 && centerColor[3] <= 0 && innerColor[3] <= 0) return;
        state(node);
        Ring ring = node.ring;
        Matrix4f transform = matrix;
        if (ring.billboard != VfxDefinition.BILLBOARD_FIXED) {
            Vector3f[] axes = billboard(ring.billboard, matrix);
            Vector3f translation = matrix.getTranslation(new Vector3f());
            Vector3f scale = matrix.getScale(new Vector3f());
            Vector3f forward = normalize(new Vector3f(axes[0]).cross(axes[1]));
            transform = new Matrix4f(axes[0].x, axes[0].y, axes[0].z, 0, axes[1].x, axes[1].y, axes[1].z, 0, forward.x, forward.y, forward.z, 0, translation.x, translation.y, translation.z, 1)
                    .scale(scale);
        }
        float outerRadius = values[0];
        float outerHeight = values[1];
        float innerRadius = values[2];
        float innerHeight = values[3];
        float ratio = values[4];
        float centerRadius = innerRadius + (outerRadius - innerRadius) * ratio;
        float centerHeight = innerHeight + (outerHeight - innerHeight) * ratio;
        int count = ring.vertexCount;
        float circle = ring.endAngle - ring.startAngle;
        float stepDegree = circle / count;
        double begin = Math.toRadians(ring.startAngle + 90);
        double step = Math.toRadians(stepDegree);
        float u0 = node.uv[0];
        float uStep = node.uv[2] / count;
        float v1 = node.uv[1];
        float v2 = v1 + node.uv[3] * .5F;
        float v3 = v1 + node.uv[3];
        float[] outerCurrent = outerColor.clone();
        float[] centerCurrent = centerColor.clone();
        float[] innerCurrent = innerColor.clone();
        if (ring.startFade > 0) {
            outerCurrent[3] = 0;
            centerCurrent[3] = 0;
            innerCurrent[3] = 0;
        }
        float currentDegree = 0;
        float fadeStart = Math.max(.0001F, ring.startFade);
        float fadeEnd = Math.max(.0001F, ring.endFade);
        for (int index = 0; index < count; index++) {
            double angle = begin + step * index;
            double next = angle + step;
            currentDegree = Math.min(currentDegree + stepDegree, circle);
            float alpha = 1;
            if (currentDegree < fadeStart) alpha = currentDegree / fadeStart;
            else if (currentDegree > circle - fadeEnd) alpha = 1 - (currentDegree - (circle - fadeEnd)) / fadeEnd;
            alpha = Math.max(0, Math.min(1, alpha));
            float[] outerNext = outerColor.clone();
            float[] centerNext = centerColor.clone();
            float[] innerNext = innerColor.clone();
            outerNext[3] = (float) Math.floor(outerNext[3] * alpha);
            centerNext[3] = (float) Math.floor(centerNext[3] * alpha);
            innerNext[3] = (float) Math.floor(innerNext[3] * alpha);
            Vector3f outerA = point(transform, angle, outerRadius, outerHeight);
            Vector3f outerB = point(transform, next, outerRadius, outerHeight);
            Vector3f centerA = point(transform, angle, centerRadius, centerHeight);
            Vector3f centerB = point(transform, next, centerRadius, centerHeight);
            Vector3f innerA = point(transform, angle, innerRadius, innerHeight);
            Vector3f innerB = point(transform, next, innerRadius, innerHeight);
            float ua = u0 + uStep * index;
            float ub = ua + uStep;
            vertex(outerA, ua, v1, outerCurrent, WHITE);
            vertex(outerB, ub, v1, outerNext, WHITE);
            vertex(centerB, ub, v2, centerNext, WHITE);
            vertex(centerA, ua, v2, centerCurrent, WHITE);
            vertex(centerA, ua, v2, centerCurrent, WHITE);
            vertex(centerB, ub, v2, centerNext, WHITE);
            vertex(innerB, ub, v3, innerNext, WHITE);
            vertex(innerA, ua, v3, innerCurrent, WHITE);
            outerCurrent = outerNext;
            centerCurrent = centerNext;
            innerCurrent = innerNext;
        }
    }

    private static Vector3f point(Matrix4f transform, double angle, float radius, float height) {
        return transform.transformPosition((float) Math.cos(angle) * radius, (float) Math.sin(angle) * radius, height, new Vector3f());
    }
}
