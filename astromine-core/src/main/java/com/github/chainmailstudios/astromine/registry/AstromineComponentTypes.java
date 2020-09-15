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
import com.github.chainmailstudios.astromine.common.component.block.entity.BlockEntityTransferComponent;
import com.github.chainmailstudios.astromine.common.component.entity.OxygenComponent;
import com.github.chainmailstudios.astromine.common.component.world.ChunkAtmosphereComponent;
import com.github.chainmailstudios.astromine.common.component.world.WorldBridgeComponent;
import com.github.chainmailstudios.astromine.common.component.world.WorldNetworkComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AstromineComponentTypes {
	@CapabilityInject(WorldNetworkComponent.class)
	public static Capability<WorldNetworkComponent> WORLD_NETWORK_COMPONENT = null;
	@CapabilityInject(ChunkAtmosphereComponent.class)
	public static Capability<ChunkAtmosphereComponent> CHUNK_ATMOSPHERE_COMPONENT = null;
	@CapabilityInject(WorldBridgeComponent.class)
	public static Capability<WorldBridgeComponent> WORLD_BRIDGE_COMPONENT = null;

	@CapabilityInject(BlockEntityTransferComponent.class)
	public static Capability<BlockEntityTransferComponent> BLOCK_ENTITY_TRANSFER_COMPONENT = null;
	@CapabilityInject(OxygenComponent.class)
	public static Capability<OxygenComponent> ENTITY_OXYGEN_COMPONENT = null;

	public static void initialize() {
		CapabilityManager.INSTANCE.register(ChunkAtmosphereComponent.class, new Capability.IStorage<ChunkAtmosphereComponent>() {
			@Nullable
			@Override
			public INBT writeNBT(Capability<ChunkAtmosphereComponent> capability, ChunkAtmosphereComponent instance, Direction side) {
				return instance.toTag(new CompoundNBT());
			}

			@Override
			public void readNBT(Capability<ChunkAtmosphereComponent> capability, ChunkAtmosphereComponent instance, Direction side, INBT nbt) {
				instance.fromTag((CompoundNBT) nbt);
			}
		}, ChunkAtmosphereComponent::new);
		CapabilityManager.INSTANCE.register(WorldBridgeComponent.class, new Capability.IStorage<WorldBridgeComponent>() {
			@Nullable
			@Override
			public INBT writeNBT(Capability<WorldBridgeComponent> capability, WorldBridgeComponent instance, Direction side) {
				return instance.toTag(new CompoundNBT());
			}

			@Override
			public void readNBT(Capability<WorldBridgeComponent> capability, WorldBridgeComponent instance, Direction side, INBT nbt) {
				instance.fromTag((CompoundNBT) nbt);
			}
		}, WorldBridgeComponent::new);
		CapabilityManager.INSTANCE.register(BlockEntityTransferComponent.class, new Capability.IStorage<BlockEntityTransferComponent>() {
			@Nullable
			@Override
			public INBT writeNBT(Capability<BlockEntityTransferComponent> capability, BlockEntityTransferComponent instance, Direction side) {
				return instance.toTag(new CompoundNBT());
			}

			@Override
			public void readNBT(Capability<BlockEntityTransferComponent> capability, BlockEntityTransferComponent instance, Direction side, INBT nbt) {
				instance.fromTag((CompoundNBT) nbt);
			}
		}, BlockEntityTransferComponent::new);
		CapabilityManager.INSTANCE.register(OxygenComponent.class, new Capability.IStorage<OxygenComponent>() {
			@Nullable
			@Override
			public INBT writeNBT(Capability<OxygenComponent> capability, OxygenComponent instance, Direction side) {
				return instance.toTag(new CompoundNBT());
			}

			@Override
			public void readNBT(Capability<OxygenComponent> capability, OxygenComponent instance, Direction side, INBT nbt) {
				instance.fromTag((CompoundNBT) nbt);
			}
		}, OxygenComponent::new);
		MinecraftForge.EVENT_BUS.<AttachCapabilitiesEvent<Chunk>, Chunk>addGenericListener(Chunk.class, event -> {
			event.addCapability(AstromineCommon.identifier("chunk_atmosphere"), new ICapabilitySerializable<CompoundNBT>() {
				private ChunkAtmosphereComponent atmosphereComponent = new ChunkAtmosphereComponent();

				@Override
				public CompoundNBT serializeNBT() {
					return atmosphereComponent.toTag(new CompoundNBT());
				}

				@Override
				public void deserializeNBT(CompoundNBT nbt) {
					atmosphereComponent.fromTag(nbt);
				}

				@NotNull
				@Override
				public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
					if (cap == CHUNK_ATMOSPHERE_COMPONENT)
						return LazyOptional.of(() -> atmosphereComponent).cast();
					return LazyOptional.empty();
				}
			});
		});
		MinecraftForge.EVENT_BUS.<AttachCapabilitiesEvent<World>, World>addGenericListener(World.class, event -> {
			event.addCapability(AstromineCommon.identifier("world_network"), new ICapabilitySerializable<CompoundNBT>() {
				private WorldNetworkComponent networkComponent = new WorldNetworkComponent();

				@Override
				public CompoundNBT serializeNBT() {
					return networkComponent.toTag(new CompoundNBT());
				}

				@Override
				public void deserializeNBT(CompoundNBT nbt) {
					networkComponent.fromTag(nbt);
				}

				@NotNull
				@Override
				public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
					if (cap == WORLD_NETWORK_COMPONENT)
						return LazyOptional.of(() -> networkComponent).cast();
					return LazyOptional.empty();
				}
			});
			event.addCapability(AstromineCommon.identifier("world_bridge"), new ICapabilitySerializable<CompoundNBT>() {
				private WorldBridgeComponent bridgeComponent = new WorldBridgeComponent();

				@Override
				public CompoundNBT serializeNBT() {
					return bridgeComponent.toTag(new CompoundNBT());
				}

				@Override
				public void deserializeNBT(CompoundNBT nbt) {
					bridgeComponent.fromTag(nbt);
				}

				@NotNull
				@Override
				public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
					if (cap == WORLD_BRIDGE_COMPONENT)
						return LazyOptional.of(() -> bridgeComponent).cast();
					return LazyOptional.empty();
				}
			});
		});
		MinecraftForge.EVENT_BUS.<AttachCapabilitiesEvent<Entity>, Entity>addGenericListener(Entity.class, event -> {
			event.addCapability(AstromineCommon.identifier("entity_oxygen"), new ICapabilitySerializable<CompoundNBT>() {
				private OxygenComponent oxygenComponent = new OxygenComponent();

				@Override
				public CompoundNBT serializeNBT() {
					return oxygenComponent.toTag(new CompoundNBT());
				}

				@Override
				public void deserializeNBT(CompoundNBT nbt) {
					oxygenComponent.fromTag(nbt);
				}

				@NotNull
				@Override
				public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
					if (cap == ENTITY_OXYGEN_COMPONENT)
						return LazyOptional.of(() -> oxygenComponent).cast();
					return LazyOptional.empty();
				}
			});
		});
	}
}
