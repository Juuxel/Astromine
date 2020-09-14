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

package com.github.chainmailstudios.astromine.mixin;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.PortalInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.github.chainmailstudios.astromine.client.cca.ClientAtmosphereManager;
import com.github.chainmailstudios.astromine.common.component.world.ChunkAtmosphereComponent;
import com.github.chainmailstudios.astromine.common.entity.GravityEntity;
import com.github.chainmailstudios.astromine.common.registry.DimensionLayerRegistry;
import com.github.chainmailstudios.astromine.registry.AstromineCommonCallbacks;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;
import com.github.chainmailstudios.astromine.registry.AstromineConfig;
import nerdhub.cardinal.components.api.component.ComponentProvider;

import com.google.common.collect.Lists;
import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin implements GravityEntity {
	@Shadow
	public Level world;
	int lastY = 0;
	@Unique
	Entity lastVehicle = null;
	@Unique
	PortalInfo nextTeleportTarget = null;
	private Level astromine_lastWorld = null;

	@Shadow
	public abstract BlockPos getBlockPos();

	@ModifyVariable(at = @At("HEAD"), method = "handleFallDamage(FF)Z", index = 1)
	float getDamageMultiplier(float damageMultiplier) {
		return (float) (damageMultiplier * getGravity() * getGravity());
	}

	@Override
	public double getGravity() {
		Level world = ((Entity) (Object) this).level;
		return getGravity(world);
	}

	@Inject(at = @At("HEAD"), method = "tickNetherPortal()V")
	void onTick(CallbackInfo callbackInformation) {
		Entity entity = (Entity) (Object) this;

		if ((int) entity.position().y() != lastY && !entity.level.isClientSide && entity.getVehicle() == null) {
			lastY = (int) entity.position().y();

			int bottomPortal = DimensionLayerRegistry.INSTANCE.getLevel(DimensionLayerRegistry.Type.BOTTOM, entity.level.dimension());
			int topPortal = DimensionLayerRegistry.INSTANCE.getLevel(DimensionLayerRegistry.Type.TOP, entity.level.dimension());

			if (lastY <= bottomPortal && bottomPortal != Integer.MIN_VALUE) {
				ResourceKey<Level> worldKey = ResourceKey.create(Registry.DIMENSION_REGISTRY, DimensionLayerRegistry.INSTANCE.getDimension(DimensionLayerRegistry.Type.BOTTOM, entity.level.dimension()).location());

				astromine_teleport(entity, worldKey, DimensionLayerRegistry.Type.BOTTOM);
			} else if (lastY >= topPortal && topPortal != Integer.MIN_VALUE) {
				ResourceKey<Level> worldKey = ResourceKey.create(Registry.DIMENSION_REGISTRY, DimensionLayerRegistry.INSTANCE.getDimension(DimensionLayerRegistry.Type.TOP, entity.level.dimension()).location());

				astromine_teleport(entity, worldKey, DimensionLayerRegistry.Type.TOP);
			}
		}

		if (entity.getVehicle() != null)
			lastVehicle = null;
		if (lastVehicle != null) {
			if (lastVehicle.getCommandSenderWorld().dimension().equals(entity.getCommandSenderWorld().dimension())) {
				entity.startRiding(lastVehicle);
				lastVehicle = null;
			}
		}
	}

	void astromine_teleport(Entity entity, ResourceKey<Level> destinationKey, DimensionLayerRegistry.Type type) {
		ServerLevel serverWorld = entity.level.getServer().getLevel(destinationKey);

		List<Entity> existingPassengers = Lists.newArrayList(entity.getPassengers());

		List<SynchedEntityData.DataItem<?>> entries = Lists.newArrayList();
		for (SynchedEntityData.DataItem<?> entry : entity.getEntityData().getAll()) {
			entries.add(entry.copy());
		}

		nextTeleportTarget = DimensionLayerRegistry.INSTANCE.getPlacer(type, entity.level.dimension()).placeEntity(entity);
		Entity newEntity = entity.changeDimension(serverWorld);

		for (SynchedEntityData.DataItem entry : entries) {
			newEntity.getEntityData().set(entry.getAccessor(), entry.getValue());
		}

		for (Entity existingEntity : existingPassengers) {
			((EntityMixin) (Object) existingEntity).lastVehicle = newEntity;
		}
	}

	@Inject(method = "getTeleportTarget", at = @At("HEAD"), cancellable = true)
	protected void getTeleportTarget(ServerLevel destination, CallbackInfoReturnable<PortalInfo> cir) {
		if (nextTeleportTarget != null) {
			cir.setReturnValue(nextTeleportTarget);
			nextTeleportTarget = null;
		}
	}

	@Inject(at = @At("HEAD"), method = "tick()V")
	void onThing(CallbackInfo ci) {
		// TODO Make this sync all visible chunks around the player.
		if (AstromineCommonCallbacks.atmosphereTickCounter == AstromineConfig.get().gasTickRate && ((Entity) (Object) this) instanceof ServerPlayer && world != astromine_lastWorld) {
			astromine_lastWorld = world;

			ServerSidePacketRegistry.INSTANCE.sendToPlayer(((Player) (Object) this), ClientAtmosphereManager.GAS_ERASED, ClientAtmosphereManager.ofGasErased());

			ComponentProvider componentProvider = ComponentProvider.fromChunk(world.getChunk(getBlockPos()));

			ChunkAtmosphereComponent atmosphereComponent = componentProvider.getComponent(AstromineComponentTypes.CHUNK_ATMOSPHERE_COMPONENT);

			atmosphereComponent.getVolumes().forEach(((blockPos, volume) -> {
				ServerSidePacketRegistry.INSTANCE.sendToPlayer(((Player) (Object) this), ClientAtmosphereManager.GAS_ADDED, ClientAtmosphereManager.ofGasAdded(blockPos, volume));
			}));
		}
	}
}
