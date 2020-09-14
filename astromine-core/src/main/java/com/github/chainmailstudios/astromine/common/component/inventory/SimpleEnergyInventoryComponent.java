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
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundNBT;

public class SimpleEnergyInventoryComponent implements EnergyInventoryComponent {
	private final EnergyVolume content;

	private final List<Runnable> listeners = new ArrayList<>();

	public SimpleEnergyInventoryComponent() {
		this.content = EnergyVolume.attached(this);
	}

	public SimpleEnergyInventoryComponent(double size) {
		this.content = EnergyVolume.attached(size, this);
	}

	public SimpleEnergyInventoryComponent(EnergyVolume volume) {
		this.content = volume;
		this.content.setRunnable(this::dispatchConsumers);
	}

	@Override
	public EnergyVolume getVolume() {
		return content;
	}

	@Override
	public List<Runnable> getListeners() {
		return listeners;
	}

	@Override
	public void fromTag(CompoundNBT compoundTag) {
		read(compoundTag);
	}

	@Override
	public CompoundNBT toTag(CompoundNBT compoundTag) {
		write(compoundTag);
		return compoundTag;
	}

	@Override
	public SimpleEnergyInventoryComponent copy() {
		SimpleEnergyInventoryComponent component = new SimpleEnergyInventoryComponent();
		component.fromTag(toTag(new CompoundNBT()));
		return component;
	}
}
