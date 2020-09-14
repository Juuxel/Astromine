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

import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.foundations.common.world.feature.MeteorFeature;
import com.github.chainmailstudios.astromine.foundations.common.world.feature.MeteorGenerator;
import com.github.chainmailstudios.astromine.registry.AstromineFeatures;
import me.shedaniel.cloth.api.dynamic.registry.v1.BiomesRegistry;
import me.shedaniel.cloth.api.dynamic.registry.v1.DynamicRegistryCallback;
import net.earthcomputer.libstructure.LibStructure;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.StructureSeparationSettings;

public class AstromineFoundationsFeatures extends AstromineFeatures {
	public static final ResourceLocation METEOR_ID = AstromineCommon.identifier("meteor");
	public static final IStructurePieceType METEOR_STRUCTURE = register(MeteorGenerator::new, METEOR_ID);
	public static final RegistryKey<StructureFeature<?, ?>> METEOR_KEY = RegistryKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, METEOR_ID);

	public static void initialize() {
		MeteorFeature meteor = new MeteorFeature(NoFeatureConfig.CODEC);
		StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> meteorStructure = meteor.configured(new NoFeatureConfig());
		LibStructure.registerStructure(METEOR_ID, meteor, GenerationStage.Decoration.RAW_GENERATION, new StructureSeparationSettings(32, 8, 12345), meteorStructure);

		DynamicRegistryCallback.callback(Registry.BIOME_REGISTRY).register((manager, key, biome) -> {
			if (biome.getBiomeCategory() != Biome.Category.NETHER && biome.getBiomeCategory() != Biome.Category.THEEND) {
				BiomesRegistry.registerStructure(manager, biome, () -> meteorStructure);
			}
		});
	}
}
