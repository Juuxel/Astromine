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
import com.github.chainmailstudios.astromine.transportations.common.block.entity.VerticalConveyorBlockEntity;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.github.chainmailstudios.astromine.transportations.common.block.entity.ConveyorBlockEntity;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.Conveyable;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.Conveyor;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.ConveyorTypes;
import com.github.chainmailstudios.astromine.common.utilities.capability.block.FacingBlockWrenchable;
import com.github.chainmailstudios.astromine.common.utilities.RotationUtilities;

import javax.annotation.Nullable;

public class VerticalConveyorBlock extends HorizontalDirectionalBlock implements EntityBlock, Conveyor, FacingBlockWrenchable, SimpleWaterloggedBlock {
	private int speed;

	public VerticalConveyorBlock(Properties settings, int speed) {
		super(settings);

		this.speed = speed;
		registerDefaultState(defaultBlockState().setValue(ConveyorProperties.FRONT, false).setValue(ConveyorProperties.CONVEYOR, false).setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	public int getSpeed() {
		return speed;
	}

	@Override
	public ConveyorTypes getType() {
		return ConveyorTypes.VERTICAL;
	}

	@Override
	public BlockEntity newBlockEntity(BlockGetter blockView) {
		return new VerticalConveyorBlockEntity();
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) ? Fluids.WATER.defaultFluidState() : super.getFluidState(state);
	}

	@Override
	public InteractionResult use(BlockState blockState, Level world, BlockPos blockPos, Player playerEntity, InteractionHand hand, BlockHitResult blockHitResult) {
		ConveyorBlockEntity blockEntity = (ConveyorBlockEntity) world.getBlockEntity(blockPos);

		if (!playerEntity.getItemInHand(hand).isEmpty() && Block.byItem(playerEntity.getItemInHand(hand).getItem()) instanceof Conveyor) {
			return InteractionResult.PASS;
		} else if (!playerEntity.getItemInHand(hand).isEmpty() && blockEntity.isEmpty()) {
			blockEntity.setStack(playerEntity.getItemInHand(hand));
			playerEntity.setItemInHand(hand, ItemStack.EMPTY);

			return InteractionResult.SUCCESS;
		} else if (!blockEntity.isEmpty()) {
			playerEntity.inventory.placeItemBackInInventory(world, blockEntity.getStack());
			blockEntity.removeStack();

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	public void onPlace(BlockState blockState, Level world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
		Direction direction = blockState.getValue(FACING);

		world.neighborChanged(blockPos.relative(direction).above(), this, blockPos);
	}

	@Override
	public void onRemove(BlockState blockState, Level world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
		Direction direction = blockState.getValue(FACING);
		if (blockState.getBlock() != blockState2.getBlock()) {
			BlockEntity blockEntity_1 = world.getBlockEntity(blockPos);
			if (blockEntity_1 instanceof VerticalConveyorBlockEntity) {
				((VerticalConveyorBlockEntity) blockEntity_1).setRemoved(true);
				Containers.dropItemStack(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), ((VerticalConveyorBlockEntity) blockEntity_1).getStack());
				world.updateNeighbourForOutputSignal(blockPos, this);
			}

			world.neighborChanged(blockPos.relative(direction).above(), this, blockPos);
			super.onRemove(blockState, world, blockPos, blockState2, boolean_1);
		}
	}

	@Override
	public BlockState updateShape(BlockState blockState, Direction fromDirection, BlockState fromState, LevelAccessor world, BlockPos blockPos, BlockPos fromPos) {
		BlockState newState = blockState;
		Direction direction = newState.getValue(FACING);

		BlockPos frontPos = blockPos.relative(direction.getOpposite());
		BlockPos upPos = blockPos.above();
		BlockPos conveyorPos = blockPos.relative(direction).above();

		BlockEntity frontBlockEntity = world.getBlockEntity(frontPos);
		if (frontBlockEntity instanceof Conveyable && ((Conveyable) frontBlockEntity).isOutputSide(direction, getType())) {
			newState = newState.setValue(ConveyorProperties.FRONT, true);
		} else newState = newState.setValue(ConveyorProperties.FRONT, false);

		BlockEntity conveyorBlockEntity = world.getBlockEntity(conveyorPos);
		if (world.isEmptyBlock(upPos) && conveyorBlockEntity instanceof Conveyable && !((Conveyable) conveyorBlockEntity).hasBeenRemoved() && ((Conveyable) conveyorBlockEntity).validInputSide(direction.getOpposite()))
			newState = newState.setValue(ConveyorProperties.CONVEYOR, true);
		else newState = newState.setValue(ConveyorProperties.CONVEYOR, false);

		return newState;
	}

	@Override
	public void neighborChanged(BlockState blockState, Level world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean boolean_1) {
		Direction direction = blockState.getValue(FACING);
		ConveyorBlockEntity blockEntity = (ConveyorBlockEntity) world.getBlockEntity(blockPos);

		BlockPos upPos = blockPos.above();
		BlockPos conveyorPos = blockPos.relative(direction).above();

		BlockEntity upBlockEntity = world.getBlockEntity(upPos);
		if (upBlockEntity instanceof Conveyable && ((Conveyable) upBlockEntity).validInputSide(Direction.DOWN))
			((VerticalConveyorBlockEntity) blockEntity).setUp(true);
		else((VerticalConveyorBlockEntity) blockEntity).setUp(false);

		if (blockPos2.getY() > blockPos.getY()) {
			BlockEntity conveyorBlockEntity = world.getBlockEntity(conveyorPos);
			checkForConveyor(world, blockState, conveyorBlockEntity, direction, blockPos, upPos);
		}
	}

	public void checkForConveyor(Level world, BlockState blockState, BlockEntity conveyorBlockEntity, Direction direction, BlockPos pos, BlockPos upPos) {
		BlockState newState = blockState;

		if (world.isEmptyBlock(upPos) && conveyorBlockEntity instanceof Conveyable && !((Conveyable) conveyorBlockEntity).hasBeenRemoved() && ((Conveyable) conveyorBlockEntity).validInputSide(direction.getOpposite())) {
			newState = newState.setValue(ConveyorProperties.CONVEYOR, true);
		} else {
			newState = newState.setValue(ConveyorProperties.CONVEYOR, false);
		}

		world.setBlock(pos, newState, 8);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState blockState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos blockPos) {
		return ((ConveyorBlockEntity) world.getBlockEntity(blockPos)).isEmpty() ? 0 : 15;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManagerBuilder) {
		stateManagerBuilder.add(FACING, ConveyorProperties.FRONT, ConveyorProperties.CONVEYOR, BlockStateProperties.WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level world = context.getLevel();
		BlockPos blockPos = context.getClickedPos();
		BlockState newState = this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());

		newState = newState.updateShape(null, newState, world, blockPos, blockPos);

		return newState.setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getBlockState(context.getClickedPos()).getBlock() == Blocks.WATER);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState blockState_1, BlockGetter blockView_1, BlockPos blockPos_1) {
		return true;
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter blockView, BlockPos blockPos, CollisionContext entityContext) {
		VoxelShape box1 = RotationUtilities.getRotatedShape(new AABB(0, 0, 0, 1, 1, (4F / 16F)), blockState.getValue(FACING));
		VoxelShape box2 = RotationUtilities.getRotatedShape(new AABB(0, 0, 0, 1, (4F / 16F), 1), blockState.getValue(FACING));

		if (blockState.getValue(ConveyorProperties.FRONT)) {
			return Shapes.or(box1, box2);
		} else {
			return box1;
		}
	}
}
