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
import com.github.chainmailstudios.astromine.common.utilities.tier.MachineTier;
import com.github.chainmailstudios.astromine.common.volume.energy.EnergyVolume;
import com.github.chainmailstudios.astromine.common.volume.fraction.Fraction;
import com.github.chainmailstudios.astromine.common.volume.handler.FluidHandler;
import com.github.chainmailstudios.astromine.registry.AstromineConfig;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.EnergySizeProvider;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.FluidSizeProvider;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.SpeedProvider;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.TierProvider;
import com.github.chainmailstudios.astromine.technologies.common.recipe.LiquidGeneratingRecipe;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesBlockEntityTypes;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesBlocks;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

public abstract class LiquidGeneratorBlockEntity extends ComponentEnergyFluidBlockEntity implements EnergySizeProvider, TierProvider, SpeedProvider, FluidSizeProvider {
	public double progress = 0;
	public int limit = 100;
	public boolean shouldTry;

	private Optional<LiquidGeneratingRecipe> optionalRecipe = Optional.empty();

	public LiquidGeneratorBlockEntity(Block energyBlock, TileEntityType<?> type) {
		super(energyBlock, type);
	}

	@Override
	protected EnergyInventoryComponent createEnergyComponent() {
		return new SimpleEnergyInventoryComponent(getEnergySize());
	}

	@Override
	protected IFluidHandler createFluidComponent() {
		FluidInventoryComponent fluidComponent = new SimpleFluidInventoryComponent(1)
				.withInsertPredicate((direction, volume, slot) -> {
					if (slot != 0) {
						return false;
					}

					FluidInventoryComponent inventory = new SimpleFluidInventoryComponent(1);

					inventory.setVolume(0, volume);

					if (level != null) {
						optionalRecipe = level.getRecipeManager().getAllRecipesFor(LiquidGeneratingRecipe.Type.INSTANCE).stream().filter(recipe -> recipe.matches(inventory)).findFirst();
						return optionalRecipe.isPresent();
					}

					return false;
				}).withExtractPredicate((direction, volume, slot) -> {
					return false;
				}).withListener((inventory) -> {
					shouldTry = true;
					progress = 0;
					limit = 100;
					optionalRecipe = Optional.empty();
				});

		FluidHandler.of(fluidComponent).getFirst().setSize(getFluidSize());

		return fluidComponent;
	}

	@Override
	public void tick() {
		super.tick();

		if (level == null) return;
		if (level.isClientSide) return;

		FluidHandler.ofOptional(this).ifPresent(fluids -> {
			EnergyVolume energyVolume = getEnergyComponent().getVolume();
			if (!optionalRecipe.isPresent() && shouldTry) {
				optionalRecipe = level.getRecipeManager().getAllRecipesFor(LiquidGeneratingRecipe.Type.INSTANCE).stream().filter(recipe -> recipe.matches(fluidComponent)).findFirst();
				shouldTry = false;
			}

			if (!optionalRecipe.isPresent()) {
				tickInactive();
			}

			if (optionalRecipe.isPresent()) {
				LiquidGeneratingRecipe recipe = optionalRecipe.get();

				if (recipe.matches(fluidComponent)) {
					limit = recipe.getTime();

					double speed = Math.min(getMachineSpeed(), limit - progress);
					double generated = recipe.getEnergyGenerated() * speed / limit;

					if (energyVolume.hasAvailable(generated)) {
						if (progress + speed >= limit) {
							optionalRecipe = Optional.empty();

							fluids.getFirst().shrink(recipe.getAmount());

							energyVolume.add((int) generated);
						} else {
							progress += speed;
						}

						tickActive();
					} else {
						tickInactive();
					}
				} else {
					tickInactive();
				}
			} else {
				tickInactive();
			}
		});
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		tag.putDouble("progress", progress);
		tag.putInt("limit", limit);
		return super.save(tag);
	}

	@Override
	public void load(BlockState state, @NotNull CompoundNBT tag) {
		progress = tag.getDouble("progress");
		limit = tag.getInt("limit");
		super.load(state, tag);
	}

	public static class Primitive extends LiquidGeneratorBlockEntity {
		public Primitive() {
			super(AstromineTechnologiesBlocks.PRIMITIVE_LIQUID_GENERATOR, AstromineTechnologiesBlockEntityTypes.PRIMITIVE_LIQUID_GENERATOR);
		}

		@Override
		public int getFluidSize() {
			return AstromineConfig.get().primitiveLiquidGeneratorFluid;
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().primitiveLiquidGeneratorSpeed;
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().primitiveLiquidGeneratorEnergy;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.PRIMITIVE;
		}
	}

	public static class Basic extends LiquidGeneratorBlockEntity {
		public Basic() {
			super(AstromineTechnologiesBlocks.BASIC_LIQUID_GENERATOR, AstromineTechnologiesBlockEntityTypes.BASIC_LIQUID_GENERATOR);
		}

		@Override
		public int getFluidSize() {
			return AstromineConfig.get().basicLiquidGeneratorFluid;
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().basicLiquidGeneratorSpeed;
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().basicLiquidGeneratorEnergy;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.BASIC;
		}
	}

	public static class Advanced extends LiquidGeneratorBlockEntity {
		public Advanced() {
			super(AstromineTechnologiesBlocks.ADVANCED_LIQUID_GENERATOR, AstromineTechnologiesBlockEntityTypes.ADVANCED_LIQUID_GENERATOR);
		}

		@Override
		public int getFluidSize() {
			return AstromineConfig.get().advancedLiquidGeneratorFluid;
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().advancedLiquidGeneratorSpeed;
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().advancedLiquidGeneratorEnergy;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.ADVANCED;
		}
	}

	public static class Elite extends LiquidGeneratorBlockEntity {
		public Elite() {
			super(AstromineTechnologiesBlocks.ELITE_LIQUID_GENERATOR, AstromineTechnologiesBlockEntityTypes.ELITE_LIQUID_GENERATOR);
		}

		@Override
		public int getFluidSize() {
			return AstromineConfig.get().eliteLiquidGeneratorFluid;
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().eliteLiquidGeneratorSpeed;
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().eliteLiquidGeneratorEnergy;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.ELITE;
		}
	}
}
