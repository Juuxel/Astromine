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

package com.github.chainmailstudios.astromine.common.widget.blade;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.client.BaseRenderer;
import com.github.chainmailstudios.astromine.client.render.sprite.SpriteRenderer;
import com.github.chainmailstudios.astromine.common.volume.fraction.Fraction;
import com.github.chainmailstudios.astromine.common.utilities.FluidUtilities;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import com.github.vini2003.blade.client.utilities.Layers;
import com.github.vini2003.blade.common.widget.base.AbstractWidget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import java.util.function.Supplier;

public class FluidVerticalBarWidget extends AbstractWidget {
	private final ResourceLocation FLUID_BACKGROUND = AstromineCommon.identifier("textures/widget/fluid_volume_fractional_vertical_bar_background.png");
	private Supplier<FluidVolume> volume;
	private Supplier<Fraction> progressFraction;
	private Supplier<Fraction> limitFraction;

	public ResourceLocation getBackgroundTexture() {
		return FLUID_BACKGROUND;
	}

	public FluidVolume getFluidVolume() {
		return volume.get();
	}

	public void setVolume(Supplier<FluidVolume> volume) {
		this.progressFraction = volume.get()::getAmount;
		this.limitFraction = volume.get()::getSize;

		this.volume = volume;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public List<ITextComponent> getTooltip() {
		return Lists.newArrayList(FluidUtilities.rawFraction(progressFraction.get(), limitFraction.get(), new TranslationTextComponent("text.astromine.fluid")), new TranslationTextComponent("text.astromine.tooltip.fractional_value", progressFraction.get().toDecimalString(), limitFraction.get()
			.toDecimalString()));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawWidget(MatrixStack matrices, IRenderTypeBuffer provider) {
		if (getHidden()) {
			return;
		}

		float x = getPosition().getX();
		float y = getPosition().getY();

		float sX = getSize().getWidth();
		float sY = getSize().getHeight();

		float sBGY = (((sY / limitFraction.get().floatValue()) * progressFraction.get().floatValue()));

		RenderType layer = Layers.get(getBackgroundTexture());

		BaseRenderer.drawTexturedQuad(matrices, provider, layer, x, y, getSize().getWidth(), getSize().getHeight(), getBackgroundTexture());

		if (getFluidVolume().getFluid() != Fluids.EMPTY) {
			SpriteRenderer.beginPass().setup(provider, RenderType.solid()).sprite(FluidUtilities.texture(getFluidVolume().getFluid())[0]).color(FluidUtilities.color(Minecraft.getInstance().player, getFluidVolume().getFluid())).light(0x00f000f0).overlay(
				OverlayTexture.NO_OVERLAY).alpha(0xff).normal(matrices.last().normal(), 0, 0, 0).position(matrices.last().pose(), x + 1, y + 1 + Math.max(0, sY - ((int) (sBGY) + 1)), x + sX - 1, y + sY - 1, 0F).next(AtlasTexture.LOCATION_BLOCKS);
		}
	}
}
