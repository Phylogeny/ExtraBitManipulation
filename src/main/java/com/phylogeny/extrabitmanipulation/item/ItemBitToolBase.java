package com.phylogeny.extrabitmanipulation.item;

import java.util.List;

import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.config.ConfigProperty;
import com.phylogeny.extrabitmanipulation.config.ConfigBitToolSettingBase;
import com.phylogeny.extrabitmanipulation.init.KeyBindingsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.reference.Configs;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

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
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return slotChanged;
	}
	
	protected void damageTool(ItemStack stack, EntityPlayer player)
	{
		ConfigProperty config = (ConfigProperty) Configs.itemPropertyMap.get(this);
		if (config.takesDamage)
		{
			stack.damageItem(1, player);
			if (stack.getItemDamage() > config.maxDamage)
			{
				player.renderBrokenItemStack(stack);
				player.destroyCurrentEquippedItem();
			}
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
			tooltip.add(EnumChatFormatting.BLUE + "Blue = data stored/accessed per client");
			tooltip.add(EnumChatFormatting.GREEN + "Green = data stored/accessed per tool");
			tooltip.add("");
		}
	}
	
	protected void addKeyInformation(List tooltip, boolean hasSettings)
	{
		if (hasSettings)
			tooltip.add("Hold SHIFT for settings.");
		
		tooltip.add("Hold CONTROL for controls.");
		tooltip.add(EnumChatFormatting.AQUA + "Use the Chisels & Bits radial");
		tooltip.add(EnumChatFormatting.AQUA + "    menu key ["
				+ GameSettings.getKeyDisplayString(ClientHelper.getChiselsAndBitsMenuKeyBind().getKeyCode()) + "] or the");
		tooltip.add(EnumChatFormatting.AQUA + "    controls listed above");
		tooltip.add(EnumChatFormatting.AQUA + "    to change tool settings.");
	}
	
	protected void addKeybindReminders(List<String> tooltip, KeyBindingsExtraBitManipulation... keyBinds)
	{
		tooltip.add("");
		tooltip.add(EnumChatFormatting.DARK_AQUA + ">>Replacable with " + (keyBinds.length > 1 ? "Keybinds" : "a Keybind") + "<<");
	}
	
	protected String getColoredKeyBindText(KeyBindingsExtraBitManipulation keyBind)
	{
		return EnumChatFormatting.DARK_AQUA + keyBind.getText() + EnumChatFormatting.GRAY;
	}
	
}
