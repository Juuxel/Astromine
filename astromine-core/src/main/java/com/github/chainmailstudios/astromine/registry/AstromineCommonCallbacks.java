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

import com.github.chainmailstudios.astromine.common.callback.ServerChunkTickEvent;
import com.github.chainmailstudios.astromine.common.component.world.ChunkAtmosphereComponent;
import com.github.chainmailstudios.astromine.common.component.world.WorldNetworkComponent;
import com.github.chainmailstudios.astromine.common.entity.base.*;
import com.github.chainmailstudios.astromine.common.screenhandler.base.block.ComponentBlockEntityScreenHandler;
import nerdhub.cardinal.components.api.event.EntityComponentCallback;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = "astromine")
public class AstromineCommonCallbacks {
	public static int atmosphereTickCounter = 0;

	@SuppressWarnings("UnstableApiUsage")
	public static void initialize() {
		EntityComponentCallback.register(AstromineComponentTypes.FLUID_INVENTORY_COMPONENT, ComponentFluidInventoryEntity.class, ComponentFluidInventoryEntity::createFluidComponent);
		EntityComponentCallback.register(AstromineComponentTypes.ITEM_INVENTORY_COMPONENT, ComponentFluidInventoryEntity.class, ComponentFluidInventoryEntity::createItemComponent);

		EntityComponentCallback.register(AstromineComponentTypes.ITEM_INVENTORY_COMPONENT, ComponentEnergyItemEntity.class, ComponentEnergyItemEntity::createItemComponent);
		EntityComponentCallback.register(AstromineComponentTypes.ENERGY_INVENTORY_COMPONENT, ComponentEnergyItemEntity.class, ComponentEnergyItemEntity::createEnergyComponent);

		EntityComponentCallback.register(AstromineComponentTypes.ITEM_INVENTORY_COMPONENT, ComponentItemEntity.class, ComponentItemEntity::createItemComponent);

		EntityComponentCallback.register(AstromineComponentTypes.FLUID_INVENTORY_COMPONENT, ComponentFluidEntity.class, ComponentFluidEntity::createFluidComponent);

		EntityComponentCallback.register(AstromineComponentTypes.ENERGY_INVENTORY_COMPONENT, ComponentEnergyEntity.class, ComponentEnergyEntity::createEnergyComponent);
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (atmosphereTickCounter < AstromineConfig.get().gasTickRate) {
			atmosphereTickCounter++;
		} else {
			atmosphereTickCounter = 0;
		}

		for (PlayerEntity playerEntity : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
			if (playerEntity.containerMenu instanceof ComponentBlockEntityScreenHandler) {
				ComponentBlockEntityScreenHandler screenHandler = (ComponentBlockEntityScreenHandler) playerEntity.containerMenu;

				if (screenHandler.syncBlockEntity != null) {
					((ServerPlayerEntity) playerEntity).connection.send(screenHandler.syncBlockEntity.getUpdatePacket());
					break;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onWorldTick(TickEvent.WorldTickEvent event) {
		World world = event.world;
		WorldNetworkComponent component = world.getCapability(AstromineComponentTypes.WORLD_NETWORK_COMPONENT).orElse(null);
		if (component != null) {
			component.tick(world);
		}
	}

	@SubscribeEvent
	public static void onChunkTick(ServerChunkTickEvent event) {
		Chunk chunk = event.chunk;
		ServerWorld world = event.world;
		ChunkAtmosphereComponent component = chunk.getCapability(AstromineComponentTypes.CHUNK_ATMOSPHERE_COMPONENT).orElse(null);
		if (component != null) {
			if (atmosphereTickCounter == AstromineConfig.get().gasTickRate && world.hasChunk(chunk.getPos().x, chunk.getPos().z)) {
				component.tick(chunk, world);
			}
		}
	}
}
