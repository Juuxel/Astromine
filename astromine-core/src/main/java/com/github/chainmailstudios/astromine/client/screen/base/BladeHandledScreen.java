package com.github.chainmailstudios.astromine.client.screen.base;

import com.github.vini2003.blade.client.handler.BaseHandledScreen;
import com.github.vini2003.blade.common.handler.BaseScreenHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BladeHandledScreen<T extends BaseScreenHandler> extends BaseHandledScreen<T> {
	public BladeHandledScreen(@NotNull BaseScreenHandler handler, @NotNull Inventory inventory, @NotNull Component title) {
		super(handler, inventory, title);
	}

	@Override
	protected void renderLabels(@Nullable PoseStack matrices, int mouseX, int mouseY) {}
}
