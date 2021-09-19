package grondag.fonthack.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import grondag.fonthack.ext.MinecraftClientExt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.FontManager;

@Mixin(Minecraft.class)
public class MixinMinecraftClient implements MinecraftClientExt {
	@Shadow private FontManager fontManager;

	@Override
	public FontManager ext_fontManager() {
		return fontManager;
	}
}
