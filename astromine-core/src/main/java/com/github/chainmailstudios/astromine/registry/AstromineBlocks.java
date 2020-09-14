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
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;

public class AstromineBlocks {
	private static final DeferredRegister<Block> BLOCK_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, AstromineCommon.MOD_ID);

	public static void initialize(IEventBus modBus) {
		BLOCK_DEFERRED_REGISTER.register(modBus);
	}

	/**
	 * @param name
	 *        Name of block instance to be registered
	 * @param block
	 *        Block instance to be registered
	 * @param settings
	 *        Item.Settings of BlockItem of Block instance to be registered
	 *
	 * @return Block instance registered
	 */
	public static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, Item.Properties settings) {
		return register(name, block, blockObj -> () -> new BlockItem(blockObj.get(), settings));
	}

	/**
	 * @param name
	 *        Name of block instance to be registered
	 * @param block
	 *        Block instance to be registered
	 * @param item
	 *        BlockItem instance of Block to be registered
	 *
	 * @return Block instance registered
	 */
	public static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, Supplier<BlockItem> item) {
		return register(name, block, ignore -> item);
	}

	/**
	 * @param name Name of block instance to be registered
	 * @param block Block instance to be registered
	 * @param itemFunction Function that creates block item based on Block to be registered
	 * @return Block instance registered
	 */
	public static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, Function<RegistryObject<T>, Supplier<? extends BlockItem>> itemFunction) {
		RegistryObject<T> b = register(AstromineCommon.identifier(name), block);
		if (itemFunction != null) {
			AstromineItems.register(name, () -> itemFunction.apply(b).get());
		}
		return b;
	}

	/**
	 * @param name
	 *        Name of block instance to be registered
	 * @param block
	 *        Block instance to be registered
	 *
	 * @return Block instance registered
	 */
	public static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
		return BLOCK_DEFERRED_REGISTER.register(name, block);
	}

	/**
	 * @param name
	 *        Identifier of block instance to be registered
	 * @param block
	 *        Block instance to be registered
	 *
	 * @return Block instance registered
	 */
	public static <T extends Block> RegistryObject<T> register(ResourceLocation name, Supplier<T> block) {
		return register(name.getPath(), block);
	}

	public static AbstractBlock.Properties getPrimitiveSettings() {
		return buildProperties(Material.METAL, MaterialColor.COLOR_ORANGE, ToolType.PICKAXE, 1, 4, 6).sound(SoundType.METAL);
	}

	public static AbstractBlock.Properties getBasicSettings() {
		return buildProperties(Material.METAL, MaterialColor.TERRACOTTA_ORANGE, ToolType.PICKAXE, 2, 6, 6).sound(SoundType.METAL);
	}

	public static AbstractBlock.Properties getAdvancedSettings() {
		return buildProperties(Material.METAL, MaterialColor.COLOR_GRAY, ToolType.PICKAXE, 2, 8, 6).sound(SoundType.METAL);
	}

	public static AbstractBlock.Properties getEliteSettings() {
		return buildProperties(Material.METAL, MaterialColor.TERRACOTTA_ORANGE, ToolType.PICKAXE, 4, 8, 100).sound(SoundType.METAL);
	}

	public static AbstractBlock.Properties getCreativeSettings() {
		return AbstractBlock.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_GREEN)
				.noDrops()
				.strength(-1.0F, 3600000.8F)
				.sound(SoundType.METAL);
	}

	public static AbstractBlock.Properties buildProperties(Material material, MaterialColor color, ToolType harvestToolType, int harvestLevel,
														   float destroyTime, float explosionResistance) {
		return AbstractBlock.Properties.of(material, color != null? color : material.getColor())
				.requiresCorrectToolForDrops()
				.harvestTool(harvestToolType)
				.harvestLevel(harvestLevel)
				.strength(destroyTime, explosionResistance);
	}
}
