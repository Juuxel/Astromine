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

import com.github.chainmailstudios.astromine.common.volume.energy.EnergyVolume;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;
import com.github.chainmailstudios.astromine.registry.AstromineItems;
import nerdhub.cardinal.components.api.ComponentType;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface EnergyInventoryComponent extends NameableComponent {
	EnergyVolume getVolume();

	default Item getSymbol() {
		return AstromineItems.ENERGY.asItem();
	}

	default TranslationTextComponent getName() {
		return new TranslationTextComponent("text.astromine.energy");
	}

	default EnergyVolume getSimulated() {
		return getVolume().copy();
	}

	default boolean canInsert(@Nullable Direction direction) {
		return true;
	}

	default boolean canInsert(@Nullable Direction direction, double volume) {
		return true;
	}

	default boolean canExtract(@Nullable Direction direction) {
		return true;
	}

	default boolean canExtract(@Nullable Direction direction, double volume) {
		return true;
	}

	default ActionResult<EnergyVolume> insert(Direction direction, EnergyVolume volume) {
		if (this.canInsert(direction)) {
			return this.insert(direction, volume);
		} else {
			return new ActionResult<>(ActionResultType.FAIL, volume);
		}
	}

	default ActionResult<EnergyVolume> insert(Direction direction, double amount) {
		EnergyVolume volume = getVolume();
		if (canInsert(direction, amount)) {
			volume.add(amount);
			return new ActionResult<>(ActionResultType.SUCCESS, volume);
		}
		return new ActionResult<>(ActionResultType.FAIL, null);
	}

	default void setVolume(EnergyVolume volume) {
		getVolume().setSize(volume.getSize());
		getVolume().setAmount(volume.getAmount());
	}

	default void dispatchConsumers() {
		this.getListeners().forEach(Runnable::run);
	}

	List<Runnable> getListeners();

	default ActionResult<Collection<EnergyVolume>> extractMatching(Direction direction, Predicate<EnergyVolume> predicate) {
		HashSet<EnergyVolume> extractedVolumes = new HashSet<>();
		EnergyVolume volume = getVolume();
		if (canExtract(direction) && predicate.test(volume)) {
			ActionResult<EnergyVolume> extractionResult = this.extract(direction);

			if (extractionResult.getResult().consumesAction()) {
				extractedVolumes.add(extractionResult.getObject());
			}
		}

		if (!extractedVolumes.isEmpty()) {
			return new ActionResult<>(ActionResultType.SUCCESS, extractedVolumes);
		} else {
			return new ActionResult<>(ActionResultType.FAIL, extractedVolumes);
		}
	}

	default ActionResult<EnergyVolume> extract(Direction direction) {
		EnergyVolume volume = this.getVolume();

		if (!volume.isEmpty() && this.canExtract(direction)) {
			return this.extract(direction, volume.getAmount());
		} else {
			return new ActionResult<>(ActionResultType.FAIL, EnergyVolume.empty());
		}
	}

	@Nullable
	default EnergyVolume getFirstExtractableVolume(Direction direction) {
		EnergyVolume volume = getVolume();
		if (canExtract(direction) && !volume.isEmpty()) return volume;
		return null;
	}

	@Nullable
	default EnergyVolume getFirstInsertableVolume(double amount, Direction direction) {
		EnergyVolume volume = getVolume();
		if (canInsert(direction) && volume.hasAvailable(amount)) return volume;
		return null;
	}

	@Nullable
	default EnergyVolume getFirstInsertableVolume(Direction direction) {
		EnergyVolume volume = getVolume();
		if (canInsert(direction) && !volume.isFull()) return volume;
		return null;
	}

	default ActionResult<EnergyVolume> extract(Direction direction, double amount) {
		EnergyVolume volume = this.getVolume();

		if (canExtract(direction, amount)) {
			return new ActionResult<>(ActionResultType.SUCCESS, volume.minus(amount));
		} else {
			return new ActionResult<>(ActionResultType.FAIL, EnergyVolume.empty());
		}
	}

	default CompoundNBT write() {
		CompoundNBT tag = new CompoundNBT();
		this.write(tag);
		return tag;
	}

	default void write(CompoundNBT tag) {
		tag.putDouble("energy", getVolume().getAmount());
	}

	default void read(CompoundNBT tag) {
		clear();
		EnergyVolume volume = getVolume();
		if (tag.contains("energy", NbtType.COMPOUND)) {
			EnergyVolume energy = EnergyVolume.fromTag(tag.getCompound("energy"));
			volume.setAmount(energy.getAmount());
		} else if (tag.contains("energy", NbtType.DOUBLE)) {
			double energy = tag.getDouble("energy");
			volume.setAmount(energy);
		}
	}

	default void addListener(Runnable listener) {
		this.getListeners().add(listener);
	}

	default EnergyInventoryComponent withListener(Consumer<EnergyInventoryComponent> listener) {
		addListener(() -> listener.accept(this));
		return this;
	}

	default void removeListener(Runnable listener) {
		this.getListeners().remove(listener);
	}

	default void clear() {
		this.getVolume().setAmount(0.0);
	}

	default boolean isEmpty() {
		return this.getVolume().isEmpty();
	}

	<T extends EnergyInventoryComponent> T copy();

	@Override
	default @NotNull ComponentType<?> getComponentType() {
		return AstromineComponentTypes.ENERGY_INVENTORY_COMPONENT;
	}

	default void setAmount(double amount) {
		getVolume().setAmount(amount);
	}
	
	default double getAmount() {
		return getVolume().getAmount();
	}

	default void setSize(double amount) {
		getVolume().setSize(amount);
	}

	default double getSize() {
		return getVolume().getSize();
	}
}
