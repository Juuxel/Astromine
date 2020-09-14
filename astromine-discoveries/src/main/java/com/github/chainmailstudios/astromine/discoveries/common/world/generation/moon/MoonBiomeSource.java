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

package com.github.chainmailstudios.astromine.discoveries.common.world.generation.moon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.github.chainmailstudios.astromine.discoveries.common.world.layer.moon.MoonBiomeLayer;

import com.google.common.collect.ImmutableList;
import java.util.function.LongFunction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.layer.Layer;
import net.minecraft.world.gen.layer.ZoomLayer;

public class MoonBiomeSource extends BiomeProvider {
	public static Codec<MoonBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.LONG.fieldOf("seed").stable().forGetter(source -> source.seed), RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(source -> source.biomeRegistry)).apply(instance,
		instance.stable(MoonBiomeSource::new)));
	private final long seed;
	private final Registry<Biome> biomeRegistry;
	private final Layer sampler;

	public MoonBiomeSource(long seed, Registry<Biome> biomeRegistry) {
		super(ImmutableList.of());
		this.seed = seed;
		this.biomeRegistry = biomeRegistry;
		this.sampler = build(seed);
	}

	@Override
	protected Codec<? extends BiomeProvider> codec() {
		return CODEC;
	}

	@Override
	public BiomeProvider withSeed(long seed) {
		return new MoonBiomeSource(seed, biomeRegistry);
	}

	@Override
	public Biome getNoiseBiome(int biomeX, int biomeY, int biomeZ) {
		return sampler.get(biomeRegistry, biomeX, biomeZ);
	}

	public Layer build(long seed) {
		return new Layer(build((salt) -> new LazyAreaLayerContext(25, seed, salt)));
	}

	private <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> build(LongFunction<C> contextProvider) {
		IAreaFactory<T> mainLayer = new MoonBiomeLayer(biomeRegistry).run(contextProvider.apply(4L));
		for (int i = 0; i < 5; i++) {
			mainLayer = ZoomLayer.FUZZY.run(contextProvider.apply(43 + i), mainLayer);
		}

		return mainLayer;
	}
}
