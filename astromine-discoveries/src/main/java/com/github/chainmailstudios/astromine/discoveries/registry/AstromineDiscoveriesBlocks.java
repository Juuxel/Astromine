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

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MagmaBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import com.github.chainmailstudios.astromine.discoveries.common.block.AltarBlock;
import com.github.chainmailstudios.astromine.discoveries.common.block.AltarPedestalBlock;
import com.github.chainmailstudios.astromine.foundations.common.block.AstromineOreBlock;
import com.github.chainmailstudios.astromine.foundations.registry.AstromineFoundationsItems;
import com.github.chainmailstudios.astromine.registry.AstromineBlocks;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;

public class AstromineDiscoveriesBlocks extends AstromineBlocks {
	private static final AbstractBlock.Properties ASTEROID_STONE_PROPERTIES = buildProperties(Material.STONE, MaterialColor.COLOR_GRAY, ToolType.PICKAXE, 3, 50, 1500);
	private static final AbstractBlock.Properties MOON_STONE_PROPERTIES = buildProperties(Material.STONE, MaterialColor.COLOR_GRAY, ToolType.PICKAXE, 0, 1, 3);
	private static final AbstractBlock.Properties VULCAN_STONE_PROPERTIES = buildProperties(Material.STONE, MaterialColor.COLOR_GRAY, ToolType.PICKAXE, 1, 3, 4);
	private static final AbstractBlock.Properties MARTIAN_STONE_PROPERTIES = buildProperties(Material.STONE, MaterialColor.COLOR_RED, ToolType.PICKAXE, 0, 1.5F, 6.0F);

	public static final RegistryObject<Block> ASTEROID_STONE = register("asteroid_stone", () -> new Block(ASTEROID_STONE_PROPERTIES), AstromineDiscoveriesItems.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> MOON_STONE = register("moon_stone", () -> new Block(MOON_STONE_PROPERTIES), AstromineDiscoveriesItems.getBasicSettings());
	public static final RegistryObject<Block> VULCAN_STONE = register("vulcan_stone", () -> new Block(VULCAN_STONE_PROPERTIES), AstromineDiscoveriesItems.getBasicSettings());
	public static final RegistryObject<Block> MARTIAN_SOIL = register("martian_soil", () -> new Block(buildProperties(Material.DIRT, MaterialColor.COLOR_RED, ToolType.SHOVEL, 0, 0.5F, 0.75F)), AstromineDiscoveriesItems.getBasicSettings());
	public static final RegistryObject<Block> MARTIAN_STONE = register("martian_stone", () -> new Block(MARTIAN_STONE_PROPERTIES), AstromineDiscoveriesItems.getBasicSettings());
	public static final RegistryObject<Block> BLAZING_ASTEROID_STONE = register("blazing_asteroid_stone", () -> new MagmaBlock(buildProperties(Material.STONE, MaterialColor.COLOR_GRAY, ToolType.PICKAXE, 3, 50, 1500).lightLevel((state) -> 3).randomTicks()
		.isValidSpawn((state, world, pos, entityType) -> entityType.fireImmune()).hasPostProcess((state, world, pos) -> true).emissiveRendering((state, world, pos) -> true)), AstromineDiscoveriesItems.getBasicSettings().fireResistant());

	public static final RegistryObject<Block> ASTEROID_STONE_SLAB = register("asteroid_stone_slab", () -> new SlabBlock(ASTEROID_STONE_PROPERTIES), AstromineDiscoveriesItems.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> MOON_STONE_SLAB = register("moon_stone_slab", () -> new SlabBlock(MOON_STONE_PROPERTIES), AstromineDiscoveriesItems.getBasicSettings());
	public static final RegistryObject<Block> VULCAN_STONE_SLAB = register("vulcan_stone_slab", () -> new SlabBlock(VULCAN_STONE_PROPERTIES), AstromineDiscoveriesItems.getBasicSettings());
	public static final RegistryObject<Block> MARTIAN_STONE_SLAB = register("martian_stone_slab", () -> new SlabBlock(MARTIAN_STONE_PROPERTIES), AstromineDiscoveriesItems.getBasicSettings());

	public static final RegistryObject<Block> ASTEROID_STONE_STAIRS = register("asteroid_stone_stairs", () -> new StairsBlock(() -> ASTEROID_STONE.get().defaultBlockState(), ASTEROID_STONE_PROPERTIES), AstromineDiscoveriesItems.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> MOON_STONE_STAIRS = register("moon_stone_stairs", () -> new StairsBlock(() -> MOON_STONE.get().defaultBlockState(), MOON_STONE_PROPERTIES), AstromineDiscoveriesItems.getBasicSettings());
	public static final RegistryObject<Block> VULCAN_STONE_STAIRS = register("vulcan_stone_stairs", () -> new StairsBlock(() -> VULCAN_STONE.get().defaultBlockState(), VULCAN_STONE_PROPERTIES), AstromineDiscoveriesItems.getBasicSettings());
	public static final RegistryObject<Block> MARTIAN_STONE_STAIRS = register("martian_stone_stairs", () -> new StairsBlock(() -> MARTIAN_STONE.get().defaultBlockState(), MARTIAN_STONE_PROPERTIES), AstromineDiscoveriesItems.getBasicSettings());

	public static final RegistryObject<Block> ASTEROID_STONE_WALL = register("asteroid_stone_wall", () -> new WallBlock(ASTEROID_STONE_PROPERTIES), AstromineDiscoveriesItems.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> MOON_STONE_WALL = register("moon_stone_wall", () -> new WallBlock(MOON_STONE_PROPERTIES), AstromineDiscoveriesItems.getBasicSettings());
	public static final RegistryObject<Block> VULCAN_STONE_WALL = register("vulcan_stone_wall", () -> new WallBlock(VULCAN_STONE_PROPERTIES), AstromineDiscoveriesItems.getBasicSettings());
	public static final RegistryObject<Block> MARTIAN_STONE_WALL = register("martian_stone_wall", () -> new WallBlock(MARTIAN_STONE_PROPERTIES), AstromineDiscoveriesItems.getBasicSettings());

	public static final RegistryObject<Block> ASTEROID_METITE_ORE = register("asteroid_metite_ore", () -> new AstromineOreBlock(buildProperties(Material.STONE, null, ToolType.PICKAXE, 4, 15, 100).sound(SoundType.STONE)), AstromineDiscoveriesItems
		.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> ASTEROID_ASTERITE_ORE = register("asteroid_asterite_ore", () -> new AstromineOreBlock(buildProperties(Material.STONE, null, ToolType.PICKAXE, 4, 40, 1000).sound(SoundType.STONE)), AstromineDiscoveriesItems
		.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> ASTEROID_STELLUM_ORE = register("asteroid_stellum_ore", () -> new AstromineOreBlock(buildProperties(Material.STONE, null, ToolType.PICKAXE, 4, 25, 80).sound(SoundType.STONE)), AstromineDiscoveriesItems
		.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> ASTEROID_GALAXIUM_ORE = register("asteroid_galaxium_ore", () -> new AstromineOreBlock(buildProperties(Material.STONE, null, ToolType.PICKAXE, 5, 80, 1300).sound(SoundType.STONE)), AstromineDiscoveriesItems
		.getBasicSettings().fireResistant());

	public static final RegistryObject<Block> ASTEROID_COPPER_ORE = register("asteroid_copper_ore", () -> new AstromineOreBlock(buildProperties(Material.STONE, null, ToolType.PICKAXE, 4, 15, 100).sound(SoundType.STONE)), AstromineDiscoveriesItems
		.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> ASTEROID_TIN_ORE = register("asteroid_tin_ore", () -> new AstromineOreBlock(buildProperties(Material.STONE, null, ToolType.PICKAXE, 4, 15, 100).sound(SoundType.STONE)), AstromineDiscoveriesItems
		.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> ASTEROID_SILVER_ORE = register("asteroid_silver_ore", () -> new AstromineOreBlock(buildProperties(Material.STONE, null, ToolType.PICKAXE, 4, 15, 100).sound(SoundType.STONE)), AstromineDiscoveriesItems
		.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> ASTEROID_LEAD_ORE = register("asteroid_lead_ore", () -> new AstromineOreBlock(buildProperties(Material.STONE, null, ToolType.PICKAXE, 4, 15, 100).sound(SoundType.STONE)), AstromineDiscoveriesItems
		.getBasicSettings().fireResistant());

	public static final RegistryObject<Block> ASTEROID_COAL_ORE = register("asteroid_coal_ore", () -> new AstromineOreBlock(buildProperties(Material.STONE, null, ToolType.PICKAXE, 4, 15, 100).sound(SoundType.STONE)), AstromineDiscoveriesItems
		.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> ASTEROID_IRON_ORE = register("asteroid_iron_ore", () -> new AstromineOreBlock(buildProperties(Material.STONE, null, ToolType.PICKAXE, 4, 15, 100).sound(SoundType.STONE)), AstromineDiscoveriesItems
		.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> ASTEROID_GOLD_ORE = register("asteroid_gold_ore", () -> new AstromineOreBlock(buildProperties(Material.STONE, null, ToolType.PICKAXE, 4, 15, 100).sound(SoundType.STONE)), AstromineDiscoveriesItems
		.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> ASTEROID_REDSTONE_ORE = register("asteroid_redstone_ore", () -> new AstromineOreBlock(buildProperties(Material.STONE, null, ToolType.PICKAXE, 4, 15, 100).sound(SoundType.STONE)), AstromineDiscoveriesItems
		.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> ASTEROID_LAPIS_ORE = register("asteroid_lapis_ore", () -> new AstromineOreBlock(buildProperties(Material.STONE, null, ToolType.PICKAXE, 4, 15, 100).sound(SoundType.STONE)), AstromineDiscoveriesItems
		.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> ASTEROID_DIAMOND_ORE = register("asteroid_diamond_ore", () -> new AstromineOreBlock(buildProperties(Material.STONE, null, ToolType.PICKAXE, 4, 15, 100).sound(SoundType.STONE)), AstromineDiscoveriesItems
		.getBasicSettings().fireResistant());
	public static final RegistryObject<Block> ASTEROID_EMERALD_ORE = register("asteroid_emerald_ore", () -> new AstromineOreBlock(buildProperties(Material.STONE, null, ToolType.PICKAXE, 4, 15, 100).sound(SoundType.STONE)), AstromineDiscoveriesItems
		.getBasicSettings().fireResistant());

	public static final RegistryObject<Block> MOON_LUNUM_ORE = register("moon_lunum_ore", () -> new AstromineOreBlock(buildProperties(Material.STONE, null, ToolType.PICKAXE, 4, 5, 10).sound(SoundType.STONE)), AstromineDiscoveriesItems
		.getBasicSettings());

	public static final RegistryObject<Block> ALTAR_PEDESTAL = register("altar_pedestal", () -> new AltarPedestalBlock(buildProperties(Material.METAL, MaterialColor.GOLD, ToolType.PICKAXE, 2, 5, 6).sound(SoundType.METAL).noOcclusion()),
		AstromineFoundationsItems.getBasicSettings());
	public static final RegistryObject<Block> ALTAR = register("altar", () -> new AltarBlock(buildProperties(Material.METAL, MaterialColor.GOLD, ToolType.PICKAXE, 2,5, 6).sound(SoundType.METAL).noOcclusion()), AstromineFoundationsItems
		.getBasicSettings());

	public static void initialize() {

	}
}
