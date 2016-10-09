package com.phylogeny.extrabitmanipulation.init;

import java.util.ArrayList;

import com.phylogeny.extrabitmanipulation.config.ConfigRecipe;
import com.phylogeny.extrabitmanipulation.reference.Configs;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipesExtraBitManipulation
{
	public static void recipeInit()
	{
		if (!OreDictionary.doesOreNameExist("nuggetDiamond"))
		{
			OreDictionary.registerOre("nuggetDiamond", ItemsExtraBitManipulation.DiamondNugget);
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.DIAMOND),
					new Object[]{
						"nuggetDiamond", "nuggetDiamond", "nuggetDiamond",
						"nuggetDiamond", "nuggetDiamond", "nuggetDiamond",
						"nuggetDiamond", "nuggetDiamond", "nuggetDiamond"
			}));
			GameRegistry.addShapelessRecipe(new ItemStack(ItemsExtraBitManipulation.DiamondNugget, 9),
					new Object[]{
						Items.DIAMOND
			});
		}
		for (Item item : Configs.itemRecipeMap.keySet())
		{
			ConfigRecipe configRecipe = (ConfigRecipe) Configs.itemRecipeMap.get(item);
			if (configRecipe.isEnabled)
				registerRecipe(item, configRecipe.isShaped, configRecipe.useOreDictionary, configRecipe.recipe, configRecipe.getRecipeDefault());
		}
	}
	
	private static void registerRecipe(Item item, boolean isShaped, boolean useOreDictionary,
			String[] userInput, String[] defaultInput)
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
				{
					recipe[i] = i < root ? template.get(i) : ingredients.get(i - root);
				}
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
				{
					recipe[i] = ingredients.get(i);
				}
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
			ItemStack itemStack = getStack(Item.getByNameOrId(name));
			if (isShaped || !ingredients.contains(itemStack))
				ingredients.add(itemStack);
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