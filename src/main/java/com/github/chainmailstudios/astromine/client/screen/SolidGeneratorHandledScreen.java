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

package com.github.chainmailstudios.astromine.client.screen;

import com.github.chainmailstudios.astromine.client.screen.base.DefaultedEnergyItemHandledScreen;
import com.github.chainmailstudios.astromine.common.block.entity.SolidGeneratorBlockEntity;
import com.github.chainmailstudios.astromine.common.screenhandler.SolidGeneratorScreenHandler;
import com.github.chainmailstudios.astromine.common.screenhandler.base.DefaultedEnergyItemScreenHandler;
import com.github.chainmailstudios.astromine.common.widget.HorizontalArrowWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import spinnery.widget.WSlot;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

public class SolidGeneratorHandledScreen extends DefaultedEnergyItemHandledScreen<SolidGeneratorScreenHandler> {
	public SolidGeneratorHandledScreen(Text name, DefaultedEnergyItemScreenHandler linkedScreenHandler, PlayerEntity player) {
		super(name, linkedScreenHandler, player);

		SolidGeneratorBlockEntity generator = (SolidGeneratorBlockEntity) linkedScreenHandler.blockEntity;

		WSlot input = mainPanel.createChild(WSlot::new, Position.of(mainPanel, 7, energyBar.getHeight() - 12, 0), Size.of(18, 18)).setInventoryNumber(1).setSlotNumber(0);

		HorizontalArrowWidget arrow = mainPanel.createChild(HorizontalArrowWidget::new, Position.of(input, 31, 0, 1), Size.of(22, 16)).setLimitSupplier(() -> generator.limit).setProgressSupplier(() -> (int) generator.current);

		energyBar.setPosition(Position.of(arrow, 29, -energyBar.getHeight() / 2 + 8, 5));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
		super.render(matrices, mouseX, mouseY, tickDelta);
	}
}
