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

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class StackUtilities {
	public static boolean areEqual(List<ItemStack> stackListA, List<ItemStack> stackListB) {
		for (ItemStack stackA : stackListA) {
			boolean found = false;
			for (ItemStack stackB : stackListB) {
				if (ItemStack.matches(stackA, stackB)) {
					found = true;
				}
				if (found) {
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	public static boolean equalItemAndTag(ItemStack stackA, ItemStack stackB) {
		return ItemStack.tagMatches(stackA, stackB) && ItemStack.isSameIgnoreDurability(stackA, stackB);
	}

	public static ItemStack fromPacket(FriendlyByteBuf buffer) {
		return buffer.readItem();
	}

	public static void toPacket(FriendlyByteBuf buffer, ItemStack stack) {
		buffer.writeItem(stack);
	}

	public static ItemStack fromJson(JsonObject jsonObject) {
		return ShapedRecipe.itemFromJson(jsonObject);
	}

	public static ItemStack withLore(ItemStack stack, Collection<Component> texts) {
		List<Component> entries = (List<Component>) texts;

		ListTag loreListTag = new ListTag();

		entries.forEach(text -> loreListTag.add(StringTag.valueOf(Component.Serializer.toJson(text))));

		CompoundTag displayTag = stack.getOrCreateTag().getCompound("display");

		displayTag.put("Lore", loreListTag);

		CompoundTag stackTag = stack.getOrCreateTag();

		stackTag.put("display", displayTag);

		stack.setTag(stackTag);

		return stack;
	}

	public static Tuple<ItemStack, ItemStack> merge(Supplier<ItemStack> supplierA, Supplier<ItemStack> supplierB, Supplier<Integer> sA, Supplier<Integer> sB) {
		return merge(supplierA.get(), supplierB.get(), sA.get(), sB.get());
	}

	/**
	 * Support merging stacks with customized maximum count.
	 *
	 * @param stackA
	 *        Source ItemStack
	 * @param stackB
	 *        Destination ItemStack
	 * @param maxA
	 *        Max. count of stackA
	 * @param maxB
	 *        Max. count of stackB
	 *
	 * @return Resulting ItemStacks
	 */
	public static Tuple<ItemStack, ItemStack> merge(ItemStack stackA, ItemStack stackB, int maxA, int maxB) {
		if (equalItemAndTag(stackA, stackB)) {
			int countA = stackA.getCount();
			int countB = stackB.getCount();

			int availableA = Math.max(0, maxA - countA);
			int availableB = Math.max(0, maxB - countB);

			stackB.grow(Math.min(countA, availableB));
			stackA.setCount(Math.max(countA - availableB, 0));
		} else {
			if (stackA.isEmpty() && !stackB.isEmpty()) {
				int countA = stackA.getCount();
				int availableA = maxA - countA;

				int countB = stackB.getCount();

				stackA = stackB.copy();
				stackA.setCount(Math.min(countB, availableA));

				stackA.setTag(stackB.getTag());
				stackB.shrink(Math.min(countB, availableA));
			} else if (stackB.isEmpty() && !stackA.isEmpty()) {
				int countB = stackB.getCount();
				int availableB = maxB - countB;

				int countA = stackA.getCount();

				stackB = stackA.copy();
				stackB.setCount(Math.min(countA, availableB));
				stackB.setTag(stackA.getTag());
				stackA.shrink(Math.min(countA, availableB));
			}
		}

		return new Tuple<>(stackA, stackB);
	}
}
