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
import com.github.chainmailstudios.astromine.common.component.inventory.ItemInventoryComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.compatibility.ItemInventoryComponentFromItemInventory;
import com.github.chainmailstudios.astromine.common.recipe.AstromineRecipeType;
import com.github.chainmailstudios.astromine.common.recipe.base.EnergyConsumingRecipe;
import com.github.chainmailstudios.astromine.common.recipe.ingredient.ArrayIngredient;
import com.github.chainmailstudios.astromine.common.utilities.EnergyUtilities;
import com.github.chainmailstudios.astromine.common.utilities.IngredientUtilities;
import com.github.chainmailstudios.astromine.common.utilities.PacketUtilities;
import com.github.chainmailstudios.astromine.common.utilities.StackUtilities;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesBlocks;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class AlloySmeltingRecipe implements EnergyConsumingRecipe<IInventory> {
	final ResourceLocation identifier;
	final ArrayIngredient firstInput;
	final ArrayIngredient secondInput;
	final ItemStack output;
	final int energyConsumed;
	final int time;

	public AlloySmeltingRecipe(ResourceLocation identifier, ArrayIngredient firstInput, ArrayIngredient secondInput, ItemStack output, int energyConsumed, int time) {
		this.identifier = identifier;
		this.firstInput = firstInput;
		this.secondInput = secondInput;
		this.output = output;
		this.energyConsumed = energyConsumed;
		this.time = time;
	}

	@Override
	public boolean matches(IInventory inventory, World world) {
		ItemInventoryComponent component = ItemInventoryComponentFromItemInventory.of(inventory);
		if (component.getItemSize() < 2)
			return false;
		ItemStack stack1 = component.getStack(0);
		ItemStack stack2 = component.getStack(1);
		if (firstInput.test(stack1))
			return secondInput.test(stack2);
		if (firstInput.test(stack2))
			return secondInput.test(stack1);
		return false;
	}

	@Override
	public ItemStack assemble(IInventory inventory) {
		return output.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getResultItem() {
		return output.copy();
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
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> defaultedList = NonNullList.create();
		defaultedList.add(this.firstInput.asIngredient());
		defaultedList.add(this.secondInput.asIngredient());
		return defaultedList;
	}

	public ArrayIngredient getFirstInput() {
		return firstInput;
	}

	public ArrayIngredient getSecondInput() {
		return secondInput;
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(AstromineTechnologiesBlocks.ADVANCED_ALLOY_SMELTER);
	}

	public int getTime() {
		return time;
	}

	public int getEnergyConsumed() {
		return energyConsumed;
	}

	public static final class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AlloySmeltingRecipe> {
		public static final ResourceLocation ID = AstromineCommon.identifier("alloy_smelting");

		public static final Serializer INSTANCE = new Serializer();

		private Serializer() {
			// Locked.
		}

		@Override
		public AlloySmeltingRecipe fromJson(ResourceLocation identifier, JsonObject object) {
			AlloySmeltingRecipe.Format format = new Gson().fromJson(object, AlloySmeltingRecipe.Format.class);

			return new AlloySmeltingRecipe(identifier, IngredientUtilities.fromBetterJson(format.firstInput), IngredientUtilities.fromBetterJson(format.secondInput), StackUtilities.fromJson(format.output), format.energyConsumed,
					format.time);
		}

		@Override
		public AlloySmeltingRecipe fromNetwork(ResourceLocation identifier, PacketBuffer buffer) {
			return new AlloySmeltingRecipe(identifier, IngredientUtilities.fromBetterPacket(buffer), IngredientUtilities.fromBetterPacket(buffer), StackUtilities.fromPacket(buffer), buffer.readInt(), buffer.readInt());
		}

		@Override
		public void toNetwork(PacketBuffer buffer, AlloySmeltingRecipe recipe) {
			IngredientUtilities.toBetterPacket(buffer, recipe.firstInput);
			IngredientUtilities.toBetterPacket(buffer, recipe.secondInput);
			StackUtilities.toPacket(buffer, recipe.output);
			EnergyUtilities.toPacket(buffer, recipe.energyConsumed);
			PacketUtilities.toPacket(buffer, recipe.time);
		}
	}

	public static final class Type implements AstromineRecipeType<AlloySmeltingRecipe> {
		public static final Type INSTANCE = new Type();

		private Type() {
			// Locked.
		}
	}

	public static final class Format {
		JsonObject firstInput;
		JsonObject secondInput;
		JsonObject output;
		@SerializedName("time")
		int time;
		@SerializedName("energy_consumed")
		int energyConsumed;

		@Override
		public String toString() {
			return "Format{" + "firstInput=" + firstInput + ", secondInput=" + secondInput + ", output=" + output + ", time=" + time + ", energyConsumed=" + energyConsumed + '}';
		}
	}
}
