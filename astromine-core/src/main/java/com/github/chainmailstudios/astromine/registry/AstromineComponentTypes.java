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
import com.github.chainmailstudios.astromine.common.component.entity.EntityOxygenComponent;
import com.github.chainmailstudios.astromine.common.component.entity.OxygenComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.EnergyInventoryComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.FluidInventoryComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.ItemInventoryComponent;
import com.github.chainmailstudios.astromine.common.component.world.ChunkAtmosphereComponent;
import com.github.chainmailstudios.astromine.common.component.world.WorldBridgeComponent;
import com.github.chainmailstudios.astromine.common.component.world.WorldNetworkComponent;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.jetbrains.annotations.Nullable;

public class AstromineComponentTypes {
	public static final ComponentType<WorldNetworkComponent> WORLD_NETWORK_COMPONENT = ComponentRegistry.INSTANCE.registerIfAbsent(AstromineCommon.identifier("world_network_component"), WorldNetworkComponent.class);
	public static final ComponentType<ChunkAtmosphereComponent> CHUNK_ATMOSPHERE_COMPONENT = ComponentRegistry.INSTANCE.registerIfAbsent(AstromineCommon.identifier("chunk_atmosphere_component"), ChunkAtmosphereComponent.class);
	public static final ComponentType<WorldBridgeComponent> WORLD_BRIDGE_COMPONENT = ComponentRegistry.INSTANCE.registerIfAbsent(AstromineCommon.identifier("world_bridge_component"), WorldBridgeComponent.class);

	@CapabilityInject(BlockEntityTransferComponent.class)
	public static Capability<BlockEntityTransferComponent> BLOCK_ENTITY_TRANSFER_COMPONENT = null;
	@CapabilityInject(OxygenComponent.class)
	public static Capability<OxygenComponent> ENTITY_OXYGEN_COMPONENT = null;

	public static void initialize() {
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
	}
}
