package com.hp.thermalshockreforged.client.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.multiplayer.ClientLevel;

public abstract class LegacySingleQuadParticle extends SingleQuadParticle {
    private static TextureAtlasSprite fallbackSprite() {
        return ((TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_PARTICLES)).missingSprite();
    }
    protected LegacySingleQuadParticle(ClientLevel level, double x, double y, double z) { super(level, x, y, z, fallbackSprite()); }
    protected LegacySingleQuadParticle(ClientLevel level, double x, double y, double z, double xa, double ya, double za) { super(level, x, y, z, xa, ya, za, fallbackSprite()); }
    protected void pickSprite(SpriteSet sprites) { setSprite(sprites.get(0, 1)); }
    public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
    @Override protected Layer getLayer() { return Layer.bySprite(sprite); }
}
