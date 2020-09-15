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
import com.github.chainmailstudios.astromine.common.component.inventory.SimpleEnergyInventoryComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.SimpleItemInventoryComponent;
import com.github.chainmailstudios.astromine.common.inventory.BaseInventory;
import com.github.chainmailstudios.astromine.common.utilities.tier.MachineTier;
import com.github.chainmailstudios.astromine.common.volume.energy.EnergyVolume;
import com.github.chainmailstudios.astromine.common.volume.handler.ItemHandler;
import com.github.chainmailstudios.astromine.registry.AstromineConfig;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.EnergySizeProvider;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.SpeedProvider;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.TierProvider;
import com.github.chainmailstudios.astromine.technologies.common.recipe.AlloySmeltingRecipe;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesBlockEntityTypes;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesBlocks;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

public abstract class AlloySmelterBlockEntity extends ComponentEnergyInventoryBlockEntity implements EnergySizeProvider, TierProvider, SpeedProvider {
	public double progress = 0;
	public int limit = 100;
	public boolean shouldTry = false;

	private Optional<AlloySmeltingRecipe> optionalRecipe = Optional.empty();

	public AlloySmelterBlockEntity(Block energyBlock, TileEntityType<?> type) {
		super(energyBlock, type);
	}

	@Override
	protected IItemHandler createItemComponent() {
		return new SimpleItemInventoryComponent(3).withInsertPredicate((direction, stack, slot) -> {
			return slot == 0 || slot == 1;
		}).withExtractPredicate(((direction, stack, slot) -> {
			return slot == 2;
		})).withListener((inventory) -> {
			shouldTry = true;
			progress = 0;
			limit = 100;
			optionalRecipe = Optional.empty();
		});
	}

	@Override
	protected EnergyInventoryComponent createEnergyComponent() {
		return new SimpleEnergyInventoryComponent(getEnergySize());
	}

	@Override
	public IntSet getItemInputSlots() {
		return new IntArraySet(new int[]{ 0, 1 });
	}

	@Override
	public IntSet getItemOutputSlots() {
		return IntSets.singleton(2);
	}

	@Override
	public void tick() {
		super.tick();

		if (level == null) return;
		if (level.isClientSide) return;

		ItemHandler.ofOptional(this).ifPresent(items -> {
			EnergyVolume volume = getEnergyComponent().getVolume();
			BaseInventory inputInventory = BaseInventory.of(items.getFirst(), items.getSecond());

			if (!optionalRecipe.isPresent() && shouldTry) {
				optionalRecipe = level.getRecipeManager().getRecipeFor(AlloySmeltingRecipe.Type.INSTANCE, inputInventory, level);
			}

			if (optionalRecipe.isPresent()) {
				AlloySmeltingRecipe recipe = optionalRecipe.get();

				if (recipe.matches(inputInventory, level)) {
					limit = recipe.getTime();

					double speed = Math.min(getMachineSpeed(), limit - progress);
					double consumed = recipe.getEnergyConsumed() * speed / limit;

					ItemStack output = recipe.getOutput().copy();

					boolean isEmpty = items.getThird().isEmpty();
					boolean isEqual = ItemStack.isSame(items.getThird(), output) && ItemStack.tagMatches(items.getThird(), output);

					if (volume.hasStored(consumed) && (isEmpty || isEqual) && items.getThird().getCount() + output.getCount() <= items.getThird().getMaxStackSize()) {
						volume.minus(consumed);

						if (progress + speed >= limit) {
							optionalRecipe = Optional.empty();

							ItemStack first = items.getFirst();
							ItemStack second = items.getSecond();

							if (recipe.getFirstInput().test(first) && recipe.getSecondInput().test(second)) {
								first.shrink(recipe.getFirstInput().testMatching(first).getCount());
								second.shrink(recipe.getSecondInput().testMatching(second).getCount());
							} else if (recipe.getFirstInput().test(second) && recipe.getSecondInput().test(first)) {
								second.shrink(recipe.getFirstInput().testMatching(second).getCount());
								first.shrink(recipe.getSecondInput().testMatching(first).getCount());
							}

							if (isEmpty) {
								items.setThird(output);
							} else {
								items.getThird().grow(output.getCount());
								shouldTry = true;
							}

							progress = 0;
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

	public static class Primitive extends AlloySmelterBlockEntity {
		public Primitive() {
			super(AstromineTechnologiesBlocks.PRIMITIVE_ALLOY_SMELTER, AstromineTechnologiesBlockEntityTypes.PRIMITIVE_ALLOY_SMELTER);
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().primitiveAlloySmelterSpeed;
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().primitiveAlloySmelterEnergy;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.PRIMITIVE;
		}
	}

	public static class Basic extends AlloySmelterBlockEntity {
		public Basic() {
			super(AstromineTechnologiesBlocks.BASIC_ALLOY_SMELTER, AstromineTechnologiesBlockEntityTypes.BASIC_ALLOY_SMELTER);
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().basicAlloySmelterSpeed;
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().basicAlloySmelterEnergy;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.BASIC;
		}
	}

	public static class Advanced extends AlloySmelterBlockEntity {
		public Advanced() {
			super(AstromineTechnologiesBlocks.ADVANCED_ALLOY_SMELTER, AstromineTechnologiesBlockEntityTypes.ADVANCED_ALLOY_SMELTER);
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().advancedAlloySmelterSpeed;
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().advancedAlloySmelterEnergy;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.ADVANCED;
		}
	}

	public static class Elite extends AlloySmelterBlockEntity {
		public Elite() {
			super(AstromineTechnologiesBlocks.ELITE_ALLOY_SMELTER, AstromineTechnologiesBlockEntityTypes.ELITE_ALLOY_SMELTER);
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().eliteAlloySmelterSpeed;
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().eliteAlloySmelterEnergy;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.ELITE;
		}
	}
}
