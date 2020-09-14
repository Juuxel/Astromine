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

import com.github.chainmailstudios.astromine.common.component.inventory.FluidInventoryComponent;
import com.github.chainmailstudios.astromine.common.volume.fraction.Fraction;
import com.github.chainmailstudios.astromine.common.registry.BreathableRegistry;
import com.github.chainmailstudios.astromine.common.registry.FluidEffectRegistry;
import com.github.chainmailstudios.astromine.common.volume.handler.FluidHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.github.chainmailstudios.astromine.common.component.entity.EntityOxygenComponent;
import com.github.chainmailstudios.astromine.common.component.world.ChunkAtmosphereComponent;
import com.github.chainmailstudios.astromine.common.entity.GravityEntity;
import com.github.chainmailstudios.astromine.common.registry.AtmosphereRegistry;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import com.github.chainmailstudios.astromine.registry.AstromineAttributes;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;
import com.github.chainmailstudios.astromine.registry.AstromineDimensions;
import com.github.chainmailstudios.astromine.registry.AstromineTags;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import java.util.Optional;
import java.util.stream.Collectors;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements GravityEntity {
	@Shadow
	@Final
	private NonNullList<ItemStack> equippedArmor;

	@Shadow
	public abstract double getAttributeValue(Attribute attribute);

	@Shadow public abstract Iterable<ItemStack> getArmorItems();

	@Shadow public float flyingSpeed;

	@ModifyConstant(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", constant = @Constant(doubleValue = 0.08D, ordinal = 0))
	private double modifyGravity(double original) {
		return getGravity();
	}

	public double getGravityMultiplier() {
		return getAttributeValue(AstromineAttributes.GRAVITY_MULTIPLIER);
	}

	@Inject(at = @At("HEAD"), method = "tick()V")
	void onTick(CallbackInfo callbackInformation) {
		Entity entity = (Entity) (Object) this;
		if (entity.level.isClientSide) return;

		if (!entity.getType().is(AstromineTags.DOES_NOT_BREATHE)) {
			ComponentProvider chunkProvider = ComponentProvider.fromChunk(entity.level.getChunk(entity.blockPosition()));

			ChunkAtmosphereComponent atmosphereComponent = chunkProvider.getComponent(AstromineComponentTypes.CHUNK_ATMOSPHERE_COMPONENT);

			if (atmosphereComponent != null) {
				FluidVolume atmosphereVolume;

				if (!AstromineDimensions.isAstromine(entity.level.dimension())) {
					atmosphereVolume = atmosphereComponent.get(entity.blockPosition().relative(Direction.UP));

					if (atmosphereVolume.isEmpty()) {
						atmosphereVolume = FluidVolume.oxygen();
					}
				} else {
					atmosphereVolume = atmosphereComponent.get(entity.blockPosition().relative(Direction.UP));
				}

				boolean isSubmerged = false;

				AxisAlignedBB collisionBox = entity.getBoundingBox();

				for (BlockPos blockPos : (Iterable<BlockPos>) () -> BlockPos.betweenClosedStream(collisionBox).iterator()) {
					BlockState blockState = entity.level.getBlockState(blockPos);

					if (blockState.getBlock() instanceof FlowingFluidBlock) {
						isSubmerged = true;

						Optional.ofNullable(FluidEffectRegistry.INSTANCE.get(blockState.getFluidState().getType())).ifPresent(it -> it.accept((LivingEntity) (Object) this));
					}
				}

				if (!isSubmerged) {
					boolean isBreathing = true;

					ComponentProvider entityProvider = ComponentProvider.fromEntity(entity);

					EntityOxygenComponent oxygenComponent = entityProvider.getComponent(AstromineComponentTypes.ENTITY_OXYGEN_COMPONENT);

					if (oxygenComponent != null) {
						boolean hasHelmet = false;
						boolean hasChestplate = false;
						boolean hasLeggings = false;
						boolean hasBoots = false;

						for (ItemStack stack : getArmorItems()) {
							if (!stack.isEmpty()) {
								if (Registry.ITEM.getKey(stack.getItem()).toString().equals("astromine:space_suit_helmet")) {
									hasHelmet = true;
								}
								if (Registry.ITEM.getKey(stack.getItem()).toString().equals("astromine:space_suit_chestplate")) {
									hasChestplate = true;
								}
								if (Registry.ITEM.getKey(stack.getItem()).toString().equals("astromine:space_suit_leggings")) {
									hasLeggings = true;
								}
								if (Registry.ITEM.getKey(stack.getItem()).toString().equals("astromine:space_suit_boots")) {
									hasBoots = true;
								}
							}
						}

						boolean hasSuit = hasHelmet && hasChestplate && hasLeggings && hasBoots;

						for (ItemStack stack : getArmorItems()) {
							if (!stack.isEmpty()) {
								if (Registry.ITEM.getKey(stack.getItem()).toString().equals("astromine:space_suit_chestplate")) { // TODO: Properly verify for Space Suit.
									ComponentProvider provider = ComponentProvider.fromItemStack(stack);

									FluidInventoryComponent fluidComponent = provider.getComponent(AstromineComponentTypes.FLUID_INVENTORY_COMPONENT);

									if (fluidComponent != null) {
										FluidVolume volume = FluidHandler.of(fluidComponent).getFirst();

										if (volume != null) {
											boolean canBreathe = BreathableRegistry.INSTANCE.canBreathe(entity.getType(), volume.getFluid());

											if ((volume.isEmpty() || !canBreathe) && hasSuit) { // TODO: Check if can breathe!
												isBreathing = false;
											}

											if (!canBreathe) {
												if (FluidEffectRegistry.INSTANCE.contains(volume.getFluid())) {
													FluidEffectRegistry.INSTANCE.get(volume.getFluid()).accept((LivingEntity) entity);
												}
											}
										}
									}
								}
							}
						}

						if (!isBreathing) {
							oxygenComponent.simulate(false);
						} else {
							if (!hasSuit && BreathableRegistry.INSTANCE.containsKey(entity.getType())) {
								if (!BreathableRegistry.INSTANCE.canBreathe(entity.getType(), atmosphereVolume.getFluid())) {
									isBreathing = false;
								}
							}

							oxygenComponent.simulate(isBreathing);
						}
					}
				}
			}
		}
	}

	@Inject(at = @At("RETURN"), method = "createLivingAttributes()Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;")
	private static void createLivingAttributesInject(CallbackInfoReturnable<AttributeModifierMap.MutableAttribute> cir) {
		cir.getReturnValue().add(AstromineAttributes.GRAVITY_MULTIPLIER);
	}
}
