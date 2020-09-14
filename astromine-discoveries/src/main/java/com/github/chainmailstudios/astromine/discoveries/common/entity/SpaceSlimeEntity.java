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

package com.github.chainmailstudios.astromine.discoveries.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import com.github.chainmailstudios.astromine.discoveries.common.entity.ai.superspaceslime.SpaceSlimeJumpHoverGoal;
import com.github.chainmailstudios.astromine.discoveries.registry.AstromineDiscoveriesParticles;

import java.util.Random;

public class SpaceSlimeEntity extends SlimeEntity {
	private static final DataParameter<Integer> FLOATING_PROGRESS = EntityDataManager.defineId(SpaceSlimeEntity.class, DataSerializers.INT);
	private static final DataParameter<Boolean> FLOATING = EntityDataManager.defineId(SpaceSlimeEntity.class, DataSerializers.BOOLEAN);
	private int floatingCooldown;

	public SpaceSlimeEntity(EntityType<? extends SlimeEntity> entityType, World world) {
		super(entityType, world);
		this.floatingCooldown = world.getFreeMapId().nextInt(200);
	}

	public static boolean canSpawnInDark(EntityType<? extends SpaceSlimeEntity> type, IWorld world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return world.getDifficulty() != Difficulty.PEACEFUL && isSpawnDark(world, pos, random) && checkMobSpawnRules(type, world, spawnReason, pos, random) && random.nextDouble() <= .15;
	}

	public static boolean isSpawnDark(IWorld world, BlockPos pos, Random random) {
		if (world.getBrightness(LightType.SKY, pos) > random.nextInt(32)) {
			return false;
		} else {
			int i = ((ServerWorld) world).isThundering() ? world.getMaxLocalRawBrightness(pos, 10) : world.getMaxLocalRawBrightness(pos);
			return i <= random.nextInt(8);
		}
	}

	@Override
	public void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(3, new SpaceSlimeJumpHoverGoal(this));
	}

	@Override
	public void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(FLOATING, false);
		this.entityData.define(FLOATING_PROGRESS, 0);
	}

	@Override
	protected IParticleData getParticleType() {
		return AstromineDiscoveriesParticles.SPACE_SLIME;
	}

	@Override
	public void tick() {
		if (this.floatingCooldown > 0) {
			this.floatingCooldown--;
		}

		super.tick();
	}

	@Override
	protected int calculateFallDamage(float fallDistance, float damageMultiplier) {
		return 0;
	}

	@Override
	public boolean isNoGravity() {
		return this.entityData.get(FLOATING);
	}

	public int getFloatingCooldown() {
		return this.floatingCooldown;
	}

	public void setFloatingCooldown(int cooldown) {
		this.floatingCooldown = cooldown;
	}

	public boolean isFloating() {
		return this.entityData.get(FLOATING);
	}

	public void setFloating(boolean floating) {
		this.entityData.set(FLOATING, floating);
	}

	public int getFloatingProgress() {
		return this.entityData.get(FLOATING_PROGRESS);
	}

	public void setFloatingProgress(int progress) {
		this.entityData.set(FLOATING_PROGRESS, progress);
	}

	@Override
	public SoundEvent getSquishSound() {
		return SoundEvents.GLASS_BREAK;
	}
}
