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

import com.github.chainmailstudios.astromine.common.block.entity.base.ComponentEnergyFluidBlockEntity;
import com.github.chainmailstudios.astromine.common.component.inventory.EnergyInventoryComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.FluidInventoryComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.SimpleEnergyInventoryComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.SimpleFluidInventoryComponent;
import com.github.chainmailstudios.astromine.common.volume.energy.EnergyVolume;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import com.github.chainmailstudios.astromine.common.volume.fraction.Fraction;
import com.github.chainmailstudios.astromine.common.volume.handler.FluidHandler;
import com.github.chainmailstudios.astromine.registry.AstromineConfig;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.EnergyConsumedProvider;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.EnergySizeProvider;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.SpeedProvider;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesBlockEntityTypes;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class FluidExtractorBlockEntity extends ComponentEnergyFluidBlockEntity implements EnergySizeProvider, SpeedProvider, EnergyConsumedProvider {
	private Fraction cooldown = Fraction.empty();

	public FluidExtractorBlockEntity() {
		super(AstromineTechnologiesBlocks.FLUID_EXTRACTOR, AstromineTechnologiesBlockEntityTypes.FLUID_EXTRACTOR);
	}

	@Override
	protected FluidInventoryComponent createFluidComponent() {
		FluidInventoryComponent fluidComponent = new SimpleFluidInventoryComponent(1);
		FluidHandler.of(fluidComponent).getFirst().setSize(Fraction.of(8));
		return fluidComponent;
	}

	@Override
	protected EnergyInventoryComponent createEnergyComponent() {
		return new SimpleEnergyInventoryComponent(getEnergySize());
	}

	@Override
	public int getEnergyConsumed() {
		return AstromineConfig.get().fluidExtractorEnergyConsumed;
	}

	@Override
	public double getEnergySize() {
		return AstromineConfig.get().fluidExtractorEnergy;
	}

	@Override
	public double getMachineSpeed() {
		return AstromineConfig.get().fluidExtractorSpeed;
	}

	@Override
	public void tick() {
		super.tick();

		if (level == null) return;
		if (level.isClientSide) return;

		FluidHandler.ofOptional(this).ifPresent(fluids -> {
			EnergyVolume energyVolume = getEnergyComponent().getVolume();
			if (energyVolume.getAmount() < getEnergyConsumed()) {
				cooldown = Fraction.empty();

				tickInactive();
			} else {
				tickActive();

				cooldown = cooldown.add(Fraction.ofDecimal(1.0D / getMachineSpeed()));

				cooldown.ifBiggerOrEqualThan(Fraction.of(1), () -> {
					cooldown = Fraction.empty();

					FluidVolume fluidVolume = fluids.getFirst();

					Direction direction = getBlockState().getValue(HorizontalBlock.FACING);

					BlockPos targetPos = worldPosition.relative(direction);

					FluidState targetFluidState = level.getFluidState(targetPos);

					if (targetFluidState.isSource()) {
						FluidVolume toInsert = FluidVolume.of(Fraction.bucket(), targetFluidState.getType());

						if ((fluidVolume.canAccept(toInsert.getFluid())) && fluidVolume.hasAvailable(toInsert.getAmount())) {
							fluidVolume.moveFrom(toInsert, toInsert.getAmount());

							energyVolume.minus(getEnergyConsumed());

							level.setBlockAndUpdate(targetPos, Blocks.AIR.defaultBlockState());
							level.playSound(null, worldPosition, SoundEvents.BUCKET_FILL, SoundCategory.BLOCKS, 1, 1);
						}
					}
				});
			}
		});
	}
}
