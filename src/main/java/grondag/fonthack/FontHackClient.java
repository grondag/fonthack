package grondag.fonthack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import grondag.fonthack.ext.FontManagerExt;
import grondag.fonthack.ext.MinecraftExt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;

public class FontHackClient {
	public static final String MOD_ID = "fonthack";
	public static Logger LOG = LogManager.getLogger("Font Hack");
	public static final ResourceLocation READING_FONT = new ResourceLocation("fonthack:reading");

	/**
	 * Text renderer with given font as the default font.
	 */
	public static Font getTextRenderer(ResourceLocation fontId) {
		return ((FontManagerExt)((MinecraftExt)Minecraft.getInstance()).ext_fontManager()).ext_createTextRenderer(fontId);
	}
}
