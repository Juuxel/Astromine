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
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AltarPedestalBlock extends WrenchableBlockWithEntity {
	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

	public AltarPedestalBlock(Settings settings) {
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
	public BlockEntity createBlockEntity() {
		return new AltarPedestalBlockEntity();
	}

	@Override
	public AbstractContainerMenu createScreenHandler(BlockState state, Level world, BlockPos pos, int syncId, Inventory playerInventory, Player player) {
		return null;
	}

	@Override
	public void populateScreenHandlerBuffer(BlockState state, Level world, BlockPos pos, ServerPlayer player, FriendlyByteBuf buffer) {

	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!world.isClientSide) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			ItemStack stackInHand = player.getItemInHand(hand);

			if (blockEntity instanceof AltarPedestalBlockEntity) {
				AltarPedestalBlockEntity displayerBlockEntity = (AltarPedestalBlockEntity) blockEntity;
				if (displayerBlockEntity.getStack(0).isEmpty()) {
					if (!stackInHand.isEmpty()) {
						displayerBlockEntity.setStack(0, stackInHand.split(1));
						displayerBlockEntity.sync();
						return InteractionResult.SUCCESS;
					}
					return InteractionResult.CONSUME;
				} else if (canMergeItems(stackInHand, displayerBlockEntity.getStack(0))) {
					ItemStack copy = stackInHand.copy();
					copy.grow(1);
					player.setItemInHand(hand, copy);
					displayerBlockEntity.setStack(0, ItemStack.EMPTY);
					player.playNotifySound(SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, .6F, 1);
					displayerBlockEntity.sync();
				} else if (stackInHand.isEmpty()) {
					player.setItemInHand(hand, displayerBlockEntity.getStack(0).copy());
					displayerBlockEntity.setStack(0, ItemStack.EMPTY);
					player.playNotifySound(SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, .6F, 1);
					displayerBlockEntity.sync();
				} else {
					return InteractionResult.CONSUME;
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
	public void onStateReplaced(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.is(newState.getBlock())) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof Container) {
				Containers.dropContents(world, pos.offset(0, 1, 0), (Container) blockEntity);
				world.updateNeighbourForOutputSignal(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public void onBreak(Level world, BlockPos pos, BlockState state, Player player) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof AltarPedestalBlockEntity) {
			((AltarPedestalBlockEntity) blockEntity).onRemove();
		}
		super.onBreak(world, pos, state, player);
	}
}
