package com.phylogeny.extrabitmanipulation.api.jei.armor;

import java.util.List;

import mezz.jei.api.IGuiHelper;
import net.minecraft.item.ItemStack;

import com.phylogeny.extrabitmanipulation.api.jei.InfoRecipeCategoryBase;
import com.phylogeny.extrabitmanipulation.api.jei.icon.CategoryIconStackList;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class ChiseledArmorInfoRecipeCategory extends InfoRecipeCategoryBase<ChiseledArmorInfoRecipe>
{
	public static final String NAME = "chiseled_armor";
	public static final String UID = Reference.MOD_ID + NAME;
	
	public ChiseledArmorInfoRecipeCategory(IGuiHelper guiHelper, List<ItemStack> stacks)
	{
		super(guiHelper, new CategoryIconStackList(16, 16, stacks), NAME, 160, 125);
	}
	
	@Override
	public String getUid()
	{
		return UID;
	}
	
}