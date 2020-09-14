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

package com.github.chainmailstudios.astromine.common.block.entity.base;

import com.github.chainmailstudios.astromine.common.block.base.BlockWithEntity;
import com.github.chainmailstudios.astromine.common.utilities.capability.inventory.ExtendedComponentSidedInventoryProvider;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.item.ItemAttributes;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.common.block.transfer.TransferType;
import com.github.chainmailstudios.astromine.common.component.SidedComponentProvider;
import com.github.chainmailstudios.astromine.common.component.block.entity.BlockEntityTransferComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.FluidInventoryComponent;
import com.github.chainmailstudios.astromine.common.packet.PacketConsumer;
import com.github.chainmailstudios.astromine.common.utilities.TransportUtilities;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.component.Component;
import org.jetbrains.annotations.NotNull;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHandler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class ComponentBlockEntity extends net.minecraft.world.level.block.entity.BlockEntity implements SidedComponentProvider, PacketConsumer, BlockEntityClientSerializable, TickableBlockEntity {
	protected final BlockEntityTransferComponent transferComponent = new BlockEntityTransferComponent();

	protected final Map<ComponentType<?>, Component> allComponents = Maps.newHashMap();

	protected final Map<ResourceLocation, BiConsumer<FriendlyByteBuf, PacketContext>> allHandlers = Maps.newHashMap();

	protected boolean skipInventory = true;

	public boolean isActive = false;

	public boolean[] activity = { false, false, false, false, false };

	public static final ResourceLocation TRANSFER_UPDATE_PACKET = AstromineCommon.identifier("transfer_update_packet");

	public ComponentBlockEntity(BlockEntityType<?> type) {
		super(type);

		addConsumer(TRANSFER_UPDATE_PACKET, ((buffer, context) -> {
			ResourceLocation packetIdentifier = buffer.readResourceLocation();
			Direction packetDirection = buffer.readEnum(Direction.class);
			TransferType packetTransferType = buffer.readEnum(TransferType.class);

			transferComponent.get(ComponentRegistry.INSTANCE.get(packetIdentifier)).set(packetDirection, packetTransferType);
			setChanged();
			sync();
		}));
	}

	public void doNotSkipInventory() {
		this.skipInventory = false;
	}

	public void addComponent(ComponentType<?> type, Component component) {
		allComponents.put(type, component);
		transferComponent.add(type);
	}

	public void addConsumer(ResourceLocation identifier, BiConsumer<FriendlyByteBuf, PacketContext> consumer) {
		allHandlers.put(identifier, consumer);
	}

	@Override
	public void consumePacket(ResourceLocation identifier, FriendlyByteBuf buffer, PacketContext context) {
		allHandlers.get(identifier).accept(buffer, context);
	}

	@Override
	public <T extends Component> Collection<T> getSidedComponents(Direction direction) {
		if (direction == null) {
			return (Collection<T>) allComponents.values();
		} else {
			if (getBlockState().hasProperty(HorizontalDirectionalBlock.FACING)) {
				return (Collection<T>) getComponentTypes().stream().map(type -> new Tuple<>((ComponentType) type, (Component) getComponent(type))).filter(pair -> !transferComponent.get(pair.getA()).get(direction).isNone()).map(Tuple::getB).collect(Collectors.toList());
			} else if (getBlockState().hasProperty(DirectionalBlock.FACING)) {
				return (Collection<T>) getComponentTypes().stream().map(type -> new Tuple<>((ComponentType) type, (Component) getComponent(type))).filter(pair -> !transferComponent.get(pair.getA()).get(direction).isNone()).map(Tuple::getB).collect(Collectors.toList());
			} else {
				return Lists.newArrayList();
			}
		}
	}

	@Override
	public boolean hasComponent(ComponentType<?> componentType) {
		return allComponents.containsKey(componentType) || componentType == AstromineComponentTypes.BLOCK_ENTITY_TRANSFER_COMPONENT;
	}

	@Override
	public <C extends Component> C getComponent(ComponentType<C> componentType) {
		return componentType == AstromineComponentTypes.BLOCK_ENTITY_TRANSFER_COMPONENT ? (C) transferComponent : (C) allComponents.get(componentType);
	}

	@Override
	public Set<ComponentType<?>> getComponentTypes() {
		return allComponents.keySet();
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		tag.put("transfer", transferComponent.toTag(new CompoundTag()));

		allComponents.forEach((type, component) -> {
			tag.put(type.getId().toString(), component.toTag(new CompoundTag()));
		});

		return super.save(tag);
	}

	@Override
	public void load(BlockState state, @NotNull CompoundTag tag) {
		transferComponent.fromTag(tag.getCompound("transfer"));

		allComponents.forEach((type, component) -> {
			if (tag.contains(type.getId().toString())) {
				component.fromTag(tag.getCompound(type.getId().toString()));
			}
		});

		super.load(state, tag);
	}

	@Override
	public CompoundTag toClientTag(CompoundTag compoundTag) {
		compoundTag = save(compoundTag);
		if (skipInventory) {
			compoundTag.remove(AstromineComponentTypes.ITEM_INVENTORY_COMPONENT.getId().toString());
		} else {
			skipInventory = true;
		}
		return compoundTag;
	}

	@Override
	public void fromClientTag(CompoundTag compoundTag) {
		load(null, compoundTag);
	}

	@Override
	public void tick() {
		if (!hasLevel() || level.isClientSide())
			return;

		FluidInventoryComponent fluidComponent = getComponent(AstromineComponentTypes.FLUID_INVENTORY_COMPONENT);

		List<Tuple<EnergyHandler, EnergyHandler>> energyTransfers = Lists.newArrayList();

		for (Direction offsetDirection : Direction.values()) {
			BlockPos neighborPos = getBlockPos().relative(offsetDirection);
			BlockState neighborState = level.getBlockState(neighborPos);

			net.minecraft.world.level.block.entity.BlockEntity neighborBlockEntity = level.getBlockEntity(neighborPos);
			if (neighborBlockEntity != null) {
				SidedComponentProvider neighborProvider = SidedComponentProvider.fromBlockEntity(neighborBlockEntity);
				Direction neighborDirection = offsetDirection.getOpposite();
				BlockEntityTransferComponent neighborTransferComponent = neighborProvider != null ? neighborProvider.getComponent(AstromineComponentTypes.BLOCK_ENTITY_TRANSFER_COMPONENT) : null;

				// Handle Item Siding
				if (this instanceof ExtendedComponentSidedInventoryProvider) {
					if (!transferComponent.get(AstromineComponentTypes.ITEM_INVENTORY_COMPONENT).get(offsetDirection).isDefault()) {
						// input
						ItemExtractable neighbor = ItemAttributes.EXTRACTABLE.get(level, neighborPos, SearchOptions.inDirection(offsetDirection));
						ItemInsertable self = ItemAttributes.INSERTABLE.get(level, getBlockPos(), SearchOptions.inDirection(neighborDirection));

						TransportUtilities.move(neighbor, self, 1);
					}
					if (!transferComponent.get(AstromineComponentTypes.ITEM_INVENTORY_COMPONENT).get(offsetDirection).isDefault()) {
						// output
						ItemExtractable self = ItemAttributes.EXTRACTABLE.get(level, getBlockPos(), SearchOptions.inDirection(neighborDirection));
						ItemInsertable neighbor = ItemAttributes.INSERTABLE.get(level, neighborPos, SearchOptions.inDirection(offsetDirection));

						TransportUtilities.move(self, neighbor, 1);
					}
				}

				// Handle fluid siding
				if (fluidComponent != null && transferComponent.get(AstromineComponentTypes.FLUID_INVENTORY_COMPONENT).get(offsetDirection).canExtract()) {
					FluidInventoryComponent neighborFluidComponent = null;
					if (neighborTransferComponent != null) {
						// Get via astromine siding
						if (neighborTransferComponent.get(AstromineComponentTypes.FLUID_INVENTORY_COMPONENT).get(neighborDirection).canInsert())
							neighborFluidComponent = neighborProvider.getComponent(AstromineComponentTypes.FLUID_INVENTORY_COMPONENT);
					}

					if (neighborFluidComponent != null) {
						List<FluidVolume> matching = (List<FluidVolume>) fluidComponent.getExtractableContentsMatching(offsetDirection, (volume -> !volume.isEmpty()));

						if (!matching.isEmpty()) {
							neighborFluidComponent.insert(neighborDirection, matching.get(0));
						}
					}
				}

				// Handle energy siding
				if (TransportUtilities.isExtractingEnergy(this, transferComponent, offsetDirection)) {
					if (TransportUtilities.isInsertingEnergy(neighborBlockEntity, neighborTransferComponent, neighborDirection)) {
						energyTransfers.add(new Tuple<>(Energy.of(this).side(offsetDirection), Energy.of(neighborBlockEntity).side(neighborDirection)));
					}
				}
			}
		}

		energyTransfers.sort(Comparator.comparing(Tuple::getB, Comparator.comparingDouble(EnergyHandler::getEnergy)));
		for (int i = energyTransfers.size() - 1; i >= 0; i--) {
			Tuple<EnergyHandler, EnergyHandler> pair = energyTransfers.get(i);
			EnergyHandler input = pair.getA();
			EnergyHandler output = pair.getB();
			input.into(output).move(Math.max(0, Math.min(input.getMaxOutput() / energyTransfers.size(), Math.min(Math.min(input.getEnergy() / (i + 1), output.getMaxStored() - output.getEnergy()), Math.min(input.getMaxOutput(), output.getMaxInput())))));
		}

		if (level.getBlockState(getBlockPos()).hasProperty(BlockWithEntity.ACTIVE)) {
			if (activity.length - 1 >= 0)
				System.arraycopy(activity, 1, activity, 0, activity.length - 1);

			activity[4] = isActive;

			if (isActive && !activity[0]) {
				level.setBlockAndUpdate(getBlockPos(), level.getBlockState(getBlockPos()).setValue(BlockWithEntity.ACTIVE, true));
			} else if (!isActive && activity[0]) {
				level.setBlockAndUpdate(getBlockPos(), level.getBlockState(getBlockPos()).setValue(BlockWithEntity.ACTIVE, false));
			}
		}
	}

	public void tickActive() {
		isActive = true;
	}

	public void tickInactive() {
		isActive = false;
	}
}
