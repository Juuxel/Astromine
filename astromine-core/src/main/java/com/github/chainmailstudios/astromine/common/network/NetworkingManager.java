package com.github.chainmailstudios.astromine.common.network;

import com.google.common.collect.*;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = "astromine", bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetworkingManager {
	private static final ResourceLocation CHANNEL_ID = new ResourceLocation("astromine:astromine");
	private static final ResourceLocation SYNC_IDS = new ResourceLocation("astromine:sync_ids");
	private static final EventNetworkChannel CHANNEL = NetworkRegistry.newEventChannel(CHANNEL_ID, () -> "1", version -> true, version -> true);
	private static final Map<ResourceLocation, BiConsumer<NetworkEvent.Context, PacketBuffer>> S2C = Maps.newHashMap();
	private static final Map<ResourceLocation, BiConsumer<NetworkEvent.Context, PacketBuffer>> C2S = Maps.newHashMap();
	private static final Set<ResourceLocation> serverReceivables = Sets.newHashSet();
	private static final Multimap<PlayerEntity, ResourceLocation> clientReceivables = Multimaps.newMultimap(Maps.newHashMap(), Sets::newHashSet);

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent setupEvent) {
		CHANNEL.addListener(createPacketHandler(NetworkEvent.ClientCustomPayloadEvent.class, C2S));

		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> Client::initClient);

		MinecraftForge.EVENT_BUS.<PlayerEvent.PlayerLoggedInEvent>addListener(event -> NetworkingManager.sendToPlayer((ServerPlayerEntity) event.getPlayer(), SYNC_IDS, sendSyncPacket(C2S)));
		MinecraftForge.EVENT_BUS.<PlayerEvent.PlayerLoggedOutEvent>addListener(event -> clientReceivables.removeAll(event.getPlayer()));

		registerC2SHandler(SYNC_IDS, (context, buffer) -> {
			Set<ResourceLocation> receivables = (Set<ResourceLocation>) clientReceivables.get(context.getSender());
			int size = buffer.readInt();
			receivables.clear();
			for (int i = 0; i < size; i++) {
				receivables.add(buffer.readResourceLocation());
			}
		});
	}

	private static class Client {
		@OnlyIn(Dist.CLIENT)
		public static void initClient() {
			CHANNEL.addListener(createPacketHandler(NetworkEvent.ServerCustomPayloadEvent.class, S2C));
			MinecraftForge.EVENT_BUS.<ClientPlayerNetworkEvent.LoggedOutEvent>addListener(event -> serverReceivables.clear());

			registerS2CHandler(SYNC_IDS, (context, buffer) -> {
				Set<ResourceLocation> receivables = serverReceivables;
				int size = buffer.readInt();
				receivables.clear();
				for (int i = 0; i < size; i++) {
					receivables.add(buffer.readResourceLocation());
				}
				NetworkingManager.sendToServer(SYNC_IDS, sendSyncPacket(C2S));
			});
		}
	}

	private static <T extends NetworkEvent> Consumer<T> createPacketHandler(Class<T> clazz, Map<ResourceLocation, BiConsumer<NetworkEvent.Context, PacketBuffer>> map) {
		return event -> {
			if (event.getClass() != clazz) return;
			NetworkEvent.Context context = event.getSource().get();
			if (context.getPacketHandled()) return;
			PacketBuffer buffer = new PacketBuffer(event.getPayload().copy());
			ResourceLocation type = buffer.readResourceLocation();
			BiConsumer<NetworkEvent.Context, PacketBuffer> consumer = map.get(type);

			if (consumer != null) {
				consumer.accept(context, buffer);
			}
			context.setPacketHandled(true);
		};
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerS2CHandler(ResourceLocation id, BiConsumer<NetworkEvent.Context, PacketBuffer> consumer) {
		S2C.put(id, consumer);
	}

	public static void registerC2SHandler(ResourceLocation id, BiConsumer<NetworkEvent.Context, PacketBuffer> consumer) {
		C2S.put(id, consumer);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sendToServer(ResourceLocation id, PacketBuffer buffer) {
		ClientPlayNetHandler connection = Minecraft.getInstance().getConnection();
		if (connection != null) {
			PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
			packetBuffer.writeResourceLocation(id);
			packetBuffer.writeBytes(buffer);
			connection.send(NetworkDirection.PLAY_TO_SERVER.buildPacket(Pair.of(packetBuffer, 0), CHANNEL_ID).getThis());
		}
	}

	public static void sendToClient(ResourceLocation id, PacketDistributor.PacketTarget target, PacketBuffer buffer) {
		PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
		packetBuffer.writeResourceLocation(id);
		packetBuffer.writeBytes(buffer);
		target.send(NetworkDirection.PLAY_TO_CLIENT.buildPacket(Pair.of(packetBuffer, 0), CHANNEL_ID).getThis());
	}

	public static void sendToPlayer(ServerPlayerEntity player, ResourceLocation id, PacketBuffer buffer) {
		sendToClient(id, PacketDistributor.PLAYER.with(() -> player), buffer);
	}

	public static boolean canServerReceive(ResourceLocation id) {
		return serverReceivables.contains(id);
	}

	public static boolean canPlayerReceive(ServerPlayerEntity player, ResourceLocation id) {
		return clientReceivables.get(player).contains(id);
	}

	private static PacketBuffer sendSyncPacket(Map<ResourceLocation, BiConsumer<NetworkEvent.Context, PacketBuffer>> map) {
		List<ResourceLocation> availableIds = Lists.newArrayList(map.keySet());
		PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
		packetBuffer.writeInt(availableIds.size());
		for (ResourceLocation availableId : availableIds) {
			packetBuffer.writeResourceLocation(availableId);
		}
		return packetBuffer;
	}
}