package com.github.chainmailstudios.astromine.common.block;

import com.github.chainmailstudios.astromine.common.block.entity.ElectrolyzerBlockEntity;
import com.github.chainmailstudios.astromine.common.screenhandler.ElectrolyzerScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ElectrolyzerBlock extends TieredHorizontalFacingMachineBlock {
	public ElectrolyzerBlock(Settings settings) {
		super(settings);
	}

	public abstract static class Base extends ElectrolyzerBlock {
		public Base(Settings settings) {
			super(settings);
		}

		@Override
		public boolean hasScreenHandler() {
			return true;
		}

		@Override
		public ScreenHandler createScreenHandler(BlockState state, World world, BlockPos pos, int syncId, PlayerInventory playerInventory, PlayerEntity player) {
			return new ElectrolyzerScreenHandler(syncId, playerInventory, pos);
		}

		@Override
		public void populateScreenHandlerBuffer(BlockState state, World world, BlockPos pos, ServerPlayerEntity player, PacketByteBuf buffer) {
			buffer.writeBlockPos(pos);
		}
	}

	public static class Primitive extends ElectrolyzerBlock.Base {
		public Primitive(Settings settings) {
			super(settings);
		}

		@Override
		public BlockEntity createBlockEntity() {
			return new ElectrolyzerBlockEntity.Primitive();
		}

		@Override
		public double getMachineSpeed() {
			return 0.5;
		}
	}

	public static class Basic extends ElectrolyzerBlock.Base {
		public Basic(Settings settings) {
			super(settings);
		}

		@Override
		public BlockEntity createBlockEntity() {
			return new ElectrolyzerBlockEntity.Basic();
		}

		@Override
		public double getMachineSpeed() {
			return 1;
		}
	}

	public static class Advanced extends ElectrolyzerBlock.Base {
		public Advanced(Settings settings) {
			super(settings);
		}

		@Override
		public BlockEntity createBlockEntity() {
			return new ElectrolyzerBlockEntity.Advanced();
		}

		@Override
		public double getMachineSpeed() {
			return 2;
		}
	}

	public static class Elite extends ElectrolyzerBlock.Base {
		public Elite(Settings settings) {
			super(settings);
		}

		@Override
		public BlockEntity createBlockEntity() {
			return new ElectrolyzerBlockEntity.Elite();
		}

		@Override
		public double getMachineSpeed() {
			return 4;
		}
	}
}
