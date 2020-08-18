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

import com.github.chainmailstudios.astromine.common.world.generation.mars.MarsChunkGenerator;
import com.github.chainmailstudios.astromine.common.world.generation.moon.MoonChunkGenerator;
import com.github.chainmailstudios.astromine.common.world.generation.space.EarthSpaceChunkGenerator;
import com.github.chainmailstudios.astromine.common.world.generation.vulcan.VulcanChunkGenerator;
import com.mojang.datafixers.DataFixer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ServerChunkManager.class)
public class ServerChunkManagerMixin {

	@Mutable
	@Shadow
	@Final
	private ChunkGenerator chunkGenerator;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void handleConstructor(ServerWorld world, LevelStorage.Session session, DataFixer dataFixer, StructureManager structureManager, Executor workerExecutor, ChunkGenerator chunkGenerator, int viewDistance, boolean bl,
		WorldGenerationProgressListener worldGenerationProgressListener, Supplier<PersistentStateManager> supplier, CallbackInfo ci) {
		if (chunkGenerator instanceof EarthSpaceChunkGenerator) {
			this.chunkGenerator = ((EarthSpaceChunkGenerator) chunkGenerator).withSeedCommon(world.getSeed());
		}
		if (chunkGenerator instanceof MoonChunkGenerator) {
			this.chunkGenerator = ((MoonChunkGenerator) chunkGenerator).withSeedCommon(world.getSeed());
		}
		if (chunkGenerator instanceof MarsChunkGenerator) {
			this.chunkGenerator = ((MarsChunkGenerator) chunkGenerator).withSeedCommon(world.getSeed());
		}
		if (chunkGenerator instanceof VulcanChunkGenerator) {
			this.chunkGenerator = ((VulcanChunkGenerator) chunkGenerator).withSeedCommon(world.getSeed());
		}
	}
}