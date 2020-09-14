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

package com.github.chainmailstudios.astromine.common.screenhandler.base.block;

import com.github.chainmailstudios.astromine.common.block.base.HorizontalFacingBlockWithEntity;
import com.github.chainmailstudios.astromine.common.block.entity.base.ComponentBlockEntity;
import com.github.chainmailstudios.astromine.common.component.SidedComponentProvider;
import com.github.chainmailstudios.astromine.common.component.block.entity.BlockEntityTransferComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.NameableComponent;
import com.github.chainmailstudios.astromine.common.utilities.WidgetUtilities;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;
import com.github.vini2003.blade.common.miscellaneous.Position;
import com.github.vini2003.blade.common.miscellaneous.Size;
import com.github.vini2003.blade.common.collection.TabWidgetCollection;
import com.github.vini2003.blade.common.handler.BaseScreenHandler;
import com.github.vini2003.blade.common.utilities.Slots;
import com.github.vini2003.blade.common.widget.base.SlotWidget;
import com.github.vini2003.blade.common.widget.base.TabWidget;
import com.github.vini2003.blade.common.widget.base.TextWidget;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public abstract class ComponentBlockEntityScreenHandler extends BaseScreenHandler {
	public ComponentBlockEntity syncBlockEntity;
	public BlockPos position;
	public Block originalBlock;
	public Collection<SlotWidget> playerSlots = new HashSet<>();
	public TabWidgetCollection mainTab;
	protected TabWidget tabs;

	public ComponentBlockEntityScreenHandler(MenuType<?> type, int syncId, Player player, BlockPos position) {
		super(type, syncId, player);

		this.position = position;
		this.syncBlockEntity = (ComponentBlockEntity) player.level.getBlockEntity(position);
		this.originalBlock = player.level.getBlockState(position).getBlock();

		if (!player.level.isClientSide) {
			syncBlockEntity.doNotSkipInventory();
			syncBlockEntity.sync();
		}
	}

	public int getTabWidgetExtendedHeight() {
		return 0;
	}

	@Override
	public boolean stillValid(@Nullable Player player) {
		return stillValid(ContainerLevelAccess.create(player.level, position), player, originalBlock);
	}

	@Override
	public void initialize(int width, int height) {
		tabs = new TabWidget();
		tabs.setSize(Size.of(176F, 188F + getTabWidgetExtendedHeight()));
		tabs.setPosition(Position.of(width / 2 - tabs.getWidth() / 2, height / 2 - tabs.getHeight() / 2));

		addWidget(tabs);

		mainTab = (TabWidgetCollection) tabs.addTab(syncBlockEntity.getBlockState().getBlock().asItem());
		mainTab.setPosition(Position.of(tabs, 0, 25F + 7F));
		mainTab.setSize(Size.of(176F, 184F));

		TextWidget title = new TextWidget();
		title.setPosition(Position.of(mainTab, 8, 0));
		title.setText(new TranslatableComponent(syncBlockEntity.getBlockState().getBlock().asItem().getDescriptionId()));
		title.setColor(4210752);
		mainTab.addWidget(title);

		Position invPos = Position.of(tabs, 7F, 25F + 7F + (184 - 18 - 18 - (18 * 4) - 3 + getTabWidgetExtendedHeight()));
		TextWidget invTitle = new TextWidget();
		invTitle.setPosition(Position.of(invPos, 0, -10));
		invTitle.setText(getPlayer().inventory.getName());
		invTitle.setColor(4210752);
		mainTab.addWidget(invTitle);
		playerSlots = Slots.addPlayerInventory(invPos, Size.of(18F, 18F), mainTab, getPlayer().inventory);

		SidedComponentProvider sidedComponentProvider = SidedComponentProvider.fromBlockEntity(syncBlockEntity);

		Direction rotation = Direction.NORTH;
		Block block = syncBlockEntity.getBlockState().getBlock();

		if (block instanceof HorizontalFacingBlockWithEntity) {
			DirectionProperty property = ((HorizontalFacingBlockWithEntity) block).getDirectionProperty();
			if (property != null)
				rotation = syncBlockEntity.getBlockState().getValue(property);
		}

		final Direction finalRotation = rotation;

		BlockEntityTransferComponent transferComponent = sidedComponentProvider.getComponent(AstromineComponentTypes.BLOCK_ENTITY_TRANSFER_COMPONENT);

		transferComponent.get().forEach((type, entry) -> {
			if (sidedComponentProvider.getComponent(type) instanceof NameableComponent) {
				NameableComponent nameableComponent = (NameableComponent) sidedComponentProvider.getComponent(type);
				TabWidgetCollection current = (TabWidgetCollection) tabs.addTab(nameableComponent.getSymbol(), () -> Collections.singletonList(nameableComponent.getName()));
				WidgetUtilities.createTransferTab(current, Position.of(tabs, tabs.getWidth() / 2 - 38, getTabWidgetExtendedHeight() / 2), finalRotation, transferComponent, syncBlockEntity.getBlockPos(), type);
				TextWidget invTabTitle = new TextWidget();
				invTabTitle.setPosition(Position.of(invPos, 0, -10));
				invTabTitle.setText(getPlayer().inventory.getName());
				invTabTitle.setColor(4210752);
				current.addWidget(invTabTitle);
				playerSlots.addAll(Slots.addPlayerInventory(invPos, Size.of(18F, 18F), current, getPlayer().inventory));

				TextWidget tabTitle = new TextWidget();
				tabTitle.setPosition(Position.of(mainTab, 8, 0));
				tabTitle.setText(nameableComponent.getName());
				tabTitle.setColor(4210752);
				current.addWidget(tabTitle);
			}
		});
	}
}
