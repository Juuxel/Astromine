package com.github.chainmailstudios.astromine.discoveries.registry;

import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.discoveries.common.world.decorator.MoonOreDecorator;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class AstromineDiscoveriesDecorators {
	public static FeatureDecorator<CountConfiguration> MOON_ORE = register("moon_ore_decorator", new MoonOreDecorator(CountConfiguration.CODEC));


	public static void initialize() {

	}

	private static <T extends DecoratorConfiguration, G extends FeatureDecorator<T>> G register(String name, G decorator) {
		return Registry.register(Registry.DECORATOR, AstromineCommon.identifier(name), decorator);
	}
}
