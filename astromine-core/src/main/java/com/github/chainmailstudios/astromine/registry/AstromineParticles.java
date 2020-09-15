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

package com.github.chainmailstudios.astromine.registry;

import com.github.chainmailstudios.astromine.AstromineCommon;
import net.minecraft.block.Block;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AstromineParticles {
	private static final DeferredRegister<ParticleType<?>> PARTICLE_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, AstromineCommon.MOD_ID);

	public static void initialize(IEventBus modBus) {
		PARTICLE_DEFERRED_REGISTER.register(modBus);
	}
	
	/**
	 * Registers a new {@link DefaultParticleType} instance under the given name.
	 *
	 * @param name       Name of {@link DefaultParticleType} to register
	 * @param alwaysShow Whether or not the com.github.chainmailstudios.astromine.technologies.client.particle should always appear visible
	 * @return Registered {@link DefaultParticleType}
	 */
	public static RegistryObject<BasicParticleType> register(String name, boolean alwaysShow) {
		return PARTICLE_DEFERRED_REGISTER.register(name, () -> new BasicParticleType(alwaysShow));
	}
}
