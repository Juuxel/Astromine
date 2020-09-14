package com.github.chainmailstudios.astromine.common.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;

@FunctionalInterface
public interface ServerChunkTickCallback {
	Event<ServerChunkTickCallback> EVENT = EventFactory.createArrayBacked(ServerChunkTickCallback.class, (listeners) -> (world, chunk) -> {
		for (ServerChunkTickCallback listener : listeners) {
			listener.tickChunk(world, chunk);
		}
	});

	void tickChunk(ServerWorld world, Chunk chunk);
}
