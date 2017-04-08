package com.phylogeny.extrabitmanipulation.api.jei.model;

import com.phylogeny.extrabitmanipulation.api.jei.Translator;
import com.phylogeny.extrabitmanipulation.reference.Reference;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.BlankRecipeCategory;

public class ModelInfoRecipeCategory extends BlankRecipeCategory<ModelInfoRecipe>
{
	public static final int recipeWidth = 160;
	public static final int recipeHeight = 125;
	private final IDrawable background;
	private final String localizedName;
	public static final String NAME = "model";
	public static final String UID = Reference.GROUP_ID + NAME;
	
	public ModelInfoRecipeCategory(IGuiHelper guiHelper)
	{
		background = guiHelper.createBlankDrawable(recipeWidth, recipeHeight);
		localizedName = Translator.translateToLocal("jei." + Reference.GROUP_ID + ".category." + NAME);
	}
	
	@Override
	public String getUid()
	{
		return UID;
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
	public void setRecipe(IRecipeLayout recipeLayout, ModelInfoRecipe recipeWrapper) {}
	
}