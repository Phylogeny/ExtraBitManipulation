package com.phylogeny.extrabitmanipulation.api.jei;

import javax.annotation.Nullable;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;

import com.phylogeny.extrabitmanipulation.reference.Reference;

public abstract class InfoRecipeCategoryBase<T extends IRecipeWrapper> extends BlankRecipeCategory
{
	private final IDrawable background, icon;
	private final String localizedName;
	private final int recipeWidth, recipeHeight;
	
	public InfoRecipeCategoryBase(IGuiHelper guiHelper, IDrawable icon, String name, int width, int height)
	{
		recipeWidth = width;
		recipeHeight = height;
		background = guiHelper.createBlankDrawable(recipeWidth, recipeHeight);
		localizedName = Translator.translateToLocal("jei." + Reference.MOD_ID + ".category." + name);
		this.icon = icon;
	}
	
	@Override
	public String getTitle()
	{
		return localizedName;
	}
	
	@Nullable
	@Override
	public IDrawable getIcon()
	{
		return icon;
	}
	
	@Override
	public IDrawable getBackground()
	{
		return background;
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients)
	{
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(0, true, getSlotPosX(), 0);
		guiItemStacks.set(ingredients);
	}
	
	protected int getSlotPosX()
	{
		return 47;
	}
	
}