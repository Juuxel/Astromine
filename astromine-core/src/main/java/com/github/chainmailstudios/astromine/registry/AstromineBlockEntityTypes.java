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

import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AstromineBlockEntityTypes {
	private static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITY_TYPE_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, AstromineCommon.MOD_ID);

	public static void initialize(IEventBus modBus) {
		BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register(modBus);
	}

	/**
	 * @param name
	 *        Name of BlockEntityType instance to be registered
	 * @param supplier
	 *        Supplier of BlockEntity to use for BlockEntityType
	 * @param supportedBlocks
	 *        Blocks the BlockEntity can be attached to
	 *
	 * @return Registered BlockEntityType
	 */
	public static <B extends TileEntity> RegistryObject<TileEntityType<B>> register(String name, Supplier<B> supplier, Block... supportedBlocks) {
		// Something something datafixers
		//noinspection ConstantConditions
		return BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register(name, () -> TileEntityType.Builder.of(supplier, supportedBlocks).build(null));
	}
}
