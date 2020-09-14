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

package com.github.chainmailstudios.astromine.technologies.common.item;

import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import com.github.chainmailstudios.astromine.registry.AstromineSoundEvents;
import com.github.chainmailstudios.astromine.technologies.common.block.HolographicBridgeProjectorBlock;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.HolographicBridgeProjectorBlockEntity;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesSoundEvents;

public class HolographicConnectorItem extends Item {
	public HolographicConnectorItem(Properties settings) {
		super(settings);
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		World world = context.getLevel();

		BlockPos position = context.getClickedPos();

		if (context.isSecondaryUseActive())
			return super.useOn(context);

		if (world.getBlockState(position).getBlock() instanceof HolographicBridgeProjectorBlock) {
			HolographicBridgeProjectorBlockEntity entity = (HolographicBridgeProjectorBlockEntity) world.getBlockEntity(position);

			Tuple<RegistryKey<World>, BlockPos> pair = readBlock(context.getItemInHand());
			if (pair == null || !pair.getA().location().equals(world.dimension().location())) {
				if (!world.isClientSide) {
					context.getPlayer().setItemInHand(context.getHand(), selectBlock(context.getItemInHand(), entity.getLevel().dimension(), entity.getBlockPos()));
				} else {
					context.getPlayer().displayClientMessage(new TranslationTextComponent("text.astromine.message.holographic_connector_select", toShortString(entity.getBlockPos())).withStyle(TextFormatting.BLUE), true);
					world.playSound(context.getPlayer(), context.getClickedPos(), AstromineTechnologiesSoundEvents.HOLOGRAPHIC_CONNECTOR_CLICK, SoundCategory.PLAYERS, 0.5f, 0.33f);
				}
			} else {
				TileEntity blockEntity = world.getBlockEntity(pair.getB());
				if (!(blockEntity instanceof HolographicBridgeProjectorBlockEntity)) {
					if (!world.isClientSide) {
						context.getPlayer().setItemInHand(context.getHand(), selectBlock(context.getItemInHand(), entity.getLevel().dimension(), entity.getBlockPos()));
					} else {
						context.getPlayer().displayClientMessage(new TranslationTextComponent("text.astromine.message.holographic_connector_select", toShortString(entity.getBlockPos())).withStyle(TextFormatting.BLUE), true);
						world.playSound(context.getPlayer(), context.getClickedPos(), AstromineTechnologiesSoundEvents.HOLOGRAPHIC_CONNECTOR_CLICK, SoundCategory.PLAYERS, 0.5f, 0.33f);
					}
					return ActionResultType.SUCCESS;
				}
				HolographicBridgeProjectorBlockEntity parent = (HolographicBridgeProjectorBlockEntity) blockEntity;

				BlockPos nP = entity.getBlockPos();
				BlockPos oP = parent.getBlockPos();

				if (parent.getBlockPos().getZ() < entity.getBlockPos().getZ() || parent.getBlockPos().getX() < entity.getBlockPos().getX()) {
					HolographicBridgeProjectorBlockEntity temporary = parent;
					parent = entity;
					entity = temporary;
				}

				if ((parent.getBlockPos().getX() != entity.getBlockPos().getX() && parent.getBlockPos().getZ() != entity.getBlockPos().getZ()) || oP.distSqr(nP) > 65536) {
					if (!world.isClientSide) {
						context.getPlayer().setItemInHand(context.getHand(), unselect(context.getItemInHand()));
					} else {
						context.getPlayer().displayClientMessage(new TranslationTextComponent("text.astromine.message.holographic_connection_failed", toShortString(parent.getBlockPos()), toShortString(entity.getBlockPos())).withStyle(TextFormatting.RED), true);
						world.playSound(context.getPlayer(), context.getClickedPos(), AstromineTechnologiesSoundEvents.HOLOGRAPHIC_CONNECTOR_CLICK, SoundCategory.PLAYERS, 0.5f, 0.33f);
					}
					return ActionResultType.SUCCESS;
				} else if (parent.getBlockState().getValue(HorizontalBlock.FACING).getOpposite() != entity.getBlockState().getValue(HorizontalBlock.FACING)) {
					if (!world.isClientSide) {
						context.getPlayer().setItemInHand(context.getHand(), unselect(context.getItemInHand()));
					} else {
						context.getPlayer().displayClientMessage(new TranslationTextComponent("text.astromine.message.holographic_connection_failed", toShortString(parent.getBlockPos()), toShortString(entity.getBlockPos())).withStyle(TextFormatting.RED), true);
						world.playSound(context.getPlayer(), context.getClickedPos(), AstromineTechnologiesSoundEvents.HOLOGRAPHIC_CONNECTOR_CLICK, SoundCategory.PLAYERS, 0.5f, 0.33f);
					}
					return ActionResultType.SUCCESS;
				}

				if (world.isClientSide) {
					context.getPlayer().displayClientMessage(new TranslationTextComponent("text.astromine.message.holographic_connection_successful", toShortString(parent.getBlockPos()), toShortString(entity.getBlockPos())).withStyle(TextFormatting.GREEN), true);
					world.playSound(context.getPlayer(), context.getClickedPos(), AstromineTechnologiesSoundEvents.HOLOGRAPHIC_CONNECTOR_CLICK, SoundCategory.PLAYERS, 0.5f, 0.33f);
				} else {
					parent.setChild(entity);
					entity.setParent(parent);

					if (parent.getParent() == entity.getParent()) {
						parent.setParent(null);
					}

					parent.buildBridge();
					parent.sync();
					context.getPlayer().setItemInHand(context.getHand(), unselect(context.getItemInHand()));
				}
			}
		} else {
			if (world.isClientSide) {
				context.getPlayer().displayClientMessage(new TranslationTextComponent("text.astromine.message.holographic_connection_clear").withStyle(TextFormatting.YELLOW), true);
				world.playSound(context.getPlayer(), context.getClickedPos(), AstromineTechnologiesSoundEvents.HOLOGRAPHIC_CONNECTOR_CLICK, SoundCategory.PLAYERS, 0.5f, 0.33f);
			} else {
				context.getPlayer().setItemInHand(context.getHand(), unselect(context.getItemInHand()));
			}
		}
		return ActionResultType.SUCCESS;
	}

	private ItemStack unselect(ItemStack stack) {
		stack = stack.copy();
		CompoundNBT tag = stack.getOrCreateTag();
		tag.remove("SelectedConnectorBlock");
		return stack;
	}

	private ItemStack selectBlock(ItemStack stack, RegistryKey<World> registryKey, BlockPos pos) {
		stack = stack.copy();
		CompoundNBT tag = stack.getOrCreateTag();
		tag.remove("SelectedConnectorBlock");
		tag.put("SelectedConnectorBlock", writePos(registryKey, pos));
		return stack;
	}

	public Tuple<RegistryKey<World>, BlockPos> readBlock(ItemStack stack) {
		CompoundNBT tag = stack.getTag();
		if (tag == null)
			return null;
		if (!tag.contains("SelectedConnectorBlock"))
			return null;
		return readPos(tag.getCompound("SelectedConnectorBlock"));
	}

	private CompoundNBT writePos(RegistryKey<World> registryKey, BlockPos pos) {
		CompoundNBT tag = new CompoundNBT();
		tag.putString("World", registryKey.location().toString());
		tag.putInt("X", pos.getX());
		tag.putInt("Y", pos.getY());
		tag.putInt("Z", pos.getZ());
		return tag;
	}

	private Tuple<RegistryKey<World>, BlockPos> readPos(CompoundNBT tag) {
		RegistryKey<World> registryKey = RegistryKey.create(Registry.DIMENSION_REGISTRY, ResourceLocation.tryParse(tag.getString("World")));
		int x = tag.getInt("X");
		int y = tag.getInt("Y");
		int z = tag.getInt("Z");
		return new Tuple<>(registryKey, new BlockPos(x, y, z));
	}

	public String toShortString(BlockPos pos) {
		return "" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
	}
}
