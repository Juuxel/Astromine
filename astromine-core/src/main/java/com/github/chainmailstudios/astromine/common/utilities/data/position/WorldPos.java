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

package com.github.chainmailstudios.astromine.common.utilities.data.position;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class WorldPos {
	@NotNull
	private final Level world;
	@NotNull
	private final BlockPos pos;
	private BlockState blockState;

	private WorldPos(Level world, BlockPos pos) {
		this.world = Objects.requireNonNull(world);
		this.pos = Objects.requireNonNull(pos);
	}

	public static WorldPos of(Level world, BlockPos pos) {
		return new WorldPos(world, pos);
	}

	public WorldPos offset(Direction direction) {
		return of(world, getBlockPos().relative(direction));
	}

	public BlockState getBlockState() {
		if (blockState == null) {
			this.blockState = world.getBlockState(pos);
		}

		return blockState;
	}

	public void setBlockState(BlockState state) {
		this.blockState = null;

		this.world.setBlockAndUpdate(pos, state);
	}

	public Block getBlock() {
		return getBlockState().getBlock();
	}

	@NotNull
	public Level getWorld() {
		return world;
	}

	@NotNull
	public BlockPos getBlockPos() {
		return pos;
	}

	public int getX() {
		return pos.getX();
	}

	public int getY() {
		return pos.getY();
	}

	public int getZ() {
		return pos.getZ();
	}

	@Nullable
	public BlockEntity getBlockEntity() {
		return world.getBlockEntity(pos);
	}
}
