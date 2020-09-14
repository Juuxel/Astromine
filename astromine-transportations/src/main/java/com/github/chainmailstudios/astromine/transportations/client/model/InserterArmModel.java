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

package com.github.chainmailstudios.astromine.transportations.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class InserterArmModel extends Model {
	private final ModelRenderer lowerArm;
	private final ModelRenderer middleArm;
	private final ModelRenderer topArm;

	public InserterArmModel() {
		super(RenderType::entitySolid);
		texWidth = 16;
		texHeight = 16;

		lowerArm = new ModelRenderer(this);
		lowerArm.setPos(0.0F, 23.0F, 0.0F);
		lowerArm.texOffs(0, 0).addBox(0.5F, -8.0F, -0.5F, 1.0F, 8.0F, 1.0F, 0.0F, false);
		lowerArm.texOffs(4, 0).addBox(-1.5F, -8.0F, -0.5F, 1.0F, 8.0F, 1.0F, 0.0F, false);

		middleArm = new ModelRenderer(this);
		middleArm.setPos(0.0F, -7.0F, 0.0F);
		lowerArm.addChild(middleArm);
		middleArm.texOffs(8, 0).addBox(-0.5F, -6.0F, -0.5F, 1.0F, 6.0F, 1.0F, 0.0F, false);

		topArm = new ModelRenderer(this);
		topArm.setPos(0.0F, -5.0F, 0.0F);
		middleArm.addChild(topArm);
		topArm.texOffs(0, 13).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
		topArm.texOffs(8, 13).addBox(-0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		topArm.texOffs(0, 11).addBox(-1.5F, -3.0F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
		topArm.texOffs(8, 10).addBox(1.5F, -5.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		topArm.texOffs(12, 13).addBox(-2.5F, -5.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
	}

	public ModelRenderer getLowerArm() {
		return lowerArm;
	}

	public ModelRenderer getMiddleArm() {
		return middleArm;
	}

	public ModelRenderer getTopArm() {
		return topArm;
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		lowerArm.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer ModelPart, float x, float y, float z) {
		ModelPart.xRot = x;
		ModelPart.yRot = y;
		ModelPart.zRot = z;
	}
}
