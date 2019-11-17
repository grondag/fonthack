package grondag.fonthack;

public interface TrueTypeGlyphExt {
	int ext_width();

	int ext_height();

	float ext_bearingX();

	float ext_ascent();

	float ext_advance();

	int ext_glyphIndex();
}
