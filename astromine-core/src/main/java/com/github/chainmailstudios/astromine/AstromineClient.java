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

package com.github.chainmailstudios.astromine;

import net.fabricmc.api.ClientModInitializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


import com.github.chainmailstudios.astromine.registry.AstromineKeybinds;
import com.github.chainmailstudios.astromine.registry.client.*;

@OnlyIn(Dist.CLIENT)
public class AstromineClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		AstromineEntityRenderers.initialize();
		AstromineBlockEntityRenderers.initialize();
		AstromineClientModels.initialize();
		AstromineParticleFactories.initialize();
		AstromineSkyboxes.initialize();
		AstromineScreens.initialize();
		AstromineClientCallbacks.initialize();
		AstromineClientPackets.initialize();
		AstromineRenderLayers.initialize();
		AstrominePatchouliPages.initialize();
		AstromineKeybinds.initialize();
	}
}
