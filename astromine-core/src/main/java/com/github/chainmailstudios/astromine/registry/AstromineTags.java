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

import net.fabricmc.fabric.api.tag.TagRegistry;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

import com.github.chainmailstudios.astromine.AstromineCommon;

public class AstromineTags {
	public static final Tag<Block> GAS_TRAVERSABLE = TagRegistry.block(AstromineCommon.identifier("gas_traversable"));

	public static final Tag<Item> TRICKS_PIGLINS = TagRegistry.item(AstromineCommon.identifier("tricks_piglins"));
	public static final Tag<Item> PIGLIN_LOVED_NUGGETS = TagRegistry.item(AstromineCommon.identifier("piglin_loved_nuggets"));
	public static final Tag<Item> PIGLIN_BARTERING_ITEMS = TagRegistry.item(AstromineCommon.identifier("piglin_bartering_items"));
	public static final Tag<Item> PIGLIN_SAFE_ARMOR = TagRegistry.item(AstromineCommon.identifier("piglin_safe_armor"));

	public static final Tag<Fluid> NORMAL_BREATHABLE = TagRegistry.fluid(AstromineCommon.identifier("normal_breathable"));
	public static final Tag<Fluid> WATER_BREATHABLE = TagRegistry.fluid(AstromineCommon.identifier("water_breathable"));
	public static final Tag<Fluid> LAVA_BREATHABLE = TagRegistry.fluid(AstromineCommon.identifier("lava_breathable"));

	public static final Tag<EntityType<?>> DOES_NOT_BREATHE = TagRegistry.entityType(AstromineCommon.identifier("does_not_breathe"));
}
