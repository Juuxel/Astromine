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

package com.github.chainmailstudios.astromine.common.recipe.base;

import com.github.chainmailstudios.astromine.common.block.entity.base.ComponentBlockEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public interface AdvancedRecipe<C extends IInventory> extends AstromineRecipe<C> {
	<T extends ComponentBlockEntity> boolean canCraft(T t);

	<T extends ComponentBlockEntity> void craft(T t);

	<T extends RecipeConsumer> void tick(T t);

	@Override
	default boolean matches(C inventory, World world) {
		return false;
	}

	@Override
	default ItemStack assemble(C inventory) {
		return ItemStack.EMPTY;
	}

	@Override
	default boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	default ItemStack getResultItem() {
		return ItemStack.EMPTY;
	}

	@Override
	default NonNullList<ItemStack> getRemainingItems(C inventory) {
		return NonNullList.create();
	}

	@Override
	default NonNullList<Ingredient> getIngredients() {
		return NonNullList.create();
	}

	@Override
	default boolean isSpecial() {
		return false;
	}

	@Override
	default String getGroup() {
		return "";
	}
}
