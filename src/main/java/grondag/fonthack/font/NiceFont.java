package grondag.fonthack.font;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.annotation.Nullable;

import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;

@Environment(EnvType.CLIENT)
public class NiceFont implements Font {
	private final STBTTFontinfo info;
	private final float oversample;
	private final CharSet excludedCharacters = new CharArraySet();
	private final float shiftX;
	private final float shiftY;
	private final float scaleFactor;
	private final float ascent;

	public NiceFont(STBTTFontinfo info, float scale, float oversample, float shiftX, float shiftY, String excluded) {
		this.info = info;
		this.oversample = oversample;
		excluded.chars().forEach((int_1) -> {
			excludedCharacters.add((char)(int_1 & '\uffff'));
		});
		this.shiftX = shiftX * oversample;
		this.shiftY = shiftY * oversample;
		scaleFactor = STBTruetype.stbtt_ScaleForPixelHeight(info, scale * oversample);

		try (final MemoryStack mem = MemoryStack.stackPush()) {
			final IntBuffer bAscent = mem.mallocInt(1);
			final IntBuffer bDescent = mem.mallocInt(1);
			final IntBuffer bLineGap = mem.mallocInt(1);
			STBTruetype.stbtt_GetFontVMetrics(info, bAscent, bDescent, bLineGap);
			ascent = bAscent.get(0) * scaleFactor;
		}
	}

	@Override
	@Nullable
	public RenderableGlyph getGlyph(char c) {
		if (excludedCharacters.contains(c)) {
			return null;
		} else {
			try (final MemoryStack memStack = MemoryStack.stackPush()) {
				final int index = STBTruetype.stbtt_FindGlyphIndex(info, c);
				if (index == 0) {
					return null;
				}

				final IntBuffer ix0 = memStack.mallocInt(1);
				final IntBuffer iy0 = memStack.mallocInt(1);
				final IntBuffer ix1 = memStack.mallocInt(1);
				final IntBuffer iy1 = memStack.mallocInt(1);
				STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel(info, index, scaleFactor, scaleFactor, shiftX, shiftY, ix0, iy0, ix1, iy1);

				final int w = ix1.get(0) - ix0.get(0);
				final int h = iy1.get(0) - iy0.get(0);

				if (w == 0 || h == 0) {
					return null;
				}

				final IntBuffer advanceWidth = memStack.mallocInt(1);
				final IntBuffer leftSideBearing = memStack.mallocInt(1);
				STBTruetype.stbtt_GetGlyphHMetrics(info, index, advanceWidth, leftSideBearing);

				return new NiceGlyph(ix0.get(0), ix1.get(0), -iy0.get(0), -iy1.get(0), advanceWidth.get(0) * scaleFactor, leftSideBearing.get(0) * scaleFactor, index);
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
		private final int glyphIndex;

		private NiceGlyph(int left, int right, int bottom, int top, float advanceIn, float float_2, int index) {
			width = right - left;
			height = bottom - top;
			advance = advanceIn / oversample;
			bearingX = (float_2 + left + shiftX) / oversample;
			glyphAscent = (ascent - bottom + shiftY) / oversample;
			glyphIndex = index;
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
		public float getAscent() {
			return glyphAscent;
		}

		@Override
		public void upload(int x, int y) {
			try(final NativeImage img = new NativeImage(NativeImage.Format.LUMINANCE, width, height, false);) {
				img.makeGlyphBitmapSubpixel(info, glyphIndex, width, height, scaleFactor, scaleFactor, shiftX, shiftY, 0, 0);
				img.upload(0, x, y, 0, 0, width, height, false);
			}
		}

		@Override
		public boolean hasColor() {
			return false;
		}
	}
}

