package com.github.chainmailstudios.astromine.technologies.registry.client;

import com.github.chainmailstudios.astromine.common.component.inventory.FluidInventoryComponent;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;
import com.github.chainmailstudios.astromine.registry.client.AstromineClientCallbacks;
import com.github.chainmailstudios.astromine.technologies.common.item.HolographicConnectorItem;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesEntityTypes;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesItems;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import java.util.UUID;

public class AstromineTechnologiesClientCallbacks extends AstromineClientCallbacks {
	public static void initialize() {
		ItemTooltipCallback.EVENT.register(((stack, context, tooltip) -> {
			if (stack.getItem() instanceof HolographicConnectorItem) {
				Tuple<RegistryKey<World>, BlockPos> pair = ((HolographicConnectorItem) stack.getItem()).readBlock(stack);
				if (pair != null) {
					tooltip.add(ITextComponent.nullToEmpty(null));
					tooltip.add(new TranslationTextComponent("text.astromine.selected.dimension.pos", pair.getA().location(), pair.getB().getX(), pair.getB().getY(), pair.getB().getZ()).withStyle(TextFormatting.GRAY));
				}
			}
		}));
	}
}
