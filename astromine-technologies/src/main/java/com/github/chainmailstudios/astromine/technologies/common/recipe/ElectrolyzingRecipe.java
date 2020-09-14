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

package com.github.chainmailstudios.astromine.technologies.common.recipe;

import com.github.chainmailstudios.astromine.common.recipe.AstromineRecipeType;
import com.github.chainmailstudios.astromine.common.volume.handler.FluidHandler;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesBlocks;
import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.common.component.inventory.FluidInventoryComponent;
import com.github.chainmailstudios.astromine.common.volume.fraction.Fraction;
import com.github.chainmailstudios.astromine.common.recipe.base.EnergyConsumingRecipe;
import com.github.chainmailstudios.astromine.common.utilities.EnergyUtilities;
import com.github.chainmailstudios.astromine.common.utilities.FractionUtilities;
import com.github.chainmailstudios.astromine.common.utilities.PacketUtilities;
import com.github.chainmailstudios.astromine.common.utilities.ParsingUtilities;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.LazyValue;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ElectrolyzingRecipe implements IRecipe<IInventory>, EnergyConsumingRecipe<IInventory> {
	final ResourceLocation identifier;
	final RegistryKey<Fluid> inputFluidKey;
	final LazyValue<Fluid> inputFluid;
	final Fraction inputAmount;
	final RegistryKey<Fluid> firstOutputFluidKey;
	final LazyValue<Fluid> firstOutputFluid;
	final Fraction firstOutputAmount;
	final RegistryKey<Fluid> secondOutputFluidKey;
	final LazyValue<Fluid> secondOutputFluid;
	final Fraction secondOutputAmount;
	final double energyConsumed;
	final int time;

	public ElectrolyzingRecipe(ResourceLocation identifier, RegistryKey<Fluid> inputFluidKey, Fraction inputAmount, RegistryKey<Fluid> firstOutputFluidKey, Fraction firstOutputAmount, RegistryKey<Fluid> secondOutputFluidKey, Fraction secondOutputAmount, double energyConsumed,
		int time) {
		this.identifier = identifier;
		this.inputFluidKey = inputFluidKey;
		this.inputFluid = new LazyValue<>(() -> Registry.FLUID.get(this.inputFluidKey));
		this.inputAmount = inputAmount;
		this.firstOutputFluidKey = firstOutputFluidKey;
		this.firstOutputFluid = new LazyValue<>(() -> Registry.FLUID.get(this.firstOutputFluidKey));
		this.firstOutputAmount = firstOutputAmount;
		this.secondOutputFluidKey = secondOutputFluidKey;
		this.secondOutputFluid = new LazyValue<>(() -> Registry.FLUID.get(this.secondOutputFluidKey));
		this.secondOutputAmount = secondOutputAmount;
		this.energyConsumed = energyConsumed;
		this.time = time;
	}

	public boolean matches(FluidInventoryComponent fluidComponent) {
		FluidHandler fluidHandler = FluidHandler.of(fluidComponent);

		FluidVolume inputVolume = fluidHandler.getFirst();
		FluidVolume firstOutputVolume = fluidHandler.getSecond();
		FluidVolume secondOutputVolume = fluidHandler.getThird();

		if (!inputVolume.getFluid().matchesType(inputFluid.get())) {
			return false;
		}
		if (!inputVolume.hasStored(inputAmount)) {
			return false;
		}
		if (!firstOutputVolume.getFluid().matchesType(firstOutputFluid.get()) && !firstOutputVolume.isEmpty()) {
			return false;
		}
		if (!firstOutputVolume.hasAvailable(firstOutputAmount)) {
			return false;
		}
		if (!secondOutputVolume.getFluid().matchesType(secondOutputFluid.get()) && !secondOutputVolume.isEmpty()) {
			return false;
		}

		return secondOutputVolume.hasAvailable(secondOutputAmount);
	}

	@Override
	public ResourceLocation getId() {
		return identifier;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return Serializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return Type.INSTANCE;
	}

	@Override
	public boolean matches(IInventory inventory, World world) {
		return false;
	}

	@Override
	public ItemStack assemble(IInventory inventory) {
		return null;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return false;
	}

	@Override
	public ItemStack getResultItem() {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(AstromineTechnologiesBlocks.ADVANCED_ELECTROLYZER);
	}

	public ResourceLocation getIdentifier() {
		return identifier;
	}

	public Fluid getInputFluid() {
		return inputFluid.get();
	}

	public Fraction getInputAmount() {
		return inputAmount.copy();
	}

	public Fluid getFirstOutputFluid() {
		return firstOutputFluid.get();
	}

	public Fraction getFirstOutputAmount() {
		return firstOutputAmount.copy();
	}

	public Fluid getSecondOutputFluid() {
		return secondOutputFluid.get();
	}

	public Fraction getSecondOutputAmount() {
		return secondOutputAmount.copy();
	}

	public double getEnergyConsumed() {
		return energyConsumed;
	}

	public int getTime() {
		return time;
	}

	public static final class Serializer implements IRecipeSerializer<ElectrolyzingRecipe> {
		public static final ResourceLocation ID = AstromineCommon.identifier("electrolyzing");

		public static final Serializer INSTANCE = new Serializer();

		private Serializer() {
			// Locked.
		}

		@Override
		public ElectrolyzingRecipe fromJson(ResourceLocation identifier, JsonObject object) {
			ElectrolyzingRecipe.Format format = new Gson().fromJson(object, ElectrolyzingRecipe.Format.class);

			return new ElectrolyzingRecipe(identifier, RegistryKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(format.input)), FractionUtilities.fromJson(format.inputAmount), RegistryKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(format.firstOutput)), FractionUtilities.fromJson(
				format.firstOutputAmount), RegistryKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(format.secondOutput)), FractionUtilities.fromJson(format.secondOutputAmount), EnergyUtilities.fromJson(format.energyGenerated), ParsingUtilities.fromJson(format.time, Integer.class));
		}

		@Override
		public ElectrolyzingRecipe fromNetwork(ResourceLocation identifier, PacketBuffer buffer) {
			return new ElectrolyzingRecipe(identifier, RegistryKey.create(Registry.FLUID_REGISTRY, buffer.readResourceLocation()), FractionUtilities.fromPacket(buffer), RegistryKey.create(Registry.FLUID_REGISTRY, buffer.readResourceLocation()), FractionUtilities.fromPacket(buffer), RegistryKey.create(
				Registry.FLUID_REGISTRY, buffer.readResourceLocation()), FractionUtilities.fromPacket(buffer), EnergyUtilities.fromPacket(buffer), PacketUtilities.fromPacket(buffer, Integer.class));
		}

		@Override
		public void write(PacketBuffer buffer, ElectrolyzingRecipe recipe) {
			buffer.writeResourceLocation(recipe.inputFluidKey.location());
			FractionUtilities.toPacket(buffer, recipe.inputAmount);
			buffer.writeResourceLocation(recipe.firstOutputFluidKey.location());
			FractionUtilities.toPacket(buffer, recipe.firstOutputAmount);
			buffer.writeResourceLocation(recipe.secondOutputFluidKey.location());
			FractionUtilities.toPacket(buffer, recipe.secondOutputAmount);
			EnergyUtilities.toPacket(buffer, recipe.energyConsumed);
			buffer.writeInt(recipe.getTime());
		}
	}

	public static final class Type implements AstromineRecipeType<ElectrolyzingRecipe> {
		public static final Type INSTANCE = new Type();

		private Type() {
			// Locked.
		}
	}

	public static final class Format {
		String input;
		@SerializedName("input_amount")
		JsonElement inputAmount;

		@SerializedName("first_output")
		String firstOutput;

		@SerializedName("first_output_amount")
		JsonElement firstOutputAmount;

		@SerializedName("second_output")
		String secondOutput;

		@SerializedName("second_output_amount")
		JsonElement secondOutputAmount;

		@SerializedName("energy_consumed")
		JsonElement energyGenerated;

		JsonElement time;

		@Override
		public String toString() {
			return "Format{" + "input='" + input + '\'' + ", inputAmount=" + inputAmount + ", firstOutput='" + firstOutput + '\'' + ", firstOutputAmount=" + firstOutputAmount + ", secondOutput='" + secondOutput + '\'' + ", secondOutputAmount=" + secondOutputAmount +
				", energyGenerated=" + energyGenerated + ", time=" + time + '}';
		}
	}
}
