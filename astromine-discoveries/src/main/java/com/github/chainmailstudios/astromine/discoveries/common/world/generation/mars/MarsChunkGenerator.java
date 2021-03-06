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

package com.github.chainmailstudios.astromine.discoveries.common.world.generation.mars;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.github.chainmailstudios.astromine.common.miscellaneous.BiomeGeneratorCache;
import com.github.chainmailstudios.astromine.common.noise.OctaveNoiseSampler;
import com.github.chainmailstudios.astromine.common.noise.OpenSimplexNoise;
import com.github.chainmailstudios.astromine.discoveries.registry.AstromineDiscoveriesBlocks;

import java.util.Arrays;
import java.util.Random;

public class MarsChunkGenerator extends ChunkGenerator {
	public static Codec<MarsChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.LONG.fieldOf("seed").forGetter(gen -> gen.seed), RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(source -> source.biomeRegistry)).apply(instance,
		MarsChunkGenerator::new));

	private final long seed;
	private final Registry<Biome> biomeRegistry;
	private final OctaveNoiseSampler<OpenSimplexNoise> lowerInterpolatedNoise;
	private final OctaveNoiseSampler<OpenSimplexNoise> upperInterpolatedNoise;
	private final OctaveNoiseSampler<OpenSimplexNoise> interpolationNoise;
	private final ThreadLocal<BiomeGeneratorCache> cache;

	public MarsChunkGenerator(long seed, Registry<Biome> biomeRegistry) {
		super(new MarsBiomeSource(seed, biomeRegistry), new StructuresConfig(false));
		this.seed = seed;
		this.biomeRegistry = biomeRegistry;
		Random random = new Random(seed);
		lowerInterpolatedNoise = new OctaveNoiseSampler<>(OpenSimplexNoise.class, random, 5, 140.43, 45, 10);
		upperInterpolatedNoise = new OctaveNoiseSampler<>(OpenSimplexNoise.class, random, 5, 140.43, 45, 10);
		interpolationNoise = new OctaveNoiseSampler<>(OpenSimplexNoise.class, random, 3, 80.32, 3, 3);
		this.cache = ThreadLocal.withInitial(() -> new BiomeGeneratorCache(biomeSource));
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long seed) {
		return withSeedCommon(seed);
	}

	public ChunkGenerator withSeedCommon(long seed) {
		return new MarsChunkGenerator(seed, biomeRegistry);
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk) {

	}

	@Override
	public void populateNoise(WorldAccess world, StructureAccessor accessor, Chunk chunk) {
		int x1 = chunk.getPos().getStartX();
		int z1 = chunk.getPos().getStartZ();

		int x2 = chunk.getPos().getEndX();
		int z2 = chunk.getPos().getEndZ();

		ChunkRandom chunkRandom = new ChunkRandom();
		chunkRandom.setTerrainSeed(chunk.getPos().x, chunk.getPos().z);

		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int x = x1; x <= x2; ++x) {
			mutable.setX(x);
			for (int z = z1; z <= z2; ++z) {
				mutable.setZ(z);

				float depth = 0;
				float scale = 0;
				int i = 0;

				// Biome lerp
				for (int x0 = -5; x0 <= 5; x0++) {
					for (int z0 = -5; z0 <= 5; z0++) {
						Biome biome = this.cache.get().getBiome((x + x0) >> 2, (z + z0) >> 2);

						i++;
						depth += biome.getDepth();
						scale += biome.getScale();
					}
				}

				depth /= i;
				scale /= i;

				// Noise calculation
				double noise = interpolationNoise.sample(x, z);
				if (noise >= 1) {
					noise = upperInterpolatedNoise.sample(x, z);
				} else if (noise <= -1) {
					noise = lowerInterpolatedNoise.sample(x, z);
				} else {
					noise = MathHelper.clampedLerp(lowerInterpolatedNoise.sample(x, z), upperInterpolatedNoise.sample(x, z), noise);
				}

				int height = (int) (depth + (noise * scale));
				for (int y = 0; y <= height; ++y) {
					mutable.setY(y);
					chunk.setBlockState(mutable, y == height ? AstromineDiscoveriesBlocks.MARTIAN_SOIL.getDefaultState() : AstromineDiscoveriesBlocks.MARTIAN_STONE.getDefaultState(), false);
					if (y <= 5) {
						if (chunkRandom.nextInt(y + 1) == 0) {
							chunk.setBlockState(mutable, Blocks.BEDROCK.getDefaultState(), false);
						}
					}
				}
			}
		}
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmapType) {
		return 0;
	}

	@Override
	public BlockView getColumnSample(int x, int z) {
		BlockState[] states = new BlockState[256];
		Arrays.fill(states, Blocks.AIR.getDefaultState());
		return new VerticalBlockSample(states);
	}
}
