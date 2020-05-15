package grondag.fonthack.ext;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;

public interface FontManagerExt {
	/**
	 * Text renderer with given font as the default font.
	 */
	TextRenderer ext_createTextRenderer(Identifier fontId);
}
