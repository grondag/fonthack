package grondag.fonthack.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontManager;

import grondag.fonthack.ext.MinecraftClientExt;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient implements MinecraftClientExt {
	@Shadow private FontManager fontManager;

	@Override
	public FontManager ext_fontManager() {
		return fontManager;
	}
}
