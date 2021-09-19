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
	@Shadow private FontSet missingStorage;
	@Shadow private Map<ResourceLocation, FontSet> fontStorages;
	@Shadow private Map<ResourceLocation, ResourceLocation> idOverrides;

	@Override
	public Font ext_createTextRenderer(ResourceLocation fontId) {
		return new Font((identifier) -> {
			if(identifier.equals(Style.DEFAULT_FONT)) {
				identifier = fontId;
			}

			return fontStorages.getOrDefault(idOverrides.getOrDefault(identifier, identifier), missingStorage);
		});
	}

	@Override
	public FontSet ext_getFontStorage(ResourceLocation fontId) {
		return fontStorages.getOrDefault(idOverrides.getOrDefault(fontId, fontId), missingStorage);
	}
}
