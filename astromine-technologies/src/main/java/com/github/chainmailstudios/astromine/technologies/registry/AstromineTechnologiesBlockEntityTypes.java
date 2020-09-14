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

package com.github.chainmailstudios.astromine.technologies.registry;

import com.github.chainmailstudios.astromine.registry.AstromineBlockEntityTypes;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.*;
import net.minecraft.tileentity.TileEntityType;

public class AstromineTechnologiesBlockEntityTypes extends AstromineBlockEntityTypes {
	public static final TileEntityType<HolographicBridgeProjectorBlockEntity> HOLOGRAPHIC_BRIDGE = register("holographic_bridge", HolographicBridgeProjectorBlockEntity::new, AstromineTechnologiesBlocks.HOLOGRAPHIC_BRIDGE_PROJECTOR);

	public static final TileEntityType<VentBlockEntity> VENT = register("vent", VentBlockEntity::new, AstromineTechnologiesBlocks.VENT);

	public static final TileEntityType<TankBlockEntity.Primitive> PRIMITIVE_TANK = register("primitive_tank", TankBlockEntity.Primitive::new, AstromineTechnologiesBlocks.PRIMITIVE_TANK);
	public static final TileEntityType<TankBlockEntity.Basic> BASIC_TANK = register("basic_tank", TankBlockEntity.Basic::new, AstromineTechnologiesBlocks.BASIC_TANK);
	public static final TileEntityType<TankBlockEntity.Advanced> ADVANCED_TANK = register("advanced_tank", TankBlockEntity.Advanced::new, AstromineTechnologiesBlocks.ADVANCED_TANK);
	public static final TileEntityType<TankBlockEntity.Elite> ELITE_TANK = register("elite_tank", TankBlockEntity.Elite::new, AstromineTechnologiesBlocks.ELITE_TANK);
	public static final TileEntityType<TankBlockEntity.Elite> CREATIVE_TANK = register("creative_tank", TankBlockEntity.Elite::new, AstromineTechnologiesBlocks.CREATIVE_TANK);

	public static final TileEntityType<BufferBlockEntity.Primitive> PRIMITIVE_BUFFER = register("primitive_buffer", BufferBlockEntity.Primitive::new, AstromineTechnologiesBlocks.PRIMITIVE_BUFFER);
	public static final TileEntityType<BufferBlockEntity.Basic> BASIC_BUFFER = register("basic_buffer", BufferBlockEntity.Basic::new, AstromineTechnologiesBlocks.BASIC_BUFFER);
	public static final TileEntityType<BufferBlockEntity.Advanced> ADVANCED_BUFFER = register("advanced_buffer", BufferBlockEntity.Advanced::new, AstromineTechnologiesBlocks.ADVANCED_BUFFER);
	public static final TileEntityType<BufferBlockEntity.Elite> ELITE_BUFFER = register("elite_buffer", BufferBlockEntity.Elite::new, AstromineTechnologiesBlocks.ELITE_BUFFER);
	public static final TileEntityType<BufferBlockEntity.Elite> CREATIVE_BUFFER = register("creative_buffer", BufferBlockEntity.Elite::new, AstromineTechnologiesBlocks.CREATIVE_BUFFER);

	public static final TileEntityType<SolidGeneratorBlockEntity.Primitive> PRIMITIVE_SOLID_GENERATOR = register("primitive_solid_generator", SolidGeneratorBlockEntity.Primitive::new, AstromineTechnologiesBlocks.PRIMITIVE_SOLID_GENERATOR);
	public static final TileEntityType<SolidGeneratorBlockEntity.Basic> BASIC_SOLID_GENERATOR = register("basic_solid_generator", SolidGeneratorBlockEntity.Basic::new, AstromineTechnologiesBlocks.BASIC_SOLID_GENERATOR);
	public static final TileEntityType<SolidGeneratorBlockEntity.Advanced> ADVANCED_SOLID_GENERATOR = register("advanced_solid_generator", SolidGeneratorBlockEntity.Advanced::new, AstromineTechnologiesBlocks.ADVANCED_SOLID_GENERATOR);
	public static final TileEntityType<SolidGeneratorBlockEntity.Elite> ELITE_SOLID_GENERATOR = register("elite_solid_generator", SolidGeneratorBlockEntity.Elite::new, AstromineTechnologiesBlocks.ELITE_SOLID_GENERATOR);

	public static final TileEntityType<LiquidGeneratorBlockEntity.Primitive> PRIMITIVE_LIQUID_GENERATOR = register("primitive_liquid_generator", LiquidGeneratorBlockEntity.Primitive::new, AstromineTechnologiesBlocks.PRIMITIVE_LIQUID_GENERATOR);
	public static final TileEntityType<LiquidGeneratorBlockEntity.Basic> BASIC_LIQUID_GENERATOR = register("basic_liquid_generator", LiquidGeneratorBlockEntity.Basic::new, AstromineTechnologiesBlocks.BASIC_LIQUID_GENERATOR);
	public static final TileEntityType<LiquidGeneratorBlockEntity.Advanced> ADVANCED_LIQUID_GENERATOR = register("advanced_liquid_generator", LiquidGeneratorBlockEntity.Advanced::new, AstromineTechnologiesBlocks.ADVANCED_LIQUID_GENERATOR);
	public static final TileEntityType<LiquidGeneratorBlockEntity.Elite> ELITE_LIQUID_GENERATOR = register("elite_liquid_generator", LiquidGeneratorBlockEntity.Elite::new, AstromineTechnologiesBlocks.ELITE_LIQUID_GENERATOR);

	public static final TileEntityType<ElectricSmelterBlockEntity.Primitive> PRIMITIVE_ELECTRIC_SMELTER = register("primitive_electric_smelter", ElectricSmelterBlockEntity.Primitive::new, AstromineTechnologiesBlocks.PRIMITIVE_ELECTRIC_SMELTER);
	public static final TileEntityType<ElectricSmelterBlockEntity.Basic> BASIC_ELECTRIC_SMELTER = register("basic_electric_smelter", ElectricSmelterBlockEntity.Basic::new, AstromineTechnologiesBlocks.BASIC_ELECTRIC_SMELTER);
	public static final TileEntityType<ElectricSmelterBlockEntity.Advanced> ADVANCED_ELECTRIC_SMELTER = register("advanced_electric_smelter", ElectricSmelterBlockEntity.Advanced::new, AstromineTechnologiesBlocks.ADVANCED_ELECTRIC_SMELTER);
	public static final TileEntityType<ElectricSmelterBlockEntity.Elite> ELITE_ELECTRIC_SMELTER = register("elite_electric_smelter", ElectricSmelterBlockEntity.Elite::new, AstromineTechnologiesBlocks.ELITE_ELECTRIC_SMELTER);

	public static final TileEntityType<AlloySmelterBlockEntity.Primitive> PRIMITIVE_ALLOY_SMELTER = register("primitive_alloy_smelter", AlloySmelterBlockEntity.Primitive::new, AstromineTechnologiesBlocks.PRIMITIVE_ALLOY_SMELTER);
	public static final TileEntityType<AlloySmelterBlockEntity.Basic> BASIC_ALLOY_SMELTER = register("basic_alloy_smelter", AlloySmelterBlockEntity.Basic::new, AstromineTechnologiesBlocks.BASIC_ALLOY_SMELTER);
	public static final TileEntityType<AlloySmelterBlockEntity.Advanced> ADVANCED_ALLOY_SMELTER = register("advanced_alloy_smelter", AlloySmelterBlockEntity.Advanced::new, AstromineTechnologiesBlocks.ADVANCED_ALLOY_SMELTER);
	public static final TileEntityType<AlloySmelterBlockEntity.Elite> ELITE_ALLOY_SMELTER = register("elite_alloy_smelter", AlloySmelterBlockEntity.Elite::new, AstromineTechnologiesBlocks.ELITE_ALLOY_SMELTER);

	public static final TileEntityType<TrituratorBlockEntity.Primitive> PRIMITIVE_TRITURATOR = register("primitive_triturator", TrituratorBlockEntity.Primitive::new, AstromineTechnologiesBlocks.PRIMITIVE_TRITURATOR);
	public static final TileEntityType<TrituratorBlockEntity.Basic> BASIC_TRITURATOR = register("basic_triturator", TrituratorBlockEntity.Basic::new, AstromineTechnologiesBlocks.BASIC_TRITURATOR);
	public static final TileEntityType<TrituratorBlockEntity.Advanced> ADVANCED_TRITURATOR = register("advanced_triturator", TrituratorBlockEntity.Advanced::new, AstromineTechnologiesBlocks.ADVANCED_TRITURATOR);
	public static final TileEntityType<TrituratorBlockEntity.Elite> ELITE_TRITURATOR = register("elite_triturator", TrituratorBlockEntity.Elite::new, AstromineTechnologiesBlocks.ELITE_TRITURATOR);

	public static final TileEntityType<PresserBlockEntity.Primitive> PRIMITIVE_PRESSER = register("primitive_presser", PresserBlockEntity.Primitive::new, AstromineTechnologiesBlocks.PRIMITIVE_PRESSER);
	public static final TileEntityType<PresserBlockEntity.Basic> BASIC_PRESSER = register("basic_presser", PresserBlockEntity.Basic::new, AstromineTechnologiesBlocks.BASIC_PRESSER);
	public static final TileEntityType<PresserBlockEntity.Advanced> ADVANCED_PRESSER = register("advanced_presser", PresserBlockEntity.Advanced::new, AstromineTechnologiesBlocks.ADVANCED_PRESSER);
	public static final TileEntityType<PresserBlockEntity.Elite> ELITE_PRESSER = register("elite_presser", PresserBlockEntity.Elite::new, AstromineTechnologiesBlocks.ELITE_PRESSER);

	public static final TileEntityType<ElectrolyzerBlockEntity.Primitive> PRIMITIVE_ELECTROLYZER = register("primitive_electrolyzer", ElectrolyzerBlockEntity.Primitive::new, AstromineTechnologiesBlocks.PRIMITIVE_ELECTROLYZER);
	public static final TileEntityType<ElectrolyzerBlockEntity.Basic> BASIC_ELECTROLYZER = register("basic_electrolyzer", ElectrolyzerBlockEntity.Basic::new, AstromineTechnologiesBlocks.BASIC_ELECTROLYZER);
	public static final TileEntityType<ElectrolyzerBlockEntity.Advanced> ADVANCED_ELECTROLYZER = register("advanced_electrolyzer", ElectrolyzerBlockEntity.Advanced::new, AstromineTechnologiesBlocks.ADVANCED_ELECTROLYZER);
	public static final TileEntityType<ElectrolyzerBlockEntity.Elite> ELITE_ELECTROLYZER = register("elite_electrolyzer", ElectrolyzerBlockEntity.Elite::new, AstromineTechnologiesBlocks.ELITE_ELECTROLYZER);

	public static final TileEntityType<FluidMixerBlockEntity.Primitive> PRIMITIVE_FLUID_MIXER = register("primitive_fluid_mixer", FluidMixerBlockEntity.Primitive::new, AstromineTechnologiesBlocks.PRIMITIVE_FLUID_MIXER);
	public static final TileEntityType<FluidMixerBlockEntity.Basic> BASIC_FLUID_MIXER = register("basic_fluid_mixer", FluidMixerBlockEntity.Basic::new, AstromineTechnologiesBlocks.BASIC_FLUID_MIXER);
	public static final TileEntityType<FluidMixerBlockEntity.Advanced> ADVANCED_FLUID_MIXER = register("advanced_fluid_mixer", FluidMixerBlockEntity.Advanced::new, AstromineTechnologiesBlocks.ADVANCED_FLUID_MIXER);
	public static final TileEntityType<FluidMixerBlockEntity.Elite> ELITE_FLUID_MIXER = register("elite_fluid_mixer", FluidMixerBlockEntity.Elite::new, AstromineTechnologiesBlocks.ELITE_FLUID_MIXER);

	public static final TileEntityType<CapacitorBlockEntity.Primitive> PRIMITIVE_CAPACITOR = register("primitive_capacitor", CapacitorBlockEntity.Primitive::new, AstromineTechnologiesBlocks.PRIMITIVE_CAPACITOR);
	public static final TileEntityType<CapacitorBlockEntity.Basic> BASIC_CAPACITOR = register("basic_capacitor", CapacitorBlockEntity.Basic::new, AstromineTechnologiesBlocks.BASIC_CAPACITOR);
	public static final TileEntityType<CapacitorBlockEntity.Advanced> ADVANCED_CAPACITOR = register("advanced_capacitor", CapacitorBlockEntity.Advanced::new, AstromineTechnologiesBlocks.ADVANCED_CAPACITOR);
	public static final TileEntityType<CapacitorBlockEntity.Elite> ELITE_CAPACITOR = register("elite_capacitor", CapacitorBlockEntity.Elite::new, AstromineTechnologiesBlocks.ELITE_CAPACITOR);
	public static final TileEntityType<CapacitorBlockEntity.Creative> CREATIVE_CAPACITOR = register("creative_capacitor", CapacitorBlockEntity.Creative::new, AstromineTechnologiesBlocks.CREATIVE_CAPACITOR);

	public static final TileEntityType<FluidExtractorBlockEntity> FLUID_EXTRACTOR = register("fluid_extractor", FluidExtractorBlockEntity::new, AstromineTechnologiesBlocks.FLUID_EXTRACTOR);
	public static final TileEntityType<FluidInserterBlockEntity> FLUID_INSERTER = register("fluid_inserter", FluidInserterBlockEntity::new, AstromineTechnologiesBlocks.FLUID_INSERTER);

	public static final TileEntityType<BlockBreakerBlockEntity> BLOCK_BREAKER = register("block_breaker", BlockBreakerBlockEntity::new, AstromineTechnologiesBlocks.BLOCK_BREAKER);
	public static final TileEntityType<BlockPlacerBlockEntity> BLOCK_PLACER = register("block_placer", BlockPlacerBlockEntity::new, AstromineTechnologiesBlocks.BLOCK_PLACER);

	public static void initialize() {

	}
}
