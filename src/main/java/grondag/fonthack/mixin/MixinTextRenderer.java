package grondag.fonthack.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import grondag.fonthack.ext.TextRendererExt;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.texture.TextureManager;

@Mixin(TextRenderer.class)
public abstract class MixinTextRenderer implements TextRendererExt {
	@Shadow private TextureManager textureManager;
	@Shadow private FontStorage fontStorage;

	@Override
	public FontStorage ext_fontStorage() {
		return fontStorage;
	}
}