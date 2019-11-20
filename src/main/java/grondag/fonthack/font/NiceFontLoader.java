package grondag.fonthack.font;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import grondag.fonthack.FontHackClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class NiceFontLoader implements FontLoader {
	private final Identifier filename;
	private final float size;
	private final float oversample;
	private final float shiftX;
	private final float shiftY;
	private final String excludedCharacters;

	public NiceFontLoader(Identifier filename, float size, float oversample, float shiftX, float shiftY, String excluded) {
		this.filename = filename;
		this.size = size;
		this.oversample = oversample;
		this.shiftX = shiftX;
		this.shiftY = shiftY;
		excludedCharacters = excluded;
	}

	public static FontLoader fromJson(JsonObject json) {
		float sx = 0.0F;
		float sy = 0.0F;

		if (json.has("shift")) {
			final JsonArray jArray = json.getAsJsonArray("shift");

			if (jArray.size() != 2) {
				throw new JsonParseException("Expected 2 elements in 'shift', found " + jArray.size());
			}

			sx = JsonHelper.asFloat(jArray.get(0), "shift[0]");
			sy = JsonHelper.asFloat(jArray.get(1), "shift[1]");
		}

		final StringBuilder sb = new StringBuilder();

		if (json.has("skip")) {
			final JsonElement jElement = json.get("skip");
			if (jElement.isJsonArray()) {
				final JsonArray jArray = JsonHelper.asArray(jElement, "skip");

				for(int i = 0; i < jArray.size(); ++i) {
					sb.append(JsonHelper.asString(jArray.get(i), "skip[" + i + "]"));
				}
			} else {
				sb.append(JsonHelper.asString(jElement, "skip"));
			}
		}

		return new NiceFontLoader(new Identifier(JsonHelper.getString(json, "file")), JsonHelper.getFloat(json, "size", 11.0F), JsonHelper.getFloat(json, "oversample", 1.0F), sx, sy, sb.toString());
	}

	@Override
	@Nullable
	public Font load(ResourceManager rm) {
		try {
			return new NiceFont(new Identifier(filename.getNamespace(), "font/" + filename.getPath()), size, oversample, shiftX, shiftY, excludedCharacters);
		} catch (final Exception ex) {
			FontHackClient.LOG.error("Couldn't load truetype font {}", filename, ex);
			return null;
		}
	}
}