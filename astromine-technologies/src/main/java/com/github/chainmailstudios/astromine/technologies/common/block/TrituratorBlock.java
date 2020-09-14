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

package com.github.chainmailstudios.astromine.technologies.common.block;

import com.github.chainmailstudios.astromine.common.block.base.WrenchableHorizontalFacingTieredBlockWithEntity;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.TrituratorBlockEntity;
import com.github.chainmailstudios.astromine.technologies.common.screenhandler.TrituratorScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TrituratorBlock extends WrenchableHorizontalFacingTieredBlockWithEntity {
	public TrituratorBlock(Properties settings) {
		super(settings);
	}

	public abstract static class Base extends TrituratorBlock {
		public Base(Properties settings) {
			super(settings);
		}

		@Override
		public boolean hasScreenHandler() {
			return true;
		}

		@Override
		public Container createScreenHandler(BlockState state, World world, BlockPos pos, int syncId, PlayerInventory playerInventory, PlayerEntity player) {
			return new TrituratorScreenHandler(syncId, playerInventory.player, pos);
		}

		@Override
		public void populateScreenHandlerBuffer(BlockState state, World world, BlockPos pos, ServerPlayerEntity player, PacketBuffer buffer) {
			buffer.writeBlockPos(pos);
		}
	}

	public static class Primitive extends TrituratorBlock.Base {
		public Primitive(Properties settings) {
			super(settings);
		}

		@Override
		public TileEntity createBlockEntity() {
			return new TrituratorBlockEntity.Primitive();
		}
	}

	public static class Basic extends TrituratorBlock.Base {
		public Basic(Properties settings) {
			super(settings);
		}

		@Override
		public TileEntity createBlockEntity() {
			return new TrituratorBlockEntity.Basic();
		}
	}

	public static class Advanced extends TrituratorBlock.Base {
		public Advanced(Properties settings) {
			super(settings);
		}

		@Override
		public TileEntity createBlockEntity() {
			return new TrituratorBlockEntity.Advanced();
		}
	}

	public static class Elite extends TrituratorBlock.Base {
		public Elite(Properties settings) {
			super(settings);
		}

		@Override
		public TileEntity createBlockEntity() {
			return new TrituratorBlockEntity.Elite();
		}
	}
}
