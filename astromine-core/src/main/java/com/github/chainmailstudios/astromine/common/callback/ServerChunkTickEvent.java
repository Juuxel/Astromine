package com.github.chainmailstudios.astromine.common.callback;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event;

public class ServerChunkTickEvent extends Event {
	public ServerWorld world;
	public Chunk chunk;

	public ServerChunkTickEvent(ServerWorld world, Chunk chunk) {
		this.world = world;
		this.chunk = chunk;
	}
}
