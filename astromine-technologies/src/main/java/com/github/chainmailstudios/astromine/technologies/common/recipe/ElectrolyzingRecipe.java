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

import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.common.recipe.AstromineRecipeType;
import com.github.chainmailstudios.astromine.common.recipe.base.EnergyConsumingRecipe;
import com.github.chainmailstudios.astromine.common.utilities.ParsingUtilities;
import com.github.chainmailstudios.astromine.common.volume.handler.FluidHandler;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesBlocks;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ElectrolyzingRecipe implements IRecipe<IInventory>, EnergyConsumingRecipe<IInventory> {
	final ResourceLocation identifier;
	final RegistryKey<Fluid> inputFluidKey;
	final LazyValue<Fluid> inputFluid;
	final int inputAmount;
	final RegistryKey<Fluid> firstOutputFluidKey;
	final LazyValue<Fluid> firstOutputFluid;
	final int firstOutputAmount;
	final RegistryKey<Fluid> secondOutputFluidKey;
	final LazyValue<Fluid> secondOutputFluid;
	final int secondOutputAmount;
	final int energyConsumed;
	final int time;

	public ElectrolyzingRecipe(ResourceLocation identifier, RegistryKey<Fluid> inputFluidKey, int inputAmount, RegistryKey<Fluid> firstOutputFluidKey, int firstOutputAmount, RegistryKey<Fluid> secondOutputFluidKey, int secondOutputAmount, int energyConsumed,
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

	public boolean matches(IFluidHandler fluidComponent) {
		FluidHandler fluidHandler = FluidHandler.of(fluidComponent);

		FluidStack inputVolume = fluidHandler.getFirst();
		FluidStack firstOutputVolume = fluidHandler.getSecond();
		FluidStack secondOutputVolume = fluidHandler.getThird();

		if (!inputVolume.getFluid().isSame(inputFluid.get())) {
			return false;
		}
		if (inputVolume.getAmount() < inputAmount) {
			return false;
		}
		if (!firstOutputVolume.getFluid().isSame(firstOutputFluid.get()) && !firstOutputVolume.isEmpty()) {
			return false;
		}
		if (!(fluidComponent.getTankCapacity(1) - fluidComponent.getFluidInTank(1).getAmount() >= firstOutputAmount)) {
			return false;
		}
		if (!secondOutputVolume.getFluid().isSame(secondOutputFluid.get()) && !secondOutputVolume.isEmpty()) {
			return false;
		}

		return fluidComponent.getTankCapacity(2) - fluidComponent.getFluidInTank(2).getAmount() >= secondOutputAmount;
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

	public int getInputAmount() {
		return inputAmount;
	}

	public Fluid getFirstOutputFluid() {
		return firstOutputFluid.get();
	}

	public int getFirstOutputAmount() {
		return firstOutputAmount;
	}

	public Fluid getSecondOutputFluid() {
		return secondOutputFluid.get();
	}

	public int getSecondOutputAmount() {
		return secondOutputAmount;
	}

	public int getEnergyConsumed() {
		return energyConsumed;
	}

	public int getTime() {
		return time;
	}

	public static final class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ElectrolyzingRecipe> {
		public static final ResourceLocation ID = AstromineCommon.identifier("electrolyzing");

		public static final Serializer INSTANCE = new Serializer();

		private Serializer() {
			// Locked.
		}

		@Override
		public ElectrolyzingRecipe fromJson(ResourceLocation identifier, JsonObject object) {
			ElectrolyzingRecipe.Format format = new Gson().fromJson(object, ElectrolyzingRecipe.Format.class);

			return new ElectrolyzingRecipe(identifier, RegistryKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(format.input)), format.inputAmount, RegistryKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(format.firstOutput)),
					format.firstOutputAmount, RegistryKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(format.secondOutput)), format.secondOutputAmount, format.energyGenerated, ParsingUtilities.fromJson(format.time, Integer.class));
		}

		@Override
		public ElectrolyzingRecipe fromNetwork(ResourceLocation identifier, PacketBuffer buffer) {
			return new ElectrolyzingRecipe(identifier, RegistryKey.create(Registry.FLUID_REGISTRY, buffer.readResourceLocation()), buffer.readInt(), RegistryKey.create(Registry.FLUID_REGISTRY, buffer.readResourceLocation()), buffer.readInt(), RegistryKey.create(
					Registry.FLUID_REGISTRY, buffer.readResourceLocation()), buffer.readInt(), buffer.readInt(), buffer.readInt());
		}

		@Override
		public void toNetwork(PacketBuffer buffer, ElectrolyzingRecipe recipe) {
			buffer.writeResourceLocation(recipe.inputFluidKey.location());
			buffer.writeInt(recipe.inputAmount);
			buffer.writeResourceLocation(recipe.firstOutputFluidKey.location());
			buffer.writeInt(recipe.firstOutputAmount);
			buffer.writeResourceLocation(recipe.secondOutputFluidKey.location());
			buffer.writeInt(recipe.secondOutputAmount);
			buffer.writeInt(recipe.energyConsumed);
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
		int inputAmount;

		@SerializedName("first_output")
		String firstOutput;

		@SerializedName("first_output_amount")
		int firstOutputAmount;

		@SerializedName("second_output")
		String secondOutput;

		@SerializedName("second_output_amount")
		int secondOutputAmount;

		@SerializedName("energy_consumed")
		int energyGenerated;

		JsonElement time;

		@Override
		public String toString() {
			return "Format{" + "input='" + input + '\'' + ", inputAmount=" + inputAmount + ", firstOutput='" + firstOutput + '\'' + ", firstOutputAmount=" + firstOutputAmount + ", secondOutput='" + secondOutput + '\'' + ", secondOutputAmount=" + secondOutputAmount +
			       ", energyGenerated=" + energyGenerated + ", time=" + time + '}';
		}
	}
}
