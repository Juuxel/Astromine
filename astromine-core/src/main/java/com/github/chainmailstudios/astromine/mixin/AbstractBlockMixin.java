package com.github.chainmailstudios.astromine.mixin;

import com.github.chainmailstudios.astromine.common.block.transfer.TransferType;
import com.github.chainmailstudios.astromine.common.volume.handler.FluidHandler;
import com.github.chainmailstudios.astromine.common.volume.fraction.Fraction;
import com.github.chainmailstudios.astromine.common.item.base.FluidVolumeItem;
import com.github.chainmailstudios.astromine.common.utilities.data.Holder;
import com.github.chainmailstudios.astromine.common.volume.handler.TransferHandler;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(BlockBehaviour.class)
public class AbstractBlockMixin {
	@SuppressWarnings("all")
	@Inject(at = @At("HEAD"), method = "onUse(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;", cancellable = true)
	void astromine_onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result, CallbackInfoReturnable<InteractionResult> cir) 	{
		final ItemStack stack = player.getItemInHand(hand);

		final Item stackItem = stack.getItem();

		final boolean isBucket = stackItem instanceof BucketItem;

		final boolean isFluidVolumeItem = stackItem instanceof FluidVolumeItem;

		final Optional<FluidHandler> handler = FluidHandler.ofOptional(stack);

		if (state.getBlock().isEntityBlock()) {
			final Optional<TransferHandler> optionalTransferHandler = TransferHandler.of(world.getBlockEntity(pos));

			if (optionalTransferHandler.isPresent()) {
				TransferHandler transferHandler = optionalTransferHandler.get();

				Holder<TransferType> typeHolder = Holder.of(null);

				transferHandler.withDirection(AstromineComponentTypes.FLUID_INVENTORY_COMPONENT, result.getDirection(), (type) -> {
					typeHolder.set(type);
				});

				if (typeHolder.get() == null || (!typeHolder.get().canInsert() && !typeHolder.get().canExtract())) {
					return;
				}
			}
		}

		final Holder<Boolean> shouldSkip = Holder.of(false);

		handler.ifPresent(stackHandler -> {
			final Block block = state.getBlock();

			if (block.isEntityBlock()) {
				final BlockEntity blockEntity = world.getBlockEntity(pos);

				FluidHandler.ofOptional(blockEntity).ifPresent(blockEntityHandler -> {
					stackHandler.withVolume(0, (optionalStackVolume) -> {
						optionalStackVolume.ifPresent((stackVolume) -> {
							if (stackVolume.isEmpty()) {
								blockEntityHandler.withFirstExtractable(result.getDirection(), (optionalFirstExtractable) -> {
									optionalFirstExtractable.ifPresent((firstExtractable) -> {
										if (isBucket) {
											firstExtractable.ifStored(Fraction.bucket(), () -> {
												if (stack.getCount() == 1 || (player.inventory.getFreeSlot() == -1 && stack.getCount() == 1)) {
													stackVolume.moveFrom(firstExtractable, Fraction.bucket());
													player.setItemInHand(hand, new ItemStack(stackVolume.getFluid().getBucket()));
												} else if (player.inventory.getFreeSlot() != -1 && stack.getCount() > 1) {
													stackVolume.moveFrom(firstExtractable, Fraction.bucket());
													stack.shrink(1);
													player.addItem(new ItemStack(stackVolume.getFluid().getBucket()));
												}
											});
										} else {
											stackVolume.moveFrom(firstExtractable, Fraction.bucket());
										}
									});
								});
							} else {
								blockEntityHandler.withFirstInsertable(result.getDirection(), stackVolume.getFluid(), (optionalFirstInsertable) -> {
									optionalFirstInsertable.ifPresent((firstInsertable) -> {
										if (isBucket) {
											firstInsertable.ifAvailable(Fraction.bucket(), () -> {
												if (stack.getCount() == 1 || (player.inventory.getFreeSlot() == -1 && stack.getCount() == 1)) {
													firstInsertable.moveFrom(stackVolume, Fraction.bucket());

													if (!player.isCreative()) {
														player.setItemInHand(hand, new ItemStack(Items.BUCKET));
													}
												} else if (player.inventory.getFreeSlot() != -1 && stack.getCount() > 1) {
													firstInsertable.moveFrom(stackVolume, Fraction.bucket());

													if (!player.isCreative()) {
														stack.shrink(1);
														player.addItem(new ItemStack(Items.BUCKET));
													}
												}
											});
										} else {
											firstInsertable.moveFrom(stackVolume, Fraction.bucket());
										}
									});
								});
							}

							shouldSkip.set(true);
						});
					});
				});
			}
		});

		if (shouldSkip.get()) {
			cir.setReturnValue(InteractionResult.SUCCESS);
		}
	}
}
