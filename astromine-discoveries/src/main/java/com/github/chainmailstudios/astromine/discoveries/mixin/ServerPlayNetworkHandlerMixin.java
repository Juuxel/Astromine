package com.github.chainmailstudios.astromine.discoveries.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.github.chainmailstudios.astromine.discoveries.common.entity.base.RocketEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.client.CEntityActionPacket;

@Mixin(ServerPlayNetHandler.class)
public abstract class ServerPlayNetworkHandlerMixin implements IServerPlayNetHandler {
    @Shadow public ServerPlayerEntity player;

    @Inject(method= "onClientCommand(Lnet/minecraft/network/packet/c2s/play/ClientCommandC2SPacket;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;updateLastActionTime()V"))
    public void onClientCommandInject(CEntityActionPacket packet, CallbackInfo ci) {
        if(packet.getAction().equals(CEntityActionPacket.Action.OPEN_INVENTORY) && this.player.getVehicle() instanceof RocketEntity) { ;
            ((RocketEntity)this.player.getVehicle()).openInventory(this.player);
        }
    }
}
