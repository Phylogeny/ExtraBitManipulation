package com.phylogeny.extrabitmanipulation.api.jei.model;

import mezz.jei.api.IGuiHelper;

import com.phylogeny.extrabitmanipulation.api.jei.InfoRecipeCategoryBase;
import com.phylogeny.extrabitmanipulation.api.jei.icon.CategoryIcon;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class ModelInfoRecipeCategory extends InfoRecipeCategoryBase<ModelInfoRecipe>
{
	public static final String NAME = "model";
	public static final String UID = Reference.MOD_ID + NAME;
	
	public ModelInfoRecipeCategory(IGuiHelper guiHelper)
	{
		super(guiHelper, new CategoryIcon(0, 0, 16, 16, 367, 367, "textures/jei/graphics/" + ModelInfoRecipe.GRAPHIC_NAMES[0]), NAME, 178, 125);
	}
	
	@Override
	public String getUid()
	{
		return UID;
	}
	
}