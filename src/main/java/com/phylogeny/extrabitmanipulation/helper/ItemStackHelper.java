package com.phylogeny.extrabitmanipulation.helper;

import com.phylogeny.extrabitmanipulation.item.ItemBitToolBase;
import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool;
import com.phylogeny.extrabitmanipulation.item.ItemSculptingTool;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class ItemStackHelper
{
	
	public static void saveStackToNBT(NBTTagCompound nbt, ItemStack stack, String key)
	{
		NBTTagCompound nbt2 = new NBTTagCompound();
		if (stack != null)
			stack.writeToNBT(nbt2);
		
		nbt.setTag(key, nbt2);
	}
	
	public static ItemStack loadStackFromNBT(NBTTagCompound nbt, String key)
	{
		ItemStack stack = null;
		if (nbt.hasKey(key))
			stack = ItemStack.loadItemStackFromNBT((NBTTagCompound) nbt.getTag(key));
		
		return stack;
	}
	
	public static void stackToBytes(ByteBuf buffer, ItemStack bitStack)
	{
		boolean notNull = bitStack != null;
		buffer.writeBoolean(notNull);
		if (notNull)
			ByteBufUtils.writeItemStack(buffer, bitStack);
	}
	
	public static ItemStack stackFromBytes(ByteBuf buffer)
	{
		return buffer.readBoolean() ? ByteBufUtils.readItemStack(buffer) : null;
	}
	
	public static boolean hasNBT(ItemStack stack)
	{
		return stack == null ? false : stack.hasTagCompound();
	}
	
	public static boolean hasKey(ItemStack stack, String key)
	{
		return hasNBT(stack) && getNBT(stack).hasKey(key);
	}
	
	public static NBTTagCompound getNBT(ItemStack stack)
	{
		return stack == null ? null : stack.getTagCompound();
	}
	
	public static NBTTagCompound getNBTOrNew(ItemStack stack)
	{
		return hasNBT(stack) ? stack.getTagCompound() : new NBTTagCompound();
	}
	
	public static boolean isModelingToolStack(ItemStack stack)
	{
		return stack != null && isModelingToolItem(stack.getItem());
	}
	
	public static boolean isModelingToolItem(Item item)
	{
		return item != null && item instanceof ItemModelingTool;
	}
	
	public static boolean isSculptingToolStack(ItemStack stack)
	{
		return stack != null && isSculptingToolItem(stack.getItem());
	}
	
	public static boolean isSculptingToolItem(Item item)
	{
		return item != null && item instanceof ItemSculptingTool;
	}
	
	public static boolean isBitToolStack(ItemStack stack)
	{
		return stack != null && isBitToolItem(stack.getItem());
	}
	
	public static boolean isBitToolItem(Item item)
	{
		return item != null && item instanceof ItemBitToolBase;
	}
	
	public static boolean isBitWrenchStack(ItemStack stack)
	{
		return stack != null && isBitWrenchItem(stack.getItem());
	}
	
	public static boolean isBitWrenchItem(Item item)
	{
		return item != null && item instanceof ItemBitWrench;
	}
	
}