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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class AstromineEntityTypes {
	private static final DeferredRegister<EntityType<?>> ENTITY_TYPE_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, AstromineCommon.MOD_ID);

	public static void initialize(IEventBus modBus) {
		ENTITY_TYPE_DEFERRED_REGISTER.register(modBus);
	}

	public static <T extends Entity> RegistryObject<EntityType<T>> register(String id, Supplier<EntityType.Builder<T>> builder) {
		return registerType(id, () -> builder.get().build(id));
	}

	public static <T extends Entity> RegistryObject<EntityType<T>> register(ResourceLocation id, Supplier<EntityType.Builder<T>> builder) {
		return register(id.getPath(), builder);
	}

	/**
	 * @param id
	 *        Name of EntityType instance to be registered
	 * @param type
	 *        EntityType instance to register
	 *
	 * @return Registered EntityType
	 */
	public static <T extends Entity> RegistryObject<EntityType<T>> registerType(String id, Supplier<EntityType<T>> type) {
		return ENTITY_TYPE_DEFERRED_REGISTER.register(id, type);
	}

	public static <T extends Entity> RegistryObject<EntityType<T>> registerType(ResourceLocation id, Supplier<EntityType<T>> type) {
		return registerType(id.getPath(), type);
	}
}
