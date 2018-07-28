package com.phylogeny.extrabitmanipulation.init;

import java.util.ArrayList;

import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.registry.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.phylogeny.extrabitmanipulation.config.ConfigRecipe;
import com.phylogeny.extrabitmanipulation.recipe.RecipeChiseledArmor;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class RecipesExtraBitManipulation
{
	public static void recipeInit()
	{
		if (!Configs.disableDiamondNuggetOreDict)
			OreDictionary.registerOre("nuggetDiamond", ItemsExtraBitManipulation.diamondNugget);
		
		if (!Configs.disableDiamondToNuggets)
		{
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.DIAMOND), "nuggetDiamond", "nuggetDiamond", "nuggetDiamond",
						"nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "nuggetDiamond"));
		}
		if (!Configs.disableNuggetsToDiamond)
		{
			GameRegistry.addShapelessRecipe(new ItemStack(ItemsExtraBitManipulation.diamondNugget, 9), Items.DIAMOND);
		}
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.COBBLESTONE), BlocksExtraBitManipulation.bodyPartTemplate);
		for (Item item : Configs.itemRecipeMap.keySet())
		{
			ConfigRecipe configRecipe = (ConfigRecipe) Configs.itemRecipeMap.get(item);
			if (configRecipe.isEnabled)
				registerRecipe(item, configRecipe.isShaped, configRecipe.useOreDictionary, configRecipe.recipe, configRecipe.getRecipeDefault());
		}
		RecipeSorter.register(Reference.MOD_ID + ":chiseled_armor", RecipeChiseledArmor.class, Category.SHAPELESS, "after:minecraft:shapeless");
		registerChiseledArmorRecipes(ItemsExtraBitManipulation.chiseledHelmetDiamond, Items.DIAMOND_HELMET, 272);
		registerChiseledArmorRecipes(ItemsExtraBitManipulation.chiseledChestplateDiamond, Items.DIAMOND_CHESTPLATE, 444);
		registerChiseledArmorRecipes(ItemsExtraBitManipulation.chiseledLeggingsDiamond, Items.DIAMOND_LEGGINGS, 572);
		registerChiseledArmorRecipes(ItemsExtraBitManipulation.chiseledBootsDiamond, Items.DIAMOND_BOOTS, 272);
		registerChiseledArmorRecipes(ItemsExtraBitManipulation.chiseledHelmetIron, Items.IRON_HELMET, 272);
        registerChiseledArmorRecipes(ItemsExtraBitManipulation.chiseledChestplateIron, Items.IRON_CHESTPLATE, 444);
        registerChiseledArmorRecipes(ItemsExtraBitManipulation.chiseledLeggingsIron, Items.IRON_LEGGINGS, 572);
        registerChiseledArmorRecipes(ItemsExtraBitManipulation.chiseledBootsIron, Items.IRON_BOOTS, 272);
	}
	
	private static void registerChiseledArmorRecipes(Item output, Item inputArmor, int bitCost)
	{
		ModItems items = ChiselsAndBits.getItems();
		registerChiseledArmorRecipes(output, inputArmor, items.itemChiselStone, bitCost);
		registerChiseledArmorRecipes(output, inputArmor, items.itemChiselIron, bitCost);
		registerChiseledArmorRecipes(output, inputArmor, items.itemChiselGold, bitCost);
		registerChiseledArmorRecipes(output, inputArmor, items.itemChiselDiamond, bitCost);
		GameRegistry.addShapelessRecipe(new ItemStack(output), output);
	}
	
	private static void registerChiseledArmorRecipes(Item output, Item input, Item inputChisel, int bitCost)
	{
		registerChiseledArmorRecipe(output, input, inputChisel, bitCost);
		registerChiseledArmorRecipe(input, output, inputChisel, bitCost);
	}
	
	private static void registerChiseledArmorRecipe(Item output, Item input, Item inputChisel, int bitCost)
	{
		GameRegistry.addRecipe(new RecipeChiseledArmor(output, input, inputChisel, bitCost));
	}
	
	private static void registerRecipe(Item item, boolean isShaped, boolean useOreDictionary, String[] userInput, String[] defaultInput)
	{
		Object[] recipeArray = isShaped ? createShapedRecipeArray(userInput, useOreDictionary, isShaped)
				: createShapelessRecipeArray(userInput, useOreDictionary, isShaped);
		if (recipeArray == null && defaultInput.length > 0)
			recipeArray = isShaped ? createShapedRecipeArray(defaultInput, useOreDictionary, isShaped)
					: createShapelessRecipeArray(defaultInput, useOreDictionary, isShaped);
		
		if (recipeArray != null)
		{
			if (isShaped)
			{
				if (useOreDictionary)
				{
					GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), recipeArray));
				}
				else
				{
					GameRegistry.addRecipe(new ItemStack(item), recipeArray);
				}
			}
			else
			{
				if (useOreDictionary)
				{
					GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(item), recipeArray));
				}
				else
				{
					GameRegistry.addShapelessRecipe(new ItemStack(item), recipeArray);
				}
			}
		}
	}
	
	private static Object[] createShapedRecipeArray(String[] inputArr, boolean useOreDictionary, boolean isShaped)
	{
		Object[] recipe = null;
		double r = Math.sqrt(inputArr.length);
		if (r == (int) r && r > 0)
		{
			int root = (int) r;
			ArrayList<String> template = new ArrayList<String>();
			ArrayList ingredients = new ArrayList();
			String templateString = "";
			for (int i = 0; i < inputArr.length; i++)
			{
				String name = inputArr[i];
				char character = isValidName(useOreDictionary, name) ? ((char) (i + 65)) : ' ';
				templateString += character;
				if (templateString.length() == root)
				{
					template.add(templateString);
					templateString = "";
				}
				if (character != ' ')
				{
					ingredients.add(character);
					addIngredient(ingredients, name, useOreDictionary, isShaped);
				}
			}
			if (!ingredients.isEmpty())
			{
				recipe = new Object[root + ingredients.size()];
				for (int i = 0; i < recipe.length; i++)
					recipe[i] = i < root ? template.get(i) : ingredients.get(i - root);
			}
		}
		return recipe;
	}
	
	private static Object[] createShapelessRecipeArray(String[] inputArr, boolean useOreDictionary, boolean isShaped)
	{
		Object[] recipe = null;
		if (inputArr.length > 0)
		{
			ArrayList<String> ingredients = new ArrayList<String>();
			for (int i = 0; i < inputArr.length; i++)
			{
				String name = inputArr[i];
				if (isValidName(useOreDictionary, name))
					addIngredient(ingredients, name, useOreDictionary, isShaped);
			}
			if (!ingredients.isEmpty())
			{
				recipe = new Object[ingredients.size()];
				for (int i = 0; i < recipe.length; i++)
					recipe[i] = ingredients.get(i);
			}
		}
		return recipe;
	}
	
	private static void addIngredient(ArrayList ingredients, String name, boolean useOreDictionary, boolean isShaped)
	{
		if (useOreDictionary)
		{
			if (isShaped || !ingredients.contains(name))
				ingredients.add(name);
		}
		else
		{
			ItemStack stack = getStack(Item.getByNameOrId(name));
			if (isShaped || !ingredients.contains(stack))
				ingredients.add(stack);
		}
	}
	
	private static boolean isValidName(boolean useOreDictionary, String name)
	{
		if (useOreDictionary)
			return OreDictionary.doesOreNameExist(name);
		
		return Item.getByNameOrId(name) != null;
	}
	
	private static ItemStack getStack(Item item)
	{
		return new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
	}
	
}