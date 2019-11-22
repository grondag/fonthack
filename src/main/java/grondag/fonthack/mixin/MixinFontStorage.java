package grondag.fonthack.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import grondag.fonthack.font.FontTextureHelper;
import grondag.fonthack.font.NiceFont.NiceGlyph;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;

@Mixin(FontStorage.class)
public class MixinFontStorage {
	@Inject(method = "Lnet/minecraft/client/font/FontStorage;getGlyphRenderer(Lnet/minecraft/client/font/RenderableGlyph;)Lnet/minecraft/client/font/GlyphRenderer;", at = @At("HEAD"), cancellable = false, require = 1)
	private void onGetGlyphRenderer(RenderableGlyph glyph, CallbackInfoReturnable<GlyphRenderer> ci) {
		FontTextureHelper.isNice = glyph instanceof NiceGlyph;
	}
}
