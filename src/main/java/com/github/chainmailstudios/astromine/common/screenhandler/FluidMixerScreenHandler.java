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

package com.github.chainmailstudios.astromine.common.screenhandler;

import com.github.chainmailstudios.astromine.common.block.entity.FluidMixerBlockEntity;
import com.github.chainmailstudios.astromine.common.component.ComponentProvider;
import com.github.chainmailstudios.astromine.common.screenhandler.base.DefaultedEnergyFluidScreenHandler;
import com.github.chainmailstudios.astromine.common.widget.FluidVerticalBarWidget;
import com.github.chainmailstudios.astromine.common.widget.HorizontalArrowWidget;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;
import com.github.chainmailstudios.astromine.registry.AstromineScreenHandlers;
import com.github.vini2003.blade.common.data.Position;
import com.github.vini2003.blade.common.data.Size;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class FluidMixerScreenHandler extends DefaultedEnergyFluidScreenHandler {
	private FluidMixerBlockEntity mixer;

	public FluidMixerScreenHandler(int syncId, PlayerEntity player, BlockPos position) {
		super(AstromineScreenHandlers.FLUID_MIXER, syncId, player, position);

		mixer = (FluidMixerBlockEntity) blockEntity;
	}

	@Override
	public void initialize(int width, int height) {
		super.initialize(width, height);

		ComponentProvider componentProvider = blockEntity;

		FluidVerticalBarWidget secondInputFluidBar = new FluidVerticalBarWidget();
		secondInputFluidBar.setPosition(new Position(fluidBar.getX() + fluidBar.getWidth() + 7, fluidBar.getY()));
		secondInputFluidBar.setSize(new Size(fluidBar.getWidth(), fluidBar.getHeight()));
		secondInputFluidBar.setVolume(() -> componentProvider.getSidedComponent(null, AstromineComponentTypes.FLUID_INVENTORY_COMPONENT).getVolume(1));

		HorizontalArrowWidget arrow = new HorizontalArrowWidget();
		arrow.setPosition(new Position(secondInputFluidBar.getX() + secondInputFluidBar.getWidth() + 9, secondInputFluidBar.getY() + secondInputFluidBar.getHeight() / 2F - 8));
		arrow.setSize(new Size(22, 16));
		arrow.setLimitSupplier(() -> mixer.limit);
		arrow.setProgressSupplier(() -> (int) mixer.current);

		FluidVerticalBarWidget outputFluidBar = new FluidVerticalBarWidget();
		outputFluidBar.setPosition(new Position(arrow.getX() + arrow.getWidth() + 7, arrow.getY() - secondInputFluidBar.getHeight() / 2F + 8));
		outputFluidBar.setSize(new Size(fluidBar.getWidth(), fluidBar.getHeight()));
		outputFluidBar.setVolume(() -> componentProvider.getSidedComponent(null, AstromineComponentTypes.FLUID_INVENTORY_COMPONENT).getVolume(2));

		mainTab.addWidget(secondInputFluidBar);
		mainTab.addWidget(arrow);
		mainTab.addWidget(outputFluidBar);
	}
}
