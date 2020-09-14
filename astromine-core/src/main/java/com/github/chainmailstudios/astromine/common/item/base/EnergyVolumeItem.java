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

package com.github.chainmailstudios.astromine.common.item.base;

import com.github.chainmailstudios.astromine.common.volume.energy.EnergyVolume;
import me.shedaniel.cloth.api.durability.bar.DurabilityBarItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHolder;
import team.reborn.energy.EnergyTier;

public class EnergyVolumeItem extends BaseVolumeItem<EnergyVolume> implements EnergyHolder, DurabilityBarItem {
	private final double size;

	public EnergyVolumeItem(Item.Properties settings, double size) {
		super(settings);

		this.size = size;
	}

	public double getSize() {
		return size;
	}

	public static EnergyVolumeItem ofCreative(Item.Properties settings) {
		return new EnergyVolumeItem(settings, Double.MAX_VALUE);
	}

	public static EnergyVolumeItem of(Properties settings, double size) {
		return new EnergyVolumeItem(settings, size);
	}

	@Override
	public double getMaxStoredPower() {
		return getSize();
	}

	@Override
	public EnergyTier getTier() {
		return EnergyTier.INSANE;
	}

	@Override
	public double getDurabilityBarProgress(ItemStack stack) {
		if (!Energy.valid(stack) || getMaxStoredPower() == 0)
			return 0;
		return 1 - Energy.of(stack).getEnergy() / getMaxStoredPower();
	}

	@Override
	public boolean hasDurabilityBar(ItemStack stack) {
		return true;
	}

	@Override
	public int getDurabilityBarColor(ItemStack stack) {
		return 0x91261f;
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> stacks) {
		super.fillItemCategory(group, stacks);
		if (this.allowdedIn(group)) {
			ItemStack stack = new ItemStack(this);
			Energy.of(stack).set(getMaxStoredPower());
			stacks.add(stack);
		}
	}
}
