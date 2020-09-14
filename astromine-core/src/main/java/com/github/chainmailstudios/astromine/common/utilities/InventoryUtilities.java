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

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import com.github.chainmailstudios.astromine.common.inventory.BaseInventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class InventoryUtilities {
	@OnlyIn(Dist.CLIENT)
	public static List<ItemStack> toList(Ingredient ingredient) {
		return new ArrayList<ItemStack>() {
			{
				for (ItemStack stack : ingredient.getItems()) {
					this.add(stack);
				}
			}
		};
	}

	public static List<ItemStack> toList(ItemStack[] array) {
		return Arrays.asList(array);
	}

	public static List<ItemStack> toListNonEmpty(IInventory inventory) {
		List<ItemStack> stackSet = new ArrayList<>();
		for (ItemStack stack : toList(inventory)) {
			if (stack.isEmpty()) {
				continue;
			}
			stackSet.add(stack);
		}
		return stackSet;
	}

	public static List<ItemStack> toList(IInventory inventory) {
		List<ItemStack> stackSet = new ArrayList();
		for (int i = 0; i < inventory.getContainerSize(); ++i) {
			stackSet.add(inventory.getItem(i));
		}
		return stackSet;
	}

	public static BaseInventory singleOf(IInventory origin, int slot) {
		return rangedOf(origin, new int[]{ slot });
	}

	public static BaseInventory rangedOf(IInventory origin, int[] slots) {
		BaseInventory recipeInventory = new BaseInventory(slots.length);
		int k = 0;
		for (int i : slots) {
			recipeInventory.setItem(k, origin.getItem(i));
		}
		return recipeInventory;
	}

	/**
	 * Sorts an inventory based on stack names and count.
	 *
	 * @param inventory
	 *        the specified inventory.
	 */
	public static void sort(IInventory inventory) {
		TreeMap<String, ArrayList<ItemStack>> byType = new TreeMap<>();

		for (int i = 0; i < inventory.getContainerSize(); ++i) {
			ItemStack stack = inventory.getItem(i);

			if (!byType.containsKey(stack.getHoverName().getContents()) && !stack.isEmpty()) {
				byType.put(stack.getHoverName().getContents(), new ArrayList<>());
			}

			if (!stack.isEmpty()) {
				byType.get(stack.getHoverName().getContents()).add(stack.copy());
			}

			inventory.setItem(i, ItemStack.EMPTY);
		}

		int i = 0;
		for (Map.Entry<String, ArrayList<ItemStack>> type : byType.entrySet()) {
			ArrayList<ItemStack> stacks = type.getValue();
			boolean finished = false;
			for (int k = 0; k < 128 && !finished; ++k) {
				for (int l = 0; l < stacks.size(); ++l) {
					boolean moved = false;
					if (l < stacks.size() - 1) {
						ItemStack current = stacks.get(l);
						ItemStack next = stacks.get(l + 1);

						if (current.getCount() < next.getCount()) {
							stacks.set(l + 1, current);
							stacks.set(l, next);
							moved = true;
						}
					}
					if (l == stacks.size() - 1 && !moved) {
						finished = true;
					}
				}
			}

			for (ItemStack stack : stacks) {
				inventory.setItem(i, stack);
				++i;
			}
		}
	}
}
