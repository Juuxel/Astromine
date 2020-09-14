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

package com.github.chainmailstudios.astromine.common.utilities;

import com.github.chainmailstudios.astromine.common.recipe.ingredient.ArrayIngredient;

import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;

public class IngredientUtilities {
	public static Ingredient fromJson(JsonElement jsonElement) {
		return Ingredient.fromJson(jsonElement);
	}

	public static ArrayIngredient fromBetterJson(JsonElement jsonElement) {
		return ArrayIngredient.fromJson(jsonElement);
	}

	public static Ingredient fromPacket(FriendlyByteBuf buffer) {
		return Ingredient.fromNetwork(buffer);
	}

	public static void toPacket(FriendlyByteBuf buffer, Ingredient ingredient) {
		ingredient.toNetwork(buffer);
	}

	public static ArrayIngredient fromBetterPacket(FriendlyByteBuf buffer) {
		return ArrayIngredient.fromPacket(buffer);
	}

	public static void toBetterPacket(FriendlyByteBuf buffer, ArrayIngredient ingredient) {
		ingredient.write(buffer);
	}
}
