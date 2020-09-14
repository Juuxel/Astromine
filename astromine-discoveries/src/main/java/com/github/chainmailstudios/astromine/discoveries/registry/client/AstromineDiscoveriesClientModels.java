package com.github.chainmailstudios.astromine.discoveries.registry.client;

import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.discoveries.client.model.PrimitiveRocketEntityModel;
import com.github.chainmailstudios.astromine.discoveries.client.render.entity.PrimitiveRocketEntityRenderer;
import com.github.chainmailstudios.astromine.registry.client.AstromineClientModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.render.model.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BuiltInModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class AstromineDiscoveriesClientModels extends AstromineClientModels {
	public static void initialize() {
		ModelResourceLocation rocketItemId = new ModelResourceLocation(AstromineCommon.identifier("rocket"), "inventory");
		ModelLoadingRegistry.INSTANCE.registerVariantProvider(resourceManager -> (modelIdentifier, modelProviderContext) -> {
			if (modelIdentifier.equals(rocketItemId)) {
				return new UnbakedModel() {
					@Override
					public Collection<ResourceLocation> getDependencies() {
						return Collections.emptyList();
					}

					@Override
					public Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
						return Collections.emptyList();
					}

					@Override
					public BakedModel bake(ModelBakery loader, Function<Material, TextureAtlasSprite> textureGetter, ModelState rotationContainer, ResourceLocation modelId) {
						return new BuiltInModel(ITEM_HANDHELD.get(), ItemOverrides.EMPTY, null, true);
					}
				};
			}
			return null;
		});
	}

	public static void renderRocket(PrimitiveRocketEntityModel primitiveRocketEntityModel, ItemStack stack, ItemTransforms.TransformType mode, PoseStack matrices, MultiBufferSource vertexConsumerProvider, int i, int j) {
		matrices.pushPose();
		if (mode == ItemTransforms.TransformType.GUI) {
			matrices.translate(0.66F, 0.22F, 0F);
		}
		matrices.scale(1.0F, -1.0F, -1.0F);
		if (mode == ItemTransforms.TransformType.GUI) {
			matrices.scale(0.09F, 0.09F, 0.09F);
			matrices.mulPose(Vector3f.YP.rotationDegrees(45));
			matrices.mulPose(Vector3f.XP.rotationDegrees(45));
		} else {
			matrices.scale(0.3F, 0.3F, 0.3F);
			matrices.mulPose(Vector3f.YP.rotationDegrees(45));
			matrices.mulPose(Vector3f.XP.rotationDegrees(45));
		}
		VertexConsumer vertexConsumer2 = ItemRenderer.getFoilBufferDirect(vertexConsumerProvider, primitiveRocketEntityModel.renderType(PrimitiveRocketEntityRenderer.ID), false, stack.hasFoil());
		primitiveRocketEntityModel.renderToBuffer(matrices, vertexConsumer2, i, j, 1.0F, 1.0F, 1.0F, 1.0F);
		matrices.popPose();
	}
}
