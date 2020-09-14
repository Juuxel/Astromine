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

package com.github.chainmailstudios.astromine.common.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * A simple {@code Inventory} implementation with only default methods + an item list getter.
 * <p>
 * Originally by Juuz
 */
public interface SingularStackInventory extends IInventory {
	/**
	 * Creates an inventory from the item list.
	 */
	static SingularStackInventory of(NonNullList<ItemStack> items) {
		return () -> items;
	}
	// Creation

	/**
	 * Creates a new inventory with the size.
	 */
	static SingularStackInventory ofSize(int size) {
		return of(NonNullList.withSize(size, ItemStack.EMPTY));
	}

	/**
	 * Gets the item list of this inventory. Must return the same instance every time it's called.
	 */
	NonNullList<ItemStack> getItems();
	// Inventory

	/**
	 * Returns the inventory size.
	 */
	@Override
	default int getContainerSize() {
		return getItems().size();
	}

	/**
	 * @return true if this inventory has only empty stacks, false otherwise
	 */
	default boolean isInvEmpty() {
		for (int i = 0; i < getContainerSize(); i++) {
			ItemStack stack = getItem(i);
			if (!stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	default boolean isEmpty() {
		return isInvEmpty();
	}

	/**
	 * Gets the item in the slot.
	 */
	@Override
	default ItemStack getItem(int slot) {
		return getItems().get(slot);
	}

	default ItemStack getStack() {
		return getItem(0);
	}

	default void setStack(ItemStack stack) {
		setItem(0, stack);
	}

	/**
	 * Takes a stack of the size from the slot.
	 * <p>
	 * (default implementation) If there are less items in the slot than what are requested, takes all items in that
	 * slot.
	 */
	@Override
	default ItemStack removeItem(int slot, int count) {
		ItemStack result = ItemStackHelper.removeItem(getItems(), slot, count);
		if (!result.isEmpty()) {
			setChanged();
		}
		return result;
	}

	/**
	 * Removes the current stack in the {@code slot} and returns it.
	 */
	@Override
	default ItemStack removeItemNoUpdate(int slot) {
		ItemStack stack = ItemStackHelper.takeItem(getItems(), slot);
		setChanged();
		return stack;
	}

	default ItemStack removeStack() {
		return removeItemNoUpdate(0);
	}

	/**
	 * Replaces the current stack in the {@code slot} with the provided stack.
	 * <p>
	 * If the stack is too big for this inventory ({@link Inventory#getMaxCountPerStack()}), it gets resized to this
	 * inventory's maximum amount.
	 */
	@Override
	default void setItem(int slot, ItemStack stack) {
		getItems().set(slot, stack);
		if (stack.getCount() > getMaxStackSize()) {
			stack.setCount(getMaxStackSize());
		}
		setChanged();
	}

	/**
	 * Clears {@linkplain #getItems() the item list}}.
	 */
	@Override
	default void clearContent() {
		getItems().clear();
		setChanged();
	}

	@Override
	default void setChanged() {
		// Override if you want behavior.
	}

	@Override
	default boolean stillValid(PlayerEntity player) {
		return true;
	}
}
