package com.phylogeny.extrabitmanipulation.item;

import java.util.List;

import com.phylogeny.extrabitmanipulation.config.ConfigProperty;
import com.phylogeny.extrabitmanipulation.config.ConfigBitToolSettingBase;
import com.phylogeny.extrabitmanipulation.reference.Configs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
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
		if (stack.hasTagCompound())
			return false;
			
		stack.setTagCompound(new NBTTagCompound());
		return true;
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player)
	{
		stack.setTagCompound(new NBTTagCompound());
		initialize(stack);
	}
	
	protected void damageTool(ItemStack stack, EntityPlayer player)
	{
		ConfigProperty config = (ConfigProperty) Configs.itemPropertyMap.get(this);
		if (config.takesDamage)
		{
			stack.damageItem(1, player);
			if (stack.getItemDamage() > config.maxDamage)
				player.renderBrokenItemStack(stack);
		}
	}
	
	protected String colorSettingText(String text, ConfigBitToolSettingBase setting)
	{
		return (setting.isPerTool() ? EnumChatFormatting.GREEN : EnumChatFormatting.BLUE) + text;
	}
	
	protected void initInt(NBTTagCompound nbt, String nbtKey, int initInt)
	{
		if (!nbt.hasKey(nbtKey))
			nbt.setInteger(nbtKey, initInt);
	}
	
	protected void initBoolean(NBTTagCompound nbt, String nbtKey, boolean initBoolean)
	{
		if (!nbt.hasKey(nbtKey))
			nbt.setBoolean(nbtKey, initBoolean);
	}
	
	protected void addColorInformation(List tooltip, boolean shiftDown)
	{
		if (shiftDown)
		{
			tooltip.add("");
			tooltip.add(EnumChatFormatting.BLUE + "Blue = data stored/accessed per player");
			tooltip.add(EnumChatFormatting.GREEN + "Green = data stored/accessed per tool");
			tooltip.add("");
		}
	}
	
	protected void addKeyInformation(List tooltip)
	{
		tooltip.add("Hold SHIFT for settings.");
		tooltip.add("Hold CONTROL for controls.");
	}
	
}
