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

package com.github.chainmailstudios.astromine.registry;

import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.common.network.NetworkingManager;
import com.github.chainmailstudios.astromine.common.packet.PacketConsumer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class AstromineCommonPackets {
	public static final ResourceLocation BLOCK_ENTITY_UPDATE_PACKET = AstromineCommon.identifier("block_entity_update");

	public static void initialize() {
		NetworkingManager.registerC2SHandler(BLOCK_ENTITY_UPDATE_PACKET, (((context, buffer) -> {
			BlockPos blockPos = buffer.readBlockPos();
			ResourceLocation identifier = buffer.readResourceLocation();
			PacketBuffer storedBuffer = new PacketBuffer(buffer.copy());

			context.enqueueWork(() -> {
				TileEntity blockEntity = context.getSender().getCommandSenderWorld().getBlockEntity(blockPos);

				if (blockEntity instanceof PacketConsumer) {
					((PacketConsumer) blockEntity).consumePacket(identifier, storedBuffer, context);
				}
			});
		})));
	}

}
