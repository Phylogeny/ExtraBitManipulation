package com.phylogeny.extrabitmanipulation.api.jei.shape;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class ShapeInfoRecipeHandler implements IRecipeHandler<ShapeInfoRecipe>
{
	
	@Override
	public Class<ShapeInfoRecipe> getRecipeClass()
	{
		return ShapeInfoRecipe.class;
	}
	
	@Override
	public String getRecipeCategoryUid()
	{
		return ShapeInfoRecipeCategory.UID;
	}
	
	@Override
	public IRecipeWrapper getRecipeWrapper(ShapeInfoRecipe recipe)
	{
		return recipe;
	}
	
	@Override
	public boolean isRecipeValid(ShapeInfoRecipe recipe)
	{
		return true;
	}
	
}