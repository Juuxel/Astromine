package com.github.chainmailstudios.astromine.technologies.datagen.generators.recipe.set;

import com.github.chainmailstudios.astromine.common.utilities.GeneratorUtilities;
import com.github.chainmailstudios.astromine.datagen.generator.recipe.set.base.EnergyProcessingSetRecipeGenerator;
import com.github.chainmailstudios.astromine.datagen.material.MaterialItemType;
import com.github.chainmailstudios.astromine.datagen.material.MaterialSet;
import com.github.chainmailstudios.astromine.technologies.common.recipe.WiremillingRecipe;
import me.shedaniel.cloth.api.datagen.v1.RecipeData;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class WiremillingSetRecipeGenerator extends EnergyProcessingSetRecipeGenerator {
	public WiremillingSetRecipeGenerator(MaterialItemType input, int inputCount, MaterialItemType output, int outputCount, int time, int energyConsumed) {
		super(input, inputCount, output, outputCount, time, energyConsumed);
	}

	public WiremillingSetRecipeGenerator(MaterialItemType input, MaterialItemType output, int outputCount, int time, int energyConsumed) {
		this(input, 1, output, outputCount, time, energyConsumed);
	}

	public WiremillingSetRecipeGenerator(MaterialItemType input, MaterialItemType output, int time, int energyConsumed) {
		this(input, 1, output, 1, time, energyConsumed);
	}

	@Override
	public String getRecipeName(MaterialSet set) {
		return set.getItemIdPath(output) + "_from_wiremilling_" + input.getName();
	}

	@Override
	public void generate(RecipeData recipes, MaterialSet set) {
		recipes.accept(GeneratorUtilities.Providers.createProvider(WiremillingRecipe.Serializer.INSTANCE, getRecipeId(set), json -> {
			JsonElement inputJson = set.getIngredient(input).toJson();
			if (inputJson.isJsonObject()) {
				inputJson.getAsJsonObject().addProperty("count", inputCount);
			}

			json.add("input", inputJson);

			JsonObject outputJson = new JsonObject();
			outputJson.addProperty("item", set.getItemId(output).toString());
			outputJson.addProperty("count", outputCount);

			json.add("output", outputJson);
			json.addProperty("time", time);
			json.addProperty("energy_consumed", energyConsumed);
		}));
	}

	@Override
	public String getGeneratorName() {
		return "wiremilling_set";
	}
}
