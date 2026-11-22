package com.hp.thermalshockreforged.mixin;

import com.hp.thermalshockreforged.ThermalShockReforged;
import com.hp.thermalshockreforged.content.atlas.AtlasContent;
import com.hp.thermalshockreforged.world.LoqendiaContent;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.MoonPhase;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRenderer.class)
public abstract class DimensionSkyTexturesMixin {
    @Unique private static final Identifier THERMAL_SHOCK_REFORGED_LOQUE_SUN = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/block/loque_sun.png");
    @Unique private static final Identifier THERMAL_SHOCK_REFORGED_AQERON_PHASES = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/block/aqeron_phases.png");
    @Unique private static final Identifier THERMAL_SHOCK_REFORGED_LOQENDIA_PHASES = Identifier.fromNamespaceAndPath(ThermalShockReforged.MOD_ID, "textures/block/loqendia_phases.png");
    @Unique private GpuBuffer thermalShockReforged$sunQuad;
    @Unique private GpuBuffer thermalShockReforged$moonPhases;
    @Unique private AbstractTexture thermalShockReforged$sunAsset;
    @Unique private AbstractTexture thermalShockReforged$moonAsset;

    @Unique
    private static int thermalShockReforged$dimension() {
        var level = Minecraft.getInstance().level;
        if (level == null) return 0;
        if (level.dimension().equals(LoqendiaContent.DIMENSION)) return 1;
        return level.dimension().equals(AtlasContent.PALACE) ? 2 : 0;
    }

    @Unique
    private static void thermalShockReforged$bind(RenderPass pass, String name, AbstractTexture texture) {
        pass.bindTexture(name, texture.getTextureView(), texture.getSampler());
    }

    @Inject(method = "renderSun", at = @At("HEAD"))
    private void thermalShockReforged$prepareSun(float rainBrightness, PoseStack poseStack, CallbackInfo callback) {
        if (thermalShockReforged$dimension() == 0) return;
        thermalShockReforged$sunAsset = Minecraft.getInstance().getTextureManager().getTexture(THERMAL_SHOCK_REFORGED_LOQUE_SUN);
        thermalShockReforged$prepareSunQuad();
    }

    @Inject(method = "renderMoon", at = @At("HEAD"))
    private void thermalShockReforged$prepareMoon(MoonPhase phase, float rainBrightness, PoseStack poseStack, CallbackInfo callback) {
        int dimension = thermalShockReforged$dimension();
        if (dimension == 0) return;
        thermalShockReforged$moonAsset = Minecraft.getInstance().getTextureManager().getTexture(dimension == 1 ? THERMAL_SHOCK_REFORGED_AQERON_PHASES : THERMAL_SHOCK_REFORGED_LOQENDIA_PHASES);
        thermalShockReforged$prepareMoonPhases();
    }

    @WrapOperation(method = "renderSun", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;bindTexture(Ljava/lang/String;Lcom/mojang/blaze3d/textures/GpuTextureView;Lcom/mojang/blaze3d/textures/GpuSampler;)V"))
    private void thermalShockReforged$sunTexture(RenderPass pass, String name, GpuTextureView view, GpuSampler sampler, Operation<Void> original) {
        if (thermalShockReforged$dimension() == 0) original.call(pass, name, view, sampler);
        else original.call(pass, name, thermalShockReforged$sunAsset.getTextureView(), thermalShockReforged$sunAsset.getSampler());
    }

    @WrapOperation(method = "renderSun", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;setVertexBuffer(ILcom/mojang/blaze3d/buffers/GpuBuffer;)V"))
    private void thermalShockReforged$sunQuad(RenderPass pass, int slot, GpuBuffer buffer, Operation<Void> original) {
        original.call(pass, slot, thermalShockReforged$dimension() == 0 ? buffer : thermalShockReforged$sunQuad);
    }

    @Unique
    private void thermalShockReforged$prepareSunQuad() {
        if (thermalShockReforged$sunQuad == null) {
            VertexFormat format = DefaultVertexFormat.POSITION_TEX;
            try (ByteBufferBuilder bytes = ByteBufferBuilder.exactlySized(4 * format.getVertexSize())) {
                BufferBuilder builder = new BufferBuilder(bytes, VertexFormat.Mode.QUADS, format);
                builder.addVertex(-1.0F, 0.0F, -1.0F).setUv(0.0F, 0.0F);
                builder.addVertex(1.0F, 0.0F, -1.0F).setUv(1.0F, 0.0F);
                builder.addVertex(1.0F, 0.0F, 1.0F).setUv(1.0F, 1.0F);
                builder.addVertex(-1.0F, 0.0F, 1.0F).setUv(0.0F, 1.0F);
                try (MeshData mesh = builder.buildOrThrow()) {
                    thermalShockReforged$sunQuad = RenderSystem.getDevice().createBuffer(() -> "Thermal Shock dimension sun quad", 32, mesh.vertexBuffer());
                    ThermalShockReforged.LOGGER.debug("Prepared TSR dimension sun texture and quad before opening the sky render pass");
                }
            }
        }
    }

    @WrapOperation(method = "renderMoon", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;bindTexture(Ljava/lang/String;Lcom/mojang/blaze3d/textures/GpuTextureView;Lcom/mojang/blaze3d/textures/GpuSampler;)V"))
    private void thermalShockReforged$moonTexture(RenderPass pass, String name, GpuTextureView view, GpuSampler sampler, Operation<Void> original) {
        if (thermalShockReforged$dimension() == 0) original.call(pass, name, view, sampler);
        else original.call(pass, name, thermalShockReforged$moonAsset.getTextureView(), thermalShockReforged$moonAsset.getSampler());
    }

    @WrapOperation(method = "renderMoon", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;setVertexBuffer(ILcom/mojang/blaze3d/buffers/GpuBuffer;)V"))
    private void thermalShockReforged$moonPhases(RenderPass pass, int slot, GpuBuffer buffer, Operation<Void> original) {
        original.call(pass, slot, thermalShockReforged$dimension() == 0 ? buffer : thermalShockReforged$moonPhases);
    }

    @Unique
    private void thermalShockReforged$prepareMoonPhases() {
        if (thermalShockReforged$moonPhases == null) {
            VertexFormat format = DefaultVertexFormat.POSITION_TEX;
            try (ByteBufferBuilder bytes = ByteBufferBuilder.exactlySized(8 * 4 * format.getVertexSize())) {
                BufferBuilder builder = new BufferBuilder(bytes, VertexFormat.Mode.QUADS, format);
                for (int phase = 0; phase < 8; phase++) {
                    float u0 = (phase % 4) / 4.0F;
                    float v0 = (phase / 4 % 2) / 2.0F;
                    float u1 = (phase % 4 + 1) / 4.0F;
                    float v1 = (phase / 4 % 2 + 1) / 2.0F;
                    builder.addVertex(-1.0F, 0.0F, -1.0F).setUv(u1, v1);
                    builder.addVertex(1.0F, 0.0F, -1.0F).setUv(u0, v1);
                    builder.addVertex(1.0F, 0.0F, 1.0F).setUv(u0, v0);
                    builder.addVertex(-1.0F, 0.0F, 1.0F).setUv(u1, v0);
                }
                try (MeshData mesh = builder.buildOrThrow()) {
                    thermalShockReforged$moonPhases = RenderSystem.getDevice().createBuffer(() -> "Thermal Shock dimension moon phases", 32, mesh.vertexBuffer());
                    ThermalShockReforged.LOGGER.debug("Prepared TSR dimension moon texture and phase quads before opening the sky render pass");
                }
            }
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void thermalShockReforged$closeSkyBuffers(CallbackInfo callback) {
        if (thermalShockReforged$sunQuad != null) {
            thermalShockReforged$sunQuad.close();
            thermalShockReforged$sunQuad = null;
        }
        if (thermalShockReforged$moonPhases != null) {
            thermalShockReforged$moonPhases.close();
            thermalShockReforged$moonPhases = null;
        }
        thermalShockReforged$sunAsset = null;
        thermalShockReforged$moonAsset = null;
    }
}
