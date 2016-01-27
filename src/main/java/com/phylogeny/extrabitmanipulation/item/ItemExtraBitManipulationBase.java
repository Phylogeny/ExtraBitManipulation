package com.phylogeny.extrabitmanipulation.item;

import java.util.List;

import com.phylogeny.extrabitmanipulation.client.creativetab.CreativeTabExtraBitManipulation;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
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
		cycleData(stack, "mode", forward, modeTitles.length);
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
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player)
    {
		stack.setTagCompound(new NBTTagCompound());
    }
	
	public String getItemStackDisplayName(ItemStack stack)
    {
		int mode = stack.hasTagCompound() ? stack.getTagCompound().getInteger("mode") : 0;
        return ("" + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim() + " - " + modeTitles[mode];
    }
	
}
