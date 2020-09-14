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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import org.jetbrains.annotations.Nullable;

public abstract class HorizontalFacingBlockWithEntity extends BlockWithEntity {
	public HorizontalFacingBlockWithEntity(Properties settings) {
		super(settings);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		DirectionProperty directionProperty = getDirectionProperty();
		if (directionProperty != null) {
			builder.add(directionProperty);
		}
		super.createBlockStateDefinition(builder);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		DirectionProperty directionProperty = getDirectionProperty();
		if (directionProperty != null) {
			return super.getStateForPlacement(context).setValue(getDirectionProperty(), context.getHorizontalDirection().getOpposite());
		}
		return super.getStateForPlacement(context);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		DirectionProperty directionProperty = getDirectionProperty();
		if (directionProperty != null) {
			return state.setValue(getDirectionProperty(), rotation.rotate(state.getValue(getDirectionProperty())));
		}
		return super.rotate(state, rotation);
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		DirectionProperty directionProperty = getDirectionProperty();
		if (directionProperty != null) {
			return state.rotate(mirror.getRotation(state.getValue(getDirectionProperty())));
		}
		return super.mirror(state, mirror);
	}

	@Nullable
	public DirectionProperty getDirectionProperty() {
		return BlockStateProperties.HORIZONTAL_FACING;
	}
}
