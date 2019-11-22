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
	@Shadow private long sizeBytes;

	@Shadow abstract protected void checkAllocated();

	@Override
	public void ext_setLuminanceAlpha(int u, int v, byte luminance, byte alpha) {
		if (format != NativeImage.Format.LUMINANCE_ALPHA) {
			throw new IllegalArgumentException(String.format("ext_setLuminance only works on LUMINANCE images; have %s", format));
		} else if (u <= width && v <= height) {
			checkAllocated();
			final int offset = (u + v * width) * 2;
			MemoryUtil.memByteBuffer(pointer, (int) sizeBytes).put(offset, luminance);
			MemoryUtil.memByteBuffer(pointer, (int) sizeBytes).put(offset + 1, alpha);
		} else {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", u, v, width, height));
		}
	}

	@Override
	public int ext_getPackedLuminanceAlpha(int u, int v) {
		if (format != NativeImage.Format.LUMINANCE_ALPHA) {
			throw new IllegalArgumentException(String.format("ext_setLuminance only works on LUMINANCE images; have %s", format));
		} else if (u <= width && v <= height) {
			checkAllocated();
			final int offset = (u + v * width) * 2;
			final int l = MemoryUtil.memByteBuffer(pointer, (int) sizeBytes).get(offset);
			final int a = MemoryUtil.memByteBuffer(pointer, (int) sizeBytes).get(offset + 1);

			return l | (a << 8);
		} else {
			throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", u, v, width, height));
		}
	}
}
