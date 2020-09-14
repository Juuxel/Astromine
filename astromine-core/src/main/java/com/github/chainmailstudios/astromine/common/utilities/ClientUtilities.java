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

package com.github.chainmailstudios.astromine.common.utilities;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import com.github.chainmailstudios.astromine.AstromineCommon;

import java.util.function.Function;

public class ClientUtilities {
	public static void playSound(BlockPos position, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance) {
		Minecraft.getInstance().level.playSound(Minecraft.getInstance().player, Minecraft.getInstance().player, sound, category, volume, pitch);
	}

	@Environment(EnvType.CLIENT)
	public static void buildClient(String name, int tint, Fluid still, Fluid flowing) {
		final ResourceLocation stillSpriteIdentifier = new ResourceLocation("block/water_still");
		final ResourceLocation flowingSpriteIdentifier = new ResourceLocation("block/water_flow");
		final ResourceLocation listenerIdentifier = AstromineCommon.identifier(name + "_reload_listener");

		final TextureAtlasSprite[] fluidSprites = { null, null };

		ClientSpriteRegistryCallback.event(AtlasTexture.LOCATION_BLOCKS).register((atlasTexture, registry) -> {
			registry.register(stillSpriteIdentifier);
			registry.register(flowingSpriteIdentifier);
		});

		ResourceManagerHelper.get(ResourcePackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public ResourceLocation getFabricId() {
				return listenerIdentifier;
			}

			@Override
			public void onResourceManagerReload(IResourceManager resourceManager) {
				final Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS);
				fluidSprites[0] = atlas.apply(stillSpriteIdentifier);
				fluidSprites[1] = atlas.apply(flowingSpriteIdentifier);
			}
		});

		final FluidRenderHandler handler = new FluidRenderHandler() {
			@Override
			public TextureAtlasSprite[] getFluidSprites(IBlockDisplayReader view, BlockPos pos, FluidState state) {
				return fluidSprites;
			}

			@Override
			public int getFluidColor(IBlockDisplayReader view, BlockPos pos, FluidState state) {
				return tint;
			}
		};

		FluidRenderHandlerRegistry.INSTANCE.register(still, handler);
		FluidRenderHandlerRegistry.INSTANCE.register(flowing, handler);
	}
}
