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

package com.github.chainmailstudios.astromine.technologies.common.item;

import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesItems;
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import com.github.chainmailstudios.astromine.common.item.base.EnergyVolumeItem;
import com.github.chainmailstudios.astromine.registry.AstromineConfig;
import com.github.chainmailstudios.astromine.registry.AstromineItems;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHandler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class GravityGauntletItem extends EnergyVolumeItem implements DynamicAttributeTool {
	private static final Multimap<Attribute, AttributeModifier> EAMS = HashMultimap.create();

	public GravityGauntletItem(Properties settings, double size) {
		super(settings, size);
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getItemInHand(hand);
		if (hand == Hand.OFF_HAND)
			return ActionResult.pass(stack);
		ItemStack offStack = user.getItemInHand(Hand.OFF_HAND);
		if (offStack.getItem() == AstromineTechnologiesItems.GRAVITY_GAUNTLET) {
			EnergyHandler selfHandler = Energy.of(stack);
			EnergyHandler otherHandler = Energy.of(offStack);
			if (selfHandler.getEnergy() > AstromineConfig.get().gravityGauntletConsumed && otherHandler.getEnergy() > AstromineConfig.get().gravityGauntletConsumed) {
				user.startUsingItem(hand);
				return ActionResult.success(stack);
			}
		}
		return super.use(world, user, hand);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (world.isClientSide)
			return stack;
		ItemStack offStack = user.getItemInHand(Hand.OFF_HAND);
		if (offStack.getItem() == AstromineTechnologiesItems.GRAVITY_GAUNTLET) {
			EnergyHandler selfHandler = Energy.of(stack);
			EnergyHandler otherHandler = Energy.of(offStack);
			if (selfHandler.getEnergy() > AstromineConfig.get().gravityGauntletConsumed && otherHandler.getEnergy() > AstromineConfig.get().gravityGauntletConsumed) {
				selfHandler.extract(AstromineConfig.get().gravityGauntletConsumed);
				otherHandler.extract(AstromineConfig.get().gravityGauntletConsumed);
				stack.getOrCreateTag().putBoolean("Charged", true);
				offStack.getOrCreateTag().putBoolean("Charged", true);
				return stack;
			}
		}
		return super.finishUsing(stack, world, user);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 30;
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (attacker.level.isClientSide)
			return super.postHit(stack, target, attacker);
		ItemStack offStack = attacker.getItemInHand(Hand.OFF_HAND);
		if (offStack.getItem() == AstromineTechnologiesItems.GRAVITY_GAUNTLET) {
			if (stack.getOrCreateTag().getBoolean("Charged") && offStack.getOrCreateTag().getBoolean("Charged")) {
				target.knockback(1, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
				target.push(0f, 0.5f, 0f);
				stack.getOrCreateTag().putBoolean("Charged", false);
				offStack.getOrCreateTag().putBoolean("Charged", false);
				return true;
			}
		}
		return super.postHit(stack, target, attacker);
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return stack.getOrCreateTag().getBoolean("Charged");
	}

	// TODO: dynamic once not broken so only provide when charged
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot) {
		if (slot == EquipmentSlotType.MAINHAND) {
			return EAMS;
		}
		return super.getAttributeModifiers(slot);
	}

	static {
		EAMS.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "attack", 4f, AttributeModifier.Operation.ADDITION));
	}
}
