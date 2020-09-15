package com.github.chainmailstudios.astromine.technologies.registry.client;

import com.github.chainmailstudios.astromine.registry.client.AstromineClientCallbacks;
import com.github.chainmailstudios.astromine.technologies.common.item.HolographicConnectorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = "astromine")
public class AstromineTechnologiesClientCallbacks extends AstromineClientCallbacks {
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		List<ITextComponent> tooltip = event.getToolTip();
		if (stack.getItem() instanceof HolographicConnectorItem) {
			Tuple<RegistryKey<World>, BlockPos> pair = ((HolographicConnectorItem) stack.getItem()).readBlock(stack);
			if (pair != null) {
				tooltip.add(ITextComponent.nullToEmpty(null));
				tooltip.add(new TranslationTextComponent("text.astromine.selected.dimension.pos", pair.getA().location(), pair.getB().getX(), pair.getB().getY(), pair.getB().getZ()).withStyle(TextFormatting.GRAY));
			}
		}
	}
}
