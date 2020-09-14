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

import com.github.chainmailstudios.astromine.transportations.common.block.property.ConveyorProperties;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.zundrel.wrenchable.WrenchableUtilities;
import com.zundrel.wrenchable.block.BlockWrenchable;
import grondag.fermion.modkeys.api.ModKeys;

import javax.annotation.Nullable;

public class CatwalkBlock extends Block implements BlockWrenchable, SimpleWaterloggedBlock {
	public CatwalkBlock(Properties settings) {
		super(settings);

		registerDefaultState(this.defaultBlockState().setValue(ConveyorProperties.FLOOR, true).setValue(BlockStateProperties.NORTH, false).setValue(BlockStateProperties.EAST, false).setValue(BlockStateProperties.SOUTH, false).setValue(BlockStateProperties.WEST, false).setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getBlockState(context.getClickedPos()).getBlock() == Blocks.WATER);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) ? Fluids.WATER.defaultFluidState() : super.getFluidState(state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ConveyorProperties.FLOOR, BlockStateProperties.NORTH, BlockStateProperties.EAST, BlockStateProperties.SOUTH, BlockStateProperties.WEST, BlockStateProperties.WATERLOGGED);
	}

	@Override
	public void onWrenched(Level world, Player playerEntity, BlockHitResult blockHitResult) {
		BlockPos pos = blockHitResult.getBlockPos();
		if (ModKeys.isControlPressed(playerEntity)) {
			world.setBlockAndUpdate(pos, world.getBlockState(pos).cycle(ConveyorProperties.FLOOR));
			return;
		}

		if (blockHitResult.getDirection().getAxis().isHorizontal()) {
			world.setBlockAndUpdate(pos, world.getBlockState(pos).cycle(getPropertyFromDirection(blockHitResult.getDirection())));
		} else if (blockHitResult.getDirection().getAxis().isVertical()) {
			world.setBlockAndUpdate(pos, world.getBlockState(pos).cycle(getPropertyFromDirection(playerEntity.getDirection().getOpposite())));
		}
	}

	public BooleanProperty getPropertyFromDirection(Direction direction) {
		switch (direction) {
			case NORTH:
				return BlockStateProperties.NORTH;
			case EAST:
				return BlockStateProperties.EAST;
			case SOUTH:
				return BlockStateProperties.SOUTH;
			case WEST:
				return BlockStateProperties.WEST;
			default:
				return null;
		}
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
		BlockState newState = state;
		boolean neighborSameType = neighborState.getBlock() instanceof CatwalkBlock;

		if (facing == Direction.NORTH)
			newState = newState.setValue(BlockStateProperties.NORTH, neighborSameType);
		if (facing == Direction.EAST)
			newState = newState.setValue(BlockStateProperties.EAST, neighborSameType);
		if (facing == Direction.SOUTH)
			newState = newState.setValue(BlockStateProperties.SOUTH, neighborSameType);
		if (facing == Direction.WEST)
			newState = newState.setValue(BlockStateProperties.WEST, neighborSameType);

		return newState;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
		AABB bottom = new AABB(0, 0, 0, 1, (1F / 16F), 1);
		AABB north = new AABB(0, 0, 0, 1, 1, (1F / 16F));
		AABB east = new AABB((15F / 16F), 0, 0, 1, 1, 1);
		AABB south = new AABB(0, 0, (15F / 16F), 1, 1, 1);
		AABB west = new AABB(0, 0, 0, (1F / 16F), 1, 1);
		VoxelShape fullShape = Shapes.or(Shapes.create(bottom), Shapes.create(north), Shapes.create(east), Shapes.create(south), Shapes.create(west));

		if (context instanceof EntityCollisionContext) {
			Item heldItem = ((EntityCollisionContext) context).heldItem;

			if (WrenchableUtilities.isWrench(heldItem)) {
				return fullShape;
			}
		}

		return getCollisionShape(state, view, pos, context);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext entityContext) {
		AABB bottom = new AABB(0, 0, 0, 1, (1F / 16F), 1);
		AABB north = new AABB(0, 0, 0, 1, 1, (1F / 16F));
		AABB east = new AABB((15F / 16F), 0, 0, 1, 1, 1);
		AABB south = new AABB(0, 0, (15F / 16F), 1, 1, 1);
		AABB west = new AABB(0, 0, 0, (1F / 16F), 1, 1);
		VoxelShape shape = Shapes.empty();

		if (state.getValue(ConveyorProperties.FLOOR))
			shape = Shapes.or(shape, Shapes.create(bottom));
		if (!state.getValue(BlockStateProperties.NORTH))
			shape = Shapes.or(shape, Shapes.create(north));
		if (!state.getValue(BlockStateProperties.EAST))
			shape = Shapes.or(shape, Shapes.create(east));
		if (!state.getValue(BlockStateProperties.SOUTH))
			shape = Shapes.or(shape, Shapes.create(south));
		if (!state.getValue(BlockStateProperties.WEST))
			shape = Shapes.or(shape, Shapes.create(west));

		return shape;
	}
}
