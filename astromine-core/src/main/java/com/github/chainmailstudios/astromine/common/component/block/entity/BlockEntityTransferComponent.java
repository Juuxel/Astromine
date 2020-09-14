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

package com.github.chainmailstudios.astromine.common.component.block.entity;

import com.github.chainmailstudios.astromine.common.block.transfer.TransferType;
import com.github.chainmailstudios.astromine.common.utilities.DirectionUtilities;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BlockEntityTransferComponent {
	private final Object2ReferenceMap<String, TransferEntry> components = new Object2ReferenceOpenHashMap<>();

	public TransferEntry get(Capability<?> type) {
		return get(type.getName());
	}

	public TransferEntry get(String type) {
		return components.computeIfAbsent(type, t -> new TransferEntry());
	}

	public Map<String, TransferEntry> get() {
		return components;
	}

	public void add(Capability<?> type) {
		add(type.getName());
	}

	public void add(String type) {
		components.put(type, new TransferEntry());
	}

	public void fromTag(CompoundNBT tag) {
		CompoundNBT dataTag = tag.getCompound("data");

		for (String key : dataTag.getAllKeys()) {
			TransferEntry entry = new TransferEntry();
			entry.fromTag(dataTag.getCompound(key));
			components.put(key, entry);
		}
	}

	@NotNull
	public CompoundNBT toTag(CompoundNBT tag) {
		CompoundNBT dataTag = new CompoundNBT();

		for (Map.Entry<String, TransferEntry> entry : components.entrySet()) {
			dataTag.put(entry.getKey(), entry.getValue().toTag(new CompoundNBT()));
		}

		tag.put("data", dataTag);

		return tag;
	}

	public static class TransferEntry {
		public static final Direction[] DIRECTIONS = Direction.values();
		private final Reference2ReferenceMap<Direction, TransferType> types = new Reference2ReferenceOpenHashMap<>(6, 1);

		public TransferEntry() {
			for (Direction direction : DIRECTIONS) {
				this.set(direction, TransferType.NONE);
			}
		}

		public void set(Direction direction, TransferType type) {
			types.put(direction, type);
		}

		public TransferType get(Direction origin) {
			return types.get(origin);
		}

		public void fromTag(CompoundNBT tag) {
			for (String directionKey : tag.getAllKeys()) {
				if (tag.contains(directionKey)) {
					types.put(DirectionUtilities.byNameOrId(directionKey), TransferType.valueOf(tag.getString(directionKey)));
				}
			}
		}

		public CompoundNBT toTag(CompoundNBT tag) {
			for (Map.Entry<Direction, TransferType> entry : types.entrySet()) {
				if (entry.getValue() != TransferType.NONE)
					tag.putString(String.valueOf(entry.getKey().getName()), entry.getValue().toString());
			}

			return tag;
		}

		public boolean areAllNone() {
			for (TransferType value : types.values()) {
				if (value != TransferType.NONE)
					return false;
			}
			return true;
		}
	}

	private static class ImmutableTransferEntry extends TransferEntry {
		private static final TransferEntry INSTANCE = new ImmutableTransferEntry();

		@Override
		public void set(Direction direction, TransferType type) {}

		@Override
		public TransferType get(Direction origin) {
			return TransferType.NONE;
		}

		@Override
		public void fromTag(CompoundNBT tag) {}

		@Override
		public CompoundNBT toTag(CompoundNBT tag) {
			return tag;
		}

		@Override
		public boolean areAllNone() {
			return true;
		}
	}
}
