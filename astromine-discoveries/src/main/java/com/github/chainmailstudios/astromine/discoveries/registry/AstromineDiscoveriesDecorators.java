package com.github.chainmailstudios.astromine.discoveries.registry;

import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.discoveries.common.world.decorator.MoonOreDecorator;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeatureSpreadConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

public class AstromineDiscoveriesDecorators {
	public static Placement<FeatureSpreadConfig> MOON_ORE = register("moon_ore_decorator", new MoonOreDecorator(FeatureSpreadConfig.CODEC));


	public static void initialize() {

	}

	private static <T extends IPlacementConfig, G extends Placement<T>> G register(String name, G decorator) {
		return Registry.register(Registry.DECORATOR, AstromineCommon.identifier(name), decorator);
	}
}
