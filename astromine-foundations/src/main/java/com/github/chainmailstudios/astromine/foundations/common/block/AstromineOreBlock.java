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

package com.github.chainmailstudios.astromine.foundations.common.block;

import com.github.chainmailstudios.astromine.foundations.registry.AstromineFoundationsBlocks;
import com.github.chainmailstudios.astromine.foundations.registry.AstromineFoundationsCriteria;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class AstromineOreBlock extends OreBlock {
	public AstromineOreBlock(BlockBehaviour.Properties settings) {
		super(settings);
	}

	@Override
	protected int xpOnDrop(Random random) {
		if (this == AstromineFoundationsBlocks.METEOR_METITE_ORE) {
			return Mth.nextInt(random, 2, 3);
		} else {
			return 0;
		}
	}

	@Override
	public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
		super.playerWillDestroy(world, pos, state, player);
		if (this == AstromineFoundationsBlocks.METEOR_METITE_ORE && player instanceof ServerPlayer) {
			ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
			if (!stack.isCorrectToolForDrops(state) && stack.isCorrectToolForDrops(Blocks.STONE.defaultBlockState())) {
				AstromineFoundationsCriteria.UNDERESTIMATE_METITE.trigger((ServerPlayer) player);
			}
		}
	}
}
