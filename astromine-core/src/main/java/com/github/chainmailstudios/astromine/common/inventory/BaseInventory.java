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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;

/**
 * A BaseInventory is a class responsible for
 * effectively handling what a BasicInventory
 * does, however, allowing stack sizes
 * higher than the default of 64.
 */
public class BaseInventory implements Container, StackedContentsCompatible {
	protected int size;
	protected NonNullList<ItemStack> stacks;
	protected List<ContainerListener> listeners = new ArrayList<>();

	public BaseInventory(int size) {
		this.size = size;
		this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
	}

	public BaseInventory(ItemStack... items) {
		this.size = items.length;
		this.stacks = NonNullList.of(ItemStack.EMPTY, items);
	}

	public static BaseInventory of(int size) {
		return new BaseInventory(size);
	}

	public static BaseInventory of(ItemStack... items) {
		return new BaseInventory(items);
	}

	public void addListener(ContainerListener... listeners) {
		this.listeners.addAll(Arrays.asList(listeners));
	}

	public void removeListener(ContainerListener... listeners) {
		this.listeners.removeAll(Arrays.asList(listeners));
	}

	@Override
	public int getContainerSize() {
		return this.size;
	}

	@Override
	public boolean isEmpty() {
		return this.stacks.stream().allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getItem(int slot) {
		return slot >= 0 && slot < this.stacks.size() ? this.stacks.get(slot) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack stack = ContainerHelper.removeItem(this.stacks, slot, amount);
		if (!stack.isEmpty()) {
			this.setChanged();
		}

		return stack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack stack = this.stacks.get(slot);
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.stacks.set(slot, ItemStack.EMPTY);
			this.setChanged();
			return stack;
		}
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		this.stacks.set(slot, stack);

		this.setChanged();
	}

	@Override
	public void setChanged() {
		for (ContainerListener listener : listeners) {
			listener.containerChanged(this);
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void clearContent() {
		this.stacks.clear();
		this.setChanged();
	}

	public String toString() {
		return (this.stacks.stream().filter((stack) -> !stack.isEmpty()).collect(Collectors.toList())).toString();
	}

	@Override
	public void fillStackedContents(StackedContents recipeFinder) {}
}
