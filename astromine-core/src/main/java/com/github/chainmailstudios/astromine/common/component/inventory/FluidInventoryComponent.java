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
import com.github.chainmailstudios.astromine.common.volume.fraction.Fraction;
import com.github.chainmailstudios.astromine.common.utilities.data.Range;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;
import com.github.chainmailstudios.astromine.registry.AstromineItems;
import nerdhub.cardinal.components.api.ComponentType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface FluidInventoryComponent extends NameableComponent {
	Map<Integer, FluidVolume> getContents();

	default Item getSymbol() {
		return AstromineItems.FLUID.asItem();
	}

	default TranslationTextComponent getName() {
		return new TranslationTextComponent("text.astromine.fluid");
	}

	default Collection<FluidVolume> getContentsMatching(Predicate<FluidVolume> predicate) {
		return this.getContents().values().stream().filter(predicate).collect(Collectors.toList());
	}

	default Collection<FluidVolume> getExtractableContentsMatching(Direction direction, Predicate<FluidVolume> predicate) {
		return this.getContents().entrySet().stream().filter((entry) -> canExtract(direction, entry.getValue(), entry.getKey()) && predicate.test(entry.getValue())).map(Map.Entry::getValue).collect(Collectors.toList());
	}

	default Collection<FluidVolume> getContentsMatchingSimulated(Predicate<FluidVolume> predicate) {
		return this.getContentsSimulated().stream().filter(predicate).collect(Collectors.toList());
	}

	default Collection<FluidVolume> getContentsSimulated() {
		return this.getContents().values().stream().map((volume) -> (FluidVolume) volume.copy()).collect(Collectors.toList());
	}

	default boolean canInsert() {
		return true;
	}

	default boolean canInsert(@Nullable Direction direction, FluidVolume volume, int slot) {
		return true;
	}

	default boolean canExtract() {
		return true;
	}

	default boolean canExtract(@Nullable Direction direction, FluidVolume volume, int slot) {
		return true;
	}

	default ActionResult<FluidVolume> insert(Direction direction, FluidVolume volume) {
		if (this.canInsert()) {
			return this.insert(direction, volume.getFluid(), volume.getAmount());
		} else {
			return new ActionResult<>(ActionResultType.FAIL, volume);
		}
	}

	default ActionResult<FluidVolume> insert(Direction direction, Fluid fluid, Fraction fraction) {
		Optional<Map.Entry<Integer, FluidVolume>> matchingVolumeOptional = this.getContents().entrySet().stream().filter(entry -> {
			return canInsert(direction, entry.getValue(), entry.getKey()) && entry.getValue().getFluid() == fluid;
		}).findFirst();

		if (matchingVolumeOptional.isPresent()) {
			matchingVolumeOptional.get().getValue().add(fraction);
			return new ActionResult<>(ActionResultType.SUCCESS, matchingVolumeOptional.get().getValue());
		} else {
			return new ActionResult<>(ActionResultType.FAIL, null);
		}
	}

	default void setVolume(int slot, FluidVolume volume) {
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

	default ActionResult<Collection<FluidVolume>> extractMatching(Direction direction, Predicate<FluidVolume> predicate) {
		HashSet<FluidVolume> extractedVolumes = new HashSet<>();
		this.getContents().forEach((slot, volume) -> {
			if (canExtract(direction, volume, slot) && predicate.test(volume)) {
				ActionResult<FluidVolume> extractionResult = this.extract(direction, slot);

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

	default ActionResult<FluidVolume> extract(Direction direction, int slot) {
		FluidVolume volume = this.getVolume(slot);

		if (!volume.isEmpty() && this.canExtract(direction, volume, slot)) {
			return this.extract(direction, slot, volume.getAmount());
		} else {
			return new ActionResult<>(ActionResultType.FAIL, FluidVolume.empty());
		}
	}

	@Nullable
	default FluidVolume getVolume(int slot) {
		return this.getContents().getOrDefault(slot, null);
	}

	@Nullable
	default FluidVolume getFirstExtractableVolume(Direction direction) {
		return getContents().entrySet().stream().filter((entry) -> canExtract(direction, entry.getValue(), entry.getKey()) && !entry.getValue().isEmpty()).map(Map.Entry::getValue).findFirst().orElse(null);
	}

	@Nullable
	default FluidVolume getFirstInsertableVolume(FluidVolume volume, Direction direction) {
		return getContents().entrySet().stream().filter((entry) -> canInsert(direction, entry.getValue(), entry.getKey()) && (entry.getValue().isEmpty() || (entry.getValue().getFluid() == volume.getFluid() && entry.getValue().hasAvailable(volume.getAmount())))).map(
			Map.Entry::getValue).findFirst().orElse(null);
	}

	@Nullable
	default FluidVolume getFirstInsertableVolume(Fluid fluid, Direction direction) {
		return getContents().entrySet().stream().filter((entry) -> canInsert(direction, entry.getValue(), entry.getKey()) && (entry.getValue().isEmpty() || (entry.getValue().getFluid() == fluid))).map(Map.Entry::getValue).findFirst().orElse(null);
	}

	default ActionResult<FluidVolume> extract(Direction direction, int slot, Fraction fraction) {
		Optional<FluidVolume> matchingVolumeOptional = Optional.ofNullable(this.getVolume(slot));

		if (matchingVolumeOptional.isPresent()) {
			FluidVolume volume = matchingVolumeOptional.get();

			if (canExtract(direction, volume, slot)) {
				return new ActionResult<>(ActionResultType.SUCCESS, matchingVolumeOptional.get().minus(fraction));
			} else {
				return new ActionResult<>(ActionResultType.FAIL, FluidVolume.empty());
			}
		} else {
			return new ActionResult<>(ActionResultType.FAIL, FluidVolume.empty());
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
				FluidVolume volume = source.getVolume(position);

				if (volume != null) {
					CompoundNBT volumeTag = source.getVolume(position).toTag();

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

				FluidVolume volume = FluidVolume.fromTag(volumeTag);

				if (target.getSize() >= position) {
					if (target.getVolume(position) != null) {
						target.getVolume(position).setAmount(volume.getAmount());
						target.getVolume(position).setFluid(volume.getFluid());
					} else {
						target.setVolume(position, volume);
					}
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

	default Fraction getMaximumFradction(Fraction slot) {
		return new Fraction(16, 1);
	}

	default void clear() {
		this.getContents().clear();
	}

	default boolean isEmpty() {
		return this.getContents().values().stream().allMatch(FluidVolume::isEmpty);
	}

	<T extends FluidInventoryComponent> T copy();

	@Override
	default @NotNull ComponentType<?> getComponentType() {
		return AstromineComponentTypes.FLUID_INVENTORY_COMPONENT;
	}
}
