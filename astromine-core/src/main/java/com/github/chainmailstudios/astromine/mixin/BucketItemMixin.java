package com.github.chainmailstudios.astromine.mixin;

import com.github.chainmailstudios.astromine.common.component.SidedComponentProvider;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public class BucketItemMixin {
	@Shadow @Final public Fluid fluid;

	@Inject(at = @At("HEAD"), method = "use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;", cancellable = true)
	void onUse(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		BlockHitResult result = raycast(world, user, fluid == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);

		if (result.getType() == HitResult.Type.BLOCK) {
			BlockState state = world.getBlockState(result.getBlockPos());
			Block block = state.getBlock();

			if (block instanceof EntityBlock) {
				BlockEntity attached = world.getBlockEntity(result.getBlockPos());

				if (attached instanceof SidedComponentProvider) {
					if (((SidedComponentProvider) attached).hasComponent(AstromineComponentTypes.FLUID_INVENTORY_COMPONENT)) {
						if (((SidedComponentProvider) attached).getComponent(AstromineComponentTypes.FLUID_INVENTORY_COMPONENT).getFirstExtractableVolume(result.getDirection()) != null) {
							cir.setReturnValue(InteractionResultHolder.fail(user.getItemInHand(hand)));
						}
					}
				}
			}
		}
	}

	private static BlockHitResult raycast(Level world, Player player, ClipContext.Fluid fluidHandling) {
		float f = player.xRot;
		float g = player.yRot;

		Vec3 vec3d = player.getEyePosition(1.0F);

		float h = Mth.cos(-g * 0.017453292F - 3.1415927F);
		float i = Mth.sin(-g * 0.017453292F - 3.1415927F);
		float j = -Mth.cos(-f * 0.017453292F);
		float k = Mth.sin(-f * 0.017453292F);

		float l = i * j;
		float n = h * j;

		Vec3 vec3d2 = vec3d.add((double) l * 5.0D, (double) k * 5.0D, (double) n * 5.0D);

		return world.clip(new ClipContext(vec3d, vec3d2, ClipContext.Block.OUTLINE, fluidHandling, player));
	}
}
