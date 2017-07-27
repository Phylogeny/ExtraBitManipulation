package com.phylogeny.extrabitmanipulation.recipe;

import java.util.Random;

import javax.annotation.Nullable;

import mod.chiselsandbits.items.ItemChisel;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.phylogeny.extrabitmanipulation.reference.Reference;

public class RecipeChiseledArmor implements IRecipe
{
	private final ItemStack output;
	public final NonNullList<Ingredient> ingredients;
	private Random rand = new Random();
	private ItemStack chiselRemaining = ItemStack.EMPTY;
	private Item input;
	private int bitCost;
	private ResourceLocation registryName;
	
	public RecipeChiseledArmor(Item output, Item input, Item inputChisel, int bitCost)
	{
		ingredients = NonNullList.<Ingredient>create();
		ingredients.add(Ingredient.fromStacks(new ItemStack(input)));
		ingredients.add(Ingredient.fromStacks(new ItemStack(inputChisel)));
		this.output = new ItemStack(output);
		this.input = input;
		this.bitCost = bitCost;
		registryName = new ResourceLocation(Reference.MOD_ID, "chiseledarmor" + "_"
				+ getItemName(inputChisel) + "+" + getItemName(input) + "=" + getItemName(output));
	}
	
	private String getItemName(Item item)
	{
		ResourceLocation name = item.getRegistryName();
		return name != null ? name.toString().substring(name.toString().indexOf(":") + 1).replace("_", "") : "";
	}
	
	@Override
	public boolean matches(InventoryCrafting inv, World world)
	{
		ItemStack chisel = ItemStack.EMPTY;
		boolean foundArmor = false;
		int itemCount = 0;
		for (int x = 0; x < inv.getWidth(); x++)
		{
			for (int y = 0; y < inv.getHeight(); y++)
			{
				ItemStack stack = inv.getStackInRowAndColumn(x, y);
				if (stack.isEmpty())
					continue;
				
				itemCount++;
				boolean isChisel = stack.getItem() instanceof ItemChisel;
				if (isChisel)
					chisel = stack;
				
				boolean isArmor = stack.getItem() == input;
				if (isArmor)
					foundArmor = true;
				
				if (!isChisel && !isArmor)
					return false;
			}
		}
		if (!chisel.isEmpty() && foundArmor && itemCount == 2)
		{
			chiselRemaining = chisel;
			return true;
		}
		return false;
	}
	
	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
	{
		NonNullList<ItemStack> remainingItems = NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);
		for (int i = 0; i < remainingItems.size(); i++)
		{
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() instanceof ItemChisel)
			{
				if (chiselRemaining.isItemStackDamageable() && chiselRemaining.attemptDamageItem(bitCost, rand, null))
					chiselRemaining = ItemStack.EMPTY;
				
				remainingItems.set(i, chiselRemaining.copy());
				break;
			}
		}
		return remainingItems;
	}
	
	@Override
	public IRecipe setRegistryName(ResourceLocation name)
	{
		registryName = name;
		return this;
	}
	
	@Override
	@Nullable
	public ResourceLocation getRegistryName()
	{
		return registryName;
	}
	
	@Override
	public Class<IRecipe> getRegistryType()
	{
		return IRecipe.class;
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		return this.output.copy();
	}
	
	@Override
	public boolean canFit(int width, int height)
	{
		return width * height >= ingredients.size();
	}
	
	@Override
	public ItemStack getRecipeOutput()
	{
		return output;
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients()
	{
		return ingredients;
	}
	
}