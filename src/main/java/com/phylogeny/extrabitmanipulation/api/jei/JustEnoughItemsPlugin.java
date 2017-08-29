package com.phylogeny.extrabitmanipulation.api.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiRuntime;
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
	private static IJeiRuntime jeiRuntime;
	
	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime)
	{
		this.jeiRuntime = jeiRuntime;
	}
	
	public static void openCategory(String categoryUid)
	{
		jeiRuntime.getRecipesGui().showCategories(Collections.<String>singletonList(categoryUid));
	}
	
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
		List<ItemStack> armorStacks = addDescription(registry, "chiseled_armor", ItemsExtraBitManipulation.chiseledHelmet,
				ItemsExtraBitManipulation.chiseledChestplate, ItemsExtraBitManipulation.chiseledLeggings, ItemsExtraBitManipulation.chiseledBoots);
		Item templateItem = Item.getItemFromBlock(BlocksExtraBitManipulation.bodyPartTemplate);
		addDescription(registry, templateItem);
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		registerCatagoryAndRecipes(registry, new ShapeInfoRecipeCategory(guiHelper), new ShapeInfoRecipeHandler(),
				ShapeInfoRecipe.create(sculptingStacks));
		registerCatagoryAndRecipes(registry, new ModelInfoRecipeCategory(guiHelper), new ModelInfoRecipeHandler(),
				ModelInfoRecipe.create(modelingStacks));
		List<ItemStack> iconStacks = new ArrayList<ItemStack>();
		iconStacks.addAll(armorStacks);
		iconStacks.add(new ItemStack(templateItem));
		registerCatagoryAndRecipes(registry, new ChiseledArmorInfoRecipeCategory(guiHelper), new ChiseledArmorInfoRecipeHandler(),
				ChiseledArmorInfoRecipe.create(iconStacks));
		for (ItemStack sculptingStack : sculptingStacks)
			registry.addRecipeCategoryCraftingItem(sculptingStack, ShapeInfoRecipeCategory.UID);
		
		registry.addRecipeCategoryCraftingItem(modelingStacks.get(0), ModelInfoRecipeCategory.UID);
		for (ItemStack armorStack : armorStacks)
			registry.addRecipeCategoryCraftingItem(armorStack, ChiseledArmorInfoRecipeCategory.UID);
	}
	
	private void registerCatagoryAndRecipes(IModRegistry registry, IRecipeCategory category, IRecipeHandler handler, List recipes)
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
			stacks.add(new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
		
		registry.addDescription(stacks, "jei.description." + Reference.GROUP_ID + ":" + langKeySuffix);
		return stacks;
	}
	
}