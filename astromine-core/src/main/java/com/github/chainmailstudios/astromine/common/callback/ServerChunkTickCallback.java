package com.github.chainmailstudios.astromine.common.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

@FunctionalInterface
public interface ServerChunkTickCallback {
	Event<ServerChunkTickCallback> EVENT = EventFactory.createArrayBacked(ServerChunkTickCallback.class, (listeners) -> (world, chunk) -> {
		for (ServerChunkTickCallback listener : listeners) {
			listener.tickChunk(world, chunk);
		}
	});

	void tickChunk(ServerLevel world, LevelChunk chunk);
}
