package grondag.fonthack.font;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.InputStream;

import javax.annotation.Nullable;

import org.lwjgl.system.MemoryStack;

import com.google.common.collect.ImmutableMap;

import grondag.fonthack.ext.RenderableGlyphExt;
import it.unimi.dsi.fastutil.chars.Char2FloatOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.Font;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

/**
 * Mostly works, but needs cleanup in following areas
 * Font scaling/sizing/layout - make configurable
 * Kerning doesn't seem to work, at least on OSX, with either library
 * AWT may be JRE-variant, so consider switching back to LWJGL half-assed wrapper library
 * Needs overall performance / memory / sanity cleanup
 *
 */
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

	private final boolean isRightToLeft;
	private final int layoutFlags;
	private final FontRenderContext frc;


	// TODO: remove scale and oversample if not used
	public NiceFont(Identifier id, float scale, float oversampleIgnored, float shiftX, float shiftY, String excluded) {
		this.id = id;

		final MinecraftClient mc = MinecraftClient.getInstance();
		isRightToLeft = mc.options.language != null && mc.getLanguageManager().isRightToLeft();
		layoutFlags = isRightToLeft ? java.awt.Font.LAYOUT_RIGHT_TO_LEFT : java.awt.Font.LAYOUT_LEFT_TO_RIGHT;
		frc = new FontRenderContext(null, RenderingHints.VALUE_TEXT_ANTIALIAS_ON, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		excluded.chars().forEach((int_1) -> {
			excludedCharacters.add((char)(int_1 & '\uffff'));
		});
		//		this.shiftX = shiftX * oversample;
		//		this.shiftY = shiftY * oversample;

		// TODO: find the size that maximizes efficient space usage
		awtFont = getFont(id, 52);

		final BufferedImage sizeImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
		final Graphics2D sizeGraphics = (Graphics2D) sizeImage.getGraphics();
		sizeGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		sizeGraphics.setFont(awtFont);
		fontMetrics = sizeGraphics.getFontMetrics();
		fontHeight = fontMetrics.getHeight();
		oversample = fontHeight / 9; //oversample;
		//scaleFactor = STBTruetype.stbtt_ScaleForPixelHeight(info, scale * oversample);
		//		scaleFactor = fontHeight / (FontTextureHelper.cellHeight - FontTextureHelper.padding * 2);
		ascent = fontMetrics.getAscent();
	}

	private @Nullable java.awt.Font getFont(Identifier res, float size)
	{
		try(Resource input = MinecraftClient.getInstance().getResourceManager().getResource(res);
			InputStream stream = input.getInputStream())
		{
			return java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, stream)
				.deriveFont(size)
				.deriveFont(ImmutableMap.of(TextAttribute.KERNING, TextAttribute.KERNING_ON));
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@Nullable
	public NiceGlyph getGlyph(char c) {
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

	@Environment(EnvType.CLIENT)
	public class NiceGlyph implements RenderableGlyphExt {
		private final int width;
		private final int height;
		private final float bearingX;
		private final float glyphAscent;
		private final float rawAdvance;
		private final float scaledAdvance;
		public final char c;

		private final Char2FloatOpenHashMap kerning = new Char2FloatOpenHashMap();

		private NiceGlyph(char c) { //int left, int right, int bottom, int top, float advanceIn, float float_2, int index) {
			width = fontMetrics.charWidth(c);
			height = fontMetrics.getHeight();
			rawAdvance = computeAdvance(c);
			scaledAdvance = rawAdvance / oversample;
			bearingX = 0 / oversample;
			glyphAscent = ascent / oversample;
			//			glyphIndex = c;
			this.c = c;
		}

		private float computeAdvance(char c) {
			final char[] cArray = new char[1];
			cArray[0] = c;
			final GlyphVector gv = awtFont.layoutGlyphVector(frc, cArray, 0, 1, layoutFlags);
			final GlyphMetrics gm = gv.getGlyphMetrics(0);
			return gm.getAdvance();
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
			return scaledAdvance;
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
			return 3.0F;
		}

		@Override
		public float kerning(char priorChar) {
			return kerning.computeIfAbsentPartial(priorChar, p -> computeKerning(p));
		}

		protected float computeKerning(char priorChar) {
			final char[] kernPair = new char[2];
			kernPair[0] = priorChar;
			kernPair[1] = c;

			final GlyphVector gv = awtFont.layoutGlyphVector(frc, kernPair, 0, 2, layoutFlags);
			final Point2D total  = gv.getGlyphPosition(2);
			final GlyphMetrics gm0 = gv.getGlyphMetrics(0);
			final GlyphMetrics gm1 = gv.getGlyphMetrics(1);
			final NiceGlyph priorGlyph = getGlyph(priorChar);
			final float result = (gm0.getAdvance() - priorGlyph.rawAdvance) / oversample;

			return result;
		}

		@Override
		public void upload(int x, int y) {
			final int p = FontTextureHelper.padding;
			final int h = FontTextureHelper.cellHeight;
			final int w = FontTextureHelper.ceil16(width + p + p);

			try(final NativeImage img0 = new NativeImage(NativeImage.Format.RGBA, 64, 64, false);
				final NativeImage img1 = new NativeImage(NativeImage.Format.RGBA, 32, 32, false);
				final NativeImage img2 = new NativeImage(NativeImage.Format.RGBA, 16, 16, false);
				final NativeImage img3 = new NativeImage(NativeImage.Format.RGBA, 8, 8, false);
				) {

				final BufferedImage fontImage = getFontImage();
				final Raster rast = fontImage.getData();

				// PERF: find a way to transfer directly
				int[] px = new int[4];
				for (int u = 0; u < 64; u++) {
					for (int v = 0; v < 64; v++) {
						px = rast.getPixel(u, v, px);
						img0.setPixelRGBA(u, v, px[0] | (px[1] << 8) | (px[2] << 16) | (px[3] << 24));
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

				//				if (c == 'S') {
				//					try {
				//						img0.writeFile(c + "_0_out.png");
				//						img1.writeFile(c + "_1_out.png");
				//						img2.writeFile(c + "_2_out.png");
				//						img3.writeFile(c + "_3_out.png");
				//					} catch (final IOException e) {
				//					}
				//				}

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

