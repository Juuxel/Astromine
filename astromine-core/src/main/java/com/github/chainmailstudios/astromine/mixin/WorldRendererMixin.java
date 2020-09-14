/*
 * MIT License
 *
 * Copyright (c) 2020 Chainmail Studios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.chainmailstudios.astromine.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.github.chainmailstudios.astromine.client.cca.ClientAtmosphereManager;
import com.github.chainmailstudios.astromine.client.registry.SkyboxRegistry;
import com.github.chainmailstudios.astromine.client.render.layer.Layer;
import com.github.chainmailstudios.astromine.client.render.sky.skybox.Skybox;
import com.github.chainmailstudios.astromine.common.fluid.ExtendedFluid;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

@Mixin(LevelRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class WorldRendererMixin {
	@Shadow
	@Final
	private Minecraft client;

	@Shadow
	private ClientLevel world;

	@Shadow
	@Final
	private RenderBuffers bufferBuilders;

	@Inject(at = @At("HEAD"), method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;F)V", cancellable = true)
	void onRenderSky(PoseStack matrices, float tickDelta, CallbackInfo callbackInformation) {
		Skybox skybox = SkyboxRegistry.INSTANCE.get(this.client.level.dimension());

		if (skybox != null) {
			skybox.render(matrices, tickDelta);
			callbackInformation.cancel();
		}
	}

	@Inject(at = @At(value = "HEAD", target = "Lnet/minecraft/client/render/WorldRenderer;checkEmpty(Lnet/minecraft/client/util/math/MatrixStack;)V"),
		method = "render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/util/math/Matrix4f;)V")
	void onRender(PoseStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
		Vec3 cameraPosition = camera.getPosition();

		float cX = (float) cameraPosition.x;
		float cY = (float) cameraPosition.y;
		float cZ = (float) cameraPosition.z;

		MultiBufferSource.BufferSource immediate = this.bufferBuilders.bufferSource();

		VertexConsumer consumer = immediate.getBuffer(Layer.getFlatNoCutout());

		for (Long2ObjectMap.Entry<FluidVolume> entry : ClientAtmosphereManager.getVolumes().long2ObjectEntrySet()) {
			long blockPos = entry.getLongKey();
			FluidVolume volume = entry.getValue();

			int r = 255;
			int g = 255;
			int b = 255;
			int a = 31;

			if (volume.getFluid() instanceof ExtendedFluid) {
				int color = ((ExtendedFluid) volume.getFluid()).getTintColor();

				r = (color >> 16 & 255);
				g = (color >> 8 & 255);
				b = (color & 255);
			}

			int bX = BlockPos.getX(blockPos);
			int bZ = BlockPos.getZ(blockPos);
			if (!volume.isEmpty() && world.hasChunk(bX >> 4, bZ >> 4)) {
				int bY = BlockPos.getY(blockPos);

				float x = bX - cX;
				float y = bY - cY;
				float z = bZ - cZ;

				// Bottom
				consumer.vertex(matrices.last().pose(), x, y, z).color(r, g, b, a).uv2(15728880).endVertex();
				consumer.vertex(matrices.last().pose(), x, y, z + 1).color(r, g, b, a).uv2(15728880).endVertex();
				consumer.vertex(matrices.last().pose(), x + 1, y, z + 1).color(r, g, b, a).uv2(15728880).endVertex();
				consumer.vertex(matrices.last().pose(), x + 1, y, z).color(r, g, b, a).uv2(15728880).endVertex();

				// Top
				consumer.vertex(matrices.last().pose(), x, y + 1, z).color(r, g, b, a).uv2(15728880).endVertex();
				consumer.vertex(matrices.last().pose(), x, y + 1, z + 1).color(r, g, b, a).uv2(15728880).endVertex();
				consumer.vertex(matrices.last().pose(), x + 1, y + 1, z + 1).color(r, g, b, a).uv2(15728880).endVertex();
				consumer.vertex(matrices.last().pose(), x + 1, y + 1, z).color(r, g, b, a).uv2(15728880).endVertex();

				// Front
				consumer.vertex(matrices.last().pose(), x, y, z).color(r, g, b, a).uv2(15728880).endVertex();
				consumer.vertex(matrices.last().pose(), x, y + 1, z).color(r, g, b, a).uv2(15728880).endVertex();
				consumer.vertex(matrices.last().pose(), x + 1, y + 1, z).color(r, g, b, a).uv2(15728880).endVertex();
				consumer.vertex(matrices.last().pose(), x + 1, y, z).color(r, g, b, a).uv2(15728880).endVertex();

				// Back
				consumer.vertex(matrices.last().pose(), x, y, z + 1).color(r, g, b, a).uv2(15728880).endVertex();
				consumer.vertex(matrices.last().pose(), x, y + 1, z + 1).color(r, g, b, a).uv2(15728880).endVertex();
				consumer.vertex(matrices.last().pose(), x + 1, y + 1, z + 1).color(r, g, b, a).uv2(15728880).endVertex();
				consumer.vertex(matrices.last().pose(), x + 1, y, z + 1).color(r, g, b, a).uv2(15728880).endVertex();

				// Left
				consumer.vertex(matrices.last().pose(), x, y, z).color(r, g, b, a).uv2(15728880).endVertex();
				consumer.vertex(matrices.last().pose(), x, y + 1, z).color(r, g, b, a).uv2(15728880).endVertex();
				consumer.vertex(matrices.last().pose(), x, y + 1, z + 1).color(r, g, b, a).uv2(15728880).endVertex();
				consumer.vertex(matrices.last().pose(), x, y, z + 1).color(r, g, b, a).uv2(15728880).endVertex();

				// Right
				consumer.vertex(matrices.last().pose(), x + 1, y, z).color(r, g, b, a).uv2(15728880).endVertex();
				consumer.vertex(matrices.last().pose(), x + 1, y + 1, z).color(r, g, b, a).uv2(15728880).endVertex();
				consumer.vertex(matrices.last().pose(), x + 1, y + 1, z + 1).color(r, g, b, a).uv2(15728880).endVertex();
				consumer.vertex(matrices.last().pose(), x + 1, y, z + 1).color(r, g, b, a).uv2(15728880).endVertex();
			}
		}
	}
}
