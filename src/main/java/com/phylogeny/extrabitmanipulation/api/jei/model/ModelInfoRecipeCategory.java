package com.phylogeny.extrabitmanipulation.api.jei.model;

import javax.annotation.Nullable;

import com.phylogeny.extrabitmanipulation.api.jei.icon.CategoryIcon;
import com.phylogeny.extrabitmanipulation.reference.Reference;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.util.Translator;

public class ModelInfoRecipeCategory extends BlankRecipeCategory<ModelInfoRecipe>
{
	public static final int recipeWidth = 178;
	public static final int recipeHeight = 125;
	private final IDrawable background, icon;
	private final String localizedName;
	public static final String NAME = "model";
	public static final String UID = Reference.MOD_ID + NAME;
	
	public ModelInfoRecipeCategory(IGuiHelper guiHelper)
	{
		background = guiHelper.createBlankDrawable(recipeWidth, recipeHeight);
		icon = new CategoryIcon(0, 0, 16, 16, 367, 367, "textures/jei/graphics/" + ModelInfoRecipe.GRAPHIC_NAMES[0]);
		localizedName = Translator.translateToLocal("jei." + Reference.MOD_ID + ".category." + NAME);
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
	public void setRecipe(IRecipeLayout recipeLayout, ModelInfoRecipe recipeWrapper, IIngredients ingredients)
	{
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(0, true, 47, 0);
		guiItemStacks.set(ingredients);
	}
	
}