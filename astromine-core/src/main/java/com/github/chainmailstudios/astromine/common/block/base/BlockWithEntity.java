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

package com.github.chainmailstudios.astromine.common.block.base;

import com.github.chainmailstudios.astromine.common.item.base.EnergyVolumeItem;
import com.github.chainmailstudios.astromine.common.item.base.FluidVolumeItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BlockWithEntity extends Block {
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	protected BlockWithEntity(AbstractBlock.Properties settings) {
		super(settings);
	}

	public static void markActive(World world, BlockPos pos) {
		world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(ACTIVE, true));
	}

	public static void markInactive(World world, BlockPos pos) {
		world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(ACTIVE, false));
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (!world.isClientSide && (!(player.getItemInHand(hand).getItem() instanceof BucketItem) && !(player.getItemInHand(hand).getItem() instanceof EnergyVolumeItem) && !(player.getItemInHand(hand).getItem() instanceof FluidVolumeItem)) && hasScreenHandler()) {
			player.openMenu(state.getMenuProvider(world, pos));
			return ActionResultType.CONSUME;
		} else if (player.getItemInHand(hand).getItem() instanceof BucketItem) {
			return super.use(state, world, pos, player, hand, hit);
		} else {
			return ActionResultType.SUCCESS;
		}
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return createBlockEntity();
	}

	public abstract boolean hasScreenHandler();

	public abstract TileEntity createBlockEntity();

	public abstract Container createScreenHandler(BlockState state, World world, BlockPos pos, int syncId, PlayerInventory playerInventory, PlayerEntity player);

	public abstract void populateScreenHandlerBuffer(BlockState state, World world, BlockPos pos, ServerPlayerEntity player, PacketBuffer buffer);

	@Override
	public INamedContainerProvider getMenuProvider(BlockState state, World world, BlockPos pos) {
		return new ExtendedScreenHandlerFactory() {
			@Override
			public void writeScreenOpeningData(ServerPlayerEntity player, PacketBuffer buffer) {
				populateScreenHandlerBuffer(state, world, pos, player, buffer);
			}

			@Override
			public ITextComponent getDisplayName() {
				return new TranslationTextComponent(getDescriptionId());
			}

			@Override
			public Container createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
				return createScreenHandler(state, world, pos, syncId, playerInventory, player);
			}
		};
	}

	public boolean triggerEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		super.triggerEvent(state, world, pos, type, data);
		TileEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity != null && blockEntity.triggerEvent(type, data);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(ACTIVE);
		super.createBlockStateDefinition(builder);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).setValue(ACTIVE, false);
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(world, pos, state, placer, stack);

		TileEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null) {
			blockEntity.load(state, stack.getOrCreateTag());
			blockEntity.setPosition(pos);
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		List<ItemStack> stacks = super.getDrops(state, builder);
		TileEntity blockEntity = builder.getOptionalParameter(LootParameters.BLOCK_ENTITY);
		if (blockEntity != null && saveTagToDroppedItem()) {
			for (ItemStack drop : stacks) {
				if (drop.getItem() == asItem()) {
					CompoundNBT tag = blockEntity.save(drop.getOrCreateTag());
					tag.remove("x");
					tag.remove("y");
					tag.remove("z");
					drop.setTag(tag);
					break;
				}
			}
		}
		return stacks;
	}

	protected boolean saveTagToDroppedItem() {
		return true;
	}
}
