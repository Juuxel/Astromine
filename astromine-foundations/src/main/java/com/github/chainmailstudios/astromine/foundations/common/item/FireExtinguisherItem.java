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

package com.github.chainmailstudios.astromine.foundations.common.item;

import com.github.chainmailstudios.astromine.foundations.registry.AstromineFoundationsCriteria;
import com.github.chainmailstudios.astromine.foundations.registry.AstromineFoundationsSoundEvents;
import com.github.chainmailstudios.astromine.registry.AstromineConfig;
import com.github.chainmailstudios.astromine.registry.AstromineSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class FireExtinguisherItem extends Item {
	public FireExtinguisherItem(Item.Properties settings) {
		super(settings);
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		this.use(context.getLevel(), context.getPlayer(), context.getHand());

		return ActionResultType.PASS;
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		Vector3d placeVec = user.getEyePosition(0);

		Vector3d thrustVec = new Vector3d(0.8, 0.8, 0.8);

		thrustVec = thrustVec.multiply(user.getLookAngle());

		for (int i = 0; i < world.getFreeMapId().nextInt(64); ++i) {
			float r = world.getFreeMapId().nextFloat();
			world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, placeVec.x + thrustVec.x, placeVec.y + thrustVec.y, placeVec.z + thrustVec.z, thrustVec.x * r, thrustVec.y * r, thrustVec.z * r);
		}

		thrustVec = thrustVec.scale(-1);

		if (!user.isShiftKeyDown()) {
			user.push(thrustVec.x, thrustVec.y, thrustVec.z);
			if (user instanceof ServerPlayerEntity) {
				((ServerPlayerEntity) user).connection.aboveGroundTickCount = 0;
				AstromineFoundationsCriteria.USE_FIRE_EXTINGUISHER.trigger((ServerPlayerEntity) user);
			}
			user.getCooldowns().addCooldown(this, AstromineConfig.get().fireExtinguisherStandingDelay);
		} else {
			user.getCooldowns().addCooldown(this, AstromineConfig.get().fireExtinguisherSneakingDelay);
		}

		BlockRayTraceResult result = (BlockRayTraceResult) user.pick(6, 0, false);

		BlockPos.Mutable.betweenClosedStream(new AxisAlignedBB(result.getBlockPos()).inflate(2)).forEach(position -> {
			BlockState state = world.getBlockState(position);

			if (state.getBlock() instanceof FireBlock) {
				world.setBlockAndUpdate(position, Blocks.AIR.defaultBlockState());
			} else if (state.getBlock() instanceof CampfireBlock) {
				if (state.getValue(CampfireBlock.LIT))
					world.setBlockAndUpdate(position, state.setValue(CampfireBlock.LIT, false));
			}
		});

		world.getEntities(null, new AxisAlignedBB(result.getBlockPos()).inflate(3)).forEach(entity -> {
			if (entity.isOnFire()) {
				entity.setRemainingFireTicks(0);
				if (user instanceof ServerPlayerEntity) {
					AstromineFoundationsCriteria.PROPERLY_USE_FIRE_EXTINGUISHER.trigger((ServerPlayerEntity) user);
				}
			}
		});

		if (world.isClientSide) {
			world.playSound(user, user.blockPosition(), AstromineFoundationsSoundEvents.FIRE_EXTINGUISHER_OPEN, SoundCategory.PLAYERS, 1f, 1f);
		}

		return super.use(world, user, hand);
	}

	@Override
	public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		this.use(user.level, user, hand);

		return ActionResultType.PASS;
	}
}
