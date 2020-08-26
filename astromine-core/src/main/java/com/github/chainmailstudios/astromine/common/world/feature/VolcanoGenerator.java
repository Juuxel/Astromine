package com.github.chainmailstudios.astromine.common.world.feature;

import com.github.chainmailstudios.astromine.registry.AstromineBlocks;
import com.github.chainmailstudios.astromine.registry.AstromineFeatures;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePieceWithDimensions;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.Random;

public class VolcanoGenerator extends StructurePieceWithDimensions {
	public VolcanoGenerator(Random random, int x, int z) {
		super(AstromineFeatures.VOLCANO_STRUCTURE, random, x, 64, z, 128, 128, 128);
	}

	public VolcanoGenerator(StructureManager manager, CompoundTag tag) {
		super(AstromineFeatures.METEOR_STRUCTURE, tag);
	}

	@Override
	public boolean generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos centerChunkPos, BlockPos pos) {
		final int x1 = pos.getX() - 18;
		final int z1 = pos.getZ() - 18;

		final int x2 = pos.getX() + 18;
		final int z2 = pos.getZ() + 18;

		int y1 = pos.getY();
		int y2 = pos.getY() + random.nextInt(18);

		final double maximumDistance = Math.pow((x2 - x1), 2);

		for (int x = x1; x <= x2; ++x) {
			for (int z = z1; z < z2; ++z) {
				final BlockPos position = new BlockPos(x, y1, z);

				if (position.getSquaredDistance(pos) <= maximumDistance && world.getBlockState(position).isAir() || world.getBlockState(position).getBlock() instanceof FluidBlock) {
					--y1;
					--y2;

					x = x1;
					z = z1;
				}

				if (y1 <= 1) {
					return false;
				}
			}
		}

		for (int y = y1; y <= y2; ++y) {
			final BlockPos centerPos = new BlockPos(pos.getX(), y, pos.getZ());

			for (int x = x1; x <= x2; ++x) {
				for (int z = z1; z < z2; ++z) {
					final BlockPos blockPos = new BlockPos(x, y, z);

					final double distance = centerPos.getSquaredDistance(blockPos) * 1.0 * Math.pow(((double) y / ((double) y2 - (double) y1)), 2.0);

					if (distance <= maximumDistance && y < y2) {
						world.setBlockState(blockPos, AstromineBlocks.VULCAN_STONE.getDefaultState(), 0);
					} else if (distance <= maximumDistance / 2 && y == y2) {
						world.setBlockState(blockPos, Blocks.LAVA.getDefaultState(), 0);
					}
				}
			}
		}

		return true;
	}
}
