package com.github.chainmailstudios.astromine.mixin;

import com.github.chainmailstudios.astromine.common.registry.GravityRegistry;
import net.minecraft.block.HoneyBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(HoneyBlock.class)
public class HoneyBlockMixin {
    @ModifyConstant(method = "isSliding(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)Z", constant = @Constant(doubleValue = -0.08D))
    double getGravityB(double original) {
        World world = ((Entity) (Object) this).world;

        Identifier dimension = world.getDimensionRegistryKey().getValue();

        return -GravityRegistry.INSTANCE.get(dimension);
    }
}
