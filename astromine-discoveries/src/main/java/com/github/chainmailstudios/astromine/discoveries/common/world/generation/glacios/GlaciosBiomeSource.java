package com.github.chainmailstudios.astromine.discoveries.common.world.generation.glacios;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import com.github.chainmailstudios.astromine.discoveries.registry.AstromineDiscoveriesBiomes;

import com.google.common.collect.ImmutableList;

public class GlaciosBiomeSource extends BiomeSource {
	public static final Codec<GlaciosBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter((biomeSource) -> biomeSource.registry),
			Codec.LONG.fieldOf("seed").stable().forGetter((biomeSource) -> biomeSource.seed))
			.apply(instance, instance.stable(GlaciosBiomeSource::new)));
	private final long seed;
	private final Registry<Biome> registry;

	public GlaciosBiomeSource(Registry<Biome> registry, long seed) {
		super(ImmutableList.of());
		this.seed = seed;
		this.registry = registry;
	}

	@Override
	protected Codec<? extends BiomeSource> codec() {
		return CODEC;
	}

	@Override
	public BiomeSource withSeed(long seed) {
		return new GlaciosBiomeSource(registry, seed);
	}

	@Override
	public Biome getNoiseBiome(int biomeX, int biomeY, int biomeZ) {
		return registry.get(AstromineDiscoveriesBiomes.GLACIOS);
	}
}