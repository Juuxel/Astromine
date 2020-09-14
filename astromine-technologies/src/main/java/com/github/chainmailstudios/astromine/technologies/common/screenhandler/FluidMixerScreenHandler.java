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

package com.github.chainmailstudios.astromine.technologies.common.screenhandler;

import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesScreenHandlers;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.FluidMixerBlockEntity;
import com.github.chainmailstudios.astromine.common.component.SidedComponentProvider;
import com.github.chainmailstudios.astromine.common.screenhandler.base.block.ComponentBlockEntityEnergyFluidScreenHandler;
import com.github.chainmailstudios.astromine.common.widget.blade.FluidVerticalBarWidget;
import com.github.chainmailstudios.astromine.common.widget.blade.HorizontalArrowWidget;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;
import com.github.vini2003.blade.common.miscellaneous.Position;
import com.github.vini2003.blade.common.miscellaneous.Size;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class FluidMixerScreenHandler extends ComponentBlockEntityEnergyFluidScreenHandler {
	private FluidMixerBlockEntity mixer;

	public FluidMixerScreenHandler(int syncId, Player player, BlockPos position) {
		super(AstromineTechnologiesScreenHandlers.FLUID_MIXER, syncId, player, position);

		mixer = (FluidMixerBlockEntity) blockEntity;
	}

	@Override
	public void initialize(int width, int height) {
		super.initialize(width, height);

		SidedComponentProvider sidedComponentProvider = blockEntity;

		FluidVerticalBarWidget secondInputFluidBar = new FluidVerticalBarWidget();
		secondInputFluidBar.setPosition(Position.of(fluidBar, fluidBar.getWidth() + 7, 0));
		secondInputFluidBar.setSize(Size.absolute(fluidBar));
		secondInputFluidBar.setVolume(() -> sidedComponentProvider.getSidedComponent(null, AstromineComponentTypes.FLUID_INVENTORY_COMPONENT).getVolume(1));

		HorizontalArrowWidget arrow = new HorizontalArrowWidget();
		arrow.setPosition(Position.of(secondInputFluidBar, secondInputFluidBar.getWidth() + 9, secondInputFluidBar.getHeight() / 2F - 8));
		arrow.setSize(Size.of(22, 16));
		arrow.setLimitSupplier(() -> mixer.limit);
		arrow.setProgressSupplier(() -> (int) mixer.progress);

		FluidVerticalBarWidget outputFluidBar = new FluidVerticalBarWidget();
		outputFluidBar.setPosition(Position.of(secondInputFluidBar, secondInputFluidBar.getWidth() + 9 + arrow.getWidth() + 7, 0));
		outputFluidBar.setSize(Size.absolute(fluidBar));
		outputFluidBar.setVolume(() -> sidedComponentProvider.getSidedComponent(null, AstromineComponentTypes.FLUID_INVENTORY_COMPONENT).getVolume(2));

		mainTab.addWidget(secondInputFluidBar);
		mainTab.addWidget(arrow);
		mainTab.addWidget(outputFluidBar);
	}
}
