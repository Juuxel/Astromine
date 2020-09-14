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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.EntitySelectionContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import com.zundrel.wrenchable.WrenchableUtilities;
import com.zundrel.wrenchable.block.BlockWrenchable;
import grondag.fermion.modkeys.api.ModKeys;

import javax.annotation.Nullable;

public class CatwalkBlock extends Block implements BlockWrenchable, IWaterLoggable {
	public CatwalkBlock(Properties settings) {
		super(settings);

		registerDefaultState(this.defaultBlockState().setValue(ConveyorProperties.FLOOR, true).setValue(BlockStateProperties.NORTH, false).setValue(BlockStateProperties.EAST, false).setValue(BlockStateProperties.SOUTH, false).setValue(BlockStateProperties.WEST, false).setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getBlockState(context.getClickedPos()).getBlock() == Blocks.WATER);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) ? Fluids.WATER.defaultFluidState() : super.getFluidState(state);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(ConveyorProperties.FLOOR, BlockStateProperties.NORTH, BlockStateProperties.EAST, BlockStateProperties.SOUTH, BlockStateProperties.WEST, BlockStateProperties.WATERLOGGED);
	}

	@Override
	public void onWrenched(World world, PlayerEntity playerEntity, BlockRayTraceResult blockHitResult) {
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
	public BlockState updateShape(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
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
	public VoxelShape getShape(BlockState state, IBlockReader view, BlockPos pos, ISelectionContext context) {
		AxisAlignedBB bottom = new AxisAlignedBB(0, 0, 0, 1, (1F / 16F), 1);
		AxisAlignedBB north = new AxisAlignedBB(0, 0, 0, 1, 1, (1F / 16F));
		AxisAlignedBB east = new AxisAlignedBB((15F / 16F), 0, 0, 1, 1, 1);
		AxisAlignedBB south = new AxisAlignedBB(0, 0, (15F / 16F), 1, 1, 1);
		AxisAlignedBB west = new AxisAlignedBB(0, 0, 0, (1F / 16F), 1, 1);
		VoxelShape fullShape = VoxelShapes.or(VoxelShapes.create(bottom), VoxelShapes.create(north), VoxelShapes.create(east), VoxelShapes.create(south), VoxelShapes.create(west));

		if (context instanceof EntitySelectionContext) {
			Item heldItem = ((EntitySelectionContext) context).heldItem;

			if (WrenchableUtilities.isWrench(heldItem)) {
				return fullShape;
			}
		}

		return getCollisionShape(state, view, pos, context);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader view, BlockPos pos, ISelectionContext entityContext) {
		AxisAlignedBB bottom = new AxisAlignedBB(0, 0, 0, 1, (1F / 16F), 1);
		AxisAlignedBB north = new AxisAlignedBB(0, 0, 0, 1, 1, (1F / 16F));
		AxisAlignedBB east = new AxisAlignedBB((15F / 16F), 0, 0, 1, 1, 1);
		AxisAlignedBB south = new AxisAlignedBB(0, 0, (15F / 16F), 1, 1, 1);
		AxisAlignedBB west = new AxisAlignedBB(0, 0, 0, (1F / 16F), 1, 1);
		VoxelShape shape = VoxelShapes.empty();

		if (state.getValue(ConveyorProperties.FLOOR))
			shape = VoxelShapes.or(shape, VoxelShapes.create(bottom));
		if (!state.getValue(BlockStateProperties.NORTH))
			shape = VoxelShapes.or(shape, VoxelShapes.create(north));
		if (!state.getValue(BlockStateProperties.EAST))
			shape = VoxelShapes.or(shape, VoxelShapes.create(east));
		if (!state.getValue(BlockStateProperties.SOUTH))
			shape = VoxelShapes.or(shape, VoxelShapes.create(south));
		if (!state.getValue(BlockStateProperties.WEST))
			shape = VoxelShapes.or(shape, VoxelShapes.create(west));

		return shape;
	}
}
