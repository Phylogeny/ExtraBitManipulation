package com.phylogeny.extrabitmanipulation.api.jei.model;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;

public class ModelInfoRecipeHandler implements IRecipeWrapperFactory<ModelInfoRecipe>
{
	
	@Override
	public IRecipeWrapper getRecipeWrapper(ModelInfoRecipe recipe)
	{
		return recipe;
	}
	
}