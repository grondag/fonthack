//package grondag.fonthack.mixin;
//
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//import com.google.gson.JsonObject;
//
//import grondag.fonthack.font.NiceFontLoader;
//import net.minecraft.client.font.FontLoader;
//import net.minecraft.client.font.FontType;
//
//@Mixin(FontType.class)
//public class MixinFontType {
//	@Inject(method = "createLoader", at = @At("HEAD"), cancellable = true, require = 1)
//	private void onCreateLoader(JsonObject json, CallbackInfoReturnable<FontLoader> ci) {
//		if ((FontType)(Object) this == FontType.TTF) {
//			ci.setReturnValue(NiceFontLoader.fromJson(json));
//		}
//	}
//}
