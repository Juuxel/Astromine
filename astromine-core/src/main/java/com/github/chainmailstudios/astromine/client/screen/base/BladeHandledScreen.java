package com.github.chainmailstudios.astromine.client.screen.base;

import com.github.vini2003.blade.client.handler.BaseHandledScreen;
import com.github.vini2003.blade.common.handler.BaseScreenHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BladeHandledScreen<T extends BaseScreenHandler> extends BaseHandledScreen<T> {
	public BladeHandledScreen(@NotNull BaseScreenHandler handler, @NotNull PlayerInventory inventory, @NotNull ITextComponent title) {
		super(handler, inventory, title);
	}

	@Override
	protected void renderLabels(@Nullable MatrixStack matrices, int mouseX, int mouseY) {}
}
