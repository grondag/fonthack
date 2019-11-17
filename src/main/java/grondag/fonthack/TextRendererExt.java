package grondag.fonthack;

import org.apiguardian.api.API;

import net.minecraft.client.font.FontStorage;

@API(status = API.Status.EXPERIMENTAL)
public interface TextRendererExt {
	FontStorage ext_fontStorage();
}
