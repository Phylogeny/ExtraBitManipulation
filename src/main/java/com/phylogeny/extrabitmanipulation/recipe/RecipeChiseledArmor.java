package com.phylogeny.extrabitmanipulation.recipe;

import java.util.Arrays;
import java.util.Random;

import mod.chiselsandbits.items.ItemChisel;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class RecipeChiseledArmor extends ShapelessRecipes
{
	private Random rand = new Random();
	private ItemStack chiselRemaining = ItemStack.EMPTY;
	private Item inputArmor;
	private int bitCost;
	
	public RecipeChiseledArmor(Item output, Item inputArmor, Item inputChisel, int bitCost)
	{
		super(new ItemStack(output), Arrays.asList(new ItemStack[]{new ItemStack(inputArmor), new ItemStack(inputChisel)}));
		this.inputArmor = inputArmor;
		this.bitCost = bitCost;
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
				
				boolean isArmor = stack.getItem() == inputArmor;
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
				if (chiselRemaining.isItemStackDamageable() && chiselRemaining.attemptDamageItem(bitCost, rand))
					chiselRemaining = ItemStack.EMPTY;
				
				remainingItems.set(i, chiselRemaining.copy());
				break;
			}
		}
		return remainingItems;
	}
	
}