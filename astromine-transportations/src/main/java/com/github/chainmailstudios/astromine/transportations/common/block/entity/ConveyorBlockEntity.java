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

package com.github.chainmailstudios.astromine.transportations.common.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import com.github.chainmailstudios.astromine.common.inventory.SingularStackInventory;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.Conveyable;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.Conveyor;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.ConveyorConveyable;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.ConveyorTypes;
import com.github.chainmailstudios.astromine.transportations.registry.AstromineTransportationsBlockEntityTypes;

public class ConveyorBlockEntity extends TileEntity implements ConveyorConveyable, SingularStackInventory, ITickableTileEntity {
	protected boolean front = false;
	protected boolean down = false;
	protected boolean across = false;
	protected int position = 0;
	protected int prevPosition = 0;
	protected boolean hasBeenRemoved = false;
	private NonNullList<ItemStack> stacks = NonNullList.withSize(1, ItemStack.EMPTY);

	public ConveyorBlockEntity() {
		super(AstromineTransportationsBlockEntityTypes.CONVEYOR);
	}

	public ConveyorBlockEntity(TileEntityType type) {
		super(type);
	}

	@Override
	public void tick() {
		Direction direction = getBlockState().getValue(HorizontalBlock.FACING);
		int speed = ((Conveyor) getBlockState().getBlock()).getSpeed();

		if (!isEmpty()) {
			if (across) {
				BlockPos frontPos = getBlockPos().relative(direction);
				BlockPos frontAcrossPos = frontPos.relative(direction);
				if (getLevel().getBlockEntity(frontPos) instanceof ConveyorConveyable && getLevel().getBlockEntity(frontAcrossPos) instanceof ConveyorConveyable) {
					Conveyable conveyable = (Conveyable) getLevel().getBlockEntity(frontPos);
					Conveyable acrossConveyable = (Conveyable) getLevel().getBlockEntity(frontAcrossPos);
					handleMovementAcross(conveyable, acrossConveyable, speed, true);
				}
			} else if (front) {
				BlockPos frontPos = getBlockPos().relative(direction);
				if (getLevel().getBlockEntity(frontPos) instanceof Conveyable) {
					Conveyable conveyable = (Conveyable) getLevel().getBlockEntity(frontPos);
					handleMovement(conveyable, speed, true);
				}
			} else if (down) {
				BlockPos downPos = getBlockPos().relative(direction).below();
				if (getLevel().getBlockEntity(downPos) instanceof Conveyable) {
					Conveyable conveyable = (Conveyable) getLevel().getBlockEntity(downPos);
					handleMovement(conveyable, speed, true);
				}
			} else if (position != 0) {
				setPosition(0);
			}
		} else if (position != 0) {
			setPosition(0);
		}
	}

	public void handleMovement(Conveyable conveyable, int speed, boolean transition) {
		if (conveyable.accepts(getStack())) {
			if (position < speed) {
				setPosition(getPosition() + 1);
			} else if (transition && position == speed) {
				conveyable.give(getStack());
				if (!level.isClientSide() || level.isClientSide && Minecraft.getInstance().player.distanceToSqr(Vector3d.atLowerCornerOf(getBlockPos())) > 40 * 40)
					removeStack();
			}
		} else if (conveyable instanceof ConveyorConveyable) {
			ConveyorConveyable conveyor = (ConveyorConveyable) conveyable;

			if (position < speed && position + 1 < conveyor.getPosition() && conveyor.getPosition() > 1) {
				setPosition(getPosition() + 1);
			} else {
				prevPosition = position;
			}
		} else if (position > 0) {
			setPosition(position - 1);
		} else if (prevPosition != position) {
			prevPosition = position;
		}
	}

	public void handleMovementAcross(Conveyable conveyable, Conveyable acrossConveyable, int speed, boolean transition) {
		if (conveyable.accepts(getStack())) {
			if (position < speed) {
				if (conveyable instanceof ConveyorConveyable && acrossConveyable instanceof ConveyorConveyable) {
					ConveyorConveyable conveyor = (ConveyorConveyable) conveyable;
					ConveyorConveyable acrossConveyor = (ConveyorConveyable) acrossConveyable;

					if (position < speed && acrossConveyor.getPosition() == 0) {
						setPosition(getPosition() + 1);
					} else {
						prevPosition = position;
					}
				}
			} else if (transition && position == speed) {
				conveyable.give(getStack());
				if (!level.isClientSide() || level.isClientSide && Minecraft.getInstance().player.distanceToSqr(Vector3d.atLowerCornerOf(getBlockPos())) > 40 * 40)
					removeStack();
			}
		} else if (conveyable instanceof ConveyorConveyable && acrossConveyable instanceof ConveyorConveyable) {
			ConveyorConveyable conveyor = (ConveyorConveyable) conveyable;
			ConveyorConveyable acrossConveyor = (ConveyorConveyable) acrossConveyable;

			if (position < speed && acrossConveyor.getPosition() == 0 && position + 1 < conveyor.getPosition() && conveyor.getPosition() > 1) {
				setPosition(getPosition() + 1);
			} else {
				prevPosition = position;
			}
		} else if (position > 0) {
			setPosition(position - 1);
		} else if (prevPosition != position) {
			prevPosition = position;
		}
	}

	@Override
	public boolean hasBeenRemoved() {
		return hasBeenRemoved;
	}

	@Override
	public void setRemoved(boolean hasBeenRemoved) {
		this.hasBeenRemoved = hasBeenRemoved;
	}

	@Override
	public ConveyorTypes getConveyorType() {
		return ((Conveyor) getBlockState().getBlock()).getType();
	}

	@Override
	public boolean accepts(ItemStack stack) {
		return isEmpty();
	}

	@Override
	public boolean validInputSide(Direction direction) {
		return direction != getBlockState().getValue(HorizontalBlock.FACING) && direction != Direction.UP && direction != Direction.DOWN;
	}

	@Override
	public boolean isOutputSide(Direction direction, ConveyorTypes type) {
		return getBlockState().getValue(HorizontalBlock.FACING) == direction;
	}

	@Override
	public void give(ItemStack stack) {
		if (front || across || down)
			prevPosition = -1;

		if (!level.isClientSide())
			setStack(stack);
	}

	@Override
	public NonNullList<ItemStack> getItems() {
		return stacks;
	}

	@Override
	public int getContainerSize() {
		return 1;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		SingularStackInventory.super.setItem(slot, stack);
		if (!level.isClientSide())
			sendPacket((ServerWorld) level, save(new CompoundNBT()));
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack stack = SingularStackInventory.super.removeItemNoUpdate(slot);
		position = 0;
		prevPosition = 0;
		if (!level.isClientSide())
			sendPacket((ServerWorld) level, save(new CompoundNBT()));
		return stack;
	}

	@Override
	public void clearContent() {
		SingularStackInventory.super.clearContent();
		if (!level.isClientSide())
			sendPacket((ServerWorld) level, save(new CompoundNBT()));
	}

	public int[] getRenderAttachmentData() {
		return new int[]{ position, prevPosition };
	}

	public boolean hasFront() {
		return front;
	}

	public void setFront(boolean front) {
		this.front = front;
		setChanged();
		if (!level.isClientSide())
			sendPacket((ServerWorld) level, save(new CompoundNBT()));
	}

	public boolean hasDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
		setChanged();
		if (!level.isClientSide())
			sendPacket((ServerWorld) level, save(new CompoundNBT()));
	}

	public boolean hasAcross() {
		return across;
	}

	public void setAcross(boolean across) {
		this.across = across;
		setChanged();
		if (!level.isClientSide())
			sendPacket((ServerWorld) level, save(new CompoundNBT()));
	}

	@Override
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		if (position == 0)
			this.prevPosition = 0;
		else this.prevPosition = this.position;
		this.position = position;
	}

	@Override
	public int getPreviousPosition() {
		return prevPosition;
	}

	protected void sendPacket(ServerWorld w, CompoundNBT tag) {
		tag.putString("id", TileEntityType.getKey(getType()).toString());
		sendPacket(w, new SUpdateTileEntityPacket(getBlockPos(), 127, tag));
	}

	protected void sendPacket(ServerWorld w, SUpdateTileEntityPacket packet) {
		w.getPlayers(player -> player.distanceToSqr(Vector3d.atLowerCornerOf(getBlockPos())) < 40 * 40).forEach(player -> player.connection.send(packet));
	}

	@Override
	public void setChanged() {
		super.setChanged();
	}

	@Override
	public void load(BlockState state, CompoundNBT compoundTag) {
		super.load(state, compoundTag);
		stacks.set(0, ItemStack.of(compoundTag.getCompound("stack")));
		front = compoundTag.getBoolean("front");
		down = compoundTag.getBoolean("down");
		across = compoundTag.getBoolean("across");
		position = compoundTag.getInt("position");
		prevPosition = compoundTag.getInt("prevPosition");
	}

	@Override
	public CompoundNBT save(CompoundNBT compoundTag) {
		compoundTag.put("stack", getStack().save(new CompoundNBT()));
		compoundTag.putBoolean("front", front);
		compoundTag.putBoolean("down", down);
		compoundTag.putBoolean("across", across);
		compoundTag.putInt("position", position);
		compoundTag.putInt("prevPosition", prevPosition);
		return super.save(compoundTag);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return save(new CompoundNBT());
	}
}
