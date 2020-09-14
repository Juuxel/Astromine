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

package com.github.chainmailstudios.astromine.discoveries.common.entity.placer;

import com.github.chainmailstudios.astromine.common.entity.placer.EntityPlacer;
import com.github.chainmailstudios.astromine.registry.AstromineConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;

public class SpaceEntityPlacer implements EntityPlacer {
	public static final SpaceEntityPlacer TO_PLANET = new SpaceEntityPlacer(AstromineConfig.get().overworldSpawnYLevel);
	public static final SpaceEntityPlacer TO_SPACE = new SpaceEntityPlacer(AstromineConfig.get().spaceSpawnYLevel);

	public final int y;

	public SpaceEntityPlacer(int y) {
		this.y = y;
	}

	@Override
	public PortalInfo placeEntity(Entity entity) {
		return new PortalInfo(new Vec3(entity.getX(), y, entity.getZ()), entity.getDeltaMovement(), entity.getYHeadRot(), entity.xRot);
	}

}
