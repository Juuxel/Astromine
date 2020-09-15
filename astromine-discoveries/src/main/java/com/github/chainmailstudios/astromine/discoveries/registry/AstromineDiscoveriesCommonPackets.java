package com.github.chainmailstudios.astromine.discoveries.registry;

import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.common.network.NetworkingManager;
import com.github.chainmailstudios.astromine.discoveries.common.entity.PrimitiveRocketEntity;
import com.github.chainmailstudios.astromine.registry.AstromineCommonPackets;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class AstromineDiscoveriesCommonPackets extends AstromineCommonPackets {
	public static final ResourceLocation ROCKET_LAUNCH = AstromineCommon.identifier("rocket_launch");

	public static void initialize() {
		NetworkingManager.registerC2SHandler(ROCKET_LAUNCH, (context, buffer) -> {
			int entityId = buffer.readInt();

			context.enqueueWork(() -> {
				context.getSender().getCommandSenderWorld().getEntity(entityId).getEntityData().set(PrimitiveRocketEntity.IS_RUNNING, true);
			});
		});
	}

	public static PacketBuffer ofRocketLaunch(int entityId) {
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		buf.writeInt(entityId);
		return buf;
	}
}
