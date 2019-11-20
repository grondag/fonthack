package grondag.fonthack.font;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.annotation.Nullable;

import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class NiceFont implements Font {
	private final Identifier id;
	private final float oversample;
	private final CharSet excludedCharacters = new CharArraySet();
	//	private final float shiftX;
	//	private final float shiftY;
	//	private final float scaleFactor;
	private final float ascent;
	private final float fontHeight;
	private final FontMetrics fontMetrics;

	private final java.awt.Font awtFont;

	// TODO: remove scale and oversample if not used
	public NiceFont(Identifier id, float scale, float oversample, float shiftX, float shiftY, String excluded) {
		this.id = id;
		this.oversample = 1; //oversample;
		excluded.chars().forEach((int_1) -> {
			excludedCharacters.add((char)(int_1 & '\uffff'));
		});
		//		this.shiftX = shiftX * oversample;
		//		this.shiftY = shiftY * oversample;

		awtFont = getFont(id, 48);

		final BufferedImage sizeImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
		final Graphics2D sizeGraphics = (Graphics2D) sizeImage.getGraphics();
		sizeGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		sizeGraphics.setFont(awtFont);
		fontMetrics = sizeGraphics.getFontMetrics();
		fontHeight = fontMetrics.getHeight();

		//scaleFactor = STBTruetype.stbtt_ScaleForPixelHeight(info, scale * oversample);
		//		scaleFactor = fontHeight / (FontTextureHelper.cellHeight - FontTextureHelper.padding * 2);
		ascent = fontMetrics.getAscent();
	}

	private @Nullable java.awt.Font getFont(Identifier res, float size)
	{
		try(Resource input = MinecraftClient.getInstance().getResourceManager().getResource(res);
			InputStream stream = input.getInputStream())
		{
			return java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, stream).deriveFont(size);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@Nullable
	public RenderableGlyph getGlyph(char c) {
		if (excludedCharacters.contains(c)) {
			return null;
		} else {
			try (final MemoryStack memStack = MemoryStack.stackPush()) {
				return new NiceGlyph(c);
			} catch (final Exception e) {
				return null;
			}
		}
	}

	public static STBTTFontinfo getSTBTTFontInfo(ByteBuffer buff) throws IOException {
		final STBTTFontinfo info = STBTTFontinfo.create();
		if (!STBTruetype.stbtt_InitFont(info, buff)) {
			throw new IOException("Invalid ttf");
		} else {
			return info;
		}
	}

	@Environment(EnvType.CLIENT)
	public class NiceGlyph implements RenderableGlyph {
		private final int width;
		private final int height;
		private final float bearingX;
		private final float glyphAscent;
		private final float advance;
		public final char c;

		private NiceGlyph(char c) { //int left, int right, int bottom, int top, float advanceIn, float float_2, int index) {
			width = fontMetrics.charWidth(c);
			height = fontMetrics.getHeight();
			advance = width / oversample;
			bearingX = 0 / oversample;
			glyphAscent = ascent / oversample;
			//			glyphIndex = c;
			this.c = c;
		}

		@Override
		public int getWidth() {
			return width;
		}

		@Override
		public int getHeight() {
			return height;
		}

		@Override
		public float getOversample() {
			return oversample;
		}

		@Override
		public float getAdvance() {
			return advance;
		}

		@Override
		public float getBearingX() {
			return bearingX;
		}

		@Override
		public float getBoldOffset() {
			// FIXME: needs to be configurable per font
			return 0.5F;
		}

		@Override
		public float getAscent() {
			return glyphAscent;
		}


		@Override
		public void upload(int x, int y) {
			final int p = FontTextureHelper.padding;
			final int h = FontTextureHelper.cellHeight;
			final int w = FontTextureHelper.ceil16(width + p + p);

			try(final NativeImage img0 = new NativeImage(NativeImage.Format.LUMINANCE_ALPHA, 64, 64, false);
				final NativeImage img1 = new NativeImage(NativeImage.Format.LUMINANCE_ALPHA, 32, 32, false);
				final NativeImage img2 = new NativeImage(NativeImage.Format.LUMINANCE_ALPHA, 16, 16, false);
				final NativeImage img3 = new NativeImage(NativeImage.Format.LUMINANCE_ALPHA, 8, 8, false);
				) {

				final NativeImageExt imgExt = (NativeImageExt)(Object) img0;
				final BufferedImage fontImage = getFontImage();
				final Raster rast = fontImage.getData();
				//img0.makeGlyphBitmapSubpixel(info, glyphIndex, width, height, scaleFactor, scaleFactor, shiftX, shiftY, p, p);
				// PERF: find a way to transfer directly

				for (int u = 0; u < 64; u++) {
					for (int v = 0; v < 64; v++) {
						imgExt.ext_setLuminanceAlpha(u, v, (byte) rast.getSample(u, v, 3), (byte) rast.getSample(u, v, 0));
					}
				}

				img0.upload(0, x, y, 0, 0, w, h, true, true, true);

				// TODO: remove
				if (height + p + p > FontTextureHelper.cellHeight) {
					System.out.println("Exceeded cell height: " + y + p + p);
				}

				final NativeImage images[] = new NativeImage[4];
				images[0] = img0;
				images[1] = img1;
				images[2] = img2;
				images[3] = img3;

				FontTextureHelper.generateMipmaps(images);
				for (int i = 1; i <= 3; i++) {
					images[i].upload(i, x >> i, y >> i, 0, 0, w >> i, h >> i, true, true, true);
				}
			}
		}

		private BufferedImage getFontImage() {
			// Create another image holding the character we are creating
			final BufferedImage fontImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);

			final Graphics2D gt = (Graphics2D) fontImage.getGraphics();
			gt.setFont(awtFont);
			gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			gt.setColor(Color.WHITE);

			// TODO: should really use MaxAscent here? Would likely waste space
			gt.drawString(String.valueOf(c), FontTextureHelper.padding, fontMetrics.getAscent() + FontTextureHelper.padding);

			return fontImage;
		}

		@Override
		public boolean hasColor() {
			return false;
		}
	}
}

