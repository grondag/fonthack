package grondag.fonthack.mixin;

import java.util.Map;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import grondag.fonthack.ext.FontManagerExt;

@Mixin(FontManager.class)
public class MixinFontManager implements FontManagerExt {
	@Shadow private FontSet missingFontSet;
	@Shadow private Map<ResourceLocation, FontSet> fontSets;
	@Shadow private Map<ResourceLocation, ResourceLocation> renames;

	@Override
	public Font ext_createTextRenderer(ResourceLocation fontId) {
		return new Font((identifier) -> {
			if(identifier.equals(Style.DEFAULT_FONT)) {
				identifier = fontId;
			}

			return fontSets.getOrDefault(renames.getOrDefault(identifier, identifier), missingFontSet);
		});
	}

	@Override
	public FontSet ext_getFontStorage(ResourceLocation fontId) {
		return fontSets.getOrDefault(renames.getOrDefault(fontId, fontId), missingFontSet);
	}
}
