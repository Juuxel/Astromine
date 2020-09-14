package com.github.chainmailstudios.astromine.discoveries.common.world.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import com.github.chainmailstudios.astromine.discoveries.registry.AstromineDiscoveriesBlocks;
import com.mojang.serialization.Codec;

public class MoonLakeFeature extends Feature<NoFeatureConfig> {
	public MoonLakeFeature(Codec<NoFeatureConfig> configCodec) {
		super(configCodec);
	}

	@Override
	public boolean generate(ISeedReader world, ChunkGenerator chunkGenerator, Random random, BlockPos pos, NoFeatureConfig featureConfig) {
		BlockPos.Mutable mutable = pos.mutable();

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
