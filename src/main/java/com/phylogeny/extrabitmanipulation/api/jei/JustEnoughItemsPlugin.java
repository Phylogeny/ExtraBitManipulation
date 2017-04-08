package com.phylogeny.extrabitmanipulation.api.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.phylogeny.extrabitmanipulation.api.jei.model.ModelInfoRecipe;
import com.phylogeny.extrabitmanipulation.api.jei.model.ModelInfoRecipeCategory;
import com.phylogeny.extrabitmanipulation.api.jei.model.ModelInfoRecipeHandler;
import com.phylogeny.extrabitmanipulation.api.jei.shape.ShapeInfoRecipe;
import com.phylogeny.extrabitmanipulation.api.jei.shape.ShapeInfoRecipeCategory;
import com.phylogeny.extrabitmanipulation.api.jei.shape.ShapeInfoRecipeHandler;
import com.phylogeny.extrabitmanipulation.init.ItemsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.reference.Reference;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.registry.ModItems;

@JEIPlugin
public class JustEnoughItemsPlugin extends BlankModPlugin
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
		registry.addRecipeCategories(new ShapeInfoRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
		registry.addRecipeHandlers(new ShapeInfoRecipeHandler());
		registry.addRecipes(ShapeInfoRecipe.create(registry.getJeiHelpers().getGuiHelper(), sculptingStacks));
		registry.addRecipeCategories(new ModelInfoRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
		registry.addRecipeHandlers(new ModelInfoRecipeHandler());
		registry.addRecipes(ModelInfoRecipe.create(registry.getJeiHelpers().getGuiHelper(), Collections.singletonList(modelingStack)));
	}
	
	private ItemStack addDescription(IModRegistry registry, Item item)
	{
		ItemStack stack = new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
		registry.addDescription(stack, "jei.description." + item.getRegistryName());
		return stack;
	}
	
	private List<ItemStack> addDescription(IModRegistry registry, String langKeySuffix, Item... items)
	{
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		for (Item item : items)
		{
			stacks.add(new ItemStack(item));
		}
		registry.addDescription(stacks, "jei.description." + Reference.MOD_ID + ":" + langKeySuffix);
		return stacks;
	}
	
}