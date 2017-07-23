package com.phylogeny.extrabitmanipulation.api.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.registry.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.phylogeny.extrabitmanipulation.api.jei.model.ModelInfoRecipe;
import com.phylogeny.extrabitmanipulation.api.jei.model.ModelInfoRecipeCategory;
import com.phylogeny.extrabitmanipulation.api.jei.model.ModelInfoRecipeHandler;
import com.phylogeny.extrabitmanipulation.api.jei.shape.ShapeInfoRecipe;
import com.phylogeny.extrabitmanipulation.api.jei.shape.ShapeInfoRecipeCategory;
import com.phylogeny.extrabitmanipulation.api.jei.shape.ShapeInfoRecipeHandler;
import com.phylogeny.extrabitmanipulation.init.ItemsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.reference.Reference;

@JEIPlugin
public class JustEnoughItemsPlugin implements IModPlugin
{
	
	public static String translate(String langKey)
	{
		return Translator.translateToLocal("jei." + Reference.MOD_ID + "." + langKey);
	}
	
	@Override
	public void register(IModRegistry registry)
	{
		addDescription(registry, ItemsExtraBitManipulation.bitWrench);
		ItemStack modelingStack = addDescription(registry, ItemsExtraBitManipulation.modelingTool);
		List<ItemStack> sculptingStacks = addDescription(registry, "sculpting", ItemsExtraBitManipulation.sculptingLoop, ItemsExtraBitManipulation.sculptingSquare,
				ItemsExtraBitManipulation.sculptingSpadeCurved, ItemsExtraBitManipulation.sculptingSpadeSquared);
		ModItems items = ChiselsAndBits.getItems();
		addDescription(registry, items.itemBlockBit);
		addDescription(registry, "designs", items.itemMirrorprint, items.itemNegativeprint, items.itemPositiveprint);
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		registry.handleRecipes(ShapeInfoRecipe.class, new ShapeInfoRecipeHandler(), ShapeInfoRecipeCategory.UID);
		registry.addRecipes(ShapeInfoRecipe.create(guiHelper, sculptingStacks), ShapeInfoRecipeCategory.UID);
		registry.handleRecipes(ModelInfoRecipe.class, new ModelInfoRecipeHandler(), ModelInfoRecipeCategory.UID);
		registry.addRecipes(ModelInfoRecipe.create(guiHelper, Collections.singletonList(modelingStack)), ModelInfoRecipeCategory.UID);
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry)
	{
		registry.addRecipeCategories(new ShapeInfoRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
		registry.addRecipeCategories(new ModelInfoRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
	}
	
	private ItemStack addDescription(IModRegistry registry, Item item)
	{
		ItemStack stack = new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
		registry.addIngredientInfo(stack, ItemStack.class, "jei.description." + item.getRegistryName());
		return stack;
	}
	
	private List<ItemStack> addDescription(IModRegistry registry, String langKeySuffix, Item... items)
	{
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		for (Item item : items)
		{
			stacks.add(new ItemStack(item));
		}
		registry.addIngredientInfo(stacks, ItemStack.class, "jei.description." + Reference.MOD_ID + ":" + langKeySuffix);
		return stacks;
	}
	
}