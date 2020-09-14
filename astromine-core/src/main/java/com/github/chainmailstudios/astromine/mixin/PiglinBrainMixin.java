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

package com.github.chainmailstudios.astromine.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import com.github.chainmailstudios.astromine.registry.AstromineCriteria;
import com.github.chainmailstudios.astromine.registry.AstromineTags;

import java.util.Iterator;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(PiglinAi.class)
public abstract class PiglinBrainMixin {
	@Inject(method = "acceptsForBarter(Lnet/minecraft/item/Item;)Z", at = @At("RETURN"), cancellable = true)
	private static void acceptsForBarterInject(Item item, CallbackInfoReturnable<Boolean> cir) {
		if (item.is(AstromineTags.PIGLIN_BARTERING_ITEMS)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "wearsGoldArmor(Lnet/minecraft/entity/LivingEntity;)Z", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private static void wearsGoldArmorInject(LivingEntity entity, CallbackInfoReturnable<Boolean> cir, Iterable<ItemStack> iterable, Iterator iterator, ItemStack stack, Item item) {
		if (item.is(AstromineTags.PIGLIN_SAFE_ARMOR)) {
			cir.setReturnValue(true);
		}
	}

	@Redirect(method = "loot(Lnet/minecraft/entity/mob/PiglinEntity;Lnet/minecraft/entity/ItemEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
	private static Item lootRedirect(ItemStack stack) {
		Item item = stack.getItem();
		return item.is(AstromineTags.PIGLIN_LOVED_NUGGETS) ? Items.GOLD_NUGGET : item;
	}

	@Inject(method = "consumeOffHandItem(Lnet/minecraft/entity/mob/PiglinEntity;Z)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/mob/PiglinBrain;acceptsForBarter(Lnet/minecraft/item/Item;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void triggerCriterion(Piglin entity, boolean bl, CallbackInfo ci, ItemStack stack, boolean bl2) {
		if (bl && bl2 && stack.getItem().is(AstromineTags.TRICKS_PIGLINS)) {
			Optional<Player> optional = entity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
			if (optional.isPresent() && optional.get() instanceof ServerPlayer) {
				AstromineCriteria.TRICKED_PIGLIN.trigger((ServerPlayer) optional.get());
			}
		}
	}
}
