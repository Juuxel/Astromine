package com.github.chainmailstudios.astromine.mixin;

import com.github.chainmailstudios.astromine.common.callback.ServerChunkTickCallback;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerWorldMixin {
	@Inject(method = "tickChunk", at = @At("HEAD"))
	private void tickChunk(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
		ServerChunkTickCallback.EVENT.invoker().tickChunk((ServerLevel) (Object) this, chunk);
	}
}
