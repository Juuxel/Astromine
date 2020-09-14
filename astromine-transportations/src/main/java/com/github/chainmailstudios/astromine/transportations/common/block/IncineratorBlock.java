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

package com.github.chainmailstudios.astromine.transportations.common.block;

import com.github.chainmailstudios.astromine.common.utilities.capability.block.FacingBlockWrenchable;
import com.github.chainmailstudios.astromine.transportations.common.block.entity.IncineratorBlockEntity;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.ConveyableBlock;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class IncineratorBlock extends HorizontalDirectionalBlock implements EntityBlock, ConveyableBlock, FacingBlockWrenchable {
	public IncineratorBlock(Properties settings) {
		super(settings);
	}

	@Override
	public BlockEntity newBlockEntity(BlockGetter blockView) {
		return new IncineratorBlockEntity();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManagerBuilder) {
		stateManagerBuilder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext itemPlacementContext) {
		return this.defaultBlockState().setValue(FACING, itemPlacementContext.getPlayer().isShiftKeyDown() ? itemPlacementContext.getHorizontalDirection().getOpposite() : itemPlacementContext.getHorizontalDirection());
	}

	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
		BlockPos blockPos = pos.above();
		if (world.getBlockState(blockPos).isAir() && !world.getBlockState(blockPos).isSolidRender(world, blockPos)) {
			if (random.nextInt(100) == 0) {
				double d = (double) ((float) pos.getX() + random.nextFloat());
				double e = (double) (pos.getY() + 1);
				double f = (double) ((float) pos.getZ() + random.nextFloat());
				world.addParticle(ParticleTypes.LAVA, d, e, f, 0.0D, 0.0D, 0.0D);
				world.playLocalSound(d, e, f, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}

			if (random.nextInt(200) == 0) {
				world.playLocalSound((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}
		}
	}

	@Override
	public void onPlace(BlockState blockState, Level world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
		updateDiagonals(world, this, blockPos);
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean notify) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof IncineratorBlockEntity) {
				((IncineratorBlockEntity) blockEntity).setRemoved(true);
			}

			super.onRemove(state, world, pos, newState, notify);
		}

		updateDiagonals(world, this, pos);
	}
}
