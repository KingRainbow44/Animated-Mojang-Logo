package io.github.hashibutogarasu.mla;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;

public interface Utils {
    static void playSound(SoundEvent event){
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(event, 1.0f, 1.0f));
    }
}
