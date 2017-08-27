package com.phylogeny.extrabitmanipulation.api.jei.armor;

import mezz.jei.api.IGuiHelper;

import com.phylogeny.extrabitmanipulation.api.jei.InfoRecipeCategoryBase;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class ChiseledArmorInfoRecipeCategory extends InfoRecipeCategoryBase<ChiseledArmorInfoRecipe>
{
	public static final String NAME = "chiseled_armor";
	public static final String UID = Reference.GROUP_ID + NAME;
	
	public ChiseledArmorInfoRecipeCategory(IGuiHelper guiHelper)
	{
		super(guiHelper, NAME, 186, 125);
	}
	
	@Override
	public String getUid()
	{
		return UID;
	}
	
}