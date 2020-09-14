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

package com.github.chainmailstudios.astromine.common.volume.energy;

import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.common.component.inventory.SimpleEnergyInventoryComponent;
import com.github.chainmailstudios.astromine.common.volume.base.Volume;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class EnergyVolume extends Volume<ResourceLocation, Integer> {
	public static final ResourceLocation ID = AstromineCommon.identifier("energy");

	public EnergyVolume(int amount, int size) {
		super(ID, amount, size);
	}

	public EnergyVolume(int amount, int size, Runnable runnable) {
		super(ID, amount, size, runnable);
	}

	@Override
	public <V extends Volume<ResourceLocation, Integer>> V add(V v, Integer doubleA) {
		if (!(v instanceof EnergyVolume)) return (V) this;

		int amount = Math.min(v.getSize() - v.getAmount(), Math.min(getAmount(), doubleA));

		if (amount > 0) {
			v.setAmount(v.getAmount() + amount);
			setAmount(getAmount() - amount);
		}

		return (V) this;
	}

	@Override
	public <V extends Volume<ResourceLocation, Integer>> V add(Integer aInteger) {
		int amount = Math.min(getSize() - getAmount(), aInteger);

		setAmount(getAmount() + amount);

		return (V) this;
	}

	@Override
	public <V extends Volume<ResourceLocation, Integer>> V moveFrom(V v, Integer doubleA) {
		if (!(v instanceof EnergyVolume)) return (V) this;

		v.add(this, doubleA);

		return (V) this;
	}

	@Override
	public <V extends Volume<ResourceLocation, Integer>> V minus(Integer aInteger) {
		int amount = Math.min(getAmount(), aInteger);

		setAmount(getAmount() - amount);

		return (V) this;
	}

	public static EnergyVolume empty() {
		return new EnergyVolume(0, 0);
	}

	public static EnergyVolume attached(SimpleEnergyInventoryComponent component) {
		return new EnergyVolume(0, 0, component::dispatchConsumers);
	}

	public static EnergyVolume attached(int size, SimpleEnergyInventoryComponent component) {
		return new EnergyVolume(0, size, component::dispatchConsumers);
	}

	public static EnergyVolume of(int amount) {
		return new EnergyVolume(amount, Integer.MAX_VALUE);
	}

	public static EnergyVolume of(int amount, int size) {
		return new EnergyVolume(amount, size);
	}

	@Override
	public CompoundNBT toTag() {
		CompoundNBT tag = new CompoundNBT();
		tag.putInt("amount", getAmount());
		tag.putInt("size", getSize());
		return tag;
	}

	public static EnergyVolume fromTag(CompoundNBT tag) {
		return of(tag.getInt("amount"), tag.getInt("size"));
	}

	@Override
	public <V extends Volume<ResourceLocation, Integer>> V copy() {
		return (V) of(getAmount(), getSize());
	}
}
