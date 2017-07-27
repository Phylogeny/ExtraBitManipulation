package com.phylogeny.extrabitmanipulation.api.jei.armor;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;

public class ChiseledArmorInfoRecipeHandler implements IRecipeWrapperFactory<ChiseledArmorInfoRecipe>
{
	
	@Override
	public IRecipeWrapper getRecipeWrapper(ChiseledArmorInfoRecipe recipe)
	{
		return recipe;
	}
	
}