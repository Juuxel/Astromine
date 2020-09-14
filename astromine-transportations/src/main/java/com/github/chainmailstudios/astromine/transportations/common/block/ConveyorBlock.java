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
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
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
import com.github.chainmailstudios.astromine.transportations.common.conveyor.ConveyorConveyable;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.ConveyorTypes;

public class ConveyorBlock extends HorizontalBlock implements ITileEntityProvider, Conveyor, FacingBlockWrenchable, IWaterLoggable {
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
	public TileEntity newBlockEntity(IBlockReader blockView) {
		return new ConveyorBlockEntity();
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
	public void entityInside(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		BlockPos pos = new BlockPos(entity.position());

		if (!entity.isOnGround() || (entity.getY() - blockPos.getY()) != (4F / 16F))
			return;

		if (entity instanceof PlayerEntity && entity.isShiftKeyDown())
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
	public void onPlace(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
		updateDiagonals(world, this, blockPos);
	}

	@Override
	public void onRemove(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			TileEntity blockEntity_1 = world.getBlockEntity(blockPos);
			if (blockEntity_1 instanceof ConveyorBlockEntity) {
				((ConveyorBlockEntity) blockEntity_1).setRemoved(true);
				InventoryHelper.dropItemStack(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), ((ConveyorBlockEntity) blockEntity_1).getStack());
				world.updateNeighbourForOutputSignal(blockPos, this);
			}

			super.onRemove(blockState, world, blockPos, blockState2, boolean_1);
		}

		updateDiagonals(world, this, blockPos);
	}

	@Override
	public BlockState updateShape(BlockState blockState, Direction fromDirection, BlockState fromState, IWorld world, BlockPos blockPos, BlockPos fromPos) {
		BlockState newState = blockState;
		Direction direction = newState.getValue(FACING);
		boolean setBack = false;
		boolean backExists = false;

		BlockPos leftPos = blockPos.relative(direction.getCounterClockWise());
		BlockPos rightPos = blockPos.relative(direction.getClockWise());
		BlockPos backPos = blockPos.relative(direction.getOpposite());
		BlockPos upPos = blockPos.above();

		TileEntity leftBlockEntity = world.getBlockEntity(leftPos);
		TileEntity leftDownBlockEntity = world.getBlockEntity(leftPos.below());
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

		TileEntity rightBlockEntity = world.getBlockEntity(rightPos);
		TileEntity rightDownBlockEntity = world.getBlockEntity(rightPos.below());
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

		TileEntity backBlockEntity = world.getBlockEntity(backPos);
		TileEntity backDownBlockEntity = world.getBlockEntity(backPos.below());
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

		TileEntity upBlockEntity = world.getBlockEntity(upPos);
		if (upBlockEntity instanceof ConveyorConveyable && ((ConveyorConveyable) upBlockEntity).getConveyorType() == ConveyorTypes.NORMAL)
			newState = newState.setValue(ConveyorProperties.UP, true);
		else newState = newState.setValue(ConveyorProperties.UP, false);

		return newState;
	}

	@Override
	public void neighborChanged(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean boolean_1) {
		Direction direction = blockState.getValue(FACING);
		ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) world.getBlockEntity(blockPos);

		BlockPos frontPos = blockPos.relative(direction);

		TileEntity frontBlockEntity = world.getBlockEntity(blockPos.relative(direction));
		if (frontBlockEntity instanceof Conveyable && ((Conveyable) frontBlockEntity).validInputSide(direction.getOpposite()))
			conveyorBlockEntity.setFront(true);
		else conveyorBlockEntity.setFront(false);

		TileEntity frontAcrossBlockEntity = world.getBlockEntity(blockPos.relative(direction).relative(direction));
		if (frontBlockEntity instanceof ConveyorConveyable && ((ConveyorConveyable) frontBlockEntity).validInputSide(direction.getOpposite()) && ((ConveyorConveyable) frontBlockEntity).validInputSide(direction) && frontAcrossBlockEntity instanceof ConveyorConveyable && world
			.getBlockState(blockPos.relative(direction).relative(direction)).getValue(HorizontalBlock.FACING) == direction.getOpposite())
			conveyorBlockEntity.setAcross(true);
		else conveyorBlockEntity.setAcross(false);

		TileEntity downBlockEntity = world.getBlockEntity(blockPos.relative(direction).below());
		if (downBlockEntity instanceof Conveyable && ((Conveyable) downBlockEntity).validInputSide(Direction.UP))
			conveyorBlockEntity.setDown(true);
		else conveyorBlockEntity.setDown(false);

		if (blockPos2.getY() < blockPos.getY()) {
			BlockState newState = blockState.updateShape(direction, blockState, world, blockPos, blockPos);
			world.setBlock(blockPos, newState, 1 | 2);
		}
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
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> stateManagerBuilder) {
		stateManagerBuilder.add(FACING, ConveyorProperties.LEFT, ConveyorProperties.RIGHT, ConveyorProperties.BACK, ConveyorProperties.UP, BlockStateProperties.WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) ? Fluids.WATER.defaultFluidState() : super.getFluidState(state);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		World world = context.getLevel();
		BlockPos blockPos = context.getClickedPos();
		BlockState newState = this.defaultBlockState().setValue(FACING, context.getPlayer().isShiftKeyDown() ? context.getHorizontalDirection().getOpposite() : context.getHorizontalDirection()).setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getBlockState(context.getClickedPos()).getBlock() == Blocks.WATER);

		newState = newState.updateShape(null, newState, world, blockPos, blockPos);

		return newState;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState blockState_1, IBlockReader blockView_1, BlockPos blockPos_1) {
		return false;
	}

	@Override
	public VoxelShape getShape(BlockState blockState, IBlockReader blockView, BlockPos blockPos, ISelectionContext entityContext) {
		VoxelShape conveyor = VoxelShapes.box(0, 0, 0, 1, (4F / 16F), 1);
		if (blockState.getValue(ConveyorProperties.UP)) {
			return VoxelShapes.block();
		}
		return conveyor;
	}
}
