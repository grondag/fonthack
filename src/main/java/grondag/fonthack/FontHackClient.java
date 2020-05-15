package grondag.fonthack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;

import grondag.fonthack.ext.FontManagerExt;
import grondag.fonthack.ext.MinecraftClientExt;

public class FontHackClient {
	public static final String MOD_ID = "fonthack";
	public static Logger LOG = LogManager.getLogger("Font Hack");
	public static final Identifier READING_FONT = new Identifier("fonthack:reading");

	/**
	 * Text renderer with given font as the default font.
	 */
	public static TextRenderer getTextRenderer(Identifier fontId) {
		return ((FontManagerExt)((MinecraftClientExt)MinecraftClient.getInstance()).ext_fontManager()).ext_createTextRenderer(fontId);
	}
}
