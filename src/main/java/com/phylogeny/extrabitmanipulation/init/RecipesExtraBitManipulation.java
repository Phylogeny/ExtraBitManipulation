package com.phylogeny.extrabitmanipulation.init;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.phylogeny.extrabitmanipulation.reference.Configs;

public class RecipesExtraBitManipulation
{
	public static void recipeInit()
	{
		if (Configs.RECIPE_BIT_WRENCH_IS_ENABLED)
		{
			registerRecipe(ItemsExtraBitManipulation.BitWrench, Configs.RECIPE_BIT_WRENCH_IS_SHAPED,
					Configs.RECIPE_BIT_WRENCH_ORE_DICTIONARY, Configs.RECIPE_BIT_WRENCH, Configs.RECIPE_BIT_WRENCH_DEFAULT);
		}
	}
	
	private static void registerRecipe(Item item, boolean isShaped, boolean useOreDictionary,
			String[] userInput, String[] defaultInput)
	{
		Object[] recipeArray = isShaped ? createShapedRecipeArray(userInput, useOreDictionary, isShaped)
				: createShapelessRecipeArray(userInput, useOreDictionary, isShaped);
		if (recipeArray == null && defaultInput.length > 0)
		{
			recipeArray = isShaped ? createShapedRecipeArray(defaultInput, useOreDictionary, isShaped)
					: createShapelessRecipeArray(defaultInput, useOreDictionary, isShaped);
		}
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
				{
					addIngredient(ingredients, name, useOreDictionary, isShaped);
				}
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
			if (isShaped || !ingredients.contains(name)) ingredients.add(name);
		}
		else
		{
			ItemStack itemStack = getStack((Item) Item.getByNameOrId(name));
			if (isShaped || !ingredients.contains(itemStack)) ingredients.add(itemStack);
		}
	}
	
	private static boolean isValidName(boolean useOreDictionary, String name)
	{
		if (useOreDictionary)
		{
			return doesOreNameExist(name);
		}
		else
		{
			return Item.getByNameOrId(name) != null;
		}
	}
	
	private static boolean doesOreNameExist(String name)
	{
		String[] names = OreDictionary.getOreNames();
		for (int i = 0; i < names.length; i++)
		{
			if (names[i].equals(name))
			{
				return true;
			}
		}
		return false;
	}

	private static ItemStack getStack(Item item)
	{
		return new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
	}
	
}