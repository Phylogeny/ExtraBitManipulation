package com.phylogeny.extrabitmanipulation.init;

import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.registry.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import com.phylogeny.extrabitmanipulation.recipe.RecipeChiseledArmor;
import com.phylogeny.extrabitmanipulation.reference.Configs;

public class RecipesExtraBitManipulation
{
	public static void registerOres()
	{
		if (!Configs.disableDiamondNuggetOreDict)
			OreDictionary.registerOre("nuggetDiamond", ItemsExtraBitManipulation.diamondNugget);
	}
	
	@SubscribeEvent
	void registerRecipes(RegistryEvent.Register<IRecipe> event)
	{
		registerChiseledArmorRecipes(event, ItemsExtraBitManipulation.chiseledHelmet, Items.DIAMOND_HELMET, 272);
		registerChiseledArmorRecipes(event, ItemsExtraBitManipulation.chiseledChestplate, Items.DIAMOND_CHESTPLATE, 444);
		registerChiseledArmorRecipes(event, ItemsExtraBitManipulation.chiseledLeggings, Items.DIAMOND_LEGGINGS, 572);
		registerChiseledArmorRecipes(event, ItemsExtraBitManipulation.chiseledBoots, Items.DIAMOND_BOOTS, 272);
	}
	
	private static void registerChiseledArmorRecipes(RegistryEvent.Register<IRecipe> event, Item output, Item inputArmor, int bitCost)
	{
		ModItems items = ChiselsAndBits.getItems();
		registerChiseledArmorRecipes(event, output, inputArmor, items.itemChiselStone, bitCost);
		registerChiseledArmorRecipes(event, output, inputArmor, items.itemChiselIron, bitCost);
		registerChiseledArmorRecipes(event, output, inputArmor, items.itemChiselGold, bitCost);
		registerChiseledArmorRecipes(event, output, inputArmor, items.itemChiselDiamond, bitCost);
	}
	
	private static void registerChiseledArmorRecipes(RegistryEvent.Register<IRecipe> event, Item output, Item input, Item inputChisel, int bitCost)
	{
		registerChiseledArmorRecipe(event, output, input, inputChisel, bitCost);
		registerChiseledArmorRecipe(event, input, output, inputChisel, bitCost);
	}
	
	private static void registerChiseledArmorRecipe(RegistryEvent.Register<IRecipe> event, Item output, Item input, Item inputChisel, int bitCost)
	{
		event.getRegistry().register(new RecipeChiseledArmor(output, input, inputChisel, bitCost));
	}
	
}