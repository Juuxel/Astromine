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

package com.github.chainmailstudios.astromine.common.utilities;

import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public class RotationUtilities {
	public static AxisAlignedBB getRotatedBoundingBox(AxisAlignedBB def, Direction facing) {
		def.move(-0.5, -0.5, -0.5);
		switch (facing) {
			case SOUTH:
				def = new AxisAlignedBB(def.minZ, def.minY, (def.maxX * -1) + 1, def.maxZ, def.maxY, (def.minX * -1) + 1);
			case WEST:
				def = new AxisAlignedBB((def.maxX * -1) + 1, def.minY, (def.maxZ * -1) + 1, (def.minX * -1) + 1, def.maxY, (def.minZ * -1) + 1);
			case EAST:
				def = new AxisAlignedBB((def.maxZ * -1) + 1, def.minY, def.minX, (def.minZ * -1) + 1, def.maxY, def.maxX);
			default:

		}
		def.move(0.5, 0.5, 0.5);
		return def;
	}

	public static VoxelShape getRotatedShape(AxisAlignedBB def, Direction facing) {
		return VoxelShapes.create(getRotatedBoundingBox(def, facing));
	}
}
