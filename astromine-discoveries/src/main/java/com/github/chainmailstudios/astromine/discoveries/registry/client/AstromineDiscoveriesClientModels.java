package com.github.chainmailstudios.astromine.discoveries.registry.client;

import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.discoveries.client.model.PrimitiveRocketEntityModel;
import com.github.chainmailstudios.astromine.discoveries.client.render.entity.PrimitiveRocketEntityRenderer;
import com.github.chainmailstudios.astromine.registry.client.AstromineClientModels;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.render.model.*;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.BuiltInModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class AstromineDiscoveriesClientModels extends AstromineClientModels {
	public static void initialize() {
		ModelResourceLocation rocketItemId = new ModelResourceLocation(AstromineCommon.identifier("rocket"), "inventory");
		ModelLoadingRegistry.INSTANCE.registerVariantProvider(resourceManager -> (modelIdentifier, modelProviderContext) -> {
			if (modelIdentifier.equals(rocketItemId)) {
				return new IUnbakedModel() {
					@Override
					public Collection<ResourceLocation> getDependencies() {
						return Collections.emptyList();
					}

					@Override
					public Collection<RenderMaterial> getMaterials(Function<ResourceLocation, IUnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
						return Collections.emptyList();
					}

					@Override
					public IBakedModel bake(ModelBakery loader, Function<RenderMaterial, TextureAtlasSprite> textureGetter, IModelTransform rotationContainer, ResourceLocation modelId) {
						return new BuiltInModel(ITEM_HANDHELD.get(), ItemOverrideList.EMPTY, null, true);
					}
				};
			}
			return null;
		});
	}

	public static void renderRocket(PrimitiveRocketEntityModel primitiveRocketEntityModel, ItemStack stack, ItemCameraTransforms.TransformType mode, MatrixStack matrices, IRenderTypeBuffer vertexConsumerProvider, int i, int j) {
		matrices.pushPose();
		if (mode == ItemCameraTransforms.TransformType.GUI) {
			matrices.translate(0.66F, 0.22F, 0F);
		}
		matrices.scale(1.0F, -1.0F, -1.0F);
		if (mode == ItemCameraTransforms.TransformType.GUI) {
			matrices.scale(0.09F, 0.09F, 0.09F);
			matrices.mulPose(Vector3f.YP.rotationDegrees(45));
			matrices.mulPose(Vector3f.XP.rotationDegrees(45));
		} else {
			matrices.scale(0.3F, 0.3F, 0.3F);
			matrices.mulPose(Vector3f.YP.rotationDegrees(45));
			matrices.mulPose(Vector3f.XP.rotationDegrees(45));
		}
		IVertexBuilder vertexConsumer2 = ItemRenderer.getFoilBufferDirect(vertexConsumerProvider, primitiveRocketEntityModel.renderType(PrimitiveRocketEntityRenderer.ID), false, stack.hasFoil());
		primitiveRocketEntityModel.renderToBuffer(matrices, vertexConsumer2, i, j, 1.0F, 1.0F, 1.0F, 1.0F);
		matrices.popPose();
	}
}
