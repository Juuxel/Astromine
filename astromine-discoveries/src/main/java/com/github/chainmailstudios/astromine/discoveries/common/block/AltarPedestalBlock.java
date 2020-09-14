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

package com.github.chainmailstudios.astromine.discoveries.common.block;

import com.github.chainmailstudios.astromine.common.block.base.WrenchableBlockWithEntity;
import com.github.chainmailstudios.astromine.discoveries.common.block.entity.AltarPedestalBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class AltarPedestalBlock extends WrenchableBlockWithEntity {
	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

	public AltarPedestalBlock(Properties settings) {
		super(settings);
	}

	public static boolean canMergeItems(ItemStack first, ItemStack second) {
		if (first.getItem() != second.getItem()) {
			return false;
		} else if (first.getDamageValue() != second.getDamageValue()) {
			return false;
		} else if (first.getCount() >= first.getMaxStackSize()) {
			return false;
		} else {
			return ItemStack.tagMatches(first, second);
		}
	}

	@Override
	public boolean hasScreenHandler() {
		return false;
	}

	@Override
	public TileEntity createBlockEntity() {
		return new AltarPedestalBlockEntity();
	}

	@Override
	public Container createScreenHandler(BlockState state, World world, BlockPos pos, int syncId, PlayerInventory playerInventory, PlayerEntity player) {
		return null;
	}

	@Override
	public void populateScreenHandlerBuffer(BlockState state, World world, BlockPos pos, ServerPlayerEntity player, PacketBuffer buffer) {

	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public ActionResultType onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (!world.isClientSide) {
			TileEntity blockEntity = world.getBlockEntity(pos);
			ItemStack stackInHand = player.getItemInHand(hand);

			if (blockEntity instanceof AltarPedestalBlockEntity) {
				AltarPedestalBlockEntity displayerBlockEntity = (AltarPedestalBlockEntity) blockEntity;
				if (displayerBlockEntity.getStack(0).isEmpty()) {
					if (!stackInHand.isEmpty()) {
						displayerBlockEntity.setStack(0, stackInHand.split(1));
						displayerBlockEntity.sync();
						return ActionResultType.SUCCESS;
					}
					return ActionResultType.CONSUME;
				} else if (canMergeItems(stackInHand, displayerBlockEntity.getStack(0))) {
					ItemStack copy = stackInHand.copy();
					copy.grow(1);
					player.setItemInHand(hand, copy);
					displayerBlockEntity.setStack(0, ItemStack.EMPTY);
					player.playNotifySound(SoundEvents.ITEM_PICKUP, SoundCategory.BLOCKS, .6F, 1);
					displayerBlockEntity.sync();
				} else if (stackInHand.isEmpty()) {
					player.setItemInHand(hand, displayerBlockEntity.getStack(0).copy());
					displayerBlockEntity.setStack(0, ItemStack.EMPTY);
					player.playNotifySound(SoundEvents.ITEM_PICKUP, SoundCategory.BLOCKS, .6F, 1);
					displayerBlockEntity.sync();
				} else {
					return ActionResultType.CONSUME;
				}
			}
		}

		return super.onUse(state, world, pos, player, hand, hit);
	}

	@Override
	protected boolean saveTagToDroppedItem() {
		return false;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.is(newState.getBlock())) {
			TileEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof IInventory) {
				InventoryHelper.dropContents(world, pos.offset(0, 1, 0), (IInventory) blockEntity);
				world.updateNeighbourForOutputSignal(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof AltarPedestalBlockEntity) {
			((AltarPedestalBlockEntity) blockEntity).onRemove();
		}
		super.onBreak(world, pos, state, player);
	}
}
