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

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import com.github.chainmailstudios.astromine.client.BaseRenderer;
import com.github.chainmailstudios.astromine.common.block.entity.base.ComponentBlockEntity;
import com.github.chainmailstudios.astromine.common.component.block.entity.BlockEntityTransferComponent;
import com.github.chainmailstudios.astromine.common.utilities.MirrorUtilities;
import com.github.chainmailstudios.astromine.registry.AstromineCommonPackets;
import com.github.vini2003.blade.common.widget.base.AbstractWidget;
import com.mojang.blaze3d.matrix.MatrixStack;
import io.netty.buffer.Unpooled;
import nerdhub.cardinal.components.api.ComponentType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TransferTypeSelectorButtonWidget extends AbstractWidget {
	private BlockEntityTransferComponent component;

	private ComponentType<?> type;

	private Direction direction;

	private Direction rotation;

	private BlockPos blockPos;

	private boolean wasClicked = false;

	private String getSideName() {
		return component.get(type).get(direction).name().toLowerCase(Locale.ROOT);
	}

	private ResourceLocation getTexture() {
		return component.get(type).get(direction).texture();
	}

	public BlockEntityTransferComponent getComponent() {
		return component;
	}

	public void setComponent(BlockEntityTransferComponent component) {
		this.component = component;

	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;

	}

	public Direction getRotation() {
		return rotation;
	}

	public void setRotation(Direction rotation) {
		this.rotation = rotation;

	}

	public ComponentType<?> getType() {
		return type;
	}

	public void setType(ComponentType<?> type) {
		this.type = type;

	}

	public BlockPos getBlockPos() {
		return blockPos;
	}

	public void setBlockPos(BlockPos blockPos) {
		this.blockPos = blockPos;

	}

	@Override
	public void onMouseClicked(float mouseX, float mouseY, int mouseButton) {
		super.onMouseClicked(mouseX, mouseY, mouseButton);
		if (getFocused()) {
			wasClicked = true;
		}
	}

	@Override
	public void onMouseReleased(float mouseX, float mouseY, int mouseButton) {
		if (getFocused() && wasClicked) {
			if (getHandler().getClient()) {
				PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());

				buffer.writeBlockPos(getBlockPos());
				buffer.writeResourceLocation(ComponentBlockEntity.TRANSFER_UPDATE_PACKET);

				buffer.writeResourceLocation(type.getId());
				buffer.writeEnum(direction);
				buffer.writeEnum(component.get(type).get(direction).next());

				ClientSidePacketRegistry.INSTANCE.sendToServer(AstromineCommonPackets.BLOCK_ENTITY_UPDATE_PACKET, buffer);
			}
		}
		wasClicked = false;

		super.onMouseReleased(mouseX, mouseY, mouseButton);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public @NotNull List<ITextComponent> getTooltip() {
		Direction offset = MirrorUtilities.rotate(direction, rotation);
		return Arrays.asList(new TranslationTextComponent("text.astromine.siding." + offset.getName()), new TranslationTextComponent("text.astromine.siding." + getSideName()));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawWidget(@NotNull MatrixStack matrices, @NotNull IRenderTypeBuffer provider) {
		if (getHidden())
			return;

		BaseRenderer.drawTexturedQuad(matrices, provider, getPosition().getX(), getPosition().getY(), getSize().getWidth(), getSize().getHeight(), getTexture());
	}
}
