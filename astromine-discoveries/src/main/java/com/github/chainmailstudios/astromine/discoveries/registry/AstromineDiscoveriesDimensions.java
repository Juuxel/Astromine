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

import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.registry.AstromineDimensions;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

public class AstromineDiscoveriesDimensions extends AstromineDimensions {
	public static final ResourceLocation EARTH_SPACE_ID = AstromineCommon.identifier("earth_space");
	public static final RegistryKey<Dimension> EARTH_SPACE_OPTIONS = register(Registry.LEVEL_STEM_REGISTRY, EARTH_SPACE_ID);
	public static final RegistryKey<DimensionType> EARTH_SPACE_TYPE_KEY = register(Registry.DIMENSION_TYPE_REGISTRY, EARTH_SPACE_ID);
	public static final RegistryKey<World> EARTH_SPACE_WORLD = register(Registry.DIMENSION_REGISTRY, EARTH_SPACE_ID);

	public static final ResourceLocation MOON_ID = AstromineCommon.identifier("moon");
	public static final RegistryKey<Dimension> MOON_OPTIONS = register(Registry.LEVEL_STEM_REGISTRY, MOON_ID);
	public static final RegistryKey<DimensionType> MOON_TYPE_KEY = register(Registry.DIMENSION_TYPE_REGISTRY, MOON_ID);
	public static final RegistryKey<World> MOON_WORLD = register(Registry.DIMENSION_REGISTRY, MOON_ID);

	public static final ResourceLocation MARS_ID = AstromineCommon.identifier("mars");
	public static final RegistryKey<Dimension> MARS_OPTIONS = register(Registry.LEVEL_STEM_REGISTRY, MARS_ID);
	public static final RegistryKey<DimensionType> MARS_TYPE_KEY = register(Registry.DIMENSION_TYPE_REGISTRY, MARS_ID);
	public static final RegistryKey<World> MARS_WORLD = register(Registry.DIMENSION_REGISTRY, MARS_ID);

	public static final ResourceLocation VULCAN_ID = AstromineCommon.identifier("vulcan");
	public static final RegistryKey<Dimension> VULCAN_OPTIONS = register(Registry.LEVEL_STEM_REGISTRY, VULCAN_ID);
	public static final RegistryKey<DimensionType> VULCAN_TYPE_KEY = register(Registry.DIMENSION_TYPE_REGISTRY, VULCAN_ID);
	public static final RegistryKey<World> VULCAN_WORLD = register(Registry.DIMENSION_REGISTRY, VULCAN_ID);

	public static final ResourceLocation GLACIOS_ID = AstromineCommon.identifier("glacios");
	public static final RegistryKey<Dimension> GLACIOS_OPTIONS = register(Registry.LEVEL_STEM_REGISTRY, GLACIOS_ID);
	public static final RegistryKey<DimensionType> GLACIOS_TYPE_KEY = register(Registry.DIMENSION_TYPE_REGISTRY, GLACIOS_ID);
	public static final RegistryKey<World> GLACIOS_WORLD = register(Registry.DIMENSION_REGISTRY, GLACIOS_ID);

	public static void initialize() {

	}
}
