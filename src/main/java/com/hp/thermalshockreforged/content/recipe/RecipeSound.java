package com.hp.thermalshockreforged.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public record RecipeSound(Holder<SoundEvent> sound, float volume, float pitch) {
    public static final Codec<RecipeSound> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.SOUND_EVENT.holderByNameCodec().fieldOf("sound").forGetter(RecipeSound::sound),
            Codec.FLOAT.optionalFieldOf("volume", 1F).forGetter(RecipeSound::volume),
            Codec.FLOAT.optionalFieldOf("pitch", 1F).forGetter(RecipeSound::pitch)
    ).apply(instance, RecipeSound::new));

    public void play(ServerLevel level, BlockPos pos, SoundSource source) {
        level.playSound(null, pos, sound.value(), source, volume, pitch);
    }
}
