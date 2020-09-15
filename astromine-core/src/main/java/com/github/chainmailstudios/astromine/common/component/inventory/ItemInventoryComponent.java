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

package com.github.chainmailstudios.astromine.common.component.inventory;

import com.github.chainmailstudios.astromine.registry.AstromineItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface ItemInventoryComponent extends NameableComponent, IItemHandler {
	default Item getSymbol() {
		return AstromineItems.ITEM.get();
	}

	default TranslationTextComponent getName() {
		return new TranslationTextComponent("text.astromine.item");
	}

	/**
	 * Retrieves contents of this inventory that match a specific predicate as a collection as ItemStack copies.
	 *
	 * @param predicate the specified predicate.
	 * @return the retrieved collection.
	 */
	default List<ItemStack> getContentsMatching(Predicate<ItemStack> predicate) {
		return this.getContents().values().stream().filter(predicate).collect(Collectors.toList());
	}

	default List<ItemStack> getExtractableContentsMatching(Direction direction, Predicate<ItemStack> predicate) {
		return this.getContents().entrySet().stream().filter((entry) -> canExtract(direction, entry.getValue(), entry.getKey()) && predicate.test(entry.getValue())).map(Map.Entry::getValue).collect(Collectors.toList());
	}

	/**
	 * Retrieves contents of this inventory as a collection of the held ItemStack copies.
	 *
	 * @return the retrieved collection.
	 */
	Map<Integer, ItemStack> getContents();

	@Override
	default int getSlots() {
		return getItemSize();
	}

	@NotNull
	@Override
	default ItemStack getStackInSlot(int slot) {
		return getStack(slot);
	}

	@NotNull
	@Override
	default ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		ActionResult<ItemStack> result = insert(null, stack, simulate);
		if (result.getResult().consumesAction())
			return result.getObject();
		return stack;
	}

	@NotNull
	@Override
	default ItemStack extractItem(int slot, int amount, boolean simulate) {
		ActionResult<ItemStack> result = extract(null, amount, simulate);
		if (result.getResult().consumesAction())
			return result.getObject();
		return ItemStack.EMPTY;
	}

	@Override
	default int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	default boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return canInsert(null, stack, slot);
	}

	/**
	 * Retrieves contents of this inventory that match a specific predicate as a collection as ItemStack copies.
	 *
	 * @param predicate the specified predicate.
	 * @return the retrieved collection.
	 */
	default Collection<ItemStack> getContentsMatchingSimulated(Predicate<ItemStack> predicate) {
		return this.getContentsSimulated().stream().map(ItemStack::copy).filter(predicate).collect(Collectors.toList());
	}

	/**
	 * Retrieves contents of this inventory as a collection of the held ItemStack copies.
	 *
	 * @return the retrieved collection.
	 */
	default Collection<ItemStack> getContentsSimulated() {
		return this.getContents().values().stream().map(ItemStack::copy).collect(Collectors.toList());
	}

	default boolean canInsert() {
		return true;
	}

	default boolean canInsert(@Nullable Direction direction, ItemStack stack, int slot) {
		return true;
	}

	default boolean canExtract() {
		return true;
	}

	default boolean canExtract(Direction direction, ItemStack stack, int slot) {
		return true;
	}

	/**
	 * Inserts a specific ItemStack into this inventory if possible, from a generic, non-existent position.
	 *
	 * @param stack the specified stack.
	 * @return SUCCESS w. empty if inserted; FAIL w. stack if not.
	 */
	default ActionResult<ItemStack> insert(Direction direction, ItemStack stack, boolean simulate) {
		if (this.canInsert()) {
			return this.insert(direction, stack, stack.getCount(), simulate);
		} else {
			return new ActionResult<>(ActionResultType.FAIL, stack);
		}
	}

	/**
	 * Inserts a specific ItemStack into this inventory if possible, from a generic, non-existent position, the count
	 * inserted depending on the specified count.
	 *
	 * @param stack the specified stack.
	 * @param count the specified count.
	 * @return SUCCESS w. modified stack if inserted; FAIL w. unmodified stack if not.
	 */
	default ActionResult<ItemStack> insert(Direction direction, ItemStack stack, int count, boolean simulate) {
		ItemStack finalStack = stack;
		Optional<Map.Entry<Integer, ItemStack>> matchingStackOptional = this.getContents().entrySet().stream().filter(entry -> {
			ItemStack storedStack = entry.getValue();

			return (this.canInsert(direction, finalStack, entry.getKey())) && (storedStack.getItem() == finalStack.getItem() && storedStack.getMaxStackSize() - storedStack.getCount() >= count && (!storedStack.hasTag() && !finalStack.hasTag()) || (storedStack.hasTag() && finalStack
					.hasTag() && storedStack.getTag().equals(finalStack.getTag()) || storedStack.isEmpty()));
		}).findFirst();

		if (matchingStackOptional.isPresent()) {
			ItemStack matchingStack = matchingStackOptional.get().getValue();
			if (matchingStack.isEmpty()) {
				matchingStack = stack.copy();
				stack = ItemStack.EMPTY;
				if (!simulate)
					this.setStack(matchingStackOptional.get().getKey(), matchingStack);
			} else {
				stack = stack.copy();
				if (!simulate)
					matchingStack.grow(stack.getCount());
				stack.shrink(stack.getCount());
			}
			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		} else {
			return new ActionResult<>(ActionResultType.FAIL, stack);
		}
	}

	/**
	 * Sets an ItemStack in a given slot.
	 */
	default void setStack(int slot, ItemStack stack) {
		if (slot <= this.getItemSize()) {
			this.getContents().put(slot, stack);
			this.dispatchConsumers();
		}
	}

	/**
	 * Retrieves the inventory's size.
	 */
	int getItemSize();

	/**
	 * Dispatches updates to all listeners.
	 */
	default void dispatchConsumers() {
		this.getItemListeners().forEach(Runnable::run);
	}

	/**
	 * Retrieves all listeners listening to this inventory.
	 *
	 * @return
	 */
	List<Runnable> getItemListeners();

	/**
	 * Extracts the contents of this inventory that match a given predicate as a collection, from a generic non-existent
	 * position.
	 *
	 * @param predicate the specified predicate.
	 * @return SUCCESS w. the retrieved collection if extracted anything; FAIL w. empty if not.
	 */
	default ActionResult<Collection<ItemStack>> extractMatching(Direction direction, Predicate<ItemStack> predicate) {
		HashSet<ItemStack> extractedStacks = new HashSet<>();
		this.getContents().forEach((slot, stack) -> {
			if (predicate.test(stack)) {
				ActionResult<ItemStack> extractionResult = this.extract(direction, slot, false);

				if (extractionResult.getResult().consumesAction()) {
					extractedStacks.add(extractionResult.getObject());
				}
			}
		});

		if (!extractedStacks.isEmpty()) {
			return new ActionResult<>(ActionResultType.SUCCESS, extractedStacks);
		} else {
			return new ActionResult<>(ActionResultType.FAIL, extractedStacks);
		}
	}

	/**
	 * Extracts the first stack in the inventory that matches a given predicate.
	 *
	 * @param predicate the specified predicate.
	 * @return SUCCESS w. the retrieved collection if extracted anything; FAIL w. empty if not.
	 */
	default ActionResult<ItemStack> extractFirstMatching(Direction direction, Predicate<ItemStack> predicate) {
		AtomicReference<ItemStack> extractedStack = new AtomicReference<>();
		extractedStack.set(ItemStack.EMPTY);
		for (int slot = 0; slot < this.getContents().size(); slot++) {
			ItemStack stack = this.getContents().get(slot);
			if (predicate.test(stack)) {
				ActionResult<ItemStack> extractionResult = this.extract(direction, slot, false);

				if (extractionResult.getResult().consumesAction()) {
					extractedStack.set(extractionResult.getObject());
					break;
				}
			}
		}

		if (!extractedStack.get().isEmpty()) {
			return new ActionResult<>(ActionResultType.SUCCESS, extractedStack.get());
		} else {
			return new ActionResult<>(ActionResultType.FAIL, extractedStack.get());
		}
	}

	/**
	 * Extracts a specific ItemStack from this inventory if possible, from a generic, non-existent position.
	 *
	 * @param slot the slot of the specified stack.
	 * @return SUCCESS w. stack if extracted; FAIL w. empty if not.
	 */
	default ActionResult<ItemStack> extract(Direction direction, int slot, boolean simulate) {
		ItemStack matchingStack = this.getStack(slot);

		if (this.canExtract(direction, matchingStack, slot)) {
			return this.extract(slot, matchingStack.getCount(), simulate);
		} else {
			return new ActionResult<>(ActionResultType.FAIL, ItemStack.EMPTY);
		}
	}

	/**
	 * Retrieves an ItemStack from a given slot.
	 */
	default ItemStack getStack(int slot) {
		return this.getContents().getOrDefault(slot, ItemStack.EMPTY);
	}

	/**
	 * Extracts a specific ItemStack from this inventory if possible, from a generic non-existent position, the count
	 * extracted depending on the specified count.
	 *
	 * @param slot  the slot of the specified stack.
	 * @param count the specified count.
	 * @return SUCCESS w. stack if extracted; FAIL w. empty if not.
	 */
	default ActionResult<ItemStack> extract(int slot, int count, boolean simulate) {
		Optional<ItemStack> matchingStackOptional = Optional.ofNullable(this.getStack(slot));

		if (matchingStackOptional.isPresent()) {
			if (matchingStackOptional.get().getCount() >= count) {
				ItemStack matchingStack = matchingStackOptional.get();
				ItemStack remainingStack = matchingStack.copy();
				remainingStack.shrink(count);
				matchingStack.setCount(count);
				if (!simulate)
					this.setStack(slot, remainingStack);
				return new ActionResult<>(ActionResultType.SUCCESS, matchingStack);
			} else {
				return new ActionResult<>(ActionResultType.FAIL, ItemStack.EMPTY);
			}
		} else {
			return new ActionResult<>(ActionResultType.FAIL, ItemStack.EMPTY);
		}
	}

	/**
	 * Adds a listener to this inventory.
	 *
	 * @param listener the specified listener.
	 */
	default void addListener(Runnable listener) {
		this.getItemListeners().add(listener);
	}

	default ItemInventoryComponent withListener(Consumer<ItemInventoryComponent> listener) {
		addListener(() -> listener.accept(this));
		return this;
	}

	/**
	 * Removes a listener from this inventory.
	 *
	 * @param listener the specified listener.
	 */
	default void removeListener(Runnable listener) {
		this.getItemListeners().remove(listener);
	}

	/**
	 * Retrieves the maximum stack size for a given slot.
	 */
	default int getMaximumCount(int slot) {
		return 64;
	}

	/**
	 * Clears this inventory.
	 */
	default void clear() {
		this.getContents().clear();
	}

	/**
	 * Asserts whether this inventory is empty or not.
	 */
	default boolean isEmpty() {
		return this.getContents().values().stream().allMatch(ItemStack::isEmpty);
	}
}
