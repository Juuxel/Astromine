package com.github.chainmailstudios.astromine.client.rei.fluidmixing;

import com.github.chainmailstudios.astromine.client.rei.AstromineREIPlugin;
import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ElectrolyzingCategory implements RecipeCategory<ElectrolyzingDisplay> {
	private final Identifier id;
	private final String translationKey;
	private final EntryStack logo;

	public ElectrolyzingCategory(Identifier id, String translationKey, EntryStack logo) {
		this.id = id;
		this.translationKey = translationKey;
		this.logo = logo;
	}

	@Override
	public Identifier getIdentifier() {
		return id;
	}

	@Override
	public String getCategoryName() {
		return I18n.translate(translationKey);
	}

	@Override
	public EntryStack getLogo() {
		return logo;
	}

	@Override
	public List<Widget> setupDisplay(ElectrolyzingDisplay recipeDisplay, Rectangle bounds) {
		List<Widget> widgets = Lists.newArrayList();
		Rectangle innerBounds = new Rectangle(bounds.getCenterX() - 55, bounds.y, 110, bounds.height);
		widgets.add(Widgets.createRecipeBase(innerBounds));
		widgets.addAll(AstromineREIPlugin.createEnergyDisplay(new Rectangle(innerBounds.x + 10, bounds.getCenterY() - 23, 12, 48), recipeDisplay.getEnergy(), false, 12500));
		widgets.addAll(AstromineREIPlugin.createFluidDisplay(new Rectangle(innerBounds.x + 24, bounds.getCenterY() - 23, 12, 48), recipeDisplay.getInputEntries().get(0).get(0), recipeDisplay.getInput().getFraction(), false, 5000));
		widgets.add(Widgets.createArrow(new Point(innerBounds.getX() + 45, innerBounds.getY() + 26)));
		widgets.addAll(AstromineREIPlugin.createFluidDisplay(new Rectangle(innerBounds.getMaxX() - 32, bounds.getCenterY() - 23, 12, 48), recipeDisplay.getOutputEntries().get(0), recipeDisplay.getFirstOutput().getFraction(), true, 5000));
		widgets.addAll(AstromineREIPlugin.createFluidDisplay(new Rectangle(innerBounds.getMaxX() - 32 + 14, bounds.getCenterY() - 23, 12, 48), recipeDisplay.getOutputEntries().get(1), recipeDisplay.getSecondOutput().getFraction(), true, 5000));
		return widgets;
	}
}
