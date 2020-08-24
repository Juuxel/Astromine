package com.github.chainmailstudios.astromine.common.world.feature;

import com.github.chainmailstudios.astromine.registry.AstromineBlocks;
import com.mojang.serialization.Codec;
import com.terraformersmc.shapes.impl.Shapes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;

public class VolcanoFeature extends Feature<DefaultFeatureConfig> {
	public VolcanoFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(configCodec);
	}

	@Override
	public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos, DefaultFeatureConfig featureConfig) {
		final int x1 = pos.getX() - 18 + random.nextInt(9) * (random.nextBoolean() ? 1 : -1);
		final int z1 = pos.getZ() - 18 + random.nextInt(9) * (random.nextBoolean() ? 1 : -1);

		final int x2 = pos.getX() + 18 + random.nextInt(9) * (random.nextBoolean() ? 1 : -1);
		final int z2 = pos.getZ() + 18 + random.nextInt(9) * (random.nextBoolean() ? 1 : -1);

		final int y1 = pos.getY();
		final int y2 = pos.getY() + random.nextInt(64);

		for (int y = y1; y <= y2; ++y) {
			for (int x = x1; x <= x2; ++x) {
				for (int z = z1; z < z2; ++z) {
					final BlockPos blockPos = new BlockPos(x, y, z);

					final double distance = pos.getSquaredDistance(pos);

					final double maximumDistance = (y * Math.pow(1.0, (y2 - y1 / 100.0)));

					if (distance < maximumDistance) {
						world.setBlockState(blockPos, AstromineBlocks.VULCAN_STONE.getDefaultState(), 0b0110100);
					}
				}
			}
		}

		return true;
	}
}
