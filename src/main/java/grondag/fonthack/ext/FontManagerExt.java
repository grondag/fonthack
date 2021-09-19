package grondag.fonthack.ext;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;

public interface FontManagerExt {
	/**
	 * Text renderer with given font as the default font.
	 */
	Font ext_createTextRenderer(ResourceLocation fontId);

	FontSet ext_getFontStorage(ResourceLocation fontId);
}
