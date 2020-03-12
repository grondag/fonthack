package grondag.fonthack.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.font.GlyphAtlasTexture;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.util.Identifier;

import grondag.fonthack.font.FontTextureHelper;

@Mixin(GlyphAtlasTexture.class)
public abstract class MixinGlyphAtlasTexture extends AbstractTexture {
	@Shadow private boolean hasColor;
	@Shadow private Identifier id;

	// TODO: will probably need to use custom render layers to get anti-aliasing
	@Shadow private RenderLayer field_21690;
	@Shadow private RenderLayer field_21691;

	private boolean isNice = false;
	private int size = 256;
	private int cellHeight = 16;

	private int xNext = 0;
	private int yNext = 0;

	@Redirect(method = "<init>*", require = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/TextureUtil;method_24960(Lnet/minecraft/client/texture/NativeImage$GLFormat;III)V"))
	private void hookPrepareImage(NativeImage.GLFormat format, int glId, int w, int h) {
		if(FontTextureHelper.isNice) {
			TextureUtil.method_24961(NativeImage.GLFormat.RGBA, glId, FontTextureHelper.lod, FontTextureHelper.size, FontTextureHelper.size);
			isNice = true;
			size = FontTextureHelper.size;
			cellHeight = FontTextureHelper.ceil16(FontTextureHelper.cellHeight);
			setFilter(false, true);
		} else {
			TextureUtil.method_24960(format, glId, w, h);
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
		final int p = FontTextureHelper.padding;
		final int w = FontTextureHelper.ceil16(glyph.getWidth() + p * 2);

		if (xNext + w > size) {
			if (yNext + cellHeight > size) {
				return null;
			}
			yNext += cellHeight;
			xNext = 0;
		}

		final int x = xNext;
		final int y = yNext;
		xNext += w;

		glyph.upload(x, y);

		return new GlyphRenderer(field_21690, field_21691,
				(p + x + 0.01F) / size, (p + x - 0.01F + glyph.getWidth()) / size,
				(p + y + 0.01F) / size, (p + y - 0.01F + glyph.getHeight()) / size,
				glyph.getXMin(), glyph.getXMax(), glyph.getYMin(), glyph.getYMax());
	}
}
