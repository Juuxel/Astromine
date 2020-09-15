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
import com.github.chainmailstudios.astromine.common.component.block.entity.BlockEntityTransferComponent;
import com.github.chainmailstudios.astromine.common.network.NetworkInstance;
import com.github.chainmailstudios.astromine.common.network.NetworkMember;
import com.github.chainmailstudios.astromine.common.network.NetworkMemberNode;
import com.github.chainmailstudios.astromine.common.network.type.base.NetworkType;
import com.github.chainmailstudios.astromine.common.registry.NetworkMemberRegistry;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import com.github.chainmailstudios.astromine.common.volume.fraction.Fraction;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;
import com.google.common.collect.Lists;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

public class FluidNetworkType extends NetworkType {
	@Override
	public void tick(World world, NetworkInstance instance) {
		List<FluidVolume> inputs = Lists.newArrayList();
		List<Tuple<IFluidHandler, Direction>> outputs = Lists.newArrayList();

		for (NetworkMemberNode memberNode : instance.members) {
			TileEntity blockEntity = instance.getWorld().getBlockEntity(memberNode.getBlockPos());
			NetworkMember networkMember = NetworkMemberRegistry.get(blockEntity);

			if (networkMember.acceptsType(this)) {
				IFluidHandler fluidComponent = blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, memberNode.getDirection()).orElse(null);
				if (fluidComponent != null) {
					BlockEntityTransferComponent transferComponent = blockEntity.getCapability(AstromineComponentTypes.BLOCK_ENTITY_TRANSFER_COMPONENT).orElse(null);

					before:
					if (fluidComponent != null && transferComponent != null) {
						Property<Direction> property = blockEntity.getBlockState().hasProperty(HorizontalBlock.FACING) ? HorizontalBlock.FACING : blockEntity.getBlockState().hasProperty(DirectionalBlock.FACING) ? DirectionalBlock.FACING : null;

						if (!blockEntity.getBlockState().hasProperty(property))
							break before;

						TransferType type = transferComponent.get(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).get(memberNode.getDirection());

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
		}

		for (FluidVolume input : inputs) {
			for (Tuple<IFluidHandler, Direction> outputPair : outputs) {
				IFluidHandler component = outputPair.getA();
				Direction direction = outputPair.getB();

				for (int i = 0; i < component.getTanks(); i++) {
					FluidStack output = component.getFluidInTank(i);
					if (!input.isEmpty() && !output.isFull() && (output.getFluid().isSame(input.getFluid()))) {
						if (component.canInsert(direction, input, slot)) {
							output.moveFrom(input, Fraction.bottle());
						}
					}
				}
			}
		}
	}
}
