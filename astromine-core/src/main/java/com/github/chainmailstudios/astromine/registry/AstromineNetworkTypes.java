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

import com.github.chainmailstudios.astromine.common.network.NetworkInstance;
import com.github.chainmailstudios.astromine.common.network.type.EnergyNetworkType;
import com.github.chainmailstudios.astromine.common.network.type.FluidNetworkType;
import com.github.chainmailstudios.astromine.common.network.type.base.NetworkType;
import com.github.chainmailstudios.astromine.common.registry.NetworkTypeRegistry;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class AstromineNetworkTypes {
	public static final RegistryObject<NetworkType> ENERGY = register("energy_network", EnergyNetworkType::new);
	public static final RegistryObject<NetworkType> FLUID = register("fluid_network", FluidNetworkType::new);
	public static final RegistryObject<NetworkType> ITEM = registerLambda("item_network", () -> (world, instance) -> {
		// TODO: item network
		// TODO: still todo two months later
	});

	public static void initialize(IEventBus modBus) {
		NetworkTypeRegistry.subscribe(modBus);
	}

	private static RegistryObject<NetworkType> register(String name, Supplier<NetworkType> type) {
		return NetworkTypeRegistry.INSTANCE.register(name, type);
	}

	private static RegistryObject<NetworkType> registerLambda(String name, Supplier<BiConsumer<World, NetworkInstance>> supplier) {
		return NetworkTypeRegistry.INSTANCE.registerLambda(name, supplier);
	}
}
