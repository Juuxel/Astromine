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

package com.github.chainmailstudios.astromine.client.rei;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.client.render.sprite.SpriteRenderer;
import com.github.chainmailstudios.astromine.common.volume.fraction.Fraction;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.github.chainmailstudios.astromine.common.utilities.EnergyUtilities;
import com.github.chainmailstudios.astromine.common.utilities.FluidUtilities;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.ClientHelper;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import me.shedaniel.rei.api.widgets.Tooltip;
import me.shedaniel.rei.gui.widget.EntryWidget;
import me.shedaniel.rei.gui.widget.Widget;
import me.shedaniel.rei.impl.RenderingEntry;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class AstromineRoughlyEnoughItemsPlugin implements REIPluginV0 {
	private static final ResourceLocation ENERGY_BACKGROUND = AstromineCommon.identifier("textures/widget/energy_volume_fractional_vertical_bar_background_thin.png");
	private static final ResourceLocation ENERGY_FOREGROUND = AstromineCommon.identifier("textures/widget/energy_volume_fractional_vertical_bar_foreground_thin.png");

	public static me.shedaniel.rei.api.fractions.Fraction convertA2R(Fraction fraction) {
		return me.shedaniel.rei.api.fractions.Fraction.of(fraction.getNumerator(), fraction.getDenominator());
	}

	public static EntryStack convertA2R(FluidVolume volume) {
		return EntryStack.create(volume.getFluid(), convertA2R(volume.getAmount()));
	}

	public static List<Widget> createEnergyDisplay(Rectangle bounds, double energy, boolean generating, long speed) {
		return Collections.singletonList(new EnergyEntryWidget(bounds, speed, generating).entry(new RenderingEntry() {
			@Override
			public void render(MatrixStack matrices, Rectangle bounds, int mouseX, int mouseY, float delta) {}

			@Override
			public @Nullable Tooltip getTooltip(Point mouse) {
				if (generating)
					return Tooltip.create(mouse, new TranslationTextComponent("text.astromine.energy"), ClientHelper.getInstance().getFormattedModFromIdentifier(AstromineCommon.identifier("a")), new StringTextComponent(""), new TranslationTextComponent("category.astromine.generating.energy",
						EnergyUtilities.simpleDisplay(energy)));
				else return Tooltip.create(mouse, new TranslationTextComponent("text.astromine.energy"), ClientHelper.getInstance().getFormattedModFromIdentifier(AstromineCommon.identifier("a")), new StringTextComponent(""), new TranslationTextComponent("category.astromine.consuming.energy",
					EnergyUtilities.simpleDisplay(energy)));
			}
		}).notFavoritesInteractable());
	}

	public static List<Widget> createFluidDisplay(Rectangle bounds, List<EntryStack> fluidStacks, boolean generating, long speed) {
		EntryWidget entry = new FluidEntryWidget(bounds, speed, generating).entries(fluidStacks);
		if (generating)
			entry.markOutput();
		else entry.markInput();
		return Collections.singletonList(entry);
	}

	private static class EnergyEntryWidget extends EntryWidget {
		private final long speed;
		private final boolean generating;

		protected EnergyEntryWidget(Rectangle rectangle, long speed, boolean generating) {
			super(rectangle.x, rectangle.y);
			this.getBounds().setBounds(rectangle);
			this.speed = speed;
			this.generating = generating;
		}

		@Override
		protected void drawBackground(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			if (background) {
				Rectangle bounds = getBounds();
				Minecraft.getInstance().getTextureManager().bind(ENERGY_BACKGROUND);
				AbstractGui.blit(matrices, bounds.x, bounds.y, 0, 0, bounds.width, bounds.height, bounds.width, bounds.height);
				Minecraft.getInstance().getTextureManager().bind(ENERGY_FOREGROUND);
				int height;
				if (generating)
					height = bounds.height - MathHelper.ceil((System.currentTimeMillis() / (speed / bounds.height) % bounds.height) / 1f);
				else height = MathHelper.ceil((System.currentTimeMillis() / (speed / bounds.height) % bounds.height) / 1f);
				AbstractGui.blit(matrices, bounds.x, bounds.y + height, 0, height, bounds.width - 1, bounds.height - height - 1, bounds.width, bounds.height);
			}
		}

		@Override
		protected void drawCurrentEntry(MatrixStack matrices, int mouseX, int mouseY, float delta) {}
	}

	private static class FluidEntryWidget extends EntryWidget {
		private final long speed;
		private final boolean generating;

		protected FluidEntryWidget(Rectangle rectangle, long speed, boolean generating) {
			super(rectangle.x, rectangle.y);
			this.getBounds().setBounds(rectangle);
			this.speed = speed;
			this.generating = generating;
		}

		@Override
		protected void drawBackground(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			if (background) {
				Rectangle bounds = getBounds();
				Minecraft.getInstance().getTextureManager().bind(ENERGY_BACKGROUND);
				AbstractGui.blit(matrices, bounds.x, bounds.y, 0, 0, bounds.width, bounds.height, bounds.width, bounds.height);
			}
		}

		@Override
		protected void drawCurrentEntry(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			EntryStack entry = getCurrentEntry();
			if (entry.getType() == EntryStack.Type.FLUID) {
				Rectangle bounds = getBounds();
				int height;
				if (!generating)
					height = bounds.height - MathHelper.ceil((System.currentTimeMillis() / (speed / bounds.height) % bounds.height) / 1f);
				else height = MathHelper.ceil((System.currentTimeMillis() / (speed / bounds.height) % bounds.height) / 1f);
				IRenderTypeBuffer.Impl consumers = Minecraft.getInstance().renderBuffers().bufferSource();
				SpriteRenderer.beginPass().setup(consumers, RenderType.solid()).sprite(FluidUtilities.texture(entry.getFluid())[0]).color(FluidUtilities.color(Minecraft.getInstance().player, entry.getFluid())).light(0x00f000f0).overlay(OverlayTexture.NO_OVERLAY).alpha(
					0xff).normal(matrices.last().normal(), 0, 0, 0).position(matrices.last().pose(), bounds.x + 1, bounds.y + bounds.height - height + 1, bounds.x + bounds.width - 1, bounds.y + bounds.height - 1, getBlitOffset() + 1).next(AtlasTexture.LOCATION_BLOCKS);
				consumers.endBatch();
			}
		}
	}
}
