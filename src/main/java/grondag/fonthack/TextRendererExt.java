package grondag.fonthack;

import org.apiguardian.api.API;

import net.minecraft.client.font.FontStorage;
import net.minecraft.client.texture.TextureManager;

@API(status = API.Status.EXPERIMENTAL)
public interface TextRendererExt {
	FontStorage ext_fontStorage();
	TextureManager ext_textureManager();
}
