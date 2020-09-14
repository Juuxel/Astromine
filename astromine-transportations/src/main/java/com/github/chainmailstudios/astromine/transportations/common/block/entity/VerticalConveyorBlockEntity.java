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
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import com.github.chainmailstudios.astromine.transportations.common.block.property.ConveyorProperties;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.Conveyable;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.Conveyor;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.ConveyorConveyable;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.ConveyorTypes;
import com.github.chainmailstudios.astromine.transportations.registry.AstromineTransportationsBlockEntityTypes;

public class VerticalConveyorBlockEntity extends ConveyorBlockEntity {
	protected boolean up = false;
	protected int horizontalPosition;
	protected int prevHorizontalPosition;

	public VerticalConveyorBlockEntity() {
		super(AstromineTransportationsBlockEntityTypes.VERTICAL_CONVEYOR);
	}

	public VerticalConveyorBlockEntity(TileEntityType type) {
		super(type);
	}

	@Override
	public void tick() {
		Direction direction = getBlockState().getValue(HorizontalBlock.FACING);
		int speed = ((Conveyor) getBlockState().getBlock()).getSpeed();

		if (!isEmpty()) {
			if (getBlockState().getValue(ConveyorProperties.CONVEYOR)) {
				BlockPos conveyorPos = getBlockPos().relative(direction).above();
				if (getLevel().getBlockEntity(conveyorPos) instanceof Conveyable) {
					Conveyable conveyable = (Conveyable) getLevel().getBlockEntity(conveyorPos);
					if (position < speed) {
						handleMovement(conveyable, speed, false);
					} else {
						prevPosition = speed;
						handleMovementHorizontal(conveyable, speed, true);
					}
				}
			} else if (up) {
				BlockPos upPos = getBlockPos().above();
				if (getLevel().getBlockEntity(upPos) instanceof Conveyable) {
					Conveyable conveyable = (Conveyable) getLevel().getBlockEntity(upPos);
					handleMovement(conveyable, speed, true);
				}
			} else {
				setPosition(0);
			}
		} else {
			setPosition(0);
		}
	}

	public void handleMovementHorizontal(Conveyable conveyable, int speed, boolean transition) {
		if (conveyable.accepts(getStack())) {
			if (horizontalPosition < speed) {
				setHorizontalPosition(getHorizontalPosition() + 2);
			} else if (transition && horizontalPosition >= speed) {
				conveyable.give(getStack());
				if (!level.isClientSide() || level.isClientSide && Minecraft.getInstance().player.distanceToSqr(Vector3d.atLowerCornerOf(getBlockPos())) > 24 * 24)
					removeStack();
			}
		} else if (conveyable instanceof ConveyorConveyable) {
			ConveyorConveyable conveyor = (ConveyorConveyable) conveyable;

			if (horizontalPosition < speed && horizontalPosition + 4 < conveyor.getPosition() && conveyor.getPosition() > 4) {
				setHorizontalPosition(getHorizontalPosition() + 2);
			} else {
				prevHorizontalPosition = horizontalPosition;
			}
		}
	}

	@Override
	public boolean validInputSide(Direction direction) {
		return !getBlockState().getValue(ConveyorProperties.FRONT) && direction == Direction.DOWN || direction == getBlockState().getValue(HorizontalBlock.FACING).getOpposite();
	}

	@Override
	public boolean isOutputSide(Direction direction, ConveyorTypes type) {
		return type == ConveyorTypes.NORMAL ? getBlockState().getValue(HorizontalBlock.FACING) == direction : direction == Direction.UP;
	}

	@Override
	public ItemStack removeStack() {
		horizontalPosition = 0;
		prevHorizontalPosition = 0;
		return super.removeStack();
	}

	public boolean hasUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
		setChanged();
		if (!level.isClientSide())
			sendPacket((ServerWorld) level, save(new CompoundNBT()));
	}

	@Override
	public int[] getRenderAttachmentData() {
		return new int[]{ position, prevPosition, horizontalPosition, prevHorizontalPosition };
	}

	public int getHorizontalPosition() {
		return horizontalPosition;
	}

	public void setHorizontalPosition(int horizontalPosition) {
		if (horizontalPosition == 0)
			this.prevHorizontalPosition = 0;
		else this.prevHorizontalPosition = this.horizontalPosition;

		this.horizontalPosition = horizontalPosition;
	}

	@Override
	public void load(BlockState state, CompoundNBT compoundTag) {
		super.load(state, compoundTag);
		up = compoundTag.getBoolean("up");
		horizontalPosition = compoundTag.getInt("horizontalPosition");
		prevHorizontalPosition = horizontalPosition = compoundTag.getInt("horizontalPosition");
	}

	@Override
	public void fromClientTag(CompoundNBT compoundTag) {
		load(getBlockState(), compoundTag);
	}

	@Override
	public CompoundNBT save(CompoundNBT compoundTag) {
		compoundTag.putBoolean("up", up);
		compoundTag.putInt("horizontalPosition", horizontalPosition);
		return super.save(compoundTag);
	}

	@Override
	public CompoundNBT toClientTag(CompoundNBT compoundTag) {
		return save(compoundTag);
	}
}
