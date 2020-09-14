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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import com.github.chainmailstudios.astromine.transportations.common.block.entity.ConveyorBlockEntity;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.Conveyable;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.Conveyor;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.ConveyorTypes;
import com.github.chainmailstudios.astromine.common.utilities.capability.block.FacingBlockWrenchable;
import com.github.chainmailstudios.astromine.common.utilities.RotationUtilities;

import javax.annotation.Nullable;

public class VerticalConveyorBlock extends HorizontalBlock implements ITileEntityProvider, Conveyor, FacingBlockWrenchable, IWaterLoggable {
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
	public TileEntity newBlockEntity(IBlockReader blockView) {
		return new VerticalConveyorBlockEntity();
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) ? Fluids.WATER.defaultFluidState() : super.getFluidState(state);
	}

	@Override
	public ActionResultType use(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult blockHitResult) {
		ConveyorBlockEntity blockEntity = (ConveyorBlockEntity) world.getBlockEntity(blockPos);

		if (!playerEntity.getItemInHand(hand).isEmpty() && Block.byItem(playerEntity.getItemInHand(hand).getItem()) instanceof Conveyor) {
			return ActionResultType.PASS;
		} else if (!playerEntity.getItemInHand(hand).isEmpty() && blockEntity.isEmpty()) {
			blockEntity.setStack(playerEntity.getItemInHand(hand));
			playerEntity.setItemInHand(hand, ItemStack.EMPTY);

			return ActionResultType.SUCCESS;
		} else if (!blockEntity.isEmpty()) {
			playerEntity.inventory.placeItemBackInInventory(world, blockEntity.getStack());
			blockEntity.removeStack();

			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}

	@Override
	public void onPlace(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
		Direction direction = blockState.getValue(FACING);

		world.neighborChanged(blockPos.relative(direction).above(), this, blockPos);
	}

	@Override
	public void onRemove(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
		Direction direction = blockState.getValue(FACING);
		if (blockState.getBlock() != blockState2.getBlock()) {
			TileEntity blockEntity_1 = world.getBlockEntity(blockPos);
			if (blockEntity_1 instanceof VerticalConveyorBlockEntity) {
				((VerticalConveyorBlockEntity) blockEntity_1).setRemoved(true);
				InventoryHelper.dropItemStack(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), ((VerticalConveyorBlockEntity) blockEntity_1).getStack());
				world.updateNeighbourForOutputSignal(blockPos, this);
			}

			world.neighborChanged(blockPos.relative(direction).above(), this, blockPos);
			super.onRemove(blockState, world, blockPos, blockState2, boolean_1);
		}
	}

	@Override
	public BlockState updateShape(BlockState blockState, Direction fromDirection, BlockState fromState, IWorld world, BlockPos blockPos, BlockPos fromPos) {
		BlockState newState = blockState;
		Direction direction = newState.getValue(FACING);

		BlockPos frontPos = blockPos.relative(direction.getOpposite());
		BlockPos upPos = blockPos.above();
		BlockPos conveyorPos = blockPos.relative(direction).above();

		TileEntity frontBlockEntity = world.getBlockEntity(frontPos);
		if (frontBlockEntity instanceof Conveyable && ((Conveyable) frontBlockEntity).isOutputSide(direction, getType())) {
			newState = newState.setValue(ConveyorProperties.FRONT, true);
		} else newState = newState.setValue(ConveyorProperties.FRONT, false);

		TileEntity conveyorBlockEntity = world.getBlockEntity(conveyorPos);
		if (world.isEmptyBlock(upPos) && conveyorBlockEntity instanceof Conveyable && !((Conveyable) conveyorBlockEntity).hasBeenRemoved() && ((Conveyable) conveyorBlockEntity).validInputSide(direction.getOpposite()))
			newState = newState.setValue(ConveyorProperties.CONVEYOR, true);
		else newState = newState.setValue(ConveyorProperties.CONVEYOR, false);

		return newState;
	}

	@Override
	public void neighborChanged(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean boolean_1) {
		Direction direction = blockState.getValue(FACING);
		ConveyorBlockEntity blockEntity = (ConveyorBlockEntity) world.getBlockEntity(blockPos);

		BlockPos upPos = blockPos.above();
		BlockPos conveyorPos = blockPos.relative(direction).above();

		TileEntity upBlockEntity = world.getBlockEntity(upPos);
		if (upBlockEntity instanceof Conveyable && ((Conveyable) upBlockEntity).validInputSide(Direction.DOWN))
			((VerticalConveyorBlockEntity) blockEntity).setUp(true);
		else((VerticalConveyorBlockEntity) blockEntity).setUp(false);

		if (blockPos2.getY() > blockPos.getY()) {
			TileEntity conveyorBlockEntity = world.getBlockEntity(conveyorPos);
			checkForConveyor(world, blockState, conveyorBlockEntity, direction, blockPos, upPos);
		}
	}

	public void checkForConveyor(World world, BlockState blockState, TileEntity conveyorBlockEntity, Direction direction, BlockPos pos, BlockPos upPos) {
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
	public int getAnalogOutputSignal(BlockState blockState, World world, BlockPos blockPos) {
		return ((ConveyorBlockEntity) world.getBlockEntity(blockPos)).isEmpty() ? 0 : 15;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> stateManagerBuilder) {
		stateManagerBuilder.add(FACING, ConveyorProperties.FRONT, ConveyorProperties.CONVEYOR, BlockStateProperties.WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		World world = context.getLevel();
		BlockPos blockPos = context.getClickedPos();
		BlockState newState = this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());

		newState = newState.updateShape(null, newState, world, blockPos, blockPos);

		return newState.setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getBlockState(context.getClickedPos()).getBlock() == Blocks.WATER);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState blockState_1, IBlockReader blockView_1, BlockPos blockPos_1) {
		return true;
	}

	@Override
	public VoxelShape getShape(BlockState blockState, IBlockReader blockView, BlockPos blockPos, ISelectionContext entityContext) {
		VoxelShape box1 = RotationUtilities.getRotatedShape(new AxisAlignedBB(0, 0, 0, 1, 1, (4F / 16F)), blockState.getValue(FACING));
		VoxelShape box2 = RotationUtilities.getRotatedShape(new AxisAlignedBB(0, 0, 0, 1, (4F / 16F), 1), blockState.getValue(FACING));

		if (blockState.getValue(ConveyorProperties.FRONT)) {
			return VoxelShapes.or(box1, box2);
		} else {
			return box1;
		}
	}
}
