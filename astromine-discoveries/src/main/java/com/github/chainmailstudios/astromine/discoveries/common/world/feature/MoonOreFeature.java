package com.github.chainmailstudios.astromine.discoveries.common.world.feature;

import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import com.github.chainmailstudios.astromine.discoveries.registry.AstromineDiscoveriesBlocks;
import com.mojang.serialization.Codec;
import com.terraformersmc.shapes.api.Position;
import com.terraformersmc.shapes.api.Quaternion;
import com.terraformersmc.shapes.api.Shape;
import com.terraformersmc.shapes.impl.Shapes;
import com.terraformersmc.shapes.impl.layer.transform.RotateLayer;
import com.terraformersmc.shapes.impl.layer.transform.TranslateLayer;

public class MoonOreFeature extends Feature<NoFeatureConfig> {
	public MoonOreFeature(Codec<NoFeatureConfig> configCodec) {
		super(configCodec);
	}

	@Override
	public boolean generate(ISeedReader world, ChunkGenerator chunkGenerator, Random random, BlockPos pos, NoFeatureConfig featureConfig) {
		Shape vein = Shapes.ellipsoid(random.nextFloat() * 6, random.nextFloat() * 6, random.nextFloat() * 6)
				.applyLayer(RotateLayer.of(Quaternion.of(random.nextDouble() * 360, random.nextDouble() * 360, random.nextDouble() * 360, true)))
				.applyLayer(TranslateLayer.of(Position.of(pos)));

		for (Position streamPosition : vein.stream().collect(Collectors.toSet())) {
			BlockPos orePos = streamPosition.toBlockPos();

			if (world.getBlockState(orePos).getBlock() == AstromineDiscoveriesBlocks.MOON_STONE) {
				if (random.nextInt(24) == 0) {
					world.setBlock(orePos, AstromineDiscoveriesBlocks.MOON_LUNUM_ORE.defaultBlockState(), 0b0110100);
				}
			}
		}

		return false;
	}
}
