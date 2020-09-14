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

package com.github.chainmailstudios.astromine.common.multiblock;

import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.component.Component;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import java.util.Set;

public abstract class MultiblockMemberBlockEntity extends BlockEntity implements ComponentProvider {
	private MultiblockControllerBlockEntity controller;

	private BlockPos relative;

	public MultiblockMemberBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	public MultiblockControllerBlockEntity getController() {
		return controller;
	}

	public void setController(MultiblockControllerBlockEntity controller) {
		this.controller = controller;
	}

	public BlockPos getRelative() {
		return relative;
	}

	public void setRelative(BlockPos relative) {
		this.relative = relative;
	}

	@Override
	public boolean hasComponent(ComponentType<?> componentType) {
		return controller.hasComponent(relative, componentType);
	}

	@Override
	public <C extends Component> C getComponent(ComponentType<C> componentType) {
		return controller.getComponent(relative, componentType);
	}

	@Override
	public Set<ComponentType<?>> getComponentTypes() {
		return controller.getComponentTypes(relative);
	}
}
