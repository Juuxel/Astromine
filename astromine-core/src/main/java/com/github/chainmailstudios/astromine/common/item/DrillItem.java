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

package com.github.chainmailstudios.astromine.common.item;

import com.github.chainmailstudios.astromine.common.item.base.EnergyVolumeItem;
import com.github.chainmailstudios.astromine.registry.AstromineConfig;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import draylar.magna.api.MagnaTool;
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHandler;

public class DrillItem extends EnergyVolumeItem implements DynamicAttributeTool, IVanishable, MagnaTool, DiggerTool {
	private final int radius;
	private final IItemTier material;
	private final Multimap<Attribute, AttributeModifier> attributeModifiers;

	public DrillItem(IItemTier material, float attackDamage, float attackSpeed, int radius, double size, Properties settings) {
		super(settings, size);

		this.radius = radius;
		this.material = material;

		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", attackDamage, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", attackSpeed, AttributeModifier.Operation.ADDITION));

		this.attributeModifiers = builder.build();
	}

	@Override
	public int getEnchantmentValue() {
		return material.getEnchantmentValue();
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (!target.level.isClientSide) {
			EnergyHandler energy = Energy.of(stack);
			energy.use(getEnergy() * AstromineConfig.get().drillEntityHitMultiplier);
		}

		return true;
	}

	@Override
	public boolean mineBlock(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		if (!world.isClientSide && state.getDestroySpeed(world, pos) != 0.0F) {
			EnergyHandler energy = Energy.of(stack);
			energy.use(getEnergy());
		}

		return true;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType slot) {
		return slot == EquipmentSlotType.MAINHAND ? this.attributeModifiers : super.getDefaultAttributeModifiers(slot);
	}

	@Override
	public float getMiningSpeedMultiplier(ITag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
		return material.getSpeed();
	}

	@Override
	public int getMiningLevel(ITag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
		return material.getLevel();
	}

	@Override
	public float postProcessMiningSpeed(ITag<Item> tag, BlockState state, ItemStack stack, LivingEntity user, float currentSpeed, boolean isEffective) {
		return Energy.of(stack).getEnergy() <= getEnergy() ? 0F : currentSpeed;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return 0F; // Disallow vanilla from overriding our #postProcessMiningSpeed
	}

	public double getEnergy() {
		return AstromineConfig.get().drillConsumed * material.getSpeed();
	}

	@Override
	public int getRadius(ItemStack stack) {
		return radius;
	}

	@Override
	public boolean playBreakEffects() {
		return true;
	}
}
