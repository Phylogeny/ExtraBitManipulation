package com.phylogeny.extrabitmanipulation.init;

import net.minecraftforge.oredict.OreDictionary;

import com.phylogeny.extrabitmanipulation.reference.Configs;

public class RecipesExtraBitManipulation
{
	public static void recipeInit()
	{
		if (!Configs.disableDiamondNuggetOreDict)
			OreDictionary.registerOre("nuggetDiamond", ItemsExtraBitManipulation.diamondNugget);
	}
	
}