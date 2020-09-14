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

package com.github.chainmailstudios.astromine.transportations.client.render.block;

import com.github.chainmailstudios.astromine.transportations.common.block.entity.VerticalConveyorBlockEntity;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.Conveyor;
import com.github.chainmailstudios.astromine.transportations.common.conveyor.ConveyorTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemStack;

public class VerticalConveyorBlockEntityRenderer extends BlockEntityRenderer<VerticalConveyorBlockEntity> implements ConveyorRenderer<VerticalConveyorBlockEntity> {
	public VerticalConveyorBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	@Override
	public void render(VerticalConveyorBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, int i1) {
		int speed = ((Conveyor) blockEntity.getBlockState().getBlock()).getSpeed();
		ConveyorTypes type = ((Conveyor) blockEntity.getBlockState().getBlock()).getType();

		if (!blockEntity.getLevel().getBlockState(blockEntity.getBlockPos()).isAir() && !blockEntity.isEmpty()) {
			ItemStack stack = blockEntity.getStack();
			float position = blockEntity.getRenderAttachmentData()[1] + (blockEntity.getRenderAttachmentData()[0] - blockEntity.getRenderAttachmentData()[1]) * partialTicks;
			float horizontalPosition = blockEntity.getRenderAttachmentData()[3] + (blockEntity.getRenderAttachmentData()[2] - blockEntity.getRenderAttachmentData()[3]) * partialTicks;

			renderSupport(blockEntity, type, position, speed, horizontalPosition, matrixStack, vertexConsumerProvider);

			renderItem(blockEntity, stack, position, speed, horizontalPosition, type, matrixStack, vertexConsumerProvider);
		}
	}
}
