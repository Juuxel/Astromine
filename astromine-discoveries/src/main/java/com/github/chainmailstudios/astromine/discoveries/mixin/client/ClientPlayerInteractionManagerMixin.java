package com.github.chainmailstudios.astromine.discoveries.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.github.chainmailstudios.astromine.discoveries.common.entity.base.RocketEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;

@Mixin(PlayerController.class)
public class ClientPlayerInteractionManagerMixin {
    @Shadow @Final private Minecraft client;

    @Inject(method = "hasRidingInventory()Z", at = @At("HEAD"), cancellable = true)
    public void hasRidingInventoryInject(CallbackInfoReturnable<Boolean> cir) {
        if (this.client.player.isPassenger() && this.client.player.getVehicle() instanceof RocketEntity) cir.setReturnValue(true);
    }
}
