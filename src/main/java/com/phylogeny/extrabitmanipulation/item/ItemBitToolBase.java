package com.phylogeny.extrabitmanipulation.item;

import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemBitToolBase extends ItemExtraBitManipulationBase
{
	public String[] modeTitles;
	
	public ItemBitToolBase(String name)
	{
		super(name);
		maxStackSize = 1;
	}
	
	public void cycleModes(ItemStack stack, boolean forward)
	{
		initialize(stack);
		NBTTagCompound nbt = stack.getTagCompound();
		int mode = nbt.getInteger(NBTKeys.MODE);
		nbt.setInteger(NBTKeys.MODE, cycleData(mode, forward, modeTitles.length));
	}
	
	public int cycleData(int intValue, boolean forward, int max)
	{
		return (intValue + (forward ? 1 : max - 1)) % max;
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
	
	public String getItemStackDisplayName(ItemStack stack)
    {
		int mode = stack.hasTagCompound() ? stack.getTagCompound().getInteger(NBTKeys.MODE) : 0;
        return ("" + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim()
        		+ " - " + modeTitles[mode];
    }
	
}
