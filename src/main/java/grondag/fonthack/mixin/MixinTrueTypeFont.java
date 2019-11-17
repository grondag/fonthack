package grondag.fonthack.mixin;

import org.lwjgl.stb.STBTTFontinfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import grondag.fonthack.TrueTypeFontExt;
import it.unimi.dsi.fastutil.chars.CharSet;
import net.minecraft.client.font.TrueTypeFont;

@Mixin(TrueTypeFont.class)
public class MixinTrueTypeFont implements TrueTypeFontExt {
	@Shadow private STBTTFontinfo info;
	@Shadow private float oversample;
	@Shadow private CharSet excludedCharacters;
	@Shadow private float shiftX;
	@Shadow private float shiftY;
	@Shadow private float scaleFactor;
	@Shadow private float ascent;

	@Override
	public STBTTFontinfo ext_info() {
		return info;
	}

	@Override
	public float ext_oversample() {
		return oversample;
	}

	@Override
	public CharSet ext_excludedCharacters() {
		return excludedCharacters;
	}

	@Override
	public float ext_shiftX() {
		return shiftX;
	}

	@Override
	public float ext_shiftY() {
		return shiftY;
	}

	@Override
	public float ext_scaleFactor() {
		return scaleFactor;
	}

	@Override
	public float ext_ascent() {
		return ascent;
	}
}
