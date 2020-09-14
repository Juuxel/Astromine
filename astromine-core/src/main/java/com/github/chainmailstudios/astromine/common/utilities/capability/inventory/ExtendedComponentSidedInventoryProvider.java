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

package com.github.chainmailstudios.astromine.common.utilities.capability.inventory;

import nerdhub.cardinal.components.api.component.ComponentProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.github.chainmailstudios.astromine.common.component.SidedComponentProvider;
import com.github.chainmailstudios.astromine.common.component.inventory.ItemInventoryComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.compatibility.ItemInventoryFromInventoryComponent;
import com.github.chainmailstudios.astromine.common.utilities.TransportUtilities;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.stream.IntStream;

public interface ExtendedComponentSidedInventoryProvider extends SidedComponentProvider, WorldlyContainerHolder, WorldlyContainer, ItemInventoryFromInventoryComponent {
	@Override
	default WorldlyContainer getContainer(BlockState state, LevelAccessor world, BlockPos pos) {
		return this;
	}

	default boolean isSideOpenForItems(int slot, Direction direction, boolean inserting) {
		return inserting ? TransportUtilities.isInsertingItem((BlockEntity) this, getComponent(AstromineComponentTypes.BLOCK_ENTITY_TRANSFER_COMPONENT), direction, true) && getItemInputSlots().contains(slot) : TransportUtilities.isExtractingItem((BlockEntity) this, getComponent(
			AstromineComponentTypes.BLOCK_ENTITY_TRANSFER_COMPONENT), direction, true) && getItemOutputSlots().contains(slot);
	}

	default IntSet getItemInputSlots() {
		return new IntArraySet(IntStream.range(0, getContainerSize()).toArray());
	}

	default IntSet getItemOutputSlots() {
		return new IntArraySet(IntStream.range(0, getContainerSize()).toArray());
	}

	@Override
	default boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
		return isSideOpenForItems(slot, dir, true);
	}

	@Override
	default boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
		return isSideOpenForItems(slot, dir, false);
	}

	@Override
	default int[] getSlotsForFace(Direction side) {
		return IntStream.range(0, getContainerSize()).filter(slot -> isSideOpenForItems(slot, side, true) || isSideOpenForItems(slot, side, false)).toArray();
	}

	@Override
	default ItemInventoryComponent getItemComponent() {
		return getComponent(AstromineComponentTypes.ITEM_INVENTORY_COMPONENT);
	}
}
