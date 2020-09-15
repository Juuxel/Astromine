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

package com.github.chainmailstudios.astromine.common.multiblock;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public abstract class MultiblockControllerBlockEntity extends TileEntity {
	private final MultiblockType multiblockType;

	private final ImmutableMap<Capability<?>, Object> components;

	private final Map<BlockPos, MultiblockMemberBlockEntity> members = Maps.newHashMap();

	public MultiblockControllerBlockEntity(TileEntityType<?> blockEntityType, MultiblockType multiblockType) {
		super(blockEntityType);
		this.multiblockType = multiblockType;
		this.components = multiblockType.getSuppliers();
	}

	public boolean canBuild() {
		return multiblockType.getBlocks().entrySet().stream().allMatch((entry) -> level.getBlockState(entry.getKey()).getBlock() == entry.getValue());
	}

	public void assemble() {
		multiblockType.getBlocks().forEach((key, value) -> {
			MultiblockMemberBlockEntity blockEntity = (MultiblockMemberBlockEntity) level.getBlockEntity(key);

			blockEntity.setController(this);

			members.put(key, blockEntity);
		});
	}

	public void destroy() {
		multiblockType.getBlocks().forEach((key, value) -> {
			MultiblockMemberBlockEntity blockEntity = (MultiblockMemberBlockEntity) level.getBlockEntity(key);

			blockEntity.setController(null);

			members.remove(key, blockEntity);
		});
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		Object o = components.get(cap);
		if (o != null)
			return LazyOptional.of(() -> o).cast();
		return super.getCapability(cap, side);
	}

	public boolean hasComponent(BlockPos blockPos, Capability<?> type) {
		return multiblockType.getComponents().get(blockPos).stream().anyMatch(mapType -> mapType == type);
	}

	public <C> C getComponent(BlockPos blockPos, Capability<?> componentType) {
		return hasComponent(blockPos, componentType) ? (C) components.get(componentType) : null;
	}

	public Set<Capability<?>> getComponentTypes(BlockPos blockPos) {
		return Sets.newHashSet(multiblockType.getComponents().get(blockPos));
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		components.forEach((key, value) -> {
			tag.put(key.getName(), ((Capability<Object>) key).writeNBT(value, null));
		});

		return super.save(tag);
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		components.forEach((key, value) -> {
			((Capability<Object>) key).readNBT(value, null, tag.get(key.getName()));
		});

		super.load(state, tag);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return save(new CompoundNBT());
	}
}
