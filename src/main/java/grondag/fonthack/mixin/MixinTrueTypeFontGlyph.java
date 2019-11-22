package grondag.fonthack.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import grondag.fonthack.ext.TrueTypeFontExt;
import grondag.fonthack.ext.TrueTypeGlyphExt;
import net.minecraft.client.font.TrueTypeFont;

@Mixin(targets = "net.minecraft.client.font.TrueTypeFont$TtfGlyph")
public abstract class MixinTrueTypeFontGlyph implements TrueTypeGlyphExt {
	@Shadow private int width;
	@Shadow private int height;
	@Shadow private float bearingX;
	@Shadow private float ascent;
	@Shadow private float advance;
	@Shadow private int glyphIndex;
	@Shadow private TrueTypeFont field_2336;

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

	@Override
	public TrueTypeFontExt ext_font() {
		return (TrueTypeFontExt) field_2336;
	}
}
