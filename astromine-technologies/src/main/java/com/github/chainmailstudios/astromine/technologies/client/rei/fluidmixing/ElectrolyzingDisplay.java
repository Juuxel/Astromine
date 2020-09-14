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

package com.github.chainmailstudios.astromine.technologies.client.rei.fluidmixing;

import com.github.chainmailstudios.astromine.client.rei.AstromineRoughlyEnoughItemsPlugin;
import com.github.chainmailstudios.astromine.technologies.client.rei.AstromineTechnologiesRoughlyEnoughItemsPlugin;
import com.github.chainmailstudios.astromine.technologies.common.recipe.ElectrolyzingRecipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ElectrolyzingDisplay implements RecipeDisplay {
	private final double energy;
	private final FluidVolume input;
	private final FluidVolume firstOutput;
	private final FluidVolume secondOutput;
	private final ResourceLocation id;

	public ElectrolyzingDisplay(double energy, FluidVolume input, FluidVolume firstOutput, FluidVolume secondOutput, ResourceLocation id) {
		this.energy = energy;
		this.input = input;
		this.firstOutput = firstOutput;
		this.secondOutput = secondOutput;
		this.id = id;
	}

	public ElectrolyzingDisplay(ElectrolyzingRecipe recipe) {
		this(recipe.getEnergyConsumed(), FluidVolume.of(recipe.getInputAmount(), recipe.getInputFluid()), FluidVolume.of(recipe.getFirstOutputAmount(), recipe.getFirstOutputFluid()), FluidVolume.of(recipe.getSecondOutputAmount(), recipe.getSecondOutputFluid()), recipe.getId());
	}

	@Override
	public Optional<ResourceLocation> getRecipeLocation() {
		return Optional.ofNullable(id);
	}

	@Override
	public List<List<EntryStack>> getInputEntries() {
		return Collections.singletonList(Collections.singletonList(AstromineRoughlyEnoughItemsPlugin.convertA2R(input)));
	}

	@Override
	public List<List<EntryStack>> getRequiredEntries() {
		return getInputEntries();
	}

	@Override
	public ResourceLocation getRecipeCategory() {
		return AstromineTechnologiesRoughlyEnoughItemsPlugin.ELECTROLYZING;
	}

	@Override
	public List<List<EntryStack>> getResultingEntries() {
		return Arrays.asList(Collections.singletonList(AstromineRoughlyEnoughItemsPlugin.convertA2R(firstOutput)), Collections.singletonList(AstromineRoughlyEnoughItemsPlugin.convertA2R(secondOutput)));
	}

	public double getEnergy() {
		return energy;
	}
}
