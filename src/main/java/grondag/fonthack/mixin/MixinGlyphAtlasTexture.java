package grondag.fonthack.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.platform.TextureUtil;

import grondag.fonthack.font.FontTextureHelper;
import net.minecraft.client.font.GlyphAtlasTexture;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

@Mixin(GlyphAtlasTexture.class)
public abstract class MixinGlyphAtlasTexture extends AbstractTexture {
	@Shadow private boolean hasColor;
	@Shadow private Identifier id;

	private boolean isNice = false;
	private int size = 256;
	private int fontHeight = 16;

	private int xNext = 0;
	private int yNext = 0;

	@Redirect(method = "<init>*", require = 1, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/TextureUtil;prepareImage(Lnet/minecraft/client/texture/NativeImage$GLFormat;III)V"))
	private void hookPrepareImage(NativeImage.GLFormat format, int glId, int w, int h) {
		if (FontTextureHelper.itMe) {
			TextureUtil.prepareImage(format, glId, 0, FontTextureHelper.size, FontTextureHelper.size);
			isNice = true;
			size = FontTextureHelper.size;
			fontHeight = FontTextureHelper.ceil16(FontTextureHelper.fontHeight);
		} else {
			TextureUtil.prepareImage(format, glId, w, h);
			isNice = false;
		}
	}

	@Inject(method = "getGlyphRenderer", at = @At("HEAD"), cancellable = true, require = 1)
	private void onGetGlyphRenderer(RenderableGlyph glyph, CallbackInfoReturnable<GlyphRenderer> ci) {
		if (isNice && glyph.hasColor() == hasColor) {
			ci.setReturnValue(getNiceGlyphRenderer(glyph));
		}
	}

	private GlyphRenderer getNiceGlyphRenderer(RenderableGlyph glyph) {
		bindTexture();
		final int w = FontTextureHelper.ceil16(glyph.getWidth());

		if (xNext + w > size) {
			if (yNext + fontHeight > size) {
				return null;
			}
			yNext += fontHeight;
			xNext = 0;
		}

		final int x = xNext;
		final int y = yNext;
		xNext += w;

		// TODO: remove
		System.out.println(String.format("x=%d  y=%d  w=%d  gh=%d", x, y, w, glyph.getHeight()));

		glyph.upload(x, y);

		return new GlyphRenderer(id,
			(x + 0.01F) / size, (x - 0.01F + glyph.getWidth()) / size,
			(y + 0.01F) / size, (y - 0.01F + glyph.getHeight()) / size,
			glyph.getXMin(), glyph.getXMax(), glyph.getYMin(), glyph.getYMax());
	}
}
