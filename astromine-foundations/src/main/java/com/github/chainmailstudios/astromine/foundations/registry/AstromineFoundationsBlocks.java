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

package com.github.chainmailstudios.astromine.foundations.registry;

import com.github.chainmailstudios.astromine.foundations.common.block.AstromineOreBlock;
import com.github.chainmailstudios.astromine.registry.AstromineBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.fml.RegistryObject;

public class AstromineFoundationsBlocks extends AstromineBlocks {
	// Materials - Ores
	public static final RegistryObject<Block> COPPER_ORE = register("copper_ore", () -> new AstromineOreBlock(FabricBlockSettings.of(Material.STONE).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 1).strength(3, 3).sound(SoundType.STONE)), AstromineFoundationsItems.getBasicSettings());
	public static final RegistryObject<Block> TIN_ORE = register("tin_ore", () -> new AstromineOreBlock(FabricBlockSettings.of(Material.STONE).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 1).strength(3, 3).sound(SoundType.STONE)), AstromineFoundationsItems.getBasicSettings());
	public static final RegistryObject<Block> SILVER_ORE = register("silver_ore", () -> new AstromineOreBlock(FabricBlockSettings.of(Material.STONE).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 2).strength(3, 3).sound(SoundType.STONE)), AstromineFoundationsItems.getBasicSettings());
	public static final RegistryObject<Block> LEAD_ORE = register("lead_ore", () -> new AstromineOreBlock(FabricBlockSettings.of(Material.STONE).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 2).strength(3, 3).sound(SoundType.STONE)), AstromineFoundationsItems.getBasicSettings());

	public static final RegistryObject<Block> METEOR_METITE_ORE = register("meteor_metite_ore", () -> new AstromineOreBlock(FabricBlockSettings.of(Material.STONE).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 4).strength(15, 100).sound(SoundType.STONE)), AstromineFoundationsItems
			.getBasicSettings().fireResistant());

	// Material - Blocks
	public static final RegistryObject<Block> METITE_BLOCK = register("metite_block", () -> new Block(FabricBlockSettings.of(Material.METAL, MaterialColor.COLOR_PINK).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 4).strength(8, 100).sound(SoundType.METAL)), AstromineFoundationsItems
			.getBasicSettings());
	public static final RegistryObject<Block> ASTERITE_BLOCK = register("asterite_block", () -> new Block(FabricBlockSettings.of(Material.METAL, MaterialColor.COLOR_RED).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 4).strength(25, 1000).sound(SoundType.METAL)), AstromineFoundationsItems
			.getBasicSettings());
	public static final RegistryObject<Block> STELLUM_BLOCK = register("stellum_block", () -> new Block(FabricBlockSettings.of(Material.METAL, MaterialColor.COLOR_ORANGE).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 4).strength(10, 80).sound(SoundType.METAL)), AstromineFoundationsItems
			.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> GALAXIUM_BLOCK = register("galaxium_block", () -> new Block(FabricBlockSettings.of(Material.METAL, MaterialColor.COLOR_PURPLE).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 5).strength(50, 1300).sound(SoundType.METAL)), AstromineFoundationsItems
			.getBasicSettings());
	public static final RegistryObject<Block> UNIVITE_BLOCK = register("univite_block", () -> new Block(FabricBlockSettings.of(Material.METAL, MaterialColor.SNOW).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 6).strength(80, 2000).sound(SoundType.METAL)), AstromineFoundationsItems
			.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> LUNUM_BLOCK = register("lunum_block", () -> new Block(FabricBlockSettings.of(Material.METAL, MaterialColor.TERRACOTTA_WHITE).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 4).strength(10, 75).sound(SoundType.METAL)), AstromineFoundationsItems
			.getBasicSettings());

	public static final RegistryObject<Block> COPPER_BLOCK = register("copper_block", () -> new Block(FabricBlockSettings.of(Material.METAL, MaterialColor.COLOR_ORANGE).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 1).strength(4, 6).sound(SoundType.METAL)), AstromineFoundationsItems
			.getBasicSettings());
	public static final RegistryObject<Block> TIN_BLOCK = register("tin_block", () -> new Block(FabricBlockSettings.of(Material.METAL, MaterialColor.COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 1).strength(4, 6).sound(SoundType.METAL)), AstromineFoundationsItems
			.getBasicSettings());
	public static final RegistryObject<Block> SILVER_BLOCK = register("silver_block", () -> new Block(FabricBlockSettings.of(Material.METAL, MaterialColor.COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 2).strength(5, 6).sound(SoundType.METAL)), AstromineFoundationsItems
			.getBasicSettings());
	public static final RegistryObject<Block> LEAD_BLOCK = register("lead_block", () -> new Block(FabricBlockSettings.of(Material.METAL, MaterialColor.TERRACOTTA_BLUE).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 2).strength(6, 8).sound(SoundType.METAL)), AstromineFoundationsItems
			.getBasicSettings());

	public static final RegistryObject<Block> BRONZE_BLOCK = register("bronze_block", () -> new Block(FabricBlockSettings.of(Material.METAL, MaterialColor.TERRACOTTA_ORANGE).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 2).strength(6, 6).sound(SoundType.METAL)), AstromineFoundationsItems
			.getBasicSettings());
	public static final RegistryObject<Block> STEEL_BLOCK = register("steel_block", () -> new Block(FabricBlockSettings.of(Material.METAL, MaterialColor.COLOR_GRAY).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 2).strength(8, 6).sound(SoundType.METAL)), AstromineFoundationsItems
			.getBasicSettings());
	public static final RegistryObject<Block> ELECTRUM_BLOCK = register("electrum_block", () -> new Block(FabricBlockSettings.of(Material.METAL, MaterialColor.COLOR_YELLOW).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 2).strength(6, 6).sound(SoundType.METAL)), AstromineFoundationsItems
			.getBasicSettings());
	public static final RegistryObject<Block> ROSE_GOLD_BLOCK = register("rose_gold_block", () -> new Block(FabricBlockSettings.of(Material.METAL, MaterialColor.COLOR_RED).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 2).strength(3, 6).sound(SoundType.METAL)), AstromineFoundationsItems
			.getBasicSettings());
	public static final RegistryObject<Block> STERLING_SILVER_BLOCK = register("sterling_silver_block", () -> new Block(FabricBlockSettings.of(Material.METAL, MaterialColor.COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 2).strength(6, 6).sound(SoundType.METAL)),
			AstromineFoundationsItems.getBasicSettings());
	public static final RegistryObject<Block> FOOLS_GOLD_BLOCK = register("fools_gold_block", () -> new Block(FabricBlockSettings.of(Material.METAL, MaterialColor.GOLD).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 2).strength(5, 6).sound(SoundType.METAL)), AstromineFoundationsItems
			.getBasicSettings());
	public static final RegistryObject<Block> METEORIC_STEEL_BLOCK = register("meteoric_steel_block", () -> new Block(FabricBlockSettings.of(Material.METAL, MaterialColor.GOLD).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 2).strength(5, 6).sound(SoundType.METAL)), AstromineFoundationsItems
			.getBasicSettings());

	public static final RegistryObject<Block> METEOR_STONE = register("meteor_stone", () -> new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.COLOR_BLACK).requiresCorrectToolForDrops().breakByTool(FabricToolTags.PICKAXES, 3).strength(30, 1500)), AstromineFoundationsItems.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> METEOR_STONE_SLAB = register("meteor_stone_slab", () -> new SlabBlock(FabricBlockSettings.copyOf(METEOR_STONE)), AstromineFoundationsItems.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> METEOR_STONE_STAIRS = register("meteor_stone_stairs", () -> new StairsBlock(METEOR_STONE.defaultBlockState(), FabricBlockSettings.copyOf(METEOR_STONE)), AstromineFoundationsItems.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> METEOR_STONE_WALL = register("meteor_stone_wall", () -> new WallBlock(FabricBlockSettings.copyOf(METEOR_STONE)), AstromineFoundationsItems.getBasicSettings().fireResistant());

	public static void initialize() {

	}
}
