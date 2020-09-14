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

package com.github.chainmailstudios.astromine.client.render.layer;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

public class Layer extends RenderType {
	private static final RenderType HOLOGRAPHIC_BRIDGE = RenderType.create("holographic_bridge", DefaultVertexFormats.POSITION_COLOR_LIGHTMAP, 7, 256, false, true, RenderType.State.builder().setCullState(RenderState.NO_CULL).setLightmapState(LIGHTMAP).setShadeModelState(
		RenderType.SMOOTH_SHADE).setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY).setAlphaState(RenderType.DEFAULT_ALPHA).setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING).createCompositeState(false));

	private static final RenderType FLAT_NO_CUTOUT = create("flat_no_cutout", DefaultVertexFormats.POSITION_COLOR_LIGHTMAP, 7, 256, RenderType.State.builder().setTextureState(NO_TEXTURE).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setShadeModelState(SMOOTH_SHADE).setDepthTestState(
		NO_DEPTH_TEST).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setAlphaState(DEFAULT_ALPHA).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false));

	public Layer(String name, VertexFormat vertexFormat, int drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
		super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
	}

	public static RenderType get(ResourceLocation texture) {
		RenderType.State multiPhaseParameters = RenderType.State.builder().setTextureState(new RenderState.TextureState(texture, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(NO_DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setLightmapState(
			NO_LIGHTMAP).setOverlayState(NO_OVERLAY).createCompositeState(true);
		return create("entity_cutout", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, 7, 256, true, true, multiPhaseParameters);
	}

	public static RenderType getHolographicBridge() {
		return HOLOGRAPHIC_BRIDGE;
	}

	public static RenderType getFlatNoCutout() {
		return FLAT_NO_CUTOUT;
	}
}
