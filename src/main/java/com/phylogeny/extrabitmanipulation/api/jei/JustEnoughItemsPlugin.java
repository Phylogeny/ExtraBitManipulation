package com.phylogeny.extrabitmanipulation.api.jei;

import java.util.ArrayList;
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
import mezz.jei.util.Translator;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.registry.ModItems;

@mezz.jei.api.JEIPlugin
public class JustEnoughItemsPlugin extends BlankModPlugin
{
	
	public static String translate(String langKey)
	{
		return Translator.translateToLocal("jei." + Reference.GROUP_ID + "." + langKey);
	}
	
	@Override
	public void register(IModRegistry registry)
	{
		addDescription(registry, "bit_wrench", ItemsExtraBitManipulation.bitWrench);
		List<ItemStack> modelingStacks = addDescription(registry, "modeling_tool", ItemsExtraBitManipulation.modelingTool);
		List<ItemStack> sculptingStacks = addDescription(registry, "sculpting", ItemsExtraBitManipulation.sculptingLoop, ItemsExtraBitManipulation.sculptingSquare,
				ItemsExtraBitManipulation.sculptingSpadeCurved, ItemsExtraBitManipulation.sculptingSpadeSquared);
		ModItems items = ChiselsAndBits.getItems();
		addDescription(registry, "block_bit", items.itemBlockBit);
		addDescription(registry, "designs", items.itemMirrorprint, items.itemNegativeprint, items.itemPositiveprint);
		registry.addRecipeCategories(new ShapeInfoRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
		registry.addRecipeHandlers(new ShapeInfoRecipeHandler());
		registry.addRecipes(ShapeInfoRecipe.create(sculptingStacks));
		registry.addRecipeCategories(new ModelInfoRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
		registry.addRecipeHandlers(new ModelInfoRecipeHandler());
		registry.addRecipes(ModelInfoRecipe.create(modelingStacks));
		for (ItemStack sculptingStack : sculptingStacks)
			registry.addRecipeCategoryCraftingItem(sculptingStack, ShapeInfoRecipeCategory.UID);
		
		registry.addRecipeCategoryCraftingItem(modelingStacks.get(0), ModelInfoRecipeCategory.UID);
	}
	
	private List<ItemStack> addDescription(IModRegistry registry, String langKeySuffix, Item... items)
	{
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		for (Item item : items)
			stacks.add(new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
		
		registry.addDescription(stacks, "jei.description." + Reference.GROUP_ID + ":" + langKeySuffix);
		return stacks;
	}
	
}