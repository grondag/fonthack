package grondag.fonthack.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.FontManager;

import grondag.fonthack.ext.MinecraftExt;

@Mixin(Minecraft.class)
public class MixinMinecraft implements MinecraftExt {
	@Shadow private FontManager fontManager;

	@Override
	public FontManager ext_fontManager() {
		return fontManager;
	}
}
