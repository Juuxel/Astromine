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

package com.github.chainmailstudios.astromine.technologies.common.block.entity;

import com.github.chainmailstudios.astromine.common.block.entity.base.ComponentEnergyInventoryBlockEntity;
import com.github.chainmailstudios.astromine.common.component.inventory.EnergyInventoryComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.ItemInventoryComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.SimpleEnergyInventoryComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.SimpleItemInventoryComponent;
import com.github.chainmailstudios.astromine.common.utilities.tier.MachineTier;
import com.github.chainmailstudios.astromine.common.volume.energy.InfiniteEnergyVolume;
import com.github.chainmailstudios.astromine.registry.AstromineConfig;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.EnergySizeProvider;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.SpeedProvider;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.TierProvider;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesBlockEntityTypes;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesBlocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHandler;

public abstract class CapacitorBlockEntity extends ComponentEnergyInventoryBlockEntity implements EnergySizeProvider, TierProvider, SpeedProvider {
	public CapacitorBlockEntity(Block energyBlock, BlockEntityType<?> type) {
		super(energyBlock, type);
	}

	@Override
	protected ItemInventoryComponent createItemComponent() {
		return new SimpleItemInventoryComponent(2);
	}

	@Override
	protected EnergyInventoryComponent createEnergyComponent() {
		return new SimpleEnergyInventoryComponent(getEnergySize());
	}

	@Override
	public void tick() {
		super.tick();

		if (world == null) return;
		if (world.isClient) return;

		ItemStack inputStack = itemComponent.getStack(0);
		if (Energy.valid(inputStack)) {
			EnergyHandler energyHandler = Energy.of(inputStack);
			energyHandler.into(Energy.of(this)).move(1024 * getMachineSpeed());
		}

		ItemStack outputStack = itemComponent.getStack(1);
		if (Energy.valid(outputStack)) {
			EnergyHandler energyHandler = Energy.of(outputStack);
			Energy.of(this).into(energyHandler).move(1024 * getMachineSpeed());
		}
	}

	public static class Primitive extends CapacitorBlockEntity {
		public Primitive() {
			super(AstromineTechnologiesBlocks.PRIMITIVE_CAPACITOR, AstromineTechnologiesBlockEntityTypes.PRIMITIVE_CAPACITOR);
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().primitiveCapacitorEnergy;
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().primitiveCapacitorSpeed;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.PRIMITIVE;
		}
	}

	public static class Basic extends CapacitorBlockEntity {
		public Basic() {
			super(AstromineTechnologiesBlocks.BASIC_CAPACITOR, AstromineTechnologiesBlockEntityTypes.BASIC_CAPACITOR);
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().basicCapacitorEnergy;
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().basicCapacitorSpeed;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.BASIC;
		}
	}

	public static class Advanced extends CapacitorBlockEntity {
		public Advanced() {
			super(AstromineTechnologiesBlocks.ADVANCED_CAPACITOR, AstromineTechnologiesBlockEntityTypes.ADVANCED_CAPACITOR);
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().advancedCapacitorEnergy;
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().advancedCapacitorSpeed;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.ADVANCED;
		}
	}

	public static class Elite extends CapacitorBlockEntity {
		public Elite() {
			super(AstromineTechnologiesBlocks.ELITE_CAPACITOR, AstromineTechnologiesBlockEntityTypes.ELITE_CAPACITOR);
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().eliteCapacitorEnergy;
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().eliteCapacitorSpeed;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.ELITE;
		}
	}

	public static class Creative extends CapacitorBlockEntity {
		public Creative() {
			super(AstromineTechnologiesBlocks.CREATIVE_CAPACITOR, AstromineTechnologiesBlockEntityTypes.CREATIVE_CAPACITOR);
		}

		@Override
		protected EnergyInventoryComponent createEnergyComponent() {
			return new SimpleEnergyInventoryComponent(InfiniteEnergyVolume.of());
		}

		@Override
		public double getEnergySize() {
			return Double.MAX_VALUE;
		}

		@Override
		public double getMachineSpeed() {
			return Double.MAX_VALUE;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.CREATIVE;
		}
	}
}
