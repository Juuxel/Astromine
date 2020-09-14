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

package com.github.chainmailstudios.astromine.technologies.client.rei.generating;

import com.github.chainmailstudios.astromine.technologies.client.rei.AstromineTechnologiesRoughlyEnoughItemsPlugin;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.util.ResourceLocation;
import me.shedaniel.rei.api.EntryStack;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class SolidGeneratingDisplay extends AbstractEnergyGeneratingDisplay {
	private final List<EntryStack> stacks;
	private final ResourceLocation id;
	private final Double time;

	public SolidGeneratingDisplay(double energyGenerated, List<EntryStack> stacks, ResourceLocation id, Double time) {
		super(energyGenerated);
		this.stacks = stacks;
		this.id = id;
		this.time = time;
	}

	@Override
	public List<List<EntryStack>> getInputEntries() {
		return Collections.singletonList(stacks);
	}

	@Override
	public List<List<EntryStack>> getRequiredEntries() {
		return getInputEntries();
	}

	@Override
	public ResourceLocation getRecipeCategory() {
		return AstromineTechnologiesRoughlyEnoughItemsPlugin.SOLID_GENERATING;
	}

	@Override
	public Optional<ResourceLocation> getRecipeLocation() {
		return Optional.ofNullable(this.id);
	}

	public Double getTime() {
		return time;
	}
}
