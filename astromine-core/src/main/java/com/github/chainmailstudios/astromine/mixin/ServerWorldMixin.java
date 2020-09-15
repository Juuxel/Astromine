package com.github.chainmailstudios.astromine.mixin;

import com.github.chainmailstudios.astromine.common.callback.ServerChunkTickEvent;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
	@Inject(method = "tickChunk", at = @At("HEAD"))
	private void tickChunk(Chunk chunk, int randomTickSpeed, CallbackInfo ci) {
		ServerChunkTickEvent.EVENT.invoker().tickChunk((ServerWorld) (Object) this, chunk);
	}
}
