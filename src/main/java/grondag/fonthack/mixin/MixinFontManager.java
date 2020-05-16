package grondag.fonthack.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;

import grondag.fonthack.ext.FontManagerExt;

@Mixin(FontManager.class)
public class MixinFontManager implements FontManagerExt {
	@Shadow private FontStorage missingStorage;
	@Shadow private Map<Identifier, FontStorage> fontStorages;
	@Shadow private Map<Identifier, Identifier> idOverrides;

	@Override
	public TextRenderer ext_createTextRenderer(Identifier fontId) {
		return new TextRenderer((identifier) -> {
			if(identifier.equals(Style.DEFAULT_FONT_ID)) {
				identifier = fontId;
			}

			return fontStorages.getOrDefault(idOverrides.getOrDefault(identifier, identifier), missingStorage);
		});
	}

	@Override
	public FontStorage ext_getFontStorage(Identifier fontId) {
		return fontStorages.getOrDefault(idOverrides.getOrDefault(fontId, fontId), missingStorage);
	}
}
