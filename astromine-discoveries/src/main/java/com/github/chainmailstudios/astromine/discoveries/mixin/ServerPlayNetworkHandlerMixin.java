package com.github.chainmailstudios.astromine.discoveries.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.github.chainmailstudios.astromine.discoveries.common.entity.base.RocketEntity;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerPlayNetworkHandlerMixin implements ServerGamePacketListener {
    @Shadow public ServerPlayer player;

    @Inject(method= "onClientCommand(Lnet/minecraft/network/packet/c2s/play/ClientCommandC2SPacket;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;updateLastActionTime()V"))
    public void onClientCommandInject(ServerboundPlayerCommandPacket packet, CallbackInfo ci) {
        if(packet.getAction().equals(ServerboundPlayerCommandPacket.Action.OPEN_INVENTORY) && this.player.getVehicle() instanceof RocketEntity) { ;
            ((RocketEntity)this.player.getVehicle()).openInventory(this.player);
        }
    }
}
