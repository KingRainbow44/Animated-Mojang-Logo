package io.github.hashibutogarasu.mla.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "mla")
public final class ModConfig implements ConfigData {

    public enum Mode {
        APRIL_FOOL,
        MOJANG_STUDIOS;
    }

    public Mode mode = Mode.MOJANG_STUDIOS;
}