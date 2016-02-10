package com.phylogeny.extrabitmanipulation.item;

import com.phylogeny.extrabitmanipulation.config.ConfigProperty;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;
import com.phylogeny.extrabitmanipulation.reference.Utility;

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
		cycleData(stack, NBTKeys.MODE, forward, modeTitles.length);
	}
	
	public void cycleData(ItemStack stack, String key, boolean forward, int max)
	{
		initialize(stack);
		NBTTagCompound nbt = stack.getTagCompound();
		int mode = (nbt.getInteger(key) + (forward ? 1 : max - 1)) % max;
		nbt.setInteger(key, mode);
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
		ConfigProperty config = (ConfigProperty) Configs.itemPropertyMap.get(this);
		int size = stack.hasTagCompound() ? stack.getTagCompound().getInteger(NBTKeys.SCULPT_SEMI_DIAMETER) : config.defaultRemovalSemiDiameter;
		if (Configs.DISPLAY_NAME_DIAMETER)
		{
			size = size * 2 + 1;
		}
		String diameterText = "";
		if (Configs.DISPLAY_NAME_USE_METER_UNITS)
		{
			diameterText += Math.round(size * Utility.pixelD * 100) / 100.0 + " m";
		}
		else
		{
			diameterText += size + " b";
		}
		int mode = stack.hasTagCompound() ? stack.getTagCompound().getInteger(NBTKeys.MODE) : 0;
        return ("" + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim()
        		+ " - " + modeTitles[mode] + " - " + diameterText;
    }
	
}
