package com.phylogeny.extrabitmanipulation.api.jei.model;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class ModelInfoRecipeHandler implements IRecipeHandler<ModelInfoRecipe>
{
	
	@Override
	public Class<ModelInfoRecipe> getRecipeClass()
	{
		return ModelInfoRecipe.class;
	}
	
	@Override
	public String getRecipeCategoryUid(ModelInfoRecipe recipe)
	{
		return ModelInfoRecipeCategory.UID;
	}
	
	@Override
	public IRecipeWrapper getRecipeWrapper(ModelInfoRecipe recipe)
	{
		return recipe;
	}
	
	@Override
	public boolean isRecipeValid(ModelInfoRecipe recipe)
	{
		return true;
	}
	
}