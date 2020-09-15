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

package com.github.chainmailstudios.astromine.common.block.base;

import com.github.chainmailstudios.astromine.AstromineCommon;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public abstract class WrenchableHorizontalFacingTieredBlockWithEntity extends WrenchableHorizontalFacingBlockWithEntity {
	public WrenchableHorizontalFacingTieredBlockWithEntity(Properties settings) {
		super(settings);
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		ResourceLocation blockId = Registry.BLOCK.getKey(this);
		Tier blockTier = Tier.fromId(blockId);
		if (blockTier != null) {
			ItemStack stack = player.getItemInHand(hand);
			ResourceLocation itemId = Registry.ITEM.getKey(stack.getItem());
			if (itemId.getNamespace().equals(AstromineCommon.MOD_ID) && itemId.getPath().endsWith("_machine_upgrade_kit")) {
				Tier itemTier = Tier.fromId(itemId);

				if (itemTier != null && itemTier.ordinal() != 0 && Tier.values()[itemTier.ordinal() - 1] == blockTier) {
					ResourceLocation newBlockId = new ResourceLocation(blockId.toString().replace(blockTier.name().toLowerCase(Locale.ROOT) + "_", itemTier.name().toLowerCase(Locale.ROOT) + "_"));
					Optional<Block> newBlock = Registry.BLOCK.getOptional(newBlockId);

					if (newBlock.isPresent()) {
						if (world.isClientSide) {
							Random random = world.getRandom();
							double x = pos.getX() - 0.3;
							double y = pos.getY() - 0.3;
							double z = pos.getZ() - 0.3;
							for (int i = 0; i < 20; i++) {
								world.addParticle(ParticleTypes.COMPOSTER, x + random.nextDouble() * 1.6, y + random.nextDouble() * 1.6, z + random.nextDouble() * 1.6, -0.2 + random.nextDouble() * 0.4, -0.2 + random.nextDouble() * 0.4, -0.2 + random.nextDouble() * 0.4);
							}
							world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1, 1, false);
							return ActionResultType.CONSUME;
						}

						if(!player.isCreative()) {
							ItemStack copy = stack.copy();
							copy.shrink(1);
							player.setItemInHand(hand, copy);
						}

						TileEntity blockEntity = world.getBlockEntity(pos);
						CompoundNBT beTag = null;
						if (blockEntity != null) {
							beTag = blockEntity.save(new CompoundNBT());
							beTag.putInt("x", pos.getX());
							beTag.putInt("y", pos.getY());
							beTag.putInt("z", pos.getZ());
						}
						world.removeBlockEntity(pos);

						BlockState newState = newBlock.get().defaultBlockState();
						for (Property property : state.getProperties()) {
							if (newState.hasProperty(property)) {
								newState = newState.setValue(property, state.getValue(property));
							}
						}

						world.setBlock(pos, newState, 3, 512);
						TileEntity newBlockEntity = world.getBlockEntity(pos);
						if (newBlockEntity != null && beTag != null) {
							newBlockEntity.load(newState, beTag);
						}

						return ActionResultType.SUCCESS;
					}
				}
			}
		}

		return super.use(state, world, pos, player, hand, hit);
	}

	private enum Tier {
		PRIMITIVE,
		BASIC,
		ADVANCED,
		ELITE;

		static Tier fromId(ResourceLocation identifier) {
			String path = identifier.getPath();
			for (Tier tier : values()) {
				if (path.startsWith(tier.name().toLowerCase(Locale.ROOT) + "_")) {
					return tier;
				}
			}
			return null;
		}
	}
}
