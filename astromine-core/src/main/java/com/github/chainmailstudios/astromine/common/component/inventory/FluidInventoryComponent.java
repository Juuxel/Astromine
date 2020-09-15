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

import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.common.utilities.data.Range;
import com.github.chainmailstudios.astromine.registry.AstromineItems;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface FluidInventoryComponent extends NameableComponent {
	Map<Integer, FluidStack> getContents();

	default Item getSymbol() {
		return AstromineItems.FLUID.get().asItem();
	}

	default TranslationTextComponent getName() {
		return new TranslationTextComponent("text.astromine.fluid");
	}

	default Collection<FluidStack> getContentsMatching(Predicate<FluidStack> predicate) {
		return this.getContents().values().stream().filter(predicate).collect(Collectors.toList());
	}

	default Collection<FluidStack> getExtractableContentsMatching(Direction direction, Predicate<FluidStack> predicate) {
		return this.getContents().entrySet().stream().filter((entry) -> canExtract(direction, entry.getValue(), entry.getKey()) && predicate.test(entry.getValue())).map(Map.Entry::getValue).collect(Collectors.toList());
	}

	default Collection<FluidStack> getContentsMatchingSimulated(Predicate<FluidStack> predicate) {
		return this.getContentsSimulated().stream().filter(predicate).collect(Collectors.toList());
	}

	default Collection<FluidStack> getContentsSimulated() {
		return this.getContents().values().stream().map((volume) -> (FluidStack) volume.copy()).collect(Collectors.toList());
	}

	default boolean canInsert() {
		return true;
	}

	default boolean canInsert(@Nullable Direction direction, FluidStack volume, int slot) {
		return true;
	}

	default boolean canExtract() {
		return true;
	}

	default boolean canExtract(@Nullable Direction direction, FluidStack volume, int slot) {
		return true;
	}

	default ActionResult<FluidStack> insert(Direction direction, FluidStack volume) {
		if (this.canInsert()) {
			return this.insert(direction, volume.getFluid(), volume.getAmount());
		} else {
			return new ActionResult<>(ActionResultType.FAIL, volume);
		}
	}

	default ActionResult<FluidStack> insert(Direction direction, Fluid fluid, int fraction) {
		Optional<Map.Entry<Integer, FluidStack>> matchingVolumeOptional = this.getContents().entrySet().stream().filter(entry -> {
			return canInsert(direction, entry.getValue(), entry.getKey()) && entry.getValue().getFluid() == fluid;
		}).findFirst();

		if (matchingVolumeOptional.isPresent()) {
			matchingVolumeOptional.get().getValue().grow(fraction);
			return new ActionResult<>(ActionResultType.SUCCESS, matchingVolumeOptional.get().getValue());
		} else {
			return new ActionResult<>(ActionResultType.FAIL, null);
		}
	}

	default void setVolume(int slot, FluidStack volume) {
		if (slot <= this.getSize()) {
			this.getContents().put(slot, volume);
			this.dispatchConsumers();
		}
	}

	int getSize();

	default void dispatchConsumers() {
		this.getListeners().forEach(Runnable::run);
	}

	List<Runnable> getListeners();

	default ActionResult<Collection<FluidStack>> extractMatching(Direction direction, Predicate<FluidStack> predicate) {
		HashSet<FluidStack> extractedVolumes = new HashSet<>();
		this.getContents().forEach((slot, volume) -> {
			if (canExtract(direction, volume, slot) && predicate.test(volume)) {
				ActionResult<FluidStack> extractionResult = this.extract(direction, slot);

				if (extractionResult.getResult().consumesAction()) {
					extractedVolumes.add(extractionResult.getObject());
				}
			}
		});

		if (!extractedVolumes.isEmpty()) {
			return new ActionResult<>(ActionResultType.SUCCESS, extractedVolumes);
		} else {
			return new ActionResult<>(ActionResultType.FAIL, extractedVolumes);
		}
	}

	default ActionResult<FluidStack> extract(Direction direction, int slot) {
		FluidStack volume = this.getVolume(slot);

		if (!volume.isEmpty() && this.canExtract(direction, volume, slot)) {
			return this.extract(direction, slot, volume.getAmount());
		} else {
			return new ActionResult<>(ActionResultType.FAIL, FluidStack.EMPTY);
		}
	}

	@Nullable
	default FluidStack getVolume(int slot) {
		return this.getContents().getOrDefault(slot, null);
	}

	@Nullable
	default FluidStack getFirstExtractableVolume(Direction direction) {
		return getContents().entrySet().stream().filter((entry) -> canExtract(direction, entry.getValue(), entry.getKey()) && !entry.getValue().isEmpty()).map(Map.Entry::getValue).findFirst().orElse(null);
	}

	@Nullable
	default FluidStack getFirstInsertableVolume(FluidStack volume, Direction direction) {
		return getContents().entrySet().stream().filter((entry) -> canInsert(direction, entry.getValue(), entry.getKey()) && (entry.getValue().isEmpty() || (entry.getValue().getFluid() == volume.getFluid() && entry.getValue().hasAvailable(volume.getAmount())))).map(
				Map.Entry::getValue).findFirst().orElse(null);
	}

	@Nullable
	default FluidStack getFirstInsertableVolume(Fluid fluid, Direction direction) {
		return getContents().entrySet().stream().filter((entry) -> canInsert(direction, entry.getValue(), entry.getKey()) && (entry.getValue().isEmpty() || (entry.getValue().getFluid() == fluid))).map(Map.Entry::getValue).findFirst().orElse(null);
	}

	default ActionResult<FluidStack> extract(Direction direction, int slot, int fraction) {
		Optional<FluidStack> matchingVolumeOptional = Optional.ofNullable(this.getVolume(slot));

		if (matchingVolumeOptional.isPresent()) {
			FluidStack volume = matchingVolumeOptional.get();

			if (canExtract(direction, volume, slot)) {
				matchingVolumeOptional.get().shrink(fraction);
				return new ActionResult<>(ActionResultType.SUCCESS, matchingVolumeOptional.get());
			} else {
				return new ActionResult<>(ActionResultType.FAIL, FluidStack.EMPTY);
			}
		} else {
			return new ActionResult<>(ActionResultType.FAIL, FluidStack.EMPTY);
		}
	}

	default CompoundNBT write(FluidInventoryComponent source, Optional<String> subtag, Optional<Range<Integer>> range) {
		CompoundNBT tag = new CompoundNBT();
		this.write(source, tag, subtag, range);
		return tag;
	}

	default void write(FluidInventoryComponent source, CompoundNBT tag, Optional<String> subtag, Optional<Range<Integer>> range) {
		if (source == null || source.getSize() <= 0) {
			return;
		}

		if (tag == null) {
			return;
		}

		CompoundNBT volumesTag = new CompoundNBT();

		int minimum = range.isPresent() ? range.get().getMinimum() : 0;
		int maximum = range.isPresent() ? range.get().getMaximum() : source.getSize();

		for (int position = minimum; position < maximum; ++position) {
			if (source.getVolume(position) != null) {
				FluidStack volume = source.getVolume(position);

				if (volume != null) {
					CompoundNBT volumeTag = source.getVolume(position).writeToNBT(new CompoundNBT());

					volumesTag.put(String.valueOf(position), volumeTag);
				}
			}
		}

		if (subtag.isPresent()) {
			CompoundNBT inventoryTag = new CompoundNBT();

			inventoryTag.putInt("size", source.getSize());
			inventoryTag.put("volumes", volumesTag);

			tag.put(subtag.get(), inventoryTag);
		} else {
			tag.putInt("size", source.getSize());
			tag.put("volumes", volumesTag);
		}
	}

	default void read(FluidInventoryComponent target, CompoundNBT tag, Optional<String> subtag, Optional<Range<Integer>> range) {
		if (tag == null) {
			return;
		}

		INBT rawTag;

		if (subtag.isPresent()) {
			rawTag = tag.get(subtag.get());
		} else {
			rawTag = tag;
		}

		if (!(rawTag instanceof CompoundNBT)) {
			AstromineCommon.LOGGER.log(Level.ERROR, "Inventory contents failed to be read: " + rawTag.getClass().getName() + " is not instance of " + CompoundNBT.class.getName() + "!");
			return;
		}

		CompoundNBT compoundTag = (CompoundNBT) rawTag;

		if (!compoundTag.contains("size")) {
			AstromineCommon.LOGGER.log(Level.ERROR, "Inventory contents failed to be read: " + CompoundNBT.class.getName() + " does not contain 'size' value! (" + getClass().getName() + ")");
			return;
		}

		int size = compoundTag.getInt("size");

		if (size == 0) {
			AstromineCommon.LOGGER.log(Level.WARN, "Inventory contents size successfully read, but with size of zero. This may indicate a non-integer 'size' value! (" + getClass().getName() + ")");
		}

		if (!compoundTag.contains("volumes")) {
			AstromineCommon.LOGGER.log(Level.ERROR, "Inventory contents failed to be read: " + CompoundNBT.class.getName() + " does not contain 'volumes' subtag!");
			return;
		}

		INBT rawVolumesTag = compoundTag.get("volumes");

		if (!(rawVolumesTag instanceof CompoundNBT)) {
			AstromineCommon.LOGGER.log(Level.ERROR, "Inventory contents failed to be read: " + rawVolumesTag.getClass().getName() + " is not instance of " + CompoundNBT.class.getName() + "!");
			return;
		}

		CompoundNBT volumesTag = (CompoundNBT) rawVolumesTag;

		int minimum = range.isPresent() ? range.get().getMinimum() : 0;
		int maximum = range.isPresent() ? range.get().getMaximum() : target.getSize();

		if (size < maximum) {
			AstromineCommon.LOGGER.log(Level.WARN, "Inventory size from tag smaller than specified maximum: will continue reading!");
			maximum = size;
		}

		if (target.getSize() < maximum) {
			AstromineCommon.LOGGER.log(Level.WARN, "Inventory size from target smaller than specified maximum: will continue reading!");
			maximum = target.getSize();
		}

		for (int position = minimum; position < maximum; ++position) {
			if (volumesTag.contains(String.valueOf(position))) {
				INBT rawVolumeTag = volumesTag.get(String.valueOf(position));

				if (!(rawVolumeTag instanceof CompoundNBT)) {
					AstromineCommon.LOGGER.log(Level.ERROR, "Inventory volume skipped: stored tag not instance of " + CompoundNBT.class.getName() + "!");
					return;
				}

				CompoundNBT volumeTag = (CompoundNBT) rawVolumeTag;

				FluidStack volume = FluidStack.loadFluidStackFromNBT(volumeTag);

				if (target.getSize() >= position) {
					target.setVolume(position, volume);
				}
			}
		}
	}

	default void addListener(Runnable listener) {
		this.getListeners().add(listener);
	}

	default FluidInventoryComponent withListener(Consumer<FluidInventoryComponent> listener) {
		addListener(() -> listener.accept(this));
		return this;
	}

	default void removeListener(Runnable listener) {
		this.getListeners().remove(listener);
	}

	default void clear() {
		this.getContents().clear();
	}

	default boolean isEmpty() {
		return this.getContents().values().stream().allMatch(FluidStack::isEmpty);
	}

	<T extends FluidInventoryComponent> T copy();
}
