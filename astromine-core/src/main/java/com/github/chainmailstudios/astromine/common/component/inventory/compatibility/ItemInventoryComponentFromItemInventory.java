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

import com.github.chainmailstudios.astromine.common.component.inventory.SimpleItemInventoryComponent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

/**
 * An InventoryComponentFromInventory is a wrapper over an Inventory that provides the functions and utilities of an
 * InventoryComponent.
 */
public class ItemInventoryComponentFromItemInventory extends SimpleItemInventoryComponent {
	IInventory inventory;
	List<Runnable> listeners = new ArrayList<>();

	private ItemInventoryComponentFromItemInventory(IInventory inventory) {
		super(inventory.getContainerSize());
		this.inventory = inventory;
	}

	public static ItemInventoryComponentFromItemInventory of(IInventory inventory) {
		return new ItemInventoryComponentFromItemInventory(inventory);
	}

	@Override
	public Map<Integer, ItemStack> getContents() {
		HashMap<Integer, ItemStack> contents = new HashMap<>();
		for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
			contents.put(i, this.inventory.getItem(i));
		}
		return contents;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.inventory.setItem(slot, stack);
	}

	@Override
	public int getItemSize() {
		return this.inventory.getContainerSize();
	}

	@Override
	public List<Runnable> getItemListeners() {
		return this.listeners;
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.inventory.getItem(slot);
	}

	@Override
	public boolean canInsert(@Nullable Direction direction, ItemStack stack, int slot) {
		return this.inventory.canPlaceItem(slot, stack);
	}
}
