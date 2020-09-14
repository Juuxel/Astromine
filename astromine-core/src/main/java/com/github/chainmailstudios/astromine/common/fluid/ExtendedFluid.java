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

package com.github.chainmailstudios.astromine.common.fluid;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import com.github.chainmailstudios.astromine.common.utilities.ClientUtilities;
import com.github.chainmailstudios.astromine.registry.AstromineBlocks;
import com.github.chainmailstudios.astromine.registry.AstromineFluids;
import com.github.chainmailstudios.astromine.registry.AstromineItems;
import com.github.vini2003.blade.common.miscellaneous.Color;
import org.jetbrains.annotations.Nullable;

public abstract class ExtendedFluid extends FlowingFluid {
	private final int fogColor;
	private final int tintColor;

	private final boolean isInfinite;

	private Block block;

	private Fluid flowing;
	private Fluid still;

	private Item bucket;

	private DamageSource source;

	public ExtendedFluid(int fogColor, int tintColor, boolean isInfinite, @Nullable DamageSource source) {
		this.fogColor = fogColor;
		this.tintColor = tintColor;
		this.isInfinite = isInfinite;
		this.source = source == null ? DamageSource.GENERIC : source;
	}

	public static Builder builder() {
		return new Builder();
	}

	public DamageSource getDamageSource() {
		return source;
	}

	@Override
	public Fluid getSource() {
		return still;
	}

	@Override
	public Fluid getFlowing() {
		return flowing;
	}

	@Override
	protected boolean canConvertToSource() {
		return isInfinite;
	}

	public int getFogColor() {
		return fogColor;
	}

	public int getTintColor() {
		return tintColor;
	}

	public Block getBlock() {
		return block;
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor world, BlockPos position, BlockState state) {
		BlockEntity blockEntity = state.getBlock().isEntityBlock() ? world.getBlockEntity(position) : null;
		Block.dropResources(state, world, position, blockEntity);
	}

	@Override
	public boolean isSame(Fluid fluid) {
		return fluid == flowing || fluid == still;
	}

	@Override
	protected int getSlopeFindDistance(LevelReader world) {
		return 4;
	}

	@Override
	protected int getDropOff(LevelReader world) {
		return 1;
	}

	@Override
	public Item getBucket() {
		return bucket;
	}

	@Override
	protected boolean canBeReplacedWith(FluidState state, BlockGetter world, BlockPos pos, Fluid fluid, Direction direction) {
		return direction == Direction.DOWN && fluid != flowing && fluid != still;
	}

	@Override
	public int getTickDelay(LevelReader world) {
		return 5;
	}

	@Override
	protected float getExplosionResistance() {
		return 100.0F;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState state) {
		return block.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
	}

	@Override
	public boolean isSource(FluidState state) {
		return false;
	}

	@Override
	public int getAmount(FluidState state) {
		return 0;
	}

	public static class Builder {
		int fog = Color.standard().toInt();
		int tint = Color.standard().toInt();
		int damage = 0;

		boolean isInfinite = false;
		boolean isToxic = false;

		String name = "";

		Block block;

		Fluid flowing;
		Fluid still;

		Item bucket;

		DamageSource source;

		CreativeModeTab group;

		private Builder() {

		}

		public Builder fog(int fog) {
			this.fog = fog;
			return this;
		}

		public Builder tint(int tint) {
			this.tint = tint;
			return this;
		}

		public Builder damage(int damage) {
			this.damage = damage;
			return this;
		}

		public Builder infinite(boolean isInfinite) {
			this.isInfinite = isInfinite;
			return this;
		}

		public Builder toxic(boolean isToxic) {
			this.isToxic = isToxic;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder source(DamageSource source) {
			this.source = source;
			return this;
		}

		public Builder group(CreativeModeTab group) {
			this.group = group;
			return this;
		}

		public Fluid build() {
			ExtendedFluid flowing = AstromineFluids.register(name + "_flowing", new Flowing(fog, tint, isInfinite, source));
			ExtendedFluid still = AstromineFluids.register(name, new Still(fog, tint, isInfinite, source));

			flowing.flowing = flowing;
			still.flowing = flowing;
			this.flowing = flowing;

			flowing.still = still;
			still.still = still;
			this.still = still;

			Block block = AstromineBlocks.register(name, new LiquidBlock(still, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops()));

			Item bucket = AstromineItems.register(name + "_bucket", new BucketItem(still, (new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1).tab(group)));

			flowing.block = block;
			still.block = block;
			this.block = block;

			flowing.bucket = bucket;
			still.bucket = bucket;
			this.bucket = bucket;

			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
				ClientUtilities.buildClient(name, tint, still, flowing);
			}

			return still;
		}
	}

	public static class Flowing extends ExtendedFluid {
		public Flowing(int fogColor, int tintColor, boolean isInfinite, @Nullable DamageSource source) {
			super(fogColor, tintColor, isInfinite, source);
		}

		@Override
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		@Override
		public boolean isSource(FluidState state) {
			return false;
		}
	}

	public static class Still extends ExtendedFluid {
		public Still(int fogColor, int tintColor, boolean isInfinite, @Nullable DamageSource source) {
			super(fogColor, tintColor, isInfinite, source);
		}

		@Override
		public int getAmount(FluidState state) {
			return 8;
		}

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}
	}
}
