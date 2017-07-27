package com.phylogeny.extrabitmanipulation.item;

import java.util.List;

import mod.chiselsandbits.api.KeyBindingContext;
import mod.chiselsandbits.api.ModKeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.ForgeEventFactory;

import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.config.ConfigBitToolSettingBase;
import com.phylogeny.extrabitmanipulation.config.ConfigProperty;
import com.phylogeny.extrabitmanipulation.init.KeyBindingsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.reference.Configs;

@KeyBindingContext(value = {"menuitem"}, applyToSubClasses = true)
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
				player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
				ForgeEventFactory.onPlayerDestroyItem(player, stack, EnumHand.MAIN_HAND);
			}
		}
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
	
	public static String colorSettingText(String text, ConfigBitToolSettingBase setting)
	{
		return (setting.isPerTool() ? TextFormatting.GREEN : TextFormatting.BLUE) + text;
	}
	
	public static void addColorInformation(List tooltip, boolean shiftDown)
	{
		if (shiftDown)
		{
			tooltip.add(TextFormatting.BLUE + "Blue = data stored/accessed per client");
			tooltip.add(TextFormatting.GREEN + "Green = data stored/accessed per tool");
			tooltip.add("");
		}
	}
	
	public static void addKeyInformation(List tooltip, boolean hasSettings)
	{
		if (hasSettings)
			tooltip.add("Hold SHIFT for settings.");
		
		tooltip.add("Hold CONTROL for controls.");
		tooltip.add(TextFormatting.AQUA + "Use the Chisels & Bits radial");
		tooltip.add(TextFormatting.AQUA + "    menu key [" + (ChiselsAndBitsAPIAccess.apiInstance == null ? "null"
				: ChiselsAndBitsAPIAccess.apiInstance.getKeyBinding(ModKeyBinding.MODE_MENU).getDisplayName()) + "] or the");
		tooltip.add(TextFormatting.AQUA + "    controls listed above");
		tooltip.add(TextFormatting.AQUA + "    to change tool settings.");
	}
	
	public static void addKeybindReminders(List<String> tooltip, KeyBindingsExtraBitManipulation... keyBinds)
	{
		tooltip.add("");
		tooltip.add(TextFormatting.DARK_AQUA + ">>Replacable with " + (keyBinds.length > 1 ? "Keybinds" : "a Keybind") + "<<");
	}
	
	public static String getColoredKeyBindText(KeyBindingsExtraBitManipulation keyBind)
	{
		return TextFormatting.DARK_AQUA + keyBind.getText() + TextFormatting.GRAY;
	}
	
}
