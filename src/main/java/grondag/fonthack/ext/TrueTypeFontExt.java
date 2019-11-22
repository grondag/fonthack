package grondag.fonthack.ext;

import org.lwjgl.stb.STBTTFontinfo;

import it.unimi.dsi.fastutil.chars.CharSet;

public interface TrueTypeFontExt {
	STBTTFontinfo ext_info();

	float ext_oversample();

	CharSet ext_excludedCharacters();

	float ext_shiftX();

	float ext_shiftY();

	float ext_scaleFactor();

	float ext_ascent();
}
