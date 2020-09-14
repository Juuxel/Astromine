package com.github.chainmailstudios.astromine.discoveries.registry;

import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.discoveries.common.entity.PrimitiveRocketEntity;
import com.github.chainmailstudios.astromine.registry.AstromineCommonPackets;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class AstromineDiscoveriesCommonPackets extends AstromineCommonPackets {
	public static final ResourceLocation ROCKET_LAUNCH = AstromineCommon.identifier("rocket_launch");

	public static void initialize() {
		ServerSidePacketRegistry.INSTANCE.register(ROCKET_LAUNCH, (context, buffer) -> {
			int entityId = buffer.readInt();

			context.getTaskQueue().execute(() -> {
				context.getPlayer().getCommandSenderWorld().getEntity(entityId).getEntityData().set(PrimitiveRocketEntity.IS_RUNNING, true);
			});
		});
	}

	public static FriendlyByteBuf ofRocketLaunch(int entityId) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeInt(entityId);
		return buf;
	}
}
