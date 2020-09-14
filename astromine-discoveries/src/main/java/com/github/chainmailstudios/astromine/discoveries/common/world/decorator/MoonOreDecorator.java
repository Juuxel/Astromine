package com.github.chainmailstudios.astromine.discoveries.common.world.decorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.behavior.WeightedList;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import com.mojang.serialization.Codec;

public class MoonOreDecorator extends FeatureDecorator<CountConfiguration> {
	public MoonOreDecorator(Codec<CountConfiguration> configCodec) {
		super(configCodec);
	}

	@Override
	public Stream<BlockPos> getPositions(DecorationContext context, Random random, CountConfiguration config, BlockPos pos) {
		List<BlockPos> positions = new ArrayList<>();

		for (int i = 0; i < config.count().sample(random); i++) {
			// Create position
			int x = pos.getX() + random.nextInt(16);
			int z = pos.getZ() + random.nextInt(16);
			int maxY = context.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);

			// Create mutable for iteration
			BlockPos.MutableBlockPos mutable = pos.mutable();
			mutable.set(x, maxY, z);

			WeightedList<BlockPos> weights = new WeightedList<>();

			// Iterate from y5 (to avoid bedrock) to 1 below the max y (to avoid placing ores on the surface)
			for (int y = 5; y < maxY - 1; y++) {
				mutable.setY(y);

				// Sometimes, just pick the position and go with it
				if (random.nextInt(256) == 0) {
					weights.add(mutable.immutable(), 1);
					continue;
				}

				// Most of the time, try to pick a position near a cave
				if (context.getBlockState(mutable).canOcclude() && context.getBlockState(mutable.above()).isAir()) {
					weights.add(mutable.immutable(), 5);
				}
			}

			if (!weights.isEmpty()) {
				// Store the picked position
				positions.add(weights.getOne(random));
			}
		}

		// Return all the positions
		return positions.stream();
	}
}
