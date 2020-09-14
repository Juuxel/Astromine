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
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesBlocks;
import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.common.component.inventory.ItemInventoryComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.compatibility.ItemInventoryComponentFromItemInventory;
import com.github.chainmailstudios.astromine.common.recipe.base.EnergyConsumingRecipe;
import com.github.chainmailstudios.astromine.common.utilities.EnergyUtilities;
import com.github.chainmailstudios.astromine.common.utilities.IngredientUtilities;
import com.github.chainmailstudios.astromine.common.utilities.PacketUtilities;
import com.github.chainmailstudios.astromine.common.utilities.ParsingUtilities;
import com.github.chainmailstudios.astromine.common.utilities.StackUtilities;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class TrituratingRecipe implements EnergyConsumingRecipe<Container> {
	final ResourceLocation identifier;
	final Ingredient input;
	final ItemStack output;
	final double energyConsumed;
	final int time;

	public TrituratingRecipe(ResourceLocation identifier, Ingredient input, ItemStack output, double energyConsumed, int time) {
		this.identifier = identifier;
		this.input = input;
		this.output = output;
		this.energyConsumed = energyConsumed;
		this.time = time;
	}

	@Override
	public boolean matches(Container inventory, Level world) {
		return ItemInventoryComponentFromItemInventory.of(inventory).getContents().values().stream().anyMatch(input);
	}

	@Override
	public ItemStack craft(Container inventory) {
		ItemInventoryComponent component = ItemInventoryComponentFromItemInventory.of(inventory);
		List<ItemStack> matching = Lists.newArrayList(component.getContentsMatching(input));

		ItemStack stack = matching.isEmpty() ? ItemStack.EMPTY : matching.get(0);

		for (Map.Entry<Integer, ItemStack> entry : component.getContents().entrySet()) {
			if (entry.getValue() == stack && !stack.isEmpty()) {
				component.getStack(entry.getKey()).decrement(1);

				break;
			}
		}

		return output.copy();
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getOutput() {
		return output.copy();
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
	public NonNullList<Ingredient> getPreviewInputs() {
		NonNullList<Ingredient> defaultedList = NonNullList.create();
		defaultedList.add(this.input);
		return defaultedList;
	}

	@Override
	public ItemStack getRecipeKindIcon() {
		return new ItemStack(AstromineTechnologiesBlocks.ADVANCED_TRITURATOR);
	}

	public int getTime() {
		return time;
	}

	public double getEnergyConsumed() {
		return energyConsumed;
	}

	public static final class Serializer implements RecipeSerializer<TrituratingRecipe> {
		public static final ResourceLocation ID = AstromineCommon.identifier("triturating");

		public static final Serializer INSTANCE = new Serializer();

		private Serializer() {
			// Locked.
		}

		@Override
		public TrituratingRecipe read(ResourceLocation identifier, JsonObject object) {
			TrituratingRecipe.Format format = new Gson().fromJson(object, TrituratingRecipe.Format.class);

			return new TrituratingRecipe(identifier, IngredientUtilities.fromJson(format.input), StackUtilities.fromJson(format.output), EnergyUtilities.fromJson(format.energyConsumed), ParsingUtilities.fromJson(format.time, Integer.class));
		}

		@Override
		public TrituratingRecipe read(ResourceLocation identifier, FriendlyByteBuf buffer) {
			return new TrituratingRecipe(identifier, IngredientUtilities.fromPacket(buffer), StackUtilities.fromPacket(buffer), EnergyUtilities.fromPacket(buffer), PacketUtilities.fromPacket(buffer, Integer.class));
		}

		@Override
		public void write(FriendlyByteBuf buffer, TrituratingRecipe recipe) {
			IngredientUtilities.toPacket(buffer, recipe.input);
			StackUtilities.toPacket(buffer, recipe.output);
			EnergyUtilities.toPacket(buffer, recipe.energyConsumed);
			PacketUtilities.toPacket(buffer, recipe.time);
		}
	}

	public static final class Type implements AstromineRecipeType<TrituratingRecipe> {
		public static final Type INSTANCE = new Type();

		private Type() {
			// Locked.
		}
	}

	public static final class Format {
		JsonObject input;
		JsonObject output;
		@SerializedName("time")
		JsonPrimitive time;
		@SerializedName("energy_consumed")
		JsonElement energyConsumed;

		@Override
		public String toString() {
			return "Format{" + "input=" + input + ", output=" + output + ", time=" + time + ", energyConsumed=" + energyConsumed + '}';
		}
	}
}
