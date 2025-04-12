package io.github.hashibutogarasu.mla;

import io.github.hashibutogarasu.mla.sounds.ModSounds;
import net.fabricmc.api.ModInitializer;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MojangLogoAnimation implements ModInitializer {
	public static String MOD_ID = "mla";
	public static final Logger LOGGER = LoggerFactory.getLogger("mla");

	@Override
	public void onInitialize() {
		Registry.register(Registries.SOUND_EVENT, ModSounds.MOJANG_LOGO_SOUND, ModSounds.MOJANG_LOGO_SOUND_EVENT);
		Registry.register(Registries.SOUND_EVENT, ModSounds.MOJANG_APRIL_FOOL_SOUND, ModSounds.MOJANG_APRIL_FOOL_SOUND_EVENT);
	}
}