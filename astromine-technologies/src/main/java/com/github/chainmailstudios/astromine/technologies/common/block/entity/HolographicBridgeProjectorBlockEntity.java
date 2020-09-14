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

package com.github.chainmailstudios.astromine.technologies.common.block.entity;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import com.github.chainmailstudios.astromine.common.component.world.WorldBridgeComponent;
import com.github.chainmailstudios.astromine.common.utilities.LineUtilities;
import com.github.chainmailstudios.astromine.common.utilities.VectorUtilities;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesBlockEntityTypes;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesBlocks;
import com.github.vini2003.blade.common.miscellaneous.Color;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HolographicBridgeProjectorBlockEntity extends TileEntity implements ITickableTileEntity {
	public ArrayList<Vector3f> segments = null;

	public Color color = Color.of("0x7e80cad4");

	private HolographicBridgeProjectorBlockEntity child = null;
	private HolographicBridgeProjectorBlockEntity parent = null;

	private BlockPos childPosition = null;

	private boolean hasCheckedChild = false;

	public HolographicBridgeProjectorBlockEntity() {
		super(AstromineTechnologiesBlockEntityTypes.HOLOGRAPHIC_BRIDGE);
	}

	public boolean hasChild() {
		return this.child != null;
	}

	@Override
	public void tick() {
		if (this.level == null || this.level.isClientSide)
			return;
		if (!this.hasCheckedChild && this.childPosition != null) {
			TileEntity childEntity = this.level.getBlockEntity(this.childPosition);

			if (childEntity instanceof HolographicBridgeProjectorBlockEntity) {
				this.child = (HolographicBridgeProjectorBlockEntity) childEntity;
				this.hasCheckedChild = true;

				this.buildBridge();
			} else if (childEntity != null) {
				this.hasCheckedChild = true;
			}
		}
	}

	public void buildBridge() {
		if (this.child == null || this.level == null) {
			return;
		}

		BlockPos bCP = this.getChild().getBlockPos();
		BlockPos bOP = this.getBlockPos();

		BlockPos nCP = bCP;

		Direction cD = this.getChild().getBlockState().getValue(HorizontalBlock.FACING);

		if (cD == Direction.EAST) {
			nCP = nCP.offset(1, 0, 0);
		} else if (cD == Direction.SOUTH) {
			nCP = nCP.offset(0, 0, 1);
		}

		int distance = (int) Math.sqrt(this.getBlockPos().distSqr(this.getChild().getBlockPos()));

		if (distance == 0) {
			return;
		}

		this.segments = (ArrayList<Vector3f>) LineUtilities.getBresenhamSegments(VectorUtilities.toVector3f(bOP.relative(Direction.UP)), VectorUtilities.toVector3f(nCP.relative(Direction.UP)), 32);

		for (Vector3f v : this.segments) {
			BlockPos nP = new BlockPos(v.x(), v.y(), v.z());

			if ((nP.getX() != bCP.getX() && nP.getX() != bOP.getX()) || (nP.getZ() != bCP.getZ() && nP.getZ() != bOP.getZ())) {
				this.level.setBlockAndUpdate(nP, AstromineTechnologiesBlocks.HOLOGRAPHIC_BRIDGE_INVISIBLE_BLOCK.defaultBlockState());
			}

			ComponentProvider componentProvider = ComponentProvider.fromWorld(level);

			WorldBridgeComponent bridgeComponent = componentProvider.getComponent(AstromineComponentTypes.WORLD_BRIDGE_COMPONENT);

			bridgeComponent.add(nP, new Vector3i((v.x() - (int) v.x()) * 16f, (v.y() - (int) v.y()) * 16f, (v.z() - (int) v.z()) * 16f));
		}
	}

	public HolographicBridgeProjectorBlockEntity getChild() {
		return this.child;
	}

	public void setChild(HolographicBridgeProjectorBlockEntity child) {
		this.child = child;

		if (this.child != null) {
			this.child.setParent(this);
			this.child.setChild(null);
		}

		this.setChanged();
	}

	public HolographicBridgeProjectorBlockEntity getParent() {
		return parent;
	}

	public void setParent(HolographicBridgeProjectorBlockEntity parent) {
		this.parent = parent;
		this.setChild(null);

		this.setChanged();
	}

	@Override
	public double getViewDistance() {
		return Math.pow(2, 15);
	}

	@Override
	public void setRemoved() {
		if (this.child != null) {
			if (this.parent != null) {
				this.parent.destroyBridge();
			}

			this.destroyBridge();

			this.setChild(null);
		}

		super.setRemoved();
	}

	public void destroyBridge() {
		if (this.segments != null && this.level != null) {
			ComponentProvider componentProvider = ComponentProvider.fromWorld(level);

			WorldBridgeComponent bridgeComponent = componentProvider.getComponent(AstromineComponentTypes.WORLD_BRIDGE_COMPONENT);

			for (Vector3f vec : this.segments) {
				BlockPos pos = new BlockPos(vec.x(), vec.y(), vec.z());

				bridgeComponent.remove(pos);

				this.level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			}
		}
	}

	@Override
	public void load(BlockState state, @NotNull CompoundNBT tag) {
		if (tag.contains("child_position")) {
			this.childPosition = BlockPos.of(tag.getLong("child_position"));
		}

		super.load(state, tag);
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		if (this.child != null) {
			tag.putLong("child_position", this.child.getBlockPos().asLong());
		} else if (this.childPosition != null) {
			tag.putLong("child_position", this.childPosition.asLong());
		}

		return super.save(tag);
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		this.load(null, tag);

		this.destroyBridge();

		if (this.childPosition != null) {
			this.child = (HolographicBridgeProjectorBlockEntity) this.level.getBlockEntity(this.childPosition);
		}

		this.buildBridge();
	}
}
