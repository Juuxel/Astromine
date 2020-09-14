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

package com.github.chainmailstudios.astromine.transportations.common.block.entity.base;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import com.github.chainmailstudios.astromine.common.inventory.DoubleStackInventory;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.Conveyable;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.ConveyorConveyable;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.ConveyorTypes;

public class AbstractConveyableBlockEntity extends BlockEntity implements Conveyable, DoubleStackInventory, BlockEntityClientSerializable, RenderAttachmentBlockEntity, TickableBlockEntity {
	int leftPosition = 0;
	int prevLeftPosition = 0;
	int rightPosition = 0;
	int prevRightPosition = 0;
	boolean hasBeenRemoved = false;
	boolean left = false;
	boolean right = false;
	private NonNullList<ItemStack> stacks = NonNullList.withSize(2, ItemStack.EMPTY);

	public AbstractConveyableBlockEntity(BlockEntityType type) {
		super(type);
	}

	@Override
	public void tick() {
		Direction direction = getBlockState().getValue(HorizontalDirectionalBlock.FACING);
		int speed = 16;

		if (!getLeftStack().isEmpty()) {
			if (left) {
				BlockPos leftPos = getBlockPos().relative(direction.getCounterClockWise());
				if (getLevel().getBlockEntity(leftPos) instanceof Conveyable) {
					Conveyable conveyable = (Conveyable) getLevel().getBlockEntity(leftPos);
					handleLeftMovement(conveyable, speed, true);
				}
			} else {
				setLeftPosition(0);
			}
		} else {
			setLeftPosition(0);
		}

		if (!getRightStack().isEmpty()) {
			if (right) {
				BlockPos rightPos = getBlockPos().relative(direction.getClockWise());
				if (getLevel().getBlockEntity(rightPos) instanceof Conveyable) {
					Conveyable conveyable = (Conveyable) getLevel().getBlockEntity(rightPos);
					handleRightMovement(conveyable, speed, true);
				}
			} else {
				setRightPosition(0);
			}
		} else {
			setRightPosition(0);
		}
	}

	public void handleLeftMovement(Conveyable conveyable, int speed, boolean transition) {
		if (conveyable.accepts(getLeftStack())) {
			if (leftPosition < speed) {
				setLeftPosition(getLeftPosition() + 1);
			} else if (transition && leftPosition >= speed) {
				conveyable.give(getLeftStack());
				if (!level.isClientSide() || level.isClientSide && Minecraft.getInstance().player.distanceToSqr(Vec3.atLowerCornerOf(getBlockPos())) > 40 * 40)
					removeLeftStack();
			}
		} else if (conveyable instanceof ConveyorConveyable) {
			ConveyorConveyable conveyor = (ConveyorConveyable) conveyable;

			if (leftPosition < speed && leftPosition + 4 < conveyor.getPosition() && conveyor.getPosition() > 4) {
				setLeftPosition(getLeftPosition() + 1);
			} else {
				prevLeftPosition = leftPosition;
			}
		} else if (leftPosition > 0) {
			setLeftPosition(leftPosition - 1);
		} else if (prevLeftPosition != leftPosition) {
			prevLeftPosition = leftPosition;
		}
	}

	public void handleRightMovement(Conveyable conveyable, int speed, boolean transition) {
		if (conveyable.accepts(getRightStack())) {
			if (rightPosition < speed) {
				setRightPosition(getRightPosition() + 1);
			} else if (transition && rightPosition >= speed) {
				conveyable.give(getRightStack());
				if (!level.isClientSide() || level.isClientSide && Minecraft.getInstance().player.distanceToSqr(Vec3.atLowerCornerOf(getBlockPos())) > 40 * 40)
					removeRightStack();
			}
		} else if (conveyable instanceof ConveyorConveyable) {
			ConveyorConveyable conveyor = (ConveyorConveyable) conveyable;

			if (rightPosition < speed && rightPosition + 4 < conveyor.getPosition() && conveyor.getPosition() > 4) {
				setRightPosition(getRightPosition() + 1);
			} else {
				prevRightPosition = rightPosition;
			}
		} else if (rightPosition > 0) {
			setRightPosition(rightPosition - 1);
		} else if (prevRightPosition != rightPosition) {
			prevRightPosition = rightPosition;
		}
	}

	@Override
	public NonNullList<ItemStack> getItems() {
		return stacks;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		DoubleStackInventory.super.setStack(slot, stack);
		if (!level.isClientSide())
			sendPacket((ServerLevel) level, save(new CompoundTag()));
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack stack = DoubleStackInventory.super.removeStack(slot);
		leftPosition = 0;
		rightPosition = 0;
		prevLeftPosition = 0;
		prevRightPosition = 0;
		if (!level.isClientSide())
			sendPacket((ServerLevel) level, save(new CompoundTag()));
		return stack;
	}

	@Override
	public void clear() {
		DoubleStackInventory.super.clear();
		if (!level.isClientSide())
			sendPacket((ServerLevel) level, save(new CompoundTag()));
	}

	public int getLeftPosition() {
		return leftPosition;
	}

	public void setLeftPosition(int leftPosition) {
		if (leftPosition == 0)
			this.prevLeftPosition = 0;
		else this.prevLeftPosition = this.leftPosition;
		this.leftPosition = leftPosition;
	}

	public int getRightPosition() {
		return rightPosition;
	}

	public void setRightPosition(int rightPosition) {
		if (rightPosition == 0)
			this.prevRightPosition = 0;
		else this.prevRightPosition = this.rightPosition;
		this.rightPosition = rightPosition;
	}

	@Override
	public boolean hasBeenRemoved() {
		return hasBeenRemoved;
	}

	@Override
	public void setRemoved(boolean hasBeenRemoved) {
		this.hasBeenRemoved = hasBeenRemoved;
	}

	public boolean hasLeft() {
		return left;
	}

	public boolean hasRight() {
		return right;
	}

	public void setLeft(boolean left) {
		this.left = left;
		setChanged();
		if (!level.isClientSide())
			sendPacket((ServerLevel) level, save(new CompoundTag()));
	}

	public void setRight(boolean right) {
		this.right = right;
		setChanged();
		if (!level.isClientSide())
			sendPacket((ServerLevel) level, save(new CompoundTag()));
	}

	@Override
	public boolean accepts(ItemStack stack) {
		return !(!getLeftStack().isEmpty() && !getRightStack().isEmpty());
	}

	@Override
	public boolean validInputSide(Direction direction) {
		return direction == getBlockState().getValue(HorizontalDirectionalBlock.FACING).getOpposite();
	}

	@Override
	public boolean isOutputSide(Direction direction, ConveyorTypes type) {
		return getBlockState().getValue(HorizontalDirectionalBlock.FACING).getCounterClockWise() == direction || getBlockState().getValue(HorizontalDirectionalBlock.FACING).getClockWise() == direction;
	}

	@Override
	public void give(ItemStack stack) {

	}

	@Override
	public int[] getRenderAttachmentData() {
		return new int[]{ leftPosition, prevLeftPosition, rightPosition, prevRightPosition };
	}

	protected void sendPacket(ServerLevel w, CompoundTag tag) {
		tag.putString("id", BlockEntityType.getKey(getType()).toString());
		sendPacket(w, new ClientboundBlockEntityDataPacket(getBlockPos(), 127, tag));
	}

	protected void sendPacket(ServerLevel w, ClientboundBlockEntityDataPacket packet) {
		w.getPlayers(player -> player.distanceToSqr(Vec3.atLowerCornerOf(getBlockPos())) < 40 * 40).forEach(player -> player.connection.send(packet));
	}

	@Override
	public void setChanged() {
		super.setChanged();
	}

	@Override
	public void load(BlockState state, CompoundTag compoundTag) {
		super.load(state, compoundTag);
		getItems().set(0, ItemStack.of(compoundTag.getCompound("leftStack")));
		getItems().set(1, ItemStack.of(compoundTag.getCompound("rightStack")));
		left = compoundTag.getBoolean("left");
		right = compoundTag.getBoolean("right");
		leftPosition = compoundTag.getInt("leftPosition");
		prevLeftPosition = compoundTag.getInt("prevLeftPosition");
		rightPosition = compoundTag.getInt("rightPosition");
		prevRightPosition = compoundTag.getInt("prevRightPosition");
	}

	@Override
	public void fromClientTag(CompoundTag compoundTag) {
		load(getBlockState(), compoundTag);
	}

	@Override
	public CompoundTag save(CompoundTag compoundTag) {
		compoundTag.put("leftStack", getLeftStack().toTag(new CompoundTag()));
		compoundTag.put("rightStack", getRightStack().toTag(new CompoundTag()));
		compoundTag.putBoolean("left", left);
		compoundTag.putBoolean("right", right);
		compoundTag.putInt("leftPosition", leftPosition);
		compoundTag.putInt("prevLeftPosition", prevLeftPosition);
		compoundTag.putInt("rightPosition", rightPosition);
		compoundTag.putInt("prevRightPosition", prevRightPosition);
		return super.save(compoundTag);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return save(new CompoundTag());
	}

	@Override
	public CompoundTag toClientTag(CompoundTag compoundTag) {
		return save(compoundTag);
	}
}
