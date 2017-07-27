package com.phylogeny.extrabitmanipulation.api.jei.model;

import mezz.jei.api.IGuiHelper;

import com.phylogeny.extrabitmanipulation.api.jei.InfoRecipeCategoryBase;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class ModelInfoRecipeCategory extends InfoRecipeCategoryBase<ModelInfoRecipe>
{
	public static final String NAME = "model";
	public static final String UID = Reference.GROUP_ID + NAME;
	
	public ModelInfoRecipeCategory(IGuiHelper guiHelper)
	{
		super(guiHelper, NAME, 160, 125);
	}
	
	@Override
	public String getUid()
	{
		return UID;
	}
	
}