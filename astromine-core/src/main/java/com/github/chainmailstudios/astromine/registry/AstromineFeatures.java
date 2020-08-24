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

import com.github.chainmailstudios.astromine.common.world.feature.*;
import net.minecraft.datafixer.fix.BiomeRenameFix;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

import com.github.chainmailstudios.astromine.AstromineCommon;
import me.shedaniel.cloth.api.dynamic.registry.v1.BiomesRegistry;
import me.shedaniel.cloth.api.dynamic.registry.v1.DynamicRegistryCallback;
import net.earthcomputer.libstructure.LibStructure;

public class AstromineFeatures {
	public static final Identifier ASTEROID_ORES_ID = AstromineCommon.identifier("asteroid_ores");
	public static final Feature<DefaultFeatureConfig> ASTEROID_ORES = register(new AsteroidOreFeature(DefaultFeatureConfig.CODEC), ASTEROID_ORES_ID);
	public static final RegistryKey<ConfiguredFeature<?, ?>> ASTEROID_ORES_KEY = RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, ASTEROID_ORES_ID);

	public static final Identifier MOON_CRATER_ID = AstromineCommon.identifier("moon_crater");
	public static final Feature<DefaultFeatureConfig> MOON_CRATER = register(new MoonCraterFeature(DefaultFeatureConfig.CODEC), MOON_CRATER_ID);
	public static final RegistryKey<ConfiguredFeature<?, ?>> MOON_CRATER_KEY = RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, MOON_CRATER_ID);

	public static final Identifier METEOR_ID = AstromineCommon.identifier("meteor");
	public static final StructurePieceType METEOR_STRUCTURE = register(MeteorGenerator::new, METEOR_ID);
	public static final RegistryKey<ConfiguredStructureFeature<?, ?>> METEOR_KEY = RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN, METEOR_ID);

	public static final Identifier VOLCANO_ID = AstromineCommon.identifier("volcano");
	public static final StructurePieceType VOLCANO_STRUCTURE = register(VolcanoGenerator::new, VOLCANO_ID);
	public static final RegistryKey<ConfiguredStructureFeature<?, ?>> VOLCANO_KEY = RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN, VOLCANO_ID);

	public static <T extends FeatureConfig> Feature<T> register(Feature<T> feature, Identifier id) {
		return Registry.register(Registry.FEATURE, id, feature);
	}

	public static StructurePieceType register(StructurePieceType pieceType, Identifier id) {
		return Registry.register(Registry.STRUCTURE_PIECE, id, pieceType);
	}

	public static void initialize() {
		// initialize meteor structure/feature
		MeteorFeature meteor = new MeteorFeature(DefaultFeatureConfig.CODEC);
		ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> meteorStructure = meteor.configure(new DefaultFeatureConfig());
		LibStructure.registerStructure(METEOR_ID, meteor, GenerationStep.Feature.RAW_GENERATION, new StructureConfig(32, 8, 12345), meteorStructure);

		VolcanoFeature volcano = new VolcanoFeature(DefaultFeatureConfig.CODEC);
		ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> volcanoStructure = volcano.configure(new DefaultFeatureConfig());
		LibStructure.registerStructure(VOLCANO_ID, volcano, GenerationStep.Feature.RAW_GENERATION, new StructureConfig(0, 0, 0), volcanoStructure);

		DynamicRegistryCallback.callback(Registry.BIOME_KEY).register((manager, key, biome) -> {
			if (biome.getCategory() != Biome.Category.NETHER && biome.getCategory() != Biome.Category.THEEND) {
				BiomesRegistry.registerStructure(manager, biome, () -> meteorStructure);

				if (key.equals(AstromineBiomes.VULCAN_PLAINS)) {
					BiomesRegistry.registerStructure(manager, biome, () -> volcanoStructure);
				}
			}
		});
	}
}
