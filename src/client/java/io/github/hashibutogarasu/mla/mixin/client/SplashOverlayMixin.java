package io.github.hashibutogarasu.mla.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.hashibutogarasu.mla.MojangLogoAnimationClient;
import io.github.hashibutogarasu.mla.config.ModConfig;
import io.github.hashibutogarasu.mla.sounds.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

import static net.minecraft.client.render.RenderPhase.*;

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin {
    @Unique
    private static final Transparency LOGO_TRANSPARENCY = new Transparency(
            "mojang_logo_transparency",
            RenderSystem::enableBlend,
            RenderSystem::disableBlend
    );

    @Unique private static boolean firstLoad = true;

    @Shadow @Final private ResourceReload reload;
    @Shadow private float progress;

    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private boolean reloading;

    @Unique private boolean animationStarting = false;
    @Unique private boolean animationEnded = false;
    @Unique private int animProgress = 0;

    @Unique
    private final PositionedSoundInstance sound =
            PositionedSoundInstance.master(
                    mode == ModConfig.Mode.MOJANG_STUDIOS ?
                            ModSounds.MOJANG_LOGO_SOUND_EVENT :
                            ModSounds.MOJANG_APRIL_FOOL_SOUND_EVENT,
                    1.0f, 1.0f
            );

    @Unique private static ModConfig.Mode mode;

    @Inject(method = "init", at = @At(value = "RETURN"))
    private static void init(CallbackInfo ci) {
        mode = MojangLogoAnimationClient.config.mode;
    }

    @Redirect(method = "render", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/screen/SplashOverlay;LOGO:Lnet/minecraft/util/Identifier;"
    ))
    private Identifier logo() {
        return mode == ModConfig.Mode.MOJANG_STUDIOS ? getMojangFrame(this.animProgress) : getSharewareFrame(this.animProgress);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceReload;getProgress()F"))
    private float getProgress(ResourceReload instance) {
        return this.reload.getProgress();
    }

    @Inject(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;fill(Lnet/minecraft/client/render/RenderLayer;IIIII)V"
    ))
    private void fill(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {

    }

    @Redirect(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIFFIIIIIII)V",
            ordinal = 0
    ))
    private void drawTexture0(
            DrawContext context,
            Function<Identifier, RenderLayer> renderLayers,
            Identifier sprite,
            int x, int y,
            float u, float v,
            int width, int height,
            int regionWidth, int regionHeight,
            int textureWidth, int textureHeight,
            int color
    ) {
        var d = Math.min((double) context.getScaledWindowWidth() * 0.75, context.getScaledWindowHeight()) * 0.25;
        var e = d * 4.0;
        var r = (int) e;

        if (this.progress > 0) {
            if (!this.animationStarting && firstLoad) {
                this.reload.whenComplete().thenAccept(object -> {
                    this.client.getSoundManager().play(this.sound);
                    this.getAnimationThread().start();
                });

                this.animationStarting = true;
            }
        }

        var frameIdentifier = mode == ModConfig.Mode.MOJANG_STUDIOS ?
                this.getMojangFrame(this.animProgress) :
                this.getSharewareFrame(this.animProgress);

        var frame = RenderLayer.of(
                "animated_mojang_logo",
                VertexFormats.POSITION_TEXTURE_COLOR,
                VertexFormat.DrawMode.QUADS,
                786432,
                RenderLayer.MultiPhaseParameters.builder()
                        .texture(new RenderPhase.Texture(
                                frameIdentifier,
                                TriState.DEFAULT, false
                        ))
                        .program(RenderPhase.POSITION_TEXTURE_COLOR_PROGRAM)
                        .transparency(LOGO_TRANSPARENCY)
                        .depthTest(ALWAYS_DEPTH_TEST)
                        .writeMaskState(COLOR_MASK)
                        .build(false)
        );

        context.drawTexture(
                identifier -> frame,
                frameIdentifier,
                x, y,
                u, v,
                r, (int) d,
                regionWidth, regionHeight + 60,
                textureWidth, textureHeight
        );
    }

    @Redirect(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIFFIIIIIII)V",
            ordinal = 1
    ))
    private void drawTexture1(DrawContext instance, Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, float u, float v, int width, int height, int regionWidth, int regionHeight, int textureWidth, int textureHeight, int color) {
        // Do nothing.
        // We drop this call.
    }

    @Redirect(method = "render", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/screen/SplashOverlay;reloading:Z",
            opcode = Opcodes.GETFIELD,
            ordinal = 2
    ))
    private boolean isReloading(SplashOverlay instance) {
         return (firstLoad && !this.animationEnded) || this.reloading;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init(Lnet/minecraft/client/MinecraftClient;II)V"), method = "render")
    private void init(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
          firstLoad = false;
    }

    @Unique
    private Identifier getMojangFrame(int index){
        return firstLoad ?
                Identifier.of("mla", "textures/gui/title/mojang/mojang" + index + ".png") :
                Identifier.of("mla", "textures/gui/title/mojang/mojang38.png");
    }

    @Unique
    private Identifier getSharewareFrame(int index){
        return firstLoad ?
                Identifier.of("mla", "textures/gui/title/mojang_april_fool/mojang" + index + ".png") :
                Identifier.of("mla", "textures/gui/title/mojang/mojang38.png");
    }

    @Unique
    private Thread getAnimationThread() {
        return new Thread(() -> {
            this.animProgress = 0;
            this.animationEnded = false;

            for (var i = 0; i < 38; i++) {
                this.animProgress++;

                try {
                    Thread.sleep(70);
                } catch (InterruptedException ignored) { }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) { }

            this.animationEnded = true;
        }, "animthread");
    }
}
