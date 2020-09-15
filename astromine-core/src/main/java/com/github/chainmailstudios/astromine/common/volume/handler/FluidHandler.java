package com.github.chainmailstudios.astromine.common.volume.handler;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class FluidHandler {
	private final IFluidHandler component;

	private FluidHandler(IFluidHandler component) {
		this.component = component;
	}

	public FluidStack getVolume(int slot) {
		return component.getFluidInTank(slot);
	}

	public FluidHandler withVolume(int slot, Consumer<Optional<FluidStack>> consumer) {
		consumer.accept(Optional.ofNullable(component.getFluidInTank(slot)));

		return this;
	}

	public FluidHandler withFirstExtractable(@Nullable Direction diretion, Consumer<Optional<FluidStack>> consumer) {
		consumer.accept(Optional.ofNullable(component.getFirstExtractableVolume(diretion)));

		return this;
	}

	public FluidHandler withFirstInsertable(@Nullable Direction direction, Fluid fluid, Consumer<Optional<FluidStack>> consumer) {
		consumer.accept(Optional.ofNullable(component.getFirstInsertableVolume(fluid, direction)));

		return this;
	}

	public FluidStack getFirst() {
		return getVolume(0);
	}

	public FluidStack getSecond() {
		return getVolume(1);
	}

	public FluidStack getThird() {
		return getVolume(2);
	}

	public FluidStack getFourth() {
		return getVolume(3);
	}

	public FluidStack getFifth() {
		return getVolume(4);
	}

	public FluidStack getSixth() {
		return getVolume(5);
	}

	public FluidStack getSeventh() {
		return getVolume(6);
	}

	public FluidStack getEight() {
		return getVolume(7);
	}

	public static FluidHandler of(Object object) {
		return ofOptional(object).get();
	}

	public static Optional<FluidHandler> ofOptional(Object object) {
		if (object instanceof IFluidHandler)
			return Optional.of(new FluidHandler((IFluidHandler) object));
		if (object instanceof CapabilityProvider) {
			CapabilityProvider provider = (CapabilityProvider) object;

			IFluidHandler component = provider.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).orElse(provider.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null));

			if (component != null) {
				return Optional.of(new FluidHandler(component));
			}
		}

		return Optional.empty();
	}
}
