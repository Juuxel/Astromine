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

import com.github.chainmailstudios.astromine.client.cca.ClientAtmosphereManager;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import com.github.chainmailstudios.astromine.common.volume.fraction.Fraction;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;
import com.github.chainmailstudios.astromine.registry.AstromineConfig;
import com.google.common.collect.Lists;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import nerdhub.cardinal.components.api.component.extension.CopyableComponent;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkAtmosphereComponent implements CopyableComponent, ITickableTileEntity {
	private final List<Direction> directions = Lists.newArrayList(Direction.values());

	private final Map<BlockPos, FluidVolume> volumes = new ConcurrentHashMap<>();

	private final World world;
	private final IChunk chunk;

	public ChunkAtmosphereComponent(World world, IChunk chunk) {
		this.world = world;
		this.chunk = chunk;
	}

	public World getWorld() {
		return world;
	}

	public IChunk getChunk() {
		return chunk;
	}

	public Map<BlockPos, FluidVolume> getVolumes() {
		return volumes;
	}

	public FluidVolume get(BlockPos position) {
		return volumes.getOrDefault(position, FluidVolume.empty());
	}

	public void add(BlockPos blockPos, FluidVolume volume) {
		volumes.put(blockPos, volume);

		if (!world.isClientSide) {
			world.players().forEach((player) -> {
				ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ClientAtmosphereManager.GAS_ADDED, ClientAtmosphereManager.ofGasAdded(blockPos, volume));
			});
		}
	}

	public void remove(BlockPos blockPos) {
		volumes.remove(blockPos);

		if (!world.isClientSide) {
			world.players().forEach((player) -> {
				ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ClientAtmosphereManager.GAS_REMOVED, ClientAtmosphereManager.ofGasRemoved(blockPos));
			});
		}
	}

	@Override
	public void tick() {
		for (Map.Entry<BlockPos, FluidVolume> pair : volumes.entrySet()) {
			BlockPos centerPos = pair.getKey();

			FluidVolume centerVolume = pair.getValue();

			centerVolume.minus(Fraction.of(AstromineConfig.get().gasDecayNumerator, AstromineConfig.get().gasDecayDenominator));

			if (centerVolume.isEmpty()) {
				remove(centerPos);
			}

			Collections.shuffle(directions);

			for (Direction direction : directions) {
				BlockPos sidePos = centerPos.relative(direction);
				if (isInChunk(sidePos)) {
					FluidVolume sideVolume = get(sidePos);

					if (!(Registry.BLOCK.getKey(world.getBlockState(sidePos).getBlock()).toString().equals("astromine:airlock") && !world.getBlockState(sidePos).getValue(BlockStateProperties.POWERED)) && (world.getBlockState(sidePos).isAir() || !world.getBlockState(sidePos).isFaceSturdy(world, sidePos, direction.getOpposite())) && (world.getBlockState(centerPos).isAir() || !world.getBlockState(centerPos).isFaceSturdy(world, centerPos, direction)) && (sideVolume.isEmpty() || sideVolume.getFluid() == centerVolume.getFluid()) && (centerVolume.hasStored(Fraction.bottle()) || !world.isEmptyBlock(centerPos) && world.getBlockState(sidePos).isSolidRender(world, centerPos)) && sideVolume.smallerThan(centerVolume.getAmount())) {
						if (world.isEmptyBlock(centerPos)) {
							centerVolume.add(sideVolume, Fraction.bottle());
						} else if (!world.getBlockState(centerPos).isFaceSturdy(world, centerPos, direction)) {
							centerVolume.add(sideVolume, Fraction.bottle());
						} else {
							centerVolume.add(sideVolume, centerVolume.getAmount());
						}

						add(sidePos, sideVolume);
					}
				} else {
					ChunkPos neighborPos = getNeighborFromPos(sidePos);
					ComponentProvider provider = ComponentProvider.fromChunk(world.getChunk(neighborPos.x, neighborPos.z));
					ChunkAtmosphereComponent chunkAtmosphereComponent = provider.getComponent(AstromineComponentTypes.CHUNK_ATMOSPHERE_COMPONENT);

					FluidVolume sideVolume = chunkAtmosphereComponent.get(sidePos);

					if (!(Registry.BLOCK.getKey(world.getBlockState(sidePos).getBlock()).toString().equals("astromine:airlock") && !world.getBlockState(sidePos).getValue(BlockStateProperties.POWERED)) && (world.getBlockState(sidePos).isAir() || !world.getBlockState(sidePos).isFaceSturdy(world, sidePos, direction.getOpposite())) && (world.getBlockState(centerPos).isAir() || !world.getBlockState(centerPos).isFaceSturdy(world, centerPos, direction)) && (sideVolume.isEmpty() || sideVolume.getFluid() == centerVolume.getFluid()) && (centerVolume.hasStored(Fraction.bottle()) || !world.isEmptyBlock(centerPos) && world.getBlockState(sidePos).isSolidRender(world, centerPos)) && sideVolume.smallerThan(centerVolume.getAmount())) {
						// Keeping these here just in case I need them for debugging in the future.
						// AstromineCommon.LOGGER.info("Step 1: Moving from ChunkPos(" + chunk.getPos().x + "," + chunk.getPos().z + ") to ChunkPos(" + neighborPos.x + "," + neighborPos.z + ")");
						// AstromineCommon.LOGGER.info("Step 2: Moving from " + centerPos + " to " + sidePos);

						if (world.isEmptyBlock(centerPos)) {
							centerVolume.add(sideVolume, Fraction.bottle());
						} else if (!world.getBlockState(centerPos).isFaceSturdy(world, centerPos, direction)) {
							centerVolume.add(sideVolume, Fraction.bottle());
						} else {
							centerVolume.add(sideVolume, centerVolume.getAmount());
						}

						chunkAtmosphereComponent.add(sidePos, sideVolume);
					}
				}
			}
		}
	}

	public boolean isInChunk(BlockPos pos) {
		return isInChunk(chunk.getPos(), pos);
	}

	public ChunkPos getNeighborFromPos(BlockPos pos) {
		return getNeighborFromPos(chunk.getPos(), pos);
	}

	public static boolean isInChunk(ChunkPos chunkPos, BlockPos pos) {
		return pos.getX() >= chunkPos.getMinBlockX() && pos.getX() <= chunkPos.getMaxBlockX() && pos.getZ() >= chunkPos.getMinBlockZ() && pos.getZ() <= chunkPos.getMaxBlockZ();
	}

	public static ChunkPos getNeighborFromPos(ChunkPos chunkPos, BlockPos pos) {
		if (pos.getX() < chunkPos.getMinBlockX()) {
			return new ChunkPos(chunkPos.x - 1, chunkPos.z);
		} else if (pos.getX() > chunkPos.getMaxBlockX()) {
			return new ChunkPos(chunkPos.x + 1, chunkPos.z);
		} else if (pos.getZ() < chunkPos.getMinBlockZ()) {
			return new ChunkPos(chunkPos.x, chunkPos.z - 1);
		} else if (pos.getZ() > chunkPos.getMaxBlockZ()) {
			return new ChunkPos(chunkPos.x, chunkPos.z + 1);
		}
		return chunkPos;
	}

	@Override
	public CompoundNBT toTag(CompoundNBT tag) {
		CompoundNBT dataTag = new CompoundNBT();

		int i = 0;

		for (Map.Entry<BlockPos, FluidVolume> entry : volumes.entrySet()) {
			CompoundNBT pointTag = new CompoundNBT();
			pointTag.putLong("pos", entry.getKey().asLong());
			pointTag.put("volume", entry.getValue().toTag());

			dataTag.put(String.valueOf(i), pointTag);
			++i;
		}

		tag.put("data", dataTag);

		return tag;
	}

	@Override
	public void fromTag(CompoundNBT tag) {
		CompoundNBT dataTag = tag.getCompound("data");

		for (String key : dataTag.getAllKeys()) {
			CompoundNBT pointTag = dataTag.getCompound(key);

			volumes.put(BlockPos.of(pointTag.getLong("pos")), FluidVolume.fromTag(pointTag.getCompound("volume")));
		}
	}

	@Override
	public ComponentType<?> getComponentType() {
		return AstromineComponentTypes.CHUNK_ATMOSPHERE_COMPONENT;
	}
}
