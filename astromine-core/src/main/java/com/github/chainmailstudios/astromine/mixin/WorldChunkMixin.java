/*
 * MIT License
 *
 * Copyright (c) 2020 Chainmail Studios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.chainmailstudios.astromine.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.github.chainmailstudios.astromine.access.WorldChunkAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

@Mixin(LevelChunk.class)
public class WorldChunkMixin implements WorldChunkAccess {
	@Shadow
	@Final
	private Level world;
	@Shadow
	@Final
	private ChunkPos pos;
	@Shadow
	@Final
	private LevelChunkSection[] sections;
	private LevelChunk east, west, north, south;
	private Runnable unload;

	@Override
	public void astromine_addUnloadListener(Runnable runnable) {
		if (this.unload == null) {
			this.unload = runnable;
		} else {
			Runnable run = this.unload;
			this.unload = () -> {
				run.run();
				runnable.run();
			};
		}
	}

	@Override
	public void astromine_runUnloadListeners() {
		if (this.unload != null) {
			this.unload.run();
		}
	}

	@Override
	public void astromine_attachEast(LevelChunk chunk) {
		this.east = chunk;
		((WorldChunkAccess) chunk).astromine_addUnloadListener(() -> this.east = null);
	}

	@Override
	public void astromine_attachWest(LevelChunk chunk) {
		this.west = chunk;
		((WorldChunkAccess) chunk).astromine_addUnloadListener(() -> this.west = null);
	}

	@Override
	public void astromine_attachNorth(LevelChunk chunk) {
		this.north = chunk;
		((WorldChunkAccess) chunk).astromine_addUnloadListener(() -> this.north = null);
	}

	@Override
	public void astromine_attachSouth(LevelChunk chunk) {
		this.south = chunk;
		((WorldChunkAccess) chunk).astromine_addUnloadListener(() -> this.south = null);
	}

	@Override
	public void astromine_removeSubchunk(int subchunk) {
		this.sections[subchunk] = LevelChunk.EMPTY_SECTION;

	}

	@Override
	public LevelChunk astromine_east() {
		LevelChunk chunk = this.east;
		if (chunk == null) {
			ChunkPos pos = this.pos;
			chunk = this.east = this.world.getChunk(pos.x + 1, pos.z);
			((WorldChunkAccess) chunk).astromine_addUnloadListener(() -> this.east = null);
			((WorldChunkAccess) chunk).astromine_attachWest((LevelChunk) (Object) this);
		}

		return chunk;
	}

	@Override
	public LevelChunk astromine_west() {
		LevelChunk chunk = this.west;
		if (chunk == null) {
			ChunkPos pos = this.pos;
			chunk = this.west = this.world.getChunk(pos.x - 1, pos.z);
			((WorldChunkAccess) chunk).astromine_addUnloadListener(() -> this.west = null);
			((WorldChunkAccess) chunk).astromine_attachEast((LevelChunk) (Object) this);
		}

		return chunk;
	}

	@Override
	public LevelChunk astromine_north() {
		LevelChunk chunk = this.north;
		if (chunk == null) {
			ChunkPos pos = this.pos;
			chunk = this.north = this.world.getChunk(pos.x, pos.z - 1);
			((WorldChunkAccess) chunk).astromine_addUnloadListener(() -> this.north = null);
			((WorldChunkAccess) chunk).astromine_attachSouth((LevelChunk) (Object) this);
		}

		return chunk;
	}

	@Override
	public LevelChunk astromine_south() {
		LevelChunk chunk = this.south;
		if (chunk == null) {
			ChunkPos pos = this.pos;
			chunk = this.south = this.world.getChunk(pos.x, pos.z + 1);
			((WorldChunkAccess) chunk).astromine_addUnloadListener(() -> this.south = null);
			((WorldChunkAccess) chunk).astromine_attachNorth((LevelChunk) (Object) this);
		}

		return chunk;
	}

	@Inject(method = "setLoadedToWorld", at = @At("RETURN"))
	private void serialize(boolean loaded, CallbackInfo ci) {
		if (!loaded) { // if unloading
			this.astromine_runUnloadListeners();
		}
	}
}
