package grondag.fonthack.ext;

import net.minecraft.client.font.RenderableGlyph;

public interface RenderableGlyphExt extends RenderableGlyph {

	float kerning(char priorChar);

}
