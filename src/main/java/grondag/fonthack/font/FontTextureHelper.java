package grondag.fonthack.font;

import static net.minecraft.client.render.RenderPhase.ALWAYS_DEPTH_TEST;
import static net.minecraft.client.render.RenderPhase.COLOR_MASK;
import static net.minecraft.client.render.RenderPhase.ENABLE_LIGHTMAP;
import static net.minecraft.client.render.RenderPhase.ONE_TENTH_ALPHA;
import static net.minecraft.client.render.RenderPhase.TRANSLUCENT_TRANSPARENCY;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class FontTextureHelper {

	public static final int SIZE = 512;
	public static final int CELL_HEIGHT  = 64;
	/** padding for LOD filter, all sides */
	public static final int PADDING = 4;

	/** renderable area after padding */
	public static final int RENDERABLE_CELL_HEIGHT = CELL_HEIGHT - PADDING - PADDING;

	/** vanilla forces descent to be 3 vs 9 overall */
	public static final int DESCENT_PADDING = RENDERABLE_CELL_HEIGHT / 4;
	public static final int ASCENT = RENDERABLE_CELL_HEIGHT - DESCENT_PADDING;
	public static final int LOD = 3;
	public static boolean isNice;

	private static final float[] LUMINANCE_TO_LINEAR_MAP = new float[256];

	static  {
		for(int i = 0; i < LUMINANCE_TO_LINEAR_MAP.length; ++i) {
			LUMINANCE_TO_LINEAR_MAP[i] = (float) Math.pow(i / 255.0, 2.2D);
		}
	}

	public static void generateMipmaps(final NativeImage[] images) {
		final int limit = images.length;
		//		NativeImageExt base = (NativeImageExt)(Object) images[0];
		NativeImage base = images[0];

		if (limit > 0) {

			for(int i = 1; i < limit; ++i) {
				final NativeImage img = images[i];
				//				final NativeImageExt imgExt = (NativeImageExt)(Object) img;
				final int w = img.getWidth();
				final int h = img.getHeight();

				for(int u = 0; u < w; ++u) {
					for(int v = 0; v < h; ++v) {
						//						final int a = base.ext_getPackedLuminanceAlpha(u * 2 + 0, v * 2 + 0);
						//						final int b = base.ext_getPackedLuminanceAlpha(u * 2 + 1, v * 2 + 0);
						//						final int c = base.ext_getPackedLuminanceAlpha(u * 2 + 0, v * 2 + 1);
						//						final int d = base.ext_getPackedLuminanceAlpha(u * 2 + 1, v * 2 + 1);

						final int a = base.getPixelRgba(u * 2 + 0, v * 2 + 0);
						final int b = base.getPixelRgba(u * 2 + 1, v * 2 + 0);
						final int c = base.getPixelRgba(u * 2 + 0, v * 2 + 1);
						final int d = base.getPixelRgba(u * 2 + 1, v * 2 + 1);

						//						final byte luminance = blendPixels(a & 0xFF, b & 0xFF, c & 0xFF, d & 0xFF);
						//
						//						final byte alpha = blendPixels((a >> 8) & 0xFF, (b >> 8) & 0xFF, (c >> 8) & 0xFF, (d >> 8) & 0xFF);
						final int red = blendPixels(a & 0xFF, b & 0xFF, c & 0xFF, d & 0xFF);
						final int green = blendPixels((a >> 8) & 0xFF, (b >> 8) & 0xFF, (c >> 8) & 0xFF, (d >> 8) & 0xFF);
						final int blue = blendPixels((a >> 16) & 0xFF, (b >> 16) & 0xFF, (c >> 16) & 0xFF, (d >> 16) & 0xFF);
						final int alpha = blendPixels((a >> 24) & 0xFF, (b >> 24) & 0xFF, (c >> 24) & 0xFF, (d >> 24) & 0xFF);
						//						imgExt.ext_setLuminanceAlpha(u, v, luminance, alpha);
						img.setPixelRgba(u, v, red | (green << 8) | (blue << 16) | (alpha << 24));
					}
				}

				//				base  = (NativeImageExt)(Object) img;
				base  = img;
			}
		}
	}

	public static int ceil16(int val) {
		return (val & 0xF) == 0 ? val : val + (~(val & 0xF) & 0xF);
	}

	private static final int blendPixels(final int a, final int b, final int c, final int d) {
		return MathHelper.clamp(Math.round(0.25f * a + 0.25f *  b + 0.25f * c + 0.25f * d), 0, 255);
		//		final float v = LUMINANCE_TO_LINEAR_MAP[a & 255] * 0.25f
		//			+ LUMINANCE_TO_LINEAR_MAP[b & 255] * 0.25f
		//			+ LUMINANCE_TO_LINEAR_MAP[c & 255] * 0.25f
		//			+ LUMINANCE_TO_LINEAR_MAP[d & 255]* 0.25f;
		//
		//		final int result = MathHelper.clamp((int)(Math.pow(v, 0.45454545454545453D) * 255.0D), 0, 255);
		//
		//		return (byte) result; // < 96 ? 0 : (byte) result;
	}

	public static RenderLayer getTextLod(Identifier texture) {
		return RenderLayer.of("text", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT, 7, 256, false, true, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false, true)).alpha(ONE_TENTH_ALPHA).transparency(TRANSLUCENT_TRANSPARENCY).lightmap(ENABLE_LIGHTMAP).build(false));
	}

	public static RenderLayer getTextLodSeeThrough(Identifier texture) {
		return RenderLayer.of("text_see_through", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT, 7, 256, false, true, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false, true)).alpha(ONE_TENTH_ALPHA).transparency(TRANSLUCENT_TRANSPARENCY).lightmap(ENABLE_LIGHTMAP).depthTest(ALWAYS_DEPTH_TEST).writeMaskState(COLOR_MASK).build(false));
	}
}
