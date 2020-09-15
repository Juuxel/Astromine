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

package com.github.chainmailstudios.astromine.common.component.inventory;

import com.github.chainmailstudios.astromine.common.utilities.data.predicate.TriPredicate;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

public class SimpleFluidInventoryComponent implements FluidInventoryComponent {
	private final Map<Integer, FluidStack> contents = new Int2ObjectOpenHashMap<>();

	private final List<Runnable> listeners = new ArrayList<>();

	private TriPredicate<@Nullable Direction, FluidStack, Integer> insertPredicate = (direction, volume, slot) -> true;
	private TriPredicate<@Nullable Direction, FluidStack, Integer> extractPredicate = (direction, volume, integer) -> true;

	private final int size;

	public SimpleFluidInventoryComponent() {
		this(0);
	}

	public SimpleFluidInventoryComponent(int size) {
		this.size = size;
		for (int i = 0; i < size; ++i) {
			contents.put(i, FluidStack.EMPTY);
		}
	}

	@Override
	public boolean canInsert(@Nullable Direction direction, FluidStack volume, int slot) {
		return insertPredicate.test(direction, volume, slot);
	}

	@Override
	public boolean canExtract(@Nullable Direction direction, FluidStack volume, int slot) {
		return extractPredicate.test(direction, volume, slot);
	}

	public SimpleFluidInventoryComponent withInsertPredicate(TriPredicate<@Nullable Direction, FluidStack, Integer> predicate) {
		TriPredicate<Direction, FluidStack, Integer> triPredicate = this.insertPredicate;
		this.insertPredicate = (direction, volume, integer) -> triPredicate.test(direction, volume, integer) && predicate.test(direction, volume, integer);
		return this;
	}

	public SimpleFluidInventoryComponent withExtractPredicate(TriPredicate<@Nullable Direction, FluidStack, Integer> predicate) {
		TriPredicate<Direction, FluidStack, Integer> triPredicate = this.extractPredicate;
		this.extractPredicate = (direction, volume, integer) -> triPredicate.test(direction, volume, integer) && predicate.test(direction, volume, integer);
		return this;
	}

	@Override
	public Map<Integer, FluidStack> getContents() {
		return contents;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public List<Runnable> getListeners() {
		return listeners;
	}

	@Override
	public void fromTag(CompoundNBT compoundTag) {
		read(this, compoundTag, Optional.empty(), Optional.empty());
	}

	@Override
	public CompoundNBT toTag(CompoundNBT compoundTag) {
		write(this, compoundTag, Optional.empty(), Optional.empty());
		return compoundTag;
	}

	@Override
	public SimpleFluidInventoryComponent copy() {
		SimpleFluidInventoryComponent component = new SimpleFluidInventoryComponent(getSize());
		component.fromTag(toTag(new CompoundNBT()));
		return component;
	}
}
