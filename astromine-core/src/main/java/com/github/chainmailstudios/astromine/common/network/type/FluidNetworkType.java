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

package com.github.chainmailstudios.astromine.common.network.type;

import com.github.chainmailstudios.astromine.common.block.transfer.TransferType;
import com.github.chainmailstudios.astromine.common.component.SidedComponentProvider;
import com.github.chainmailstudios.astromine.common.component.block.entity.BlockEntityTransferComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.FluidInventoryComponent;
import com.github.chainmailstudios.astromine.common.volume.fraction.Fraction;
import com.github.chainmailstudios.astromine.common.network.NetworkInstance;
import com.github.chainmailstudios.astromine.common.network.NetworkMember;
import com.github.chainmailstudios.astromine.common.network.NetworkMemberNode;
import com.github.chainmailstudios.astromine.common.network.type.base.NetworkType;
import com.github.chainmailstudios.astromine.common.registry.NetworkMemberRegistry;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.Property;

public class FluidNetworkType extends NetworkType {
	@Override
	public void tick(NetworkInstance instance) {
		List<FluidVolume> inputs = Lists.newArrayList();
		List<Tuple<FluidInventoryComponent, Direction>> outputs = Lists.newArrayList();

		for (NetworkMemberNode memberNode : instance.members) {
			BlockEntity blockEntity = instance.getWorld().getBlockEntity(memberNode.getBlockPos());
			NetworkMember networkMember = NetworkMemberRegistry.get(blockEntity);

			if (blockEntity instanceof SidedComponentProvider && networkMember.acceptsType(this)) {
				SidedComponentProvider provider = SidedComponentProvider.fromBlockEntity(blockEntity);

				FluidInventoryComponent fluidComponent = provider.getSidedComponent(memberNode.getDirection(), AstromineComponentTypes.FLUID_INVENTORY_COMPONENT);

				BlockEntityTransferComponent transferComponent = provider.getComponent(AstromineComponentTypes.BLOCK_ENTITY_TRANSFER_COMPONENT);

				before:
				if (fluidComponent != null && transferComponent != null) {
					Property<Direction> property = blockEntity.getBlockState().hasProperty(HorizontalDirectionalBlock.FACING) ? HorizontalDirectionalBlock.FACING : blockEntity.getBlockState().hasProperty(DirectionalBlock.FACING) ? DirectionalBlock.FACING : null;

					if (!blockEntity.getBlockState().hasProperty(property))
						break before;

					TransferType type = transferComponent.get(AstromineComponentTypes.FLUID_INVENTORY_COMPONENT).get(memberNode.getDirection());

					if (!type.isDisabled()) {
						if (type.canExtract() || networkMember.isProvider(this)) {
							inputs.addAll(fluidComponent.getExtractableContentsMatching(memberNode.getDirection(), (volume -> !volume.isEmpty())));
						}

						if (type.canInsert() || networkMember.isRequester(this)) {
							outputs.add(new Tuple<>(fluidComponent, memberNode.getDirection()));
						}
					}
				}
			}
		}

		for (FluidVolume input : inputs) {
			for (Tuple<FluidInventoryComponent, Direction> outputPair : outputs) {
				FluidInventoryComponent component = outputPair.getA();
				Direction direction = outputPair.getB();

				component.getContents().forEach((slot, output) -> {
					if (!input.isEmpty() && !output.isFull() && (output.canAccept(input.getFluid()))) {
						if (component.canInsert(direction, input, slot)) {
							output.moveFrom(input, Fraction.bottle());
						}
					}
				});
			}
		}
	}
}
