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
import com.github.chainmailstudios.astromine.common.item.ManualItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class AstromineItems {
	private static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, AstromineCommon.MOD_ID);

	public static final RegistryObject<Item> ENERGY = register("energy", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> FLUID = register("fluid", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> ITEM = register("item", () -> new Item(new Item.Properties()));

	public static final RegistryObject<Item> MANUAL = register("manual", () -> new ManualItem(getBasicSettings().stacksTo(1)));

	public static void initialize(IEventBus modBus) {
		ITEM_DEFERRED_REGISTER.register(modBus);
	}

	/**
	 * @param name Name of item instance to be registered
	 * @param item RegistryObject<Item> instance to be registered
	 * @return RegistryObject<Item> instance registered
	 */
	public static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
		return ITEM_DEFERRED_REGISTER.register(name, item);
	}

	/**
	 * @param name Identifier of item instance to be registered
	 * @param item RegistryObject<Item> instance to be registered
	 * @return RegistryObject<Item> instance registered
	 */
	public static <T extends Item> RegistryObject<T> register(ResourceLocation name, Supplier<T> item) {
		return register(name.getPath(), item);
	}

	public static Item.Properties getBasicSettings() {
		return new Item.Properties().tab(AstromineItemGroups.CORE);
	}
}
