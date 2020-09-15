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

package com.github.chainmailstudios.astromine.common.component.world;

import com.github.chainmailstudios.astromine.common.utilities.VoxelShapeUtilities;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3i;

import java.util.Map;
import java.util.Set;

public class WorldBridgeComponent {
	public final Long2ObjectArrayMap<Set<Vector3i>> entries = new Long2ObjectArrayMap<>();

	public void add(BlockPos pos, Vector3i vec) {
		add(pos.asLong(), vec);
	}

	public void add(long pos, Vector3i top) {
		entries.computeIfAbsent(pos, (k) -> Sets.newHashSet());
		entries.get(pos).add(top);
	}

	public void remove(BlockPos pos) {
		remove(pos.asLong());
	}

	public void remove(long pos) {
		entries.remove(pos);
	}

	public Set<Vector3i> get(BlockPos pos) {
		return get(pos.asLong());
	}

	public Set<Vector3i> get(long pos) {
		return entries.getOrDefault(pos, Sets.newHashSet());
	}

	public VoxelShape getShape(BlockPos pos) {
		return getShape(pos.asLong());
	}

	public VoxelShape getShape(long pos) {
		Set<Vector3i> vecs = get(pos);
		if (vecs == null)
			return VoxelShapes.block();
		else return getShape(vecs);
	}

	private VoxelShape getShape(Set<Vector3i> vecs) {
		VoxelShape shape = VoxelShapes.empty();

		boolean a = vecs.stream().allMatch(vec -> vec.getZ() == 0);
		boolean b = vecs.stream().allMatch(vec -> vec.getX() == 0);
		boolean c = false;
		boolean d = false;

		for (Vector3i vec : vecs) {
			if (!c && vec.getX() < 0)
				c = true;
			if (!d && vec.getZ() < 0)
				d = true;

			shape = VoxelShapes.or(shape, Block.box(Math.abs(vec.getX()), Math.abs(vec.getY()) - 1, Math.abs(vec.getZ()), b ? 16 : Math.abs(vec.getX() + 1), Math.abs(vec.getY()) + 1, a ? 16 : Math.abs(vec.getZ() + 1)));
		}

		if (c || d) {
			return VoxelShapeUtilities.rotate(Direction.Axis.Y, Math.toRadians(180), shape);
		}

		return shape;
	}

	public CompoundNBT toTag(CompoundNBT tag) {
		CompoundNBT dataTag = new CompoundNBT();

		int k = 0;

		for (Map.Entry<Long, Set<Vector3i>> entry : entries.entrySet()) {
			CompoundNBT pointTag = new CompoundNBT();
			CompoundNBT vecTag = new CompoundNBT();

			pointTag.putLong("pos", entry.getKey());

			int i = 0;

			for (Vector3i vec : entry.getValue()) {
				vecTag.putLong(String.valueOf(i), BlockPos.asLong(vec.getX(), vec.getY(), vec.getZ()));

				++i;
			}

			pointTag.put("vecs", vecTag);

			dataTag.put(String.valueOf(k), pointTag);

			++k;
		}

		tag.put("data", dataTag);

		return tag;
	}

	public void fromTag(CompoundNBT tag) {
		CompoundNBT dataTag = tag.getCompound("data");

		for (String key : dataTag.getAllKeys()) {
			CompoundNBT pointTag = dataTag.getCompound(key);
			CompoundNBT vecTag = pointTag.getCompound("vecs");

			long pos = pointTag.getLong("pos");

			for (String vecKey : vecTag.getAllKeys()) {
				add(pos, BlockPos.of(vecTag.getLong(vecKey)));
			}
		}
	}
}
