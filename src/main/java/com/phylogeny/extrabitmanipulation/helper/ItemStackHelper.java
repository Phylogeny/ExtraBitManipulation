package com.phylogeny.extrabitmanipulation.helper;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class ItemStackHelper
{
	
	public static void saveStackToNBT(NBTTagCompound nbt, ItemStack stack, String key)
	{
		NBTTagCompound nbt2 = new NBTTagCompound();
		if (stack != null)
		{
			stack.writeToNBT(nbt2);
		}
		nbt.setTag(key, nbt2);
	}
	
	public static ItemStack loadStackFromNBT(NBTTagCompound nbt, String key)
	{
		ItemStack stack = null;
		if (nbt.hasKey(key))
		{
			stack = ItemStack.loadItemStackFromNBT((NBTTagCompound) nbt.getTag(key));
		}
		return stack;
	}
	
	public static void stackToBytes(ByteBuf buffer, ItemStack bitStack)
	{
		boolean notNull = bitStack != null;
		buffer.writeBoolean(notNull);
		if (notNull) ByteBufUtils.writeItemStack(buffer, bitStack);
	}
	
	public static ItemStack stackFromBytes(ByteBuf buffer)
	{
		return buffer.readBoolean() ? ByteBufUtils.readItemStack(buffer) : null;
	}
	
}