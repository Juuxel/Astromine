package com.github.chainmailstudios.astromine.mixin;

import com.github.chainmailstudios.astromine.common.component.SidedComponentProvider;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
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
	void onUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
		BlockRayTraceResult result = raycast(world, user, fluid == Fluids.EMPTY ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE);

		if (result.getType() == RayTraceResult.Type.BLOCK) {
			BlockState state = world.getBlockState(result.getBlockPos());
			Block block = state.getBlock();

			if (block instanceof ITileEntityProvider) {
				TileEntity attached = world.getBlockEntity(result.getBlockPos());

				if (attached instanceof SidedComponentProvider) {
					if (((SidedComponentProvider) attached).hasComponent(AstromineComponentTypes.FLUID_INVENTORY_COMPONENT)) {
						if (((SidedComponentProvider) attached).getComponent(AstromineComponentTypes.FLUID_INVENTORY_COMPONENT).getFirstExtractableVolume(result.getDirection()) != null) {
							cir.setReturnValue(ActionResult.fail(user.getItemInHand(hand)));
						}
					}
				}
			}
		}
	}

	private static BlockRayTraceResult raycast(World world, PlayerEntity player, RayTraceContext.FluidMode fluidHandling) {
		float f = player.xRot;
		float g = player.yRot;

		Vector3d vec3d = player.getEyePosition(1.0F);

		float h = MathHelper.cos(-g * 0.017453292F - 3.1415927F);
		float i = MathHelper.sin(-g * 0.017453292F - 3.1415927F);
		float j = -MathHelper.cos(-f * 0.017453292F);
		float k = MathHelper.sin(-f * 0.017453292F);

		float l = i * j;
		float n = h * j;

		Vector3d vec3d2 = vec3d.add((double) l * 5.0D, (double) k * 5.0D, (double) n * 5.0D);

		return world.clip(new RayTraceContext(vec3d, vec3d2, RayTraceContext.BlockMode.OUTLINE, fluidHandling, player));
	}
}
