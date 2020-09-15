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

import com.github.chainmailstudios.astromine.registry.*;
import com.google.gson.Gson;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

@Mod("astrominecore")
public class AstromineCommon {
	public interface SidedInit {
		void onSidedInit(IEventBus modBus, IEventBus forgeBus);
	}

	public static final String LOG_ID = "Astromine";
	public static final String MOD_ID = "astromine";

	public static final Gson GSON = new Gson();

	public static final Logger LOGGER = LogManager.getLogger(LOG_ID);

	public static ResourceLocation identifier(String name) {
		if (name.indexOf(':') >= 0)
			return new ResourceLocation(name);
		return new ResourceLocation(MOD_ID, name);
	}

	public AstromineCommon() {
		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		final IEventBus forgeBus = MinecraftForge.EVENT_BUS;

		this.onInitialize(modBus, forgeBus);
		// TODO Check safety (though it should be safer)
		DistExecutor.unsafeRunForDist(() -> () -> AstromineClient.setUp(modBus, forgeBus, this), () -> () -> AstromineDedicated.setUp(modBus, forgeBus, this));
	}

	public void onInitialize(IEventBus modBus, IEventBus forgeBus) {
		AstromineIdentifierFixes.initialize();
		AstromineDimensions.initialize();
		AstromineFeatures.initialize();
		AstromineItems.initialize(modBus);
		AstromineBlocks.initialize(modBus);
		AstromineScreenHandlers.initialize();
		AstromineEntityTypes.initialize(modBus);
		AstromineComponentTypes.initialize();
		AstromineNetworkTypes.initialize();
		AstrominePotions.initialize();
		AstromineBiomeSources.initialize();
		AstromineBiomes.initialize();
		AstromineFluids.initialize();
		AstromineBreathables.initialize();
		AstromineChunkGenerators.initialize();
		AstromineCommonPackets.initialize();
		AstromineGravities.initialize();
		AstromineDimensionLayers.initialize();
		AstromineCommonCallbacks.initialize();
		AstromineRecipeSerializers.initialize();
		AstromineCommands.initialize();
		AstromineAtmospheres.initialize();
		AstromineBlockEntityTypes.initialize(modBus);
		AstromineSoundEvents.initialize(modBus);
		AstromineNetworkMembers.initialize();
		AstromineCriteria.initialize();
		AstromineFluidEffects.initialize();

		if (ModList.get().isLoaded("libblockattributes_fluids")) {
			try {
				Class.forName("com.github.chainmailstudios.astromine.common.lba.LibBlockAttributesCompatibility").getDeclaredMethod("initialize").invoke(null);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public Supplier<? extends SidedInit> getClientInitializer() {
		return AstromineClient::new;
	}

	public Supplier<? extends SidedInit> getServerInitializer() {
		return AstromineDedicated::new;
	}
}
