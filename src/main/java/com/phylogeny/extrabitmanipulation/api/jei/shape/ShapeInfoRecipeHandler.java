package com.phylogeny.extrabitmanipulation.api.jei.shape;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;

public class ShapeInfoRecipeHandler implements IRecipeWrapperFactory<ShapeInfoRecipe>
{
	
	@Override
	public IRecipeWrapper getRecipeWrapper(ShapeInfoRecipe recipe)
	{
		return recipe;
	}
	
}