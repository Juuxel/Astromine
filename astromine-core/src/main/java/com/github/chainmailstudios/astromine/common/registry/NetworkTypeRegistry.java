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

package com.github.chainmailstudios.astromine.common.registry;

import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.common.network.NetworkInstance;
import com.github.chainmailstudios.astromine.common.network.type.base.NetworkType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class NetworkTypeRegistry implements Iterable<NetworkType> {
	private static final RegistryKey<Registry<NetworkType>> REGISTRY_KEY = RegistryKey.createRegistryKey(new ResourceLocation(AstromineCommon.MOD_ID, "network_type"));
	private static final Lazy<ForgeRegistry<NetworkType>> FORGE_REGISTRY_LAZY = () -> RegistryManager.ACTIVE.getRegistry(REGISTRY_KEY);

	private static final DeferredRegister<NetworkType> NETWORK_TYPE_DEFERRED_REGISTER = DeferredRegister.create(NetworkType.class, AstromineCommon.MOD_ID);

	public static final NetworkTypeRegistry INSTANCE = new NetworkTypeRegistry();

	private NetworkTypeRegistry() {
		NETWORK_TYPE_DEFERRED_REGISTER.makeRegistry("network_type", () ->
				new RegistryBuilder<NetworkType>().setMaxID(Integer.MAX_VALUE - 1).setDefaultKey(new ResourceLocation(AstromineCommon.MOD_ID, "empty_network")).disableSaving()
		);
	}

	public static void subscribe(IEventBus modBus) {
		NETWORK_TYPE_DEFERRED_REGISTER.register(modBus);
	}

	public RegistryObject<NetworkType> registerLambda(String id, Supplier<BiConsumer<World, NetworkInstance>> supplier) {
		return register(id, () -> new NetworkType() {
			@Override
			public void tick(World world, NetworkInstance instance) {
				supplier.get().accept(world, instance);
			}
		});
	}

	public RegistryObject<NetworkType> register(String id, Supplier<NetworkType> networkType) {
		return NETWORK_TYPE_DEFERRED_REGISTER.register(id, networkType);
	}

	public RegistryObject<NetworkType> register(ResourceLocation name, Supplier<NetworkType> networkType) {
		return register(name.getPath(), networkType);
	}

	public boolean containsKey(ResourceLocation key) {
		return FORGE_REGISTRY_LAZY.get().containsKey(key);
	}

	public boolean containsValue(NetworkType value) {
		return FORGE_REGISTRY_LAZY.get().containsValue(value);
	}

	public boolean isEmpty() {
		return FORGE_REGISTRY_LAZY.get().isEmpty();
	}

	@Nullable
	public NetworkType getValue(ResourceLocation key) {
		return FORGE_REGISTRY_LAZY.get().getValue(key);
	}

	@Nullable
	public ResourceLocation getKey(NetworkType value) {
		return FORGE_REGISTRY_LAZY.get().getKey(value);
	}

	@NotNull
	public Set<ResourceLocation> getKeys() {
		return FORGE_REGISTRY_LAZY.get().getKeys();
	}

	@NotNull
	public Collection<NetworkType> getValues() {
		return FORGE_REGISTRY_LAZY.get().getValues();
	}

	@NotNull
	public Set<Map.Entry<RegistryKey<NetworkType>, NetworkType>> getEntries() {
		return FORGE_REGISTRY_LAZY.get().getEntries();
	}

	@NotNull
	@Override
	public Iterator<NetworkType> iterator() {
		return FORGE_REGISTRY_LAZY.get().iterator();
	}
}
