package grondag.fonthack.font;

public interface NativeImageExt {
	void ext_setLuminanceAlpha(int u, int v, byte luminance, byte alpha);

	int ext_getPackedLuminanceAlpha(int u, int v);
}
