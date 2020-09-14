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

package com.github.chainmailstudios.astromine.common.utilities;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import com.github.chainmailstudios.astromine.common.volume.fraction.Fraction;

public class FluidUtilities {
	public static int color(Player player, Fluid fluid) {
		return FluidRenderHandlerRegistry.INSTANCE.get(fluid).getFluidColor(player.getCommandSenderWorld(), BlockPos.ZERO, fluid.defaultFluidState());
	}

	public static TextureAtlasSprite[] texture(Fluid fluid) {
		return FluidRenderHandlerRegistry.INSTANCE.get(fluid).getFluidSprites(null, null, fluid.defaultFluidState());
	}

	public static String shorten(long value) {
		if (value < 1000) {
			return value + "mB";
		}
		int exponent = (int) (Math.log(value) / Math.log(1000));
		String[] units = new String[]{ "B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB" };
		return String.format("%.1f%s", value / Math.pow(1000, exponent), units[exponent - 1]);
	}

	public static MutableComponent rawFraction(Fraction current, Fraction maxValue, Component unit) {
		return new TranslatableComponent("text.astromine.tooltip.fractional_bar", rawFraction(current), rawFraction(maxValue), unit);
	}

	public static MutableComponent rawFraction(double current, double maxValue, Component unit) {
		return new TranslatableComponent("text.astromine.tooltip.fractional_bar", EnergyUtilities.toDecimalString(current), EnergyUtilities.toDecimalString(maxValue), unit);
	}

	public static MutableComponent rawFraction(Fraction fraction) {
		return fraction.getDenominator() != 1 ? new TranslatableComponent("text.astromine.tooltip.fractional_value", fraction.getNumerator(), fraction.getDenominator()) : new TranslatableComponent("text.astromine.tooltip.fractional_value_simple", fraction.getNumerator());
	}
}
