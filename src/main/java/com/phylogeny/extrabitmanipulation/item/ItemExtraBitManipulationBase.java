package com.phylogeny.extrabitmanipulation.item;

import java.util.List;

import com.phylogeny.extrabitmanipulation.client.creativetab.CreativeTabExtraBitManipulation;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ItemExtraBitManipulationBase extends Item
{
	private String name;
	public String[] modeTitles;
	
	public ItemExtraBitManipulationBase(String name, boolean takesDamage, int maxDamage)
	{
		this.name = name;
		setUnlocalizedName(name);
		setCreativeTab(CreativeTabExtraBitManipulation.creativeTab);
		maxStackSize = 1;
		if (takesDamage)
		{
			setMaxDamage(maxDamage);
		}
	}
	
	public String getName()
	{
		return name;
	}
	
	public void cycleModes(ItemStack stack, boolean forward)
	{
		String key = "mode";
		if (cycleData(stack, key, forward, modeTitles.length))
		{
			setToolDisplayName(stack, 1);
		}
		else
		{
			String name = stack.getDisplayName();
			int i = name.indexOf(" - ");
			setToolDisplayName(stack, i < 0 ? name : name.substring(0, i),
					stack.getTagCompound().getInteger(key));
		}
	}
	
	public boolean cycleData(ItemStack stack, String key, boolean forward, int max)
	{
		String name = stack.getDisplayName();
		boolean setTag = !stack.hasTagCompound();
		if (setTag)
		{
			stack.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound nbt = stack.getTagCompound();
		int mode = (nbt.getInteger(key) + (forward ? 1 : max - 1)) % max;
		nbt.setInteger(key, mode);
		return setTag;
	}
	
	public void onCreated(ItemStack stack, World world, EntityPlayer player)
    {
		stack.setTagCompound(new NBTTagCompound());
		setToolDisplayName(stack, 0);
    }
	
	public void setToolDisplayName(ItemStack stack, int mode)
	{
		setToolDisplayName(stack, stack.getDisplayName(), mode);
	}
	
	public void setToolDisplayName(ItemStack stack, String name, int mode)
	{
		stack.setStackDisplayName(EnumChatFormatting.RESET + name + " - " + modeTitles[mode]);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		int mode = stack.hasTagCompound() ? stack.getTagCompound().getInteger("mode") : 0;
		if (!stack.hasTagCompound())
		{
			setToolDisplayName(stack, mode);
		}
	}
	
}
