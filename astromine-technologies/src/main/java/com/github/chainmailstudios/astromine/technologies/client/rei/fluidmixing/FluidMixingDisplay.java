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

import com.github.chainmailstudios.astromine.technologies.client.rei.AstromineTechnologiesRoughlyEnoughItemsPlugin;
import com.github.chainmailstudios.astromine.technologies.common.recipe.FluidMixingRecipe;
import net.minecraft.util.ResourceLocation;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;

public class FluidMixingDisplay extends AbstractFluidMixingDisplay {
	public FluidMixingDisplay(FluidMixingRecipe recipe) {
		super(recipe.getEnergyConsumed(), FluidVolume.of(recipe.getFirstInputAmount(), recipe.getFirstInputFluid()), FluidVolume.of(recipe.getSecondInputAmount(), recipe.getSecondInputFluid()), FluidVolume.of(recipe.getOutputAmount(), recipe.getOutputFluid()), recipe.getId());
	}

	@Override
	public ResourceLocation getRecipeCategory() {
		return AstromineTechnologiesRoughlyEnoughItemsPlugin.FLUID_MIXING;
	}
}
