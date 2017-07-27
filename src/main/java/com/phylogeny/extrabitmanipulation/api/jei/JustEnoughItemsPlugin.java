package com.phylogeny.extrabitmanipulation.api.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.registry.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.phylogeny.extrabitmanipulation.api.jei.armor.ChiseledArmorInfoRecipe;
import com.phylogeny.extrabitmanipulation.api.jei.armor.ChiseledArmorInfoRecipeCategory;
import com.phylogeny.extrabitmanipulation.api.jei.armor.ChiseledArmorInfoRecipeHandler;
import com.phylogeny.extrabitmanipulation.api.jei.model.ModelInfoRecipe;
import com.phylogeny.extrabitmanipulation.api.jei.model.ModelInfoRecipeCategory;
import com.phylogeny.extrabitmanipulation.api.jei.model.ModelInfoRecipeHandler;
import com.phylogeny.extrabitmanipulation.api.jei.shape.ShapeInfoRecipe;
import com.phylogeny.extrabitmanipulation.api.jei.shape.ShapeInfoRecipeCategory;
import com.phylogeny.extrabitmanipulation.api.jei.shape.ShapeInfoRecipeHandler;
import com.phylogeny.extrabitmanipulation.init.BlocksExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.init.ItemsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.reference.Reference;

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
		List<ItemStack> sculptingStacks = addDescription(registry, "sculpting", ItemsExtraBitManipulation.sculptingLoop,
				ItemsExtraBitManipulation.sculptingSquare, ItemsExtraBitManipulation.sculptingSpadeCurved, ItemsExtraBitManipulation.sculptingSpadeSquared);
		ModItems items = ChiselsAndBits.getItems();
		addDescription(registry, items.itemBlockBit);
		addDescription(registry, "designs", items.itemMirrorprint, items.itemNegativeprint, items.itemPositiveprint);
		List<ItemStack> armorStacks = addDescription(registry, "chiseled_armor", ItemsExtraBitManipulation.chiseledHelmet,
				ItemsExtraBitManipulation.chiseledChestplate, ItemsExtraBitManipulation.chiseledLeggings, ItemsExtraBitManipulation.chiseledBoots);
		Item templateItem = Item.getItemFromBlock(BlocksExtraBitManipulation.bodyPartTemplate);
		addDescription(registry, templateItem);
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		registerCatagoryAndRecipes(registry, new ShapeInfoRecipeCategory(guiHelper), new ShapeInfoRecipeHandler(),
				ShapeInfoRecipe.create(guiHelper, sculptingStacks));
		registerCatagoryAndRecipes(registry, new ModelInfoRecipeCategory(guiHelper), new ModelInfoRecipeHandler(),
				ModelInfoRecipe.create(guiHelper, Collections.singletonList(modelingStack)));
		List<ItemStack> iconStacks = new ArrayList<ItemStack>();
		iconStacks.addAll(armorStacks);
		iconStacks.add(new ItemStack(templateItem));
		registerCatagoryAndRecipes(registry, new ChiseledArmorInfoRecipeCategory(guiHelper, armorStacks), new ChiseledArmorInfoRecipeHandler(),
				ChiseledArmorInfoRecipe.create(guiHelper, iconStacks));
	}
	
	private void registerCatagoryAndRecipes(IModRegistry registry, IRecipeCategory category, IRecipeHandler handler, Collection recipes)
	{
		registry.addRecipeCategories(category);
		registry.addRecipeHandlers(handler);
		registry.addRecipes(recipes);
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