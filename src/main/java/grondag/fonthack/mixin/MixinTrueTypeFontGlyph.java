package grondag.fonthack.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import grondag.fonthack.TrueTypeGlyphExt;

@Mixin(targets = "net.minecraft.client.font.TrueTypeFont$TtfGlyph")
public abstract class MixinTrueTypeFontGlyph implements TrueTypeGlyphExt {
	@Shadow private int width;
	@Shadow private int height;
	@Shadow private float bearingX;
	@Shadow private float ascent;
	@Shadow private float advance;
	@Shadow private int glyphIndex;

	@Override
	public int ext_width() {
		return width;
	}

	@Override
	public int ext_height() {
		return height;
	}

	@Override
	public float ext_bearingX() {
		return bearingX;
	}

	@Override
	public float ext_ascent() {
		return ascent;
	}

	@Override
	public float ext_advance() {
		return advance;
	}

	@Override
	public int ext_glyphIndex() {
		return glyphIndex;
	}
}
