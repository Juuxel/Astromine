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

import com.github.chainmailstudios.astromine.common.component.world.WorldNetworkComponent;
import com.github.chainmailstudios.astromine.common.network.NetworkMember;
import com.github.chainmailstudios.astromine.common.network.NetworkTracer;
import com.github.chainmailstudios.astromine.common.network.type.base.NetworkType;
import com.github.chainmailstudios.astromine.common.registry.NetworkMemberRegistry;
import com.github.chainmailstudios.astromine.common.utilities.capability.block.CableWrenchable;
import com.github.chainmailstudios.astromine.common.utilities.data.position.WorldPos;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class CableBlock extends Block implements IWaterLoggable, CableWrenchable {
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	public static final BooleanProperty WEST = BooleanProperty.create("west");
	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final BooleanProperty DOWN = BooleanProperty.create("down");

	public static final Map<Direction, BooleanProperty> PROPERTIES = new HashMap<Direction, BooleanProperty>() {
		{
			put(Direction.EAST, EAST);
			put(Direction.WEST, WEST);
			put(Direction.NORTH, NORTH);
			put(Direction.SOUTH, SOUTH);
			put(Direction.UP, UP);
			put(Direction.DOWN, DOWN);
		}
	};

	public static final Map<BooleanProperty, VoxelShape> SHAPE_MAP = new HashMap<BooleanProperty, VoxelShape>() {
		{
			put(UP, Block.box(6D, 10D, 6D, 10D, 16D, 10D));
			put(DOWN, Block.box(6D, 0D, 6D, 10D, 6D, 10D));
			put(NORTH, Block.box(6D, 6D, 0D, 10D, 10D, 6D));
			put(SOUTH, Block.box(6D, 6D, 10D, 10D, 10D, 16D));
			put(EAST, Block.box(10D, 6D, 6D, 16D, 10D, 10D));
			put(WEST, Block.box(0D, 6D, 6D, 6D, 10D, 10D));
		}
	};

	protected static final Map<Integer, VoxelShape> SHAPE_CACHE = new HashMap<>();
	protected static final VoxelShape CENTER_SHAPE = Block.box(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D);

	public CableBlock(AbstractBlock.Properties settings) {
		super(settings);

		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
	}

	public abstract <T extends NetworkType> T getNetworkType();

	@Override
	public FluidState getFluidState(BlockState state) {
		return (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) ? Fluids.WATER.defaultFluidState() : super.getFluidState(state);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getBlockState(context.getClickedPos()).getBlock() == Blocks.WATER);
	}

	@Override
	public void setPlacedBy(World world, BlockPos position, BlockState stateA, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(world, position, stateA, placer, stack);

		NetworkTracer.Tracer.INSTANCE.trace(getNetworkType(), WorldPos.of(world, position));

		NetworkTracer.Modeller modeller = new NetworkTracer.Modeller();
		modeller.scanNeighbours(getNetworkType(), position, world);

		world.setBlockAndUpdate(position, modeller.applyToBlockState(stateA));

		for (Direction direction : Direction.values()) {
			BlockPos offsetPos = position.relative(direction);
			WorldPos offsetBlock = WorldPos.of(world, offsetPos);

			if (!(offsetBlock.getBlock() instanceof CableBlock))
				continue;
			NetworkMember member = NetworkMemberRegistry.get(offsetBlock);
			if (member.acceptsType(getNetworkType()))
				continue;

			NetworkTracer.Modeller offsetModeller = new NetworkTracer.Modeller();
			offsetModeller.scanNeighbours(((CableBlock) offsetBlock.getBlock()).getNetworkType(), offsetPos, world);

			world.setBlockAndUpdate(offsetPos, offsetModeller.applyToBlockState(world.getBlockState(offsetPos)));
		}
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos position, BlockState newState, boolean moved) {
		super.onRemove(state, world, position, newState, moved);

		if (state.getBlock() == newState.getBlock())
			return;

		ComponentProvider provider = ComponentProvider.fromWorld(world);

		WorldNetworkComponent networkComponent = provider.getComponent(AstromineComponentTypes.WORLD_NETWORK_COMPONENT);

		networkComponent.removeInstance(networkComponent.getInstance(getNetworkType(), position));

		for (Direction directionA : Direction.values()) {
			BlockPos offsetPos = position.relative(directionA);
			Block offsetBlock = world.getBlockState(offsetPos).getBlock();

			if (!(offsetBlock instanceof CableBlock))
				continue;
			if (((CableBlock) offsetBlock).getNetworkType() != getNetworkType())
				continue;

			NetworkTracer.Tracer.INSTANCE.trace(getNetworkType(), WorldPos.of(world, offsetPos));

			NetworkTracer.Modeller modeller = new NetworkTracer.Modeller();
			modeller.scanNeighbours(getNetworkType(), offsetPos, world);

			world.setBlockAndUpdate(offsetPos, modeller.applyToBlockState(world.getBlockState(offsetPos)));
		}
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos position, Block block, BlockPos neighborPosition, boolean moved) {
		super.neighborChanged(state, world, position, block, neighborPosition, moved);

		ComponentProvider provider = ComponentProvider.fromWorld(world);

		WorldNetworkComponent networkComponent = provider.getComponent(AstromineComponentTypes.WORLD_NETWORK_COMPONENT);

		networkComponent.removeInstance(networkComponent.getInstance(getNetworkType(), position));
		NetworkTracer.Tracer.INSTANCE.trace(getNetworkType(), WorldPos.of(world, position));

		NetworkTracer.Modeller modeller = new NetworkTracer.Modeller();
		modeller.scanNeighbours(getNetworkType(), position, world);

		world.setBlockAndUpdate(position, modeller.applyToBlockState(world.getBlockState(position)));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(EAST, WEST, NORTH, SOUTH, UP, DOWN, BlockStateProperties.WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(BlockState blockState, IBlockReader world, BlockPos position, ISelectionContext entityContext) {
		VoxelShape returnShape = CENTER_SHAPE;
		NetworkTracer.Modeller modeller = new NetworkTracer.Modeller();
		modeller.scanBlockState(blockState);
		returnShape = modeller.applyToVoxelShape(returnShape);
		return returnShape;
	}
}
