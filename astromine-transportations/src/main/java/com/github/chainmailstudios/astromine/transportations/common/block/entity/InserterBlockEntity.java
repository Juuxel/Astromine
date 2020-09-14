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

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.ItemAttributes;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import alexiil.mc.lib.attributes.item.compat.FixedInventoryVanillaWrapper;
import alexiil.mc.lib.attributes.item.impl.EmptyItemExtractable;
import alexiil.mc.lib.attributes.item.impl.RejectingItemInsertable;
import com.github.chainmailstudios.astromine.common.inventory.SingularStackInventory;
import com.github.chainmailstudios.astromine.transportations.common.block.InserterBlock;
import com.github.chainmailstudios.astromine.transportations.registry.AstromineTransportationsBlockEntityTypes;

import java.util.List;
import java.util.stream.IntStream;

public class InserterBlockEntity extends BlockEntity implements SingularStackInventory, BlockEntityClientSerializable, RenderAttachmentBlockEntity, TickableBlockEntity {
	protected int position = 0;
	protected int prevPosition = 0;
	private NonNullList<ItemStack> stacks = NonNullList.withSize(1, ItemStack.EMPTY);

	public InserterBlockEntity() {
		super(AstromineTransportationsBlockEntityTypes.INSERTER);
	}

	public InserterBlockEntity(BlockEntityType type) {
		super(type);
	}

	private static IntStream getAvailableSlots(Container inventory, Direction side) {
		return inventory instanceof WorldlyContainer ? IntStream.of(((WorldlyContainer) inventory).getSlotsForFace(side)) : IntStream.range(0, inventory.getContainerSize());
	}

	public static ItemStack transfer(Container from, Container to, ItemStack stack, Direction side) {
		if (to instanceof WorldlyContainer && side != null) {
			WorldlyContainer sidedInventory = (WorldlyContainer) to;
			int[] is = sidedInventory.getSlotsForFace(side);

			for (int i = 0; i < is.length && !stack.isEmpty(); ++i) {
				stack = transfer(from, to, stack, is[i], side);
			}
		} else {
			int j = to.getContainerSize();

			for (int k = 0; k < j && !stack.isEmpty(); ++k) {
				stack = transfer(from, to, stack, k, side);
			}
		}

		return stack;
	}

	private static boolean canInsert(Container inventory, ItemStack stack, int slot, Direction side) {
		if (!inventory.canPlaceItem(slot, stack)) {
			return false;
		} else {
			return !(inventory instanceof WorldlyContainer) || ((WorldlyContainer) inventory).canPlaceItemThroughFace(slot, stack, side);
		}
	}

	private static boolean canMergeItems(ItemStack first, ItemStack second) {
		if (first.getItem() != second.getItem()) {
			return false;
		} else if (first.getDamageValue() != second.getDamageValue()) {
			return false;
		} else if (first.getCount() > first.getMaxStackSize()) {
			return false;
		} else {
			return ItemStack.tagMatches(first, second);
		}
	}

	private static boolean canExtract(Container inventory, ItemStack stack, int slot, Direction facing) {
		return !(inventory instanceof WorldlyContainer) || ((WorldlyContainer) inventory).canTakeItemThroughFace(slot, stack, facing);
	}

	private static boolean extract(SingularStackInventory singularStackInventory, Container inventory, int slot, Direction side) {
		ItemStack stack = inventory.getItem(slot);
		if (!stack.isEmpty() && canExtract(inventory, stack, slot, side)) {
			ItemStack stackB = stack.copy();
			ItemStack stackC = transfer(inventory, singularStackInventory, inventory.removeItem(slot, inventory.getItem(slot).getCount()), null);
			if (stackC.isEmpty()) {
				inventory.setChanged();
				return true;
			}

			inventory.setItem(slot, stackB);
		}

		return false;
	}

	private static ItemStack transfer(Container from, Container to, ItemStack stackA, int slot, Direction direction) {
		ItemStack stackB = to.getItem(slot);
		if (canInsert(to, stackA, slot, direction)) {
			if (stackB.isEmpty()) {
				to.setItem(slot, stackA);
				stackA = ItemStack.EMPTY;
			} else if (canMergeItems(stackB, stackA)) {
				int i = stackA.getMaxStackSize() - stackB.getCount();
				int j = Math.min(stackA.getCount(), i);
				stackA.shrink(j);
				stackB.grow(j);
			}
		}

		return stackA;
	}

	@Override
	public void tick() {
		Direction direction = getBlockState().getValue(HorizontalDirectionalBlock.FACING);
		boolean powered = getBlockState().getValue(BlockStateProperties.POWERED);
		int speed = ((InserterBlock) getBlockState().getBlock()).getSpeed();

		if (!powered) {
			if (isEmpty()) {
				BlockState behindState = level.getBlockState(getBlockPos().relative(direction.getOpposite()));
				ItemExtractable extractable = ItemAttributes.EXTRACTABLE.get(level, getBlockPos().relative(direction.getOpposite()), SearchOptions.inDirection(direction.getOpposite()));

				if (behindState.getBlock() instanceof AbstractFurnaceBlock) {
					extractable = ItemAttributes.EXTRACTABLE.get(level, getBlockPos().relative(direction.getOpposite()), SearchOptions.inDirection(Direction.UP));
				}

				if (extractable != EmptyItemExtractable.NULL) {
					ItemStack stack = extractable.attemptAnyExtraction(64, Simulation.SIMULATE);
					if (position == 0 && !stack.isEmpty() && !(behindState.getBlock() instanceof InserterBlock)) {
						stack = extractable.attemptAnyExtraction(64, Simulation.ACTION);
						setStack(stack);
					} else if (position > 0) {
						setPosition(getPosition() - 1);
					}
				} else {
					BlockPos offsetPos = getBlockPos().relative(direction.getOpposite());
					List<MinecartChest> minecartEntities = getLevel().getEntitiesOfClass(MinecartChest.class, new AABB(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ(), offsetPos.getX() + 1, offsetPos.getY() + 1, offsetPos.getZ() + 1),
						EntitySelector.NO_SPECTATORS);
					if (position == 0 && minecartEntities.size() >= 1) {
						MinecartChest minecartEntity = minecartEntities.get(0);
						FixedInventoryVanillaWrapper wrapper = new FixedInventoryVanillaWrapper(minecartEntity);
						ItemExtractable extractableMinecart = wrapper.getExtractable();

						ItemStack stackMinecart = extractableMinecart.attemptAnyExtraction(64, Simulation.SIMULATE);
						if (position == 0 && !stackMinecart.isEmpty()) {
							stackMinecart = extractableMinecart.attemptAnyExtraction(64, Simulation.ACTION);
							setStack(stackMinecart);
							minecartEntity.setChanged();
						}
					} else if (position > 0) {
						setPosition(getPosition() - 1);
					}
				}
			} else if (!isEmpty()) {
				BlockState aheadState = getLevel().getBlockState(getBlockPos().relative(direction));

				ItemInsertable insertable = ItemAttributes.INSERTABLE.get(level, getBlockPos().relative(direction), SearchOptions.inDirection(direction));

				if (aheadState.getBlock() instanceof ComposterBlock) {
					insertable = ItemAttributes.INSERTABLE.get(level, getBlockPos().relative(direction), SearchOptions.inDirection(Direction.DOWN));
				} else if (aheadState.getBlock() instanceof AbstractFurnaceBlock && !AbstractFurnaceBlockEntity.isFuel(getStack())) {
					insertable = ItemAttributes.INSERTABLE.get(level, getBlockPos().relative(direction), SearchOptions.inDirection(Direction.DOWN));
				}

				ItemStack stack = insertable.attemptInsertion(getStack(), Simulation.SIMULATE);
				if (insertable != RejectingItemInsertable.NULL) {
					if (stack.isEmpty() || stack.getCount() != getStack().getCount()) {
						if (position < speed) {
							setPosition(getPosition() + 1);
						} else if (!getLevel().isClientSide()) {
							stack = insertable.attemptInsertion(getStack(), Simulation.ACTION);
							setStack(stack);
						}
					} else if (position > 0) {
						setPosition(getPosition() - 1);
					}
				} else {
					BlockPos offsetPos = getBlockPos().relative(direction);
					List<MinecartChest> minecartEntities = getLevel().getEntitiesOfClass(MinecartChest.class, new AABB(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ(), offsetPos.getX() + 1, offsetPos.getY() + 1, offsetPos.getZ() + 1),
						EntitySelector.NO_SPECTATORS);
					if (minecartEntities.size() >= 1) {
						MinecartChest minecartEntity = minecartEntities.get(0);
						if (minecartEntity instanceof Container) {
							FixedInventoryVanillaWrapper wrapper = new FixedInventoryVanillaWrapper((Container) minecartEntity);
							ItemInsertable insertableMinecart = wrapper.getInsertable();

							ItemStack stackMinecart = insertableMinecart.attemptInsertion(getStack(), Simulation.SIMULATE);
							if (position < speed && (stackMinecart.isEmpty() || stackMinecart.getCount() != getStack().getCount())) {
								setPosition(getPosition() + 1);
							} else if (!getLevel().isClientSide() && (stackMinecart.isEmpty() || stackMinecart.getCount() != getStack().getCount())) {
								stackMinecart = insertableMinecart.attemptInsertion(getStack(), Simulation.ACTION);
								setStack(stackMinecart);
								((Container) minecartEntity).setChanged();
							}
						}
					} else if (position > 0) {
						setPosition(getPosition() - 1);
					}
				}
			} else if (position > 0) {
				setPosition(getPosition() - 1);
			}
		} else if (position > 0) {
			setPosition(getPosition() - 1);
		}
	}

	private boolean isInventoryFull(Container inventory, Direction direction) {
		return getAvailableSlots(inventory, direction).allMatch((i) -> {
			ItemStack stack = inventory.getItem(i);
			return stack.getCount() >= stack.getMaxStackSize();
		});
	}

	@Override
	public NonNullList<ItemStack> getItems() {
		return stacks;
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		SingularStackInventory.super.setStack(slot, stack);
		if (!level.isClientSide())
			sendPacket((ServerLevel) level, save(new CompoundTag()));
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack stack = SingularStackInventory.super.removeStack(slot);
		position = 15;
		prevPosition = 15;
		if (!level.isClientSide())
			sendPacket((ServerLevel) level, save(new CompoundTag()));
		return stack;
	}

	@Override
	public void clear() {
		SingularStackInventory.super.clear();
		if (!level.isClientSide())
			sendPacket((ServerLevel) level, save(new CompoundTag()));
	}

	@Override
	public int[] getRenderAttachmentData() {
		return new int[]{ position, prevPosition };
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		if (position == 0)
			this.prevPosition = 0;
		else this.prevPosition = this.position;
		this.position = position;
	}

	public int getPrevPosition() {
		return prevPosition;
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
		getItems().set(0, ItemStack.of(compoundTag.getCompound("stack")));
		position = compoundTag.getInt("position");
	}

	@Override
	public void fromClientTag(CompoundTag compoundTag) {
		load(getBlockState(), compoundTag);
	}

	@Override
	public CompoundTag save(CompoundTag compoundTag) {
		compoundTag.put("stack", getStack().toTag(new CompoundTag()));
		compoundTag.putInt("position", position);
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
