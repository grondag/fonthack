package grondag.fonthack.mixin;

import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import grondag.fonthack.font.NativeImageExt;
import net.minecraft.client.texture.NativeImage;

@Mixin(NativeImage.class)
public abstract class MixinNativeImage implements NativeImageExt {
	@Shadow private NativeImage.Format format;
	@Shadow private int width;
	@Shadow private int height;
	@Shadow private boolean isStbImage;
	@Shadow private long pointer;
	@Shadow private int sizeBytes;

	@Shadow abstract protected void checkAllocated();

	@Override
	public void ext_setLuminance(int u, int v, byte luminance) {
		if (format != NativeImage.Format.LUMINANCE) {
			throw new IllegalArgumentException(String.format("ext_setLuminance only works on LUMINANCE images; have %s", format));
		} else if (u <= width && v <= height) {
			checkAllocated();
			MemoryUtil.memByteBuffer(pointer, sizeBytes).put(u + v * width, luminance);
		} else {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", u, v, width, height));
		}
	}
}
