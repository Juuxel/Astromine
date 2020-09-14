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
import com.github.chainmailstudios.astromine.transportations.common.block.entity.SplitterBlockEntity;
import com.github.chainmailstudios.astromine.transportations.common.block.entity.base.AbstractConveyableBlockEntity;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.Conveyable;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.ConveyableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class SplitterBlock extends HorizontalBlock implements ITileEntityProvider, ConveyableBlock, FacingBlockWrenchable {
	public SplitterBlock(Properties settings) {
		super(settings);
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader blockView) {
		return new SplitterBlockEntity();
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> stateManagerBuilder) {
		stateManagerBuilder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext itemPlacementContext) {
		return this.defaultBlockState().setValue(FACING, itemPlacementContext.getPlayer().isShiftKeyDown() ? itemPlacementContext.getHorizontalDirection().getOpposite() : itemPlacementContext.getHorizontalDirection());
	}

	@Override
	public void onPlace(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
		updateDiagonals(world, this, blockPos);
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof AbstractConveyableBlockEntity) {
				InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((AbstractConveyableBlockEntity) blockEntity).getLeftStack());
				InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((AbstractConveyableBlockEntity) blockEntity).getRightStack());
				((AbstractConveyableBlockEntity) blockEntity).setRemoved(true);
			}

			super.onRemove(state, world, pos, newState, notify);
		}

		updateDiagonals(world, this, pos);
	}

	@Override
	public void neighborChanged(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean boolean_1) {
		Direction direction = blockState.getValue(FACING);
		AbstractConveyableBlockEntity machineBlockEntity = (AbstractConveyableBlockEntity) world.getBlockEntity(blockPos);

		BlockPos leftPos = blockPos.relative(direction.getCounterClockWise());
		BlockPos rightPos = blockPos.relative(direction.getClockWise());

		TileEntity leftBlockEntity = world.getBlockEntity(leftPos);
		if (leftBlockEntity instanceof Conveyable && ((Conveyable) leftBlockEntity).validInputSide(direction.getClockWise()))
			machineBlockEntity.setLeft(true);
		else machineBlockEntity.setLeft(false);

		TileEntity rightBlockEntity = world.getBlockEntity(rightPos);
		if (rightBlockEntity instanceof Conveyable && ((Conveyable) rightBlockEntity).validInputSide(direction.getCounterClockWise()))
			machineBlockEntity.setRight(true);
		else machineBlockEntity.setRight(false);
	}
}
