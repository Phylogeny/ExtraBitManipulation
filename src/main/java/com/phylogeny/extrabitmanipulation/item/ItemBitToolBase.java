package com.phylogeny.extrabitmanipulation.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemBitToolBase extends ItemExtraBitManipulationBase
{
	
	public ItemBitToolBase(String name)
	{
		super(name);
		maxStackSize = 1;
	}
	
	public boolean initialize(ItemStack stack)
	{
		if (!stack.hasTagCompound())
		{
			stack.setTagCompound(new NBTTagCompound());
			return true;
		}
		return false;
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player)
    {
		stack.setTagCompound(new NBTTagCompound());
    }
	
}
