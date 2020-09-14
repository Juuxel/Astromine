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

import com.github.chainmailstudios.astromine.common.utilities.RotationUtilities;
import com.github.chainmailstudios.astromine.transportations.common.block.property.ConveyorProperties;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class CatwalkStairsBlock extends HorizontalBlock implements IWaterLoggable {
	public CatwalkStairsBlock(Properties settings) {
		super(settings);

		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(ConveyorProperties.LEFT, false).setValue(ConveyorProperties.RIGHT, false).setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, ConveyorProperties.LEFT, ConveyorProperties.RIGHT, BlockStateProperties.WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) ? Fluids.WATER.defaultFluidState() : super.getFluidState(state);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getPlayer().isShiftKeyDown() ? context.getHorizontalDirection().getOpposite() : context.getHorizontalDirection()).setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getBlockState(context.getClickedPos()).getBlock() == Blocks.WATER);
	}

	public boolean isAdjacentBlockOfMyType(IWorld world, BlockPos position, Direction direction) {

		assert null != world : "world cannot be null";
		assert null != position : "position cannot be null";
		assert null != this : "type cannot be null";

		BlockPos newPosition = position.relative(direction);
		BlockState blockState = world.getBlockState(newPosition);
		Block block = (null == blockState) ? null : blockState.getBlock();

		return this == block;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		BlockState newState = state;

		newState = state.setValue(ConveyorProperties.RIGHT, this.isAdjacentBlockOfMyType(world, pos, state.getValue(FACING).getClockWise())).setValue(ConveyorProperties.LEFT, this.isAdjacentBlockOfMyType(world, pos, state.getValue(FACING).getCounterClockWise()));

		return newState;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader view, BlockPos pos, ISelectionContext entityContext) {
		Direction facing = state.getValue(FACING);
		AxisAlignedBB step1 = new AxisAlignedBB(0, 0, (12F / 16F), 1, (3F / 16F), 1);
		AxisAlignedBB step2 = new AxisAlignedBB(0, 0, (8F / 16F), 1, (7F / 16F), (12F / 16F));
		AxisAlignedBB step3 = new AxisAlignedBB(0, 0, (4F / 16F), 1, (11F / 16F), (8F / 16F));
		AxisAlignedBB step4 = new AxisAlignedBB(0, 0, 0, 1, (15F / 16F), (4F / 16F));
		VoxelShape shape = VoxelShapes.or(RotationUtilities.getRotatedShape(step1, facing), RotationUtilities.getRotatedShape(step2, facing), RotationUtilities.getRotatedShape(step3, facing), RotationUtilities.getRotatedShape(step4, facing));

		return shape;
	}
}
