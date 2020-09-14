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

package com.github.chainmailstudios.astromine.discoveries.common.block.entity;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import com.github.chainmailstudios.astromine.common.component.inventory.ItemInventoryComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.SimpleItemInventoryComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.compatibility.ItemInventoryFromInventoryComponent;
import com.github.chainmailstudios.astromine.discoveries.registry.AstromineDiscoveriesBlockEntityTypes;

public class AltarPedestalBlockEntity extends TileEntity implements ItemInventoryFromInventoryComponent, ITickableTileEntity, BlockEntityClientSerializable {
	public BlockPos parent;
	private int spinAge;
	private int lastSpinAddition;
	private int yAge;
	private ItemInventoryComponent inventory = new SimpleItemInventoryComponent(1).withListener(inventory -> {
		if (hasWorld() && !world.isClient) {
			sync();
			world.playSound(null, pos, SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1, 1);
		}
	});

	public AltarPedestalBlockEntity() {
		super(AstromineDiscoveriesBlockEntityTypes.ALTAR_PEDESTAL);
	}

	@Override
	public ItemInventoryComponent getItemComponent() {
		return inventory;
	}

	@Override
	public int getMaxCountPerStack() {
		return 1;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		sync();
	}

	@Override
	public void tick() {
		lastSpinAddition = 1;
		spinAge++;
		yAge++;

		if (parent != null) {
			AltarBlockEntity blockEntity = (AltarBlockEntity) level.getBlockEntity(parent);
			spinAge += blockEntity.craftingTicks / 5;
			lastSpinAddition += blockEntity.craftingTicks / 5;

			int velX = worldPosition.getX() - parent.getX();
			int velY = worldPosition.getY() - parent.getY();
			int velZ = worldPosition.getZ() - parent.getZ();
			level.addParticle(ParticleTypes.ENCHANT, parent.getX() + 0.5, parent.getY() + 1.8, parent.getZ() + 0.5, velX, velY - 1.3, velZ);
		}
	}

	public int getSpinAge() {
		return spinAge;
	}

	public int getLastSpinAddition() {
		return lastSpinAddition;
	}

	public int getYAge() {
		return yAge;
	}

	@Override
	public void fromClientTag(CompoundNBT compoundTag) {
		load(null, compoundTag);
	}

	@Override
	public CompoundNBT toClientTag(CompoundNBT compoundTag) {
		return save(compoundTag);
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);
		inventory.fromTag(tag);
		if (tag.contains("parent"))
			parent = BlockPos.of(tag.getLong("parent"));
		else parent = null;
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		inventory.toTag(tag);
		if (parent != null)
			tag.putLong("parent", parent.asLong());
		return super.save(tag);
	}

	public void onRemove() {
		if (parent != null) {
			AltarBlockEntity blockEntity = (AltarBlockEntity) level.getBlockEntity(parent);
			blockEntity.onRemove();
		}
	}
}
