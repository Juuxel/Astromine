package com.github.chainmailstudios.astromine.discoveries.common.world.feature;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import com.github.chainmailstudios.astromine.discoveries.registry.AstromineDiscoveriesBlocks;
import com.mojang.serialization.Codec;

public class MoonLakeFeature extends Feature<NoneFeatureConfiguration> {
	public MoonLakeFeature(Codec<NoneFeatureConfiguration> configCodec) {
		super(configCodec);
	}

	@Override
	public boolean generate(WorldGenLevel world, ChunkGenerator chunkGenerator, Random random, BlockPos pos, NoneFeatureConfiguration featureConfig) {
		BlockPos.MutableBlockPos mutable = pos.mutable();

		for (int x = -4; x <= 4; x++) {
		    for (int z = -4; z <= 4; z++) {
				for (int y = -4; y <= 4; y++) {
					if (!world.getBlockState(mutable.setWithOffset(pos, x, y, z)).is(AstromineDiscoveriesBlocks.MOON_STONE)) {
						return false;
					}
				}
		    }
		}

		double radius = 4 * 4;

		for (int x = -4; x <= 4; x++) {
			for (int z = -4; z <= 4; z++) {
				for (int y = -4; y <= 4; y++) {
					double dist = (x * x) + (y * y) + (z * z);
					if (dist <= radius) {
						if (y < -1) {
							world.setBlock(mutable.setWithOffset(pos, x, y, z), Blocks.ICE.defaultBlockState(), 3);
						} else {
							world.setBlock(mutable.setWithOffset(pos, x, y, z), Blocks.AIR.defaultBlockState(), 3);
						}
					}
				}
			}
		}


		return true;
	}
}
