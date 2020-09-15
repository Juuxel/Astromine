package com.github.chainmailstudios.astromine.common.component.inventory;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

public class SimpleFluidTank implements IFluidHandler {
	private BiPredicate<Integer, FluidStack> validator = (integer, stack) -> true;
	private IntPredicate insertSlots = slot -> true;
	private IntPredicate extractSlots = slot -> true;
	private final List<FluidStack> content;
	private IntList contentCapacity;
	private Runnable listener = () -> {};

	public SimpleFluidTank(int size) {
		this.content = Lists.newArrayListWithCapacity(size);
		for (int i = 0; i < size; i++) {
			this.content.add(FluidStack.EMPTY);
		}

		this.contentCapacity = new IntArrayList(size);
	}

	public SimpleFluidTank withValidator(BiPredicate<FluidStack, Integer> predicate) {
		BiPredicate<Integer, FluidStack> biPredicate = this.validator;
		this.validator = (volume, integer) -> biPredicate.test(volume, integer) && predicate.test(integer, volume);
		return this;
	}

	public SimpleFluidTank withInsertSlots(IntPredicate predicate) {
		IntPredicate intPredicate = this.insertSlots;
		this.insertSlots = (slot) -> intPredicate.test(slot) && predicate.test(slot);
		return this;
	}

	public SimpleFluidTank withExtractSlots(IntPredicate predicate) {
		IntPredicate intPredicate = this.extractSlots;
		this.extractSlots = (slot) -> intPredicate.test(slot) && predicate.test(slot);
		return this;
	}

	public SimpleFluidTank withListener(Consumer<SimpleFluidTank> listener) {
		this.listener = () -> listener.accept(this);
		return this;
	}

	public SimpleFluidTank withCapacity(int slot, int capacity) {
		this.contentCapacity.set(slot, capacity);
		return this;
	}

	@Override
	public int getTanks() {
		return content.size();
	}

	@NotNull
	@Override
	public FluidStack getFluidInTank(int tank) {
		return content.get(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		return contentCapacity.getInt(tank);
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		return validator.test(tank, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return 0;
	}

	@NotNull
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return FluidStack.EMPTY;
	}

	@NotNull
	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return FluidStack.EMPTY;
	}

	protected void onContentsChanged() {
		this.listener.run();
	}
}
