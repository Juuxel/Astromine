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

package com.github.chainmailstudios.astromine.common.component.inventory.compatibility;

import com.github.chainmailstudios.astromine.common.component.inventory.ItemInventoryComponent;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Vanilla wrapper for an InventoryComponent.
 */
public interface ItemInventoryFromInventoryComponent extends Container {
	/**
	 * Builds an wrapper over the given component for vanilla Inventory usage.
	 *
	 * @return the requested wrapper.
	 */
	static ItemInventoryFromInventoryComponent of(ItemInventoryComponent component) {
		return () -> component;
	}

	/**
	 * Retrieves the inventory's size.
	 *
	 * @return the requested size.
	 */
	@Override
	default int getContainerSize() {
		return this.getItemComponent().getItemSize();
	}

	/**
	 * Retrieves the InventoryComponent this wrapper is wrapping.
	 *
	 * @return the requested component.
	 */
	ItemInventoryComponent getItemComponent();

	/**
	 * Asserts whether inventory is empty or not.
	 *
	 * @return true if empty; false if not.
	 */
	@Override
	default boolean isEmpty() {
		return this.getItemComponent().isEmpty();
	}

	/**
	 * Retrieves the ItemStack in the specified slot.
	 *
	 * @param slot
	 *        the specified slot.
	 *
	 * @return the requested ItemStack.
	 */
	@Override
	default ItemStack getItem(int slot) {
		return this.getItemComponent().getStack(slot);
	}

	/**
	 * Extracts an ItemStack from the specified slot, the count extracted depending on the specified count.
	 *
	 * @param slot
	 *        the specified slot.
	 * @param count
	 *        the specified count.
	 *
	 * @return the requested ItemStack.
	 */
	@Override
	default ItemStack removeItem(int slot, int count) {
		if (this.getItemComponent().getStack(slot).getCount() < count) {
			InteractionResultHolder<ItemStack> result = this.getItemComponent().extract(null, slot);
			if (!result.getObject().isEmpty()) {
				this.setChanged();
			}
			return result.getObject();
		} else {
			InteractionResultHolder<ItemStack> result = this.getItemComponent().extract(slot, count);
			if (!result.getObject().isEmpty()) {
				this.setChanged();
			}
			return result.getObject();
		}
	}

	/**
	 * Removes and retrieves the ItemStack from the specified slot.
	 *
	 * @param slot
	 *        the specified slot.
	 *
	 * @return the retrieved ItemStack.
	 */
	@Override
	default ItemStack removeItemNoUpdate(int slot) {
		return this.getItemComponent().extract(null, slot).getObject();
	}

	/**
	 * Overrides the ItemStack in the specified slot with the specified stack.
	 *
	 * @param slot
	 *        the specified slot.
	 * @param stack
	 *        the specified stack.
	 */
	@Override
	default void setItem(int slot, ItemStack stack) {
		if (this.getItemComponent().getMaximumCount(slot) < stack.getCount()) {
			stack.setCount(this.getItemComponent().getMaximumCount(slot));
		}
		this.getItemComponent().setStack(slot, stack);
	}

	/**
	 * Dispatches updates to inventory listeners.
	 */
	@Override
	default void setChanged() {
		this.getItemComponent().dispatchConsumers();
	}

	/**
	 * Asserts whether the specified player can access this inventory.
	 *
	 * @param player
	 *        the specified player.
	 *
	 * @return true if yes; false if no.
	 */
	@Override
	default boolean stillValid(Player player) {
		return true;
	}

	@Override
	default boolean canPlaceItem(int slot, ItemStack stack) {
		return this.getItemComponent().canInsert(null, stack, slot);
	}

	/**
	 * Clears this inventory.
	 */
	@Override
	default void clearContent() {
		this.getItemComponent().clear();
	}
}
