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

package com.github.chainmailstudios.astromine.technologies.common.block;

import com.github.chainmailstudios.astromine.common.utilities.VoxelShapeUtilities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import javax.annotation.Nullable;

public class AirlockBlock extends Block implements IWaterLoggable {
	public static final DirectionProperty FACING = HorizontalBlock.FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final BooleanProperty LEFT = BooleanProperty.create("left");
	public static final BooleanProperty RIGHT = BooleanProperty.create("right");
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	public static final VoxelShape LEFT_SHAPE = Block.box(0, 0, 6, 1, 16, 10);
	public static final VoxelShape RIGHT_SHAPE = Block.box(15, 0, 6, 16, 16, 10);
	public static final VoxelShape DOOR_SHAPE = Block.box(1, 0, 7, 15, 16, 9);
	public static final VoxelShape BOTTOM_SHAPE = Block.box(0, 0, 5, 16, 1, 11);
	public static final VoxelShape TOP_SHAPE = Block.box(0, 15, 6, 16, 16, 10);

	public AirlockBlock(Properties settings) {
		super(settings);

		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(HALF, DoubleBlockHalf.LOWER).setValue(LEFT, false).setValue(RIGHT, false).setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) ? Fluids.WATER.defaultFluidState() : super.getFluidState(state);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		Direction facing = state.getValue(FACING);
		VoxelShape shape = VoxelShapes.or(VoxelShapes.empty(), VoxelShapeUtilities.rotateDirection(facing, DOOR_SHAPE));

		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			shape = VoxelShapes.or(shape, VoxelShapeUtilities.rotateDirection(facing, BOTTOM_SHAPE));
		} else {
			shape = VoxelShapes.or(shape, VoxelShapeUtilities.rotateDirection(facing, TOP_SHAPE));
		}

		if (!state.getValue(LEFT)) {
			shape = VoxelShapes.or(shape, VoxelShapeUtilities.rotateDirection(facing, LEFT_SHAPE));
		}

		if (!state.getValue(RIGHT)) {
			shape = VoxelShapes.or(shape, VoxelShapeUtilities.rotateDirection(facing, RIGHT_SHAPE));
		}

		return shape;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		VoxelShape shape = VoxelShapes.empty();
		Direction facing = state.getValue(FACING);

		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			shape = VoxelShapes.or(shape, VoxelShapeUtilities.rotateDirection(facing, BOTTOM_SHAPE));
		} else {
			shape = VoxelShapes.or(shape, VoxelShapeUtilities.rotateDirection(facing, TOP_SHAPE));
		}

		if (!state.getValue(LEFT)) {
			shape = VoxelShapes.or(shape, VoxelShapeUtilities.rotateDirection(facing, LEFT_SHAPE));
		}

		if (!state.getValue(RIGHT)) {
			shape = VoxelShapes.or(shape, VoxelShapeUtilities.rotateDirection(facing, RIGHT_SHAPE));
		}

		if (!state.getValue(POWERED)) {
			shape = VoxelShapes.or(shape, VoxelShapeUtilities.rotateDirection(facing, DOOR_SHAPE));
		}

		return shape;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState newState, IWorld world, BlockPos pos, BlockPos posFrom) {
		DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
		Direction facing = state.getValue(FACING);
		BlockState changedState = state;

		if (direction == facing.getClockWise() || direction == facing.getCounterClockWise()) {
			if (newState.is(this) && (newState.getValue(FACING) == facing || newState.getValue(FACING) == facing.getOpposite())) {
				if (direction == facing.getCounterClockWise()) {
					changedState = changedState.setValue(LEFT, true);
				} else if (direction == facing.getClockWise()) {
					changedState = changedState.setValue(RIGHT, true);
				}
			} else if (direction == facing.getCounterClockWise()) {
				changedState = changedState.setValue(LEFT, false);
			} else if (direction == facing.getClockWise()) {
				changedState = changedState.setValue(RIGHT, false);
			}
		}

		if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
			return newState.is(this) && newState.getValue(HALF) != doubleBlockHalf ? changedState.setValue(FACING, newState.getValue(FACING)).setValue(POWERED, newState.getValue(POWERED)) : Blocks.AIR.defaultBlockState();
		} else {
			return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !changedState.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : changedState;
		}
	}

	@Override
	public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isClientSide && player.isCreative()) {
			DoublePlantBlock.preventCreativeDropFromBottomPart(world, pos, state, player);
		}

		super.playerWillDestroy(world, pos, state, player);
	}

	@Override
	public boolean isPathfindable(BlockState state, IBlockReader world, BlockPos pos, PathType type) {
		switch (type) {
			case LAND:
			case AIR:
				return state.getValue(POWERED);
			default:
				return false;
		}
	}

	private int getOpenSoundEventId() {
		return 1011;
	}

	private int getCloseSoundEventId() {
		return 1005;
	}

	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		BlockPos blockPos = ctx.getClickedPos();
		if (blockPos.getY() < 255 && ctx.getLevel().getBlockState(blockPos.above()).canBeReplaced(ctx)) {
			World world = ctx.getLevel();
			boolean bl = world.hasNeighborSignal(blockPos) || world.hasNeighborSignal(blockPos.above());
			return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection()).setValue(POWERED, bl).setValue(HALF, DoubleBlockHalf.LOWER).setValue(BlockStateProperties.WATERLOGGED, world.getBlockState(blockPos).getBlock() == Blocks.WATER);
		} else {
			return null;
		}
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		world.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
	}

	public boolean method_30841(BlockState blockState) {
		return blockState.getValue(POWERED);
	}

	public void setOpen(World world, BlockState blockState, BlockPos blockPos, boolean bl) {
		if (blockState.is(this) && blockState.getValue(POWERED) != bl) {
			world.setBlock(blockPos, blockState.setValue(POWERED, bl), 10);
			this.playOpenCloseSound(world, blockPos, bl);
		}
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		boolean bl = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.relative(state.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
		if (block != this && bl != state.getValue(POWERED)) {
			if (bl != state.getValue(POWERED)) {
				this.playOpenCloseSound(world, pos, bl);
			}

			world.setBlock(pos, state.setValue(POWERED, bl), 2);
		}
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		BlockPos blockPos = pos.below();
		BlockState blockState = world.getBlockState(blockPos);
		return state.getValue(HALF) == DoubleBlockHalf.LOWER ? blockState.isFaceSturdy(world, blockPos, Direction.UP) : blockState.is(this);
	}

	private void playOpenCloseSound(World world, BlockPos pos, boolean open) {
		world.levelEvent(null, open ? this.getCloseSoundEventId() : this.getOpenSoundEventId(), pos, 0);
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.DESTROY;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return mirror == Mirror.NONE ? state : state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public long getSeed(BlockState state, BlockPos pos) {
		return MathHelper.getSeed(pos.getX(), pos.below(state.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(HALF, FACING, POWERED, LEFT, RIGHT, BlockStateProperties.WATERLOGGED);
	}
}
