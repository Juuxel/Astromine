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

package com.github.chainmailstudios.astromine.discoveries.registry;

import com.github.chainmailstudios.astromine.common.callback.ServerChunkManagerCallback;
import com.github.chainmailstudios.astromine.discoveries.common.world.generation.glacios.GlaciosChunkGenerator;
import com.github.chainmailstudios.astromine.discoveries.common.world.generation.mars.MarsChunkGenerator;
import com.github.chainmailstudios.astromine.discoveries.common.world.generation.moon.MoonChunkGenerator;
import com.github.chainmailstudios.astromine.discoveries.common.world.generation.space.EarthSpaceChunkGenerator;
import com.github.chainmailstudios.astromine.discoveries.common.world.generation.vulcan.VulcanChunkGenerator;
import com.github.chainmailstudios.astromine.registry.AstromineCommonCallbacks;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class AstromineDiscoveriesCommonCallbacks extends AstromineCommonCallbacks {
	@SubscribeEvent
	public static void onSetup(ServerChunkManagerCallback event) {
		ServerChunkProvider manager = event.chunkProvider;
		if (manager.generator instanceof EarthSpaceChunkGenerator) {
			manager.generator = ((EarthSpaceChunkGenerator) manager.generator).withSeedCommon(((ServerWorld) manager.getLevel()).getSeed());
		}

		if (manager.generator instanceof MoonChunkGenerator) {
			manager.generator = ((MoonChunkGenerator) manager.generator).withSeedCommon(((ServerWorld) manager.getLevel()).getSeed());
		}

		if (manager.generator instanceof MarsChunkGenerator) {
			manager.generator = ((MarsChunkGenerator) manager.generator).withSeedCommon(((ServerWorld) manager.getLevel()).getSeed());
		}

		if (manager.generator instanceof VulcanChunkGenerator) {
			manager.generator = ((VulcanChunkGenerator) manager.generator).withSeedCommon(((ServerWorld) manager.getLevel()).getSeed());
		}

		if (manager.generator instanceof GlaciosChunkGenerator) {
			manager.generator = ((GlaciosChunkGenerator) manager.generator).withSeedCommon(((ServerWorld) manager.getLevel()).getSeed());
		}
	}
}
