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
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.LazyValue;
import net.minecraft.util.NonNullList;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class FluidMixingRecipe implements IRecipe<IInventory>, EnergyConsumingRecipe<IInventory> {
	final ResourceLocation identifier;
	final RegistryKey<Fluid> firstInputFluidKey;
	final LazyValue<Fluid> firstInputFluid;
	final Fraction firstInputAmount;
	final RegistryKey<Fluid> secondInputFluidKey;
	final LazyValue<Fluid> secondInputFluid;
	final Fraction secondInputAmount;
	final RegistryKey<Fluid> outputFluidKey;
	final LazyValue<Fluid> outputFluid;
	final Fraction outputAmount;
	final double energyConsumed;
	final int time;

	public FluidMixingRecipe(ResourceLocation identifier, RegistryKey<Fluid> firstInputFluidKey, Fraction firstInputAmount, RegistryKey<Fluid> secondInputFluidKey, Fraction secondInputAmount, RegistryKey<Fluid> outputFluidKey, Fraction outputAmount, double energyConsumed, int time) {
		this.identifier = identifier;
		this.firstInputFluidKey = firstInputFluidKey;
		this.firstInputFluid = new LazyValue<>(() -> Registry.FLUID.get(this.firstInputFluidKey));
		this.firstInputAmount = firstInputAmount;
		this.secondInputFluidKey = secondInputFluidKey;
		this.secondInputFluid = new LazyValue<>(() -> Registry.FLUID.get(this.secondInputFluidKey));
		this.secondInputAmount = secondInputAmount;
		this.outputFluidKey = outputFluidKey;
		this.outputFluid = new LazyValue<>(() -> Registry.FLUID.get(this.outputFluidKey));
		this.outputAmount = outputAmount;
		this.energyConsumed = energyConsumed;
		this.time = time;
	}

	public boolean matches(FluidInventoryComponent fluidComponent) {
		FluidHandler fluidHandler = FluidHandler.of(fluidComponent);

		FluidVolume firstInputVolume = fluidHandler.getFirst();
		FluidVolume secondInputVolume = fluidHandler.getSecond();
		FluidVolume outputVolume = fluidHandler.getThird();

		if (!firstInputVolume.getFluid().matchesType(firstInputFluid.get()) && !secondInputVolume.getFluid().matchesType(firstInputFluid.get())) {
			return false;
		}

		if (!firstInputVolume.hasStored(firstInputAmount)) {
			return false;
		}

		if (!secondInputVolume.getFluid().matchesType(secondInputFluid.get()) && !firstInputVolume.getFluid().matchesType(secondInputFluid.get())) {
			return false;
		}

		if (!secondInputVolume.hasStored(secondInputAmount)) {
			return false;
		}

		if (!outputVolume.getFluid().matchesType(outputFluid.get()) && !outputVolume.isEmpty()) {
			return false;
		}

		return outputVolume.hasAvailable(outputAmount);
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
		return ItemStack.EMPTY;
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
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.create(); // we are not dealing with items
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(AstromineTechnologiesBlocks.ADVANCED_FLUID_MIXER);
	}

	public ResourceLocation getIdentifier() {
		return identifier;
	}

	public Fluid getFirstInputFluid() {
		return firstInputFluid.get();
	}

	public Fraction getFirstInputAmount() {
		return firstInputAmount;
	}

	public Fluid getSecondInputFluid() {
		return secondInputFluid.get();
	}

	public Fraction getSecondInputAmount() {
		return secondInputAmount;
	}

	public Fluid getOutputFluid() {
		return outputFluid.get();
	}

	public Fraction getOutputAmount() {
		return outputAmount;
	}

	public double getEnergyConsumed() {
		return energyConsumed;
	}

	public int getTime() {
		return time;
	}

	public static final class Serializer implements IRecipeSerializer<FluidMixingRecipe> {
		public static final ResourceLocation ID = AstromineCommon.identifier("fluid_mixing");

		public static final Serializer INSTANCE = new Serializer();

		private Serializer() {
			// Locked.
		}

		@Override
		public FluidMixingRecipe fromJson(ResourceLocation identifier, JsonObject object) {
			FluidMixingRecipe.Format format = new Gson().fromJson(object, FluidMixingRecipe.Format.class);

			return new FluidMixingRecipe(identifier, RegistryKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(format.firstInput)), FractionUtilities.fromJson(format.firstInputAmount), RegistryKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(format.secondInput)), FractionUtilities.fromJson(
				format.secondInputAmount), RegistryKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(format.output)), FractionUtilities.fromJson(format.outputAmount), EnergyUtilities.fromJson(format.energyGenerated), ParsingUtilities.fromJson(format.time, Integer.class));
		}

		@Override
		public FluidMixingRecipe fromNetwork(ResourceLocation identifier, PacketBuffer buffer) {
			return new FluidMixingRecipe(identifier, RegistryKey.create(Registry.FLUID_REGISTRY, buffer.readResourceLocation()), FractionUtilities.fromPacket(buffer), RegistryKey.create(Registry.FLUID_REGISTRY, buffer.readResourceLocation()), FractionUtilities.fromPacket(buffer), RegistryKey.create(
				Registry.FLUID_REGISTRY, buffer.readResourceLocation()), FractionUtilities.fromPacket(buffer), EnergyUtilities.fromPacket(buffer), PacketUtilities.fromPacket(buffer, Integer.class));
		}

		@Override
		public void write(PacketBuffer buffer, FluidMixingRecipe recipe) {
			buffer.writeResourceLocation(recipe.firstInputFluidKey.location());
			FractionUtilities.toPacket(buffer, recipe.firstInputAmount);
			buffer.writeResourceLocation(recipe.secondInputFluidKey.location());
			FractionUtilities.toPacket(buffer, recipe.secondInputAmount);
			buffer.writeResourceLocation(recipe.outputFluidKey.location());
			FractionUtilities.toPacket(buffer, recipe.outputAmount);
			EnergyUtilities.toPacket(buffer, recipe.energyConsumed);
			buffer.writeInt(recipe.getTime());
		}
	}

	public static final class Type implements AstromineRecipeType<FluidMixingRecipe> {
		public static final Type INSTANCE = new Type();

		private Type() {
			// Locked.
		}
	}

	public static final class Format {
		@SerializedName("first_input")
		String firstInput;

		@SerializedName("first_input_amount")
		JsonElement firstInputAmount;

		@SerializedName("second_input")
		String secondInput;

		@SerializedName("second_input_amount")
		JsonElement secondInputAmount;

		String output;

		@SerializedName("output_amount")
		JsonElement outputAmount;

		@SerializedName("energy_consumed")
		JsonElement energyGenerated;

		JsonElement time;

		@Override
		public String toString() {
			return "Format{" + "firstInput='" + firstInput + '\'' + ", firstInputAmount=" + firstInputAmount + ", secondInput='" + secondInput + '\'' + ", secondInputAmount=" + secondInputAmount + ", output='" + output + '\'' + ", outputAmount=" + outputAmount +
				", energyGenerated=" + energyGenerated + '}';
		}
	}
}
