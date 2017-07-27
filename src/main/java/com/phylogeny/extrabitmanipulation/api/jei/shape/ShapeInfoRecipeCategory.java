package com.phylogeny.extrabitmanipulation.api.jei.shape;

import mezz.jei.api.IGuiHelper;

import com.phylogeny.extrabitmanipulation.api.jei.InfoRecipeCategoryBase;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class ShapeInfoRecipeCategory extends InfoRecipeCategoryBase<ShapeInfoRecipe>
{
	public static final String NAME = "shape";
	public static final String UID = Reference.GROUP_ID + NAME;
	
	public ShapeInfoRecipeCategory(IGuiHelper guiHelper)
	{
		super(guiHelper, NAME, 160, 125);
	}
	
	@Override
	public String getUid()
	{
		return UID;
	}
	
}