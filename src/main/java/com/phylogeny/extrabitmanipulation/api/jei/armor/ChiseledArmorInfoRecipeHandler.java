package com.phylogeny.extrabitmanipulation.api.jei.armor;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class ChiseledArmorInfoRecipeHandler implements IRecipeHandler<ChiseledArmorInfoRecipe>
{
	
	@Override
	public Class<ChiseledArmorInfoRecipe> getRecipeClass()
	{
		return ChiseledArmorInfoRecipe.class;
	}
	
	@Override
	public String getRecipeCategoryUid()
	{
		return ChiseledArmorInfoRecipeCategory.UID;
	}
	
	@Override
	public String getRecipeCategoryUid(ChiseledArmorInfoRecipe recipe)
	{
		return ChiseledArmorInfoRecipeCategory.UID;
	}
	
	@Override
	public IRecipeWrapper getRecipeWrapper(ChiseledArmorInfoRecipe recipe)
	{
		return recipe;
	}
	
	@Override
	public boolean isRecipeValid(ChiseledArmorInfoRecipe recipe)
	{
		return true;
	}
	
}