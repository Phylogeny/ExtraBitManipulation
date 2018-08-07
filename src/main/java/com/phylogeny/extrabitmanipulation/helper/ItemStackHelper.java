package com.phylogeny.extrabitmanipulation.helper;

import javax.annotation.Nullable;

import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.armor.capability.ChiseledArmorSlotsHandler;
import com.phylogeny.extrabitmanipulation.armor.capability.IChiseledArmorSlotsHandler;
import com.phylogeny.extrabitmanipulation.item.ItemBitToolBase;
import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool;
import com.phylogeny.extrabitmanipulation.item.ItemSculptingTool;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

import mod.chiselsandbits.api.ItemType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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
	
	public static boolean isChiseledArmorStack(ItemStack stack)
	{
		return stack != null && isChiseledArmorItem(stack.getItem());
	}
	
	public static boolean isChiseledArmorItem(Item item)
	{
		return item != null && item instanceof ItemChiseledArmor;
	}
	
	public static boolean isDesignStack(ItemStack stack)
	{
		ItemType itemType = ChiselsAndBitsAPIAccess.apiInstance.getItemType(stack);
		return stack != null && isDesignItemType(itemType);
	}
	
	public static boolean isDesignItemType(ItemType itemType)
	{
		return itemType == ItemType.MIRROR_DESIGN || itemType == ItemType.NEGATIVE_DESIGN || itemType == ItemType.POSITIVE_DESIGN;
	}
	
	public static NBTTagCompound getArmorData(NBTTagCompound armorNbt)
	{
		return armorNbt.getCompoundTag(NBTKeys.ARMOR_DATA);
	}
	
	public static ItemStack getChiseledArmorStack(EntityPlayer player, @Nullable ArmorType armorType, int indexArmorSet)
	{
		if (armorType == null)
			return player.getHeldItemMainhand();
		else if (indexArmorSet == 0)
			return player.getItemStackFromSlot(armorType.getEquipmentSlot());
		
		IChiseledArmorSlotsHandler cap = ChiseledArmorSlotsHandler.getCapability(player);
		return cap == null ? null : cap.getStackInSlot(armorType.getSlotIndex(indexArmorSet));
	}
	
	public static boolean isChiseledArmorNotEmpty(ItemStack stack)
	{
		return getArmorData(getNBTOrNew(stack)).getBoolean(NBTKeys.ARMOR_NOT_EMPTY);
	}
	
}