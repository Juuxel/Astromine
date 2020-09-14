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
import com.github.chainmailstudios.astromine.common.recipe.base.EnergyGeneratingRecipe;
import com.github.chainmailstudios.astromine.common.utilities.EnergyUtilities;
import com.github.chainmailstudios.astromine.common.utilities.FractionUtilities;
import com.github.chainmailstudios.astromine.common.utilities.PacketUtilities;
import com.github.chainmailstudios.astromine.common.utilities.ParsingUtilities;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public class LiquidGeneratingRecipe implements Recipe<Container>, EnergyGeneratingRecipe<Container> {
	final ResourceLocation identifier;
	final ResourceKey<Fluid> fluidKey;
	final LazyLoadedValue<Fluid> fluid;
	final Fraction amount;
	final double energyGenerated;
	final int time;

	public LiquidGeneratingRecipe(ResourceLocation identifier, ResourceKey<Fluid> fluidKey, Fraction amount, double energyGenerated, int time) {
		this.identifier = identifier;
		this.fluidKey = fluidKey;
		this.fluid = new LazyLoadedValue<>(() -> Registry.FLUID.get(this.fluidKey));
		this.amount = amount;
		this.energyGenerated = energyGenerated;
		this.time = time;
	}

	public boolean matches(FluidInventoryComponent fluidComponent) {
		FluidHandler handler = FluidHandler.of(fluidComponent);

		FluidVolume fluidVolume = handler.getFirst();

		if (!fluidVolume.getFluid().matchesType(fluid.get())) {
			return false;
		}

		return fluidVolume.hasStored(amount);
	}

	@Override
	public boolean matches(Container inventory, Level world) {
		return false;
	}

	@Override
	public ItemStack assemble(Container inventory) {
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
	public ResourceLocation getId() {
		return identifier;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return Serializer.INSTANCE;
	}

	@Override
	public RecipeType<?> getType() {
		return Type.INSTANCE;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.create(); // we are not dealing with items
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(AstromineTechnologiesBlocks.ADVANCED_LIQUID_GENERATOR);
	}

	public Fluid getFluid() {
		return fluid.get();
	}

	public Fraction getAmount() {
		return amount;
	}

	public double getEnergyGenerated() {
		return energyGenerated;
	}

	public int getTime() {
		return time;
	}

	public static final class Serializer implements RecipeSerializer<LiquidGeneratingRecipe> {
		public static final ResourceLocation ID = AstromineCommon.identifier("liquid_generating");

		public static final Serializer INSTANCE = new Serializer();

		private Serializer() {
			// Locked.
		}

		@Override
		public LiquidGeneratingRecipe fromJson(ResourceLocation identifier, JsonObject object) {
			LiquidGeneratingRecipe.Format format = new Gson().fromJson(object, LiquidGeneratingRecipe.Format.class);

			return new LiquidGeneratingRecipe(identifier, ResourceKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(format.input)), FractionUtilities.fromJson(format.amount), EnergyUtilities.fromJson(format.energyGenerated), ParsingUtilities.fromJson(format.time, Integer.class));
		}

		@Override
		public LiquidGeneratingRecipe fromNetwork(ResourceLocation identifier, FriendlyByteBuf buffer) {
			return new LiquidGeneratingRecipe(identifier, ResourceKey.create(Registry.FLUID_REGISTRY, buffer.readResourceLocation()), FractionUtilities.fromPacket(buffer), EnergyUtilities.fromPacket(buffer), PacketUtilities.fromPacket(buffer, Integer.class));
		}

		@Override
		public void write(FriendlyByteBuf buffer, LiquidGeneratingRecipe recipe) {
			buffer.writeResourceLocation(recipe.fluidKey.location());
			FractionUtilities.toPacket(buffer, recipe.amount);
			EnergyUtilities.toPacket(buffer, recipe.energyGenerated);
			buffer.writeInt(recipe.time);
		}
	}

	public static final class Type implements AstromineRecipeType<LiquidGeneratingRecipe> {
		public static final Type INSTANCE = new Type();

		private Type() {
			// Locked.
		}
	}

	public static final class Format {
		String input;

		@SerializedName("amount")
		JsonElement amount;

		@SerializedName("energy_generated")
		JsonElement energyGenerated;

		JsonElement time;

		@Override
		public String toString() {
			return "Format{" + "input='" + input + '\'' + ", amount=" + amount + ", energyGenerated=" + energyGenerated + ", time=" + time + '}';
		}
	}
}
