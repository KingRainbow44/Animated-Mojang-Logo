package io.github.hashibutogarasu.mla.sounds;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public interface ModSounds {
    Identifier MOJANG_LOGO_SOUND = Identifier.of("mla", "mojang_sound");
    SoundEvent MOJANG_LOGO_SOUND_EVENT = SoundEvent.of(MOJANG_LOGO_SOUND);

    Identifier MOJANG_APRIL_FOOL_SOUND = Identifier.of("mla", "mojang_april_fool_sound");
    SoundEvent MOJANG_APRIL_FOOL_SOUND_EVENT = SoundEvent.of(MOJANG_APRIL_FOOL_SOUND);
}
