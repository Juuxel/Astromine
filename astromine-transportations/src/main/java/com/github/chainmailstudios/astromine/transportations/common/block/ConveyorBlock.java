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
import com.github.chainmailstudios.astromine.common.utilities.MovementUtilities;
import com.github.chainmailstudios.astromine.transportations.common.block.property.ConveyorProperties;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.github.chainmailstudios.astromine.transportations.common.block.entity.ConveyorBlockEntity;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.Conveyable;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.Conveyor;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.ConveyorConveyable;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.ConveyorTypes;

public class ConveyorBlock extends HorizontalDirectionalBlock implements EntityBlock, Conveyor, FacingBlockWrenchable, SimpleWaterloggedBlock {
	private int speed;

	public ConveyorBlock(Properties settings, int speed) {
		super(settings);

		this.speed = speed;
		registerDefaultState(defaultBlockState().setValue(ConveyorProperties.LEFT, false).setValue(ConveyorProperties.RIGHT, false).setValue(ConveyorProperties.BACK, false).setValue(ConveyorProperties.UP, false).setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	public int getSpeed() {
		return speed;
	}

	@Override
	public ConveyorTypes getType() {
		return ConveyorTypes.NORMAL;
	}

	@Override
	public BlockEntity newBlockEntity(BlockGetter blockView) {
		return new ConveyorBlockEntity();
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
	public void entityInside(BlockState blockState, Level world, BlockPos blockPos, Entity entity) {
		BlockPos pos = new BlockPos(entity.position());

		if (!entity.isOnGround() || (entity.getY() - blockPos.getY()) != (4F / 16F))
			return;

		if (entity instanceof Player && entity.isShiftKeyDown())
			return;

		Direction direction = blockState.getValue(FACING);

		if (entity instanceof ItemEntity && pos.equals(blockPos) && world.getBlockEntity(blockPos) instanceof ConveyorBlockEntity) {
			ConveyorBlockEntity blockEntity = (ConveyorBlockEntity) world.getBlockEntity(blockPos);

			if (blockEntity.isEmpty()) {
				blockEntity.setStack(((ItemEntity) entity).getItem());
				entity.remove();
			}
		} else if (!(entity instanceof ItemEntity)) {
			MovementUtilities.pushEntity(entity, blockPos, 2.0F / getSpeed(), direction);
		}
	}

	@Override
	public void onPlace(BlockState blockState, Level world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
		updateDiagonals(world, this, blockPos);
	}

	@Override
	public void onRemove(BlockState blockState, Level world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			BlockEntity blockEntity_1 = world.getBlockEntity(blockPos);
			if (blockEntity_1 instanceof ConveyorBlockEntity) {
				((ConveyorBlockEntity) blockEntity_1).setRemoved(true);
				Containers.dropItemStack(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), ((ConveyorBlockEntity) blockEntity_1).getStack());
				world.updateNeighbourForOutputSignal(blockPos, this);
			}

			super.onRemove(blockState, world, blockPos, blockState2, boolean_1);
		}

		updateDiagonals(world, this, blockPos);
	}

	@Override
	public BlockState updateShape(BlockState blockState, Direction fromDirection, BlockState fromState, LevelAccessor world, BlockPos blockPos, BlockPos fromPos) {
		BlockState newState = blockState;
		Direction direction = newState.getValue(FACING);
		boolean setBack = false;
		boolean backExists = false;

		BlockPos leftPos = blockPos.relative(direction.getCounterClockWise());
		BlockPos rightPos = blockPos.relative(direction.getClockWise());
		BlockPos backPos = blockPos.relative(direction.getOpposite());
		BlockPos upPos = blockPos.above();

		BlockEntity leftBlockEntity = world.getBlockEntity(leftPos);
		BlockEntity leftDownBlockEntity = world.getBlockEntity(leftPos.below());
		if (leftBlockEntity instanceof Conveyable && ((Conveyable) leftBlockEntity).isOutputSide(direction.getClockWise(), getType())) {
			newState = newState.setValue(ConveyorProperties.LEFT, true);
			if (backExists) {
				newState = newState.setValue(ConveyorProperties.BACK, false);
				setBack = true;
			}
		} else if (leftDownBlockEntity instanceof ConveyorConveyable && ((ConveyorConveyable) leftDownBlockEntity).getConveyorType() == ConveyorTypes.VERTICAL && ((ConveyorConveyable) leftDownBlockEntity).isOutputSide(direction.getClockWise(), getType())) {
			newState = newState.setValue(ConveyorProperties.LEFT, true);
			if (backExists) {
				newState = newState.setValue(ConveyorProperties.BACK, false);
				setBack = true;
			}
		} else {
			newState = newState.setValue(ConveyorProperties.LEFT, false);
			newState = newState.setValue(ConveyorProperties.BACK, true);
		}

		BlockEntity rightBlockEntity = world.getBlockEntity(rightPos);
		BlockEntity rightDownBlockEntity = world.getBlockEntity(rightPos.below());
		if (rightBlockEntity instanceof Conveyable && ((Conveyable) rightBlockEntity).isOutputSide(direction.getCounterClockWise(), getType())) {
			newState = newState.setValue(ConveyorProperties.RIGHT, true);
			if (backExists) {
				newState = newState.setValue(ConveyorProperties.BACK, false);
			}
		} else if (rightDownBlockEntity instanceof ConveyorConveyable && ((ConveyorConveyable) rightDownBlockEntity).getConveyorType() == ConveyorTypes.VERTICAL && ((ConveyorConveyable) rightDownBlockEntity).isOutputSide(direction.getCounterClockWise(), getType())) {
			newState = newState.setValue(ConveyorProperties.RIGHT, true);
			if (backExists) {
				newState = newState.setValue(ConveyorProperties.BACK, false);
			}
		} else {
			newState = newState.setValue(ConveyorProperties.RIGHT, false);
			if (!setBack) {
				newState = newState.setValue(ConveyorProperties.BACK, true);
			}
		}

		BlockEntity backBlockEntity = world.getBlockEntity(backPos);
		BlockEntity backDownBlockEntity = world.getBlockEntity(backPos.below());
		if (backBlockEntity instanceof Conveyable && ((Conveyable) backBlockEntity).isOutputSide(direction, getType())) {
			newState = newState.setValue(ConveyorProperties.BACK, false);
		} else if (backDownBlockEntity instanceof ConveyorConveyable && !((ConveyorConveyable) backDownBlockEntity).hasBeenRemoved() && ((ConveyorConveyable) backDownBlockEntity).getConveyorType() == ConveyorTypes.VERTICAL && ((ConveyorConveyable) backDownBlockEntity)
			.isOutputSide(direction, getType())) {
				newState = newState.setValue(ConveyorProperties.BACK, false);
			} else if (newState.getValue(ConveyorProperties.LEFT) || newState.getValue(ConveyorProperties.RIGHT)) {
				newState = newState.setValue(ConveyorProperties.BACK, true);
			} else {
				newState = newState.setValue(ConveyorProperties.BACK, false);
			}

		BlockEntity upBlockEntity = world.getBlockEntity(upPos);
		if (upBlockEntity instanceof ConveyorConveyable && ((ConveyorConveyable) upBlockEntity).getConveyorType() == ConveyorTypes.NORMAL)
			newState = newState.setValue(ConveyorProperties.UP, true);
		else newState = newState.setValue(ConveyorProperties.UP, false);

		return newState;
	}

	@Override
	public void neighborChanged(BlockState blockState, Level world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean boolean_1) {
		Direction direction = blockState.getValue(FACING);
		ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) world.getBlockEntity(blockPos);

		BlockPos frontPos = blockPos.relative(direction);

		BlockEntity frontBlockEntity = world.getBlockEntity(blockPos.relative(direction));
		if (frontBlockEntity instanceof Conveyable && ((Conveyable) frontBlockEntity).validInputSide(direction.getOpposite()))
			conveyorBlockEntity.setFront(true);
		else conveyorBlockEntity.setFront(false);

		BlockEntity frontAcrossBlockEntity = world.getBlockEntity(blockPos.relative(direction).relative(direction));
		if (frontBlockEntity instanceof ConveyorConveyable && ((ConveyorConveyable) frontBlockEntity).validInputSide(direction.getOpposite()) && ((ConveyorConveyable) frontBlockEntity).validInputSide(direction) && frontAcrossBlockEntity instanceof ConveyorConveyable && world
			.getBlockState(blockPos.relative(direction).relative(direction)).getValue(HorizontalDirectionalBlock.FACING) == direction.getOpposite())
			conveyorBlockEntity.setAcross(true);
		else conveyorBlockEntity.setAcross(false);

		BlockEntity downBlockEntity = world.getBlockEntity(blockPos.relative(direction).below());
		if (downBlockEntity instanceof Conveyable && ((Conveyable) downBlockEntity).validInputSide(Direction.UP))
			conveyorBlockEntity.setDown(true);
		else conveyorBlockEntity.setDown(false);

		if (blockPos2.getY() < blockPos.getY()) {
			BlockState newState = blockState.updateShape(direction, blockState, world, blockPos, blockPos);
			world.setBlock(blockPos, newState, 1 | 2);
		}
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
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManagerBuilder) {
		stateManagerBuilder.add(FACING, ConveyorProperties.LEFT, ConveyorProperties.RIGHT, ConveyorProperties.BACK, ConveyorProperties.UP, BlockStateProperties.WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) ? Fluids.WATER.defaultFluidState() : super.getFluidState(state);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level world = context.getLevel();
		BlockPos blockPos = context.getClickedPos();
		BlockState newState = this.defaultBlockState().setValue(FACING, context.getPlayer().isShiftKeyDown() ? context.getHorizontalDirection().getOpposite() : context.getHorizontalDirection()).setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getBlockState(context.getClickedPos()).getBlock() == Blocks.WATER);

		newState = newState.updateShape(null, newState, world, blockPos, blockPos);

		return newState;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState blockState_1, BlockGetter blockView_1, BlockPos blockPos_1) {
		return false;
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter blockView, BlockPos blockPos, CollisionContext entityContext) {
		VoxelShape conveyor = Shapes.box(0, 0, 0, 1, (4F / 16F), 1);
		if (blockState.getValue(ConveyorProperties.UP)) {
			return Shapes.block();
		}
		return conveyor;
	}
}
