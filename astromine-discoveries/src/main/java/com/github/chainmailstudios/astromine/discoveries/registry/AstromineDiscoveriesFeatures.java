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
import com.github.chainmailstudios.astromine.discoveries.common.world.feature.AsteroidOreFeature;
import com.github.chainmailstudios.astromine.discoveries.common.world.feature.MoonCraterFeature;
import com.github.chainmailstudios.astromine.discoveries.common.world.feature.MoonLakeFeature;
import com.github.chainmailstudios.astromine.discoveries.common.world.feature.MoonOreFeature;
import com.github.chainmailstudios.astromine.registry.AstromineFeatures;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class AstromineDiscoveriesFeatures extends AstromineFeatures {
	public static final ResourceLocation ASTEROID_ORES_ID = AstromineCommon.identifier("asteroid_ores");
	public static final Feature<NoFeatureConfig> ASTEROID_ORES = register(new AsteroidOreFeature(NoFeatureConfig.CODEC), ASTEROID_ORES_ID);
	public static final RegistryKey<ConfiguredFeature<?, ?>> ASTEROID_ORES_KEY = RegistryKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, ASTEROID_ORES_ID);

	public static final ResourceLocation MOON_CRATER_ID = AstromineCommon.identifier("moon_crater");
	public static final Feature<NoFeatureConfig> MOON_CRATER = register(new MoonCraterFeature(NoFeatureConfig.CODEC), MOON_CRATER_ID);
	public static final RegistryKey<ConfiguredFeature<?, ?>> MOON_CRATER_KEY = RegistryKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, MOON_CRATER_ID);

	public static final ResourceLocation MOON_LAKE_ID = AstromineCommon.identifier("moon_lake");
	public static final Feature<NoFeatureConfig> MOON_LAKE = register(new MoonLakeFeature(NoFeatureConfig.CODEC), MOON_LAKE_ID);
	public static final RegistryKey<ConfiguredFeature<?, ?>> MOON_LAKE_KEY = RegistryKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, MOON_LAKE_ID);

	public static final ResourceLocation MOON_ORE_ID = AstromineCommon.identifier("moon_ore");
	public static final Feature<NoFeatureConfig> MOON_ORE = register(new MoonOreFeature(NoFeatureConfig.CODEC), MOON_ORE_ID);
	public static final RegistryKey<ConfiguredFeature<?, ?>> MOON_ORE_KEY = RegistryKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, MOON_ORE_ID);

	public static void initialize() {

	}
}
