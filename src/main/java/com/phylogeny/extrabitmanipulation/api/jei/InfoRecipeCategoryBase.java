package com.phylogeny.extrabitmanipulation.api.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;

import com.phylogeny.extrabitmanipulation.reference.Reference;

public abstract class InfoRecipeCategoryBase<T extends IRecipeWrapper> extends BlankRecipeCategory
{
	private final IDrawable background;
	private final String localizedName;
	private final int recipeWidth, recipeHeight;
	
	public InfoRecipeCategoryBase(IGuiHelper guiHelper, String name, int width, int height)
	{
		recipeWidth = width;
		recipeHeight = height;
		background = guiHelper.createBlankDrawable(recipeWidth, recipeHeight);
		localizedName = Translator.translateToLocal("jei." + Reference.GROUP_ID + ".category." + name);
	}
	
	@Override
	public String getTitle()
	{
		return localizedName;
	}
	
	@Override
	public IDrawable getBackground()
	{
		return background;
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {}
	
}