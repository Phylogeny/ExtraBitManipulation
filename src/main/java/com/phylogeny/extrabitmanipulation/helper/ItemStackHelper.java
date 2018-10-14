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
	
	public static NBTTagCompound initNBT(ItemStack stack)
	{
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		
		return stack.getTagCompound();
	}
	
	public static int getInt(NBTTagCompound nbt, int intValue, String key)
	{
		if (nbt != null && nbt.hasKey(key))
			intValue = nbt.getInteger(key);
		
		return intValue;
	}
	
	public static void setInt(EntityPlayer player, ItemStack stack, int intValue, String key)
	{
		NBTTagCompound nbt = initNBT(stack);
		nbt.setInteger(key, intValue);
		player.inventoryContainer.detectAndSendChanges();
	}
	
	public static boolean getBoolean(NBTTagCompound nbt, boolean booleanValue, String key)
	{
		if (nbt != null && nbt.hasKey(key))
			booleanValue = nbt.getBoolean(key);
		
		return booleanValue;
	}
	
	public static void setBoolean(EntityPlayer player, ItemStack stack, boolean booleanValue, String key)
	{
		NBTTagCompound nbt = initNBT(stack);
		nbt.setBoolean(key, booleanValue);
		player.inventoryContainer.detectAndSendChanges();
	}
	
	public static ItemStack getStack(@Nullable NBTTagCompound nbt, String key)
	{
		return nbt != null ? loadStackFromNBT(nbt, key) : ItemStack.EMPTY;
	}
	
	public static void setStack(EntityPlayer player, ItemStack stack, ItemStack stackToSet, String key)
	{
		NBTTagCompound nbt = initNBT(stack);
		saveStackToNBT(nbt, stackToSet, key);
		player.inventoryContainer.detectAndSendChanges();
	}
	
	public static void saveStackToNBT(NBTTagCompound nbt, ItemStack stack, String key)
	{
		NBTTagCompound nbt2 = new NBTTagCompound();
		stack.writeToNBT(nbt2);
		nbt.setTag(key, nbt2);
	}
	
	public static ItemStack loadStackFromNBT(NBTTagCompound nbt, String key)
	{
		ItemStack stack = ItemStack.EMPTY;
		if (nbt.hasKey(key))
			stack = new ItemStack((NBTTagCompound) nbt.getTag(key));
		
		return stack;
	}
	
	@SuppressWarnings("null")
	public static boolean hasKey(ItemStack stack, String key)
	{
		return stack.hasTagCompound() && stack.getTagCompound().hasKey(key);
	}
	
	public static NBTTagCompound getNBT(ItemStack stack)
	{
		return stack.getTagCompound();
	}
	
	public static NBTTagCompound getNBTOrNew(ItemStack stack)
	{
		return stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
	}
	
	public static boolean isModelingToolStack(ItemStack stack)
	{
		return isModelingToolItem(stack.getItem());
	}
	
	public static boolean isModelingToolItem(Item item)
	{
		return item != null && item instanceof ItemModelingTool;
	}
	
	public static boolean isSculptingToolStack(ItemStack stack)
	{
		return isSculptingToolItem(stack.getItem());
	}
	
	public static boolean isSculptingToolItem(Item item)
	{
		return item != null && item instanceof ItemSculptingTool;
	}
	
	public static boolean isBitToolStack(ItemStack stack)
	{
		return isBitToolItem(stack.getItem());
	}
	
	public static boolean isBitToolItem(Item item)
	{
		return item != null && item instanceof ItemBitToolBase;
	}
	
	public static boolean isBitWrenchStack(ItemStack stack)
	{
		return isBitWrenchItem(stack.getItem());
	}
	
	public static boolean isBitWrenchItem(Item item)
	{
		return item != null && item instanceof ItemBitWrench;
	}
	
	public static boolean isChiseledArmorStack(ItemStack stack)
	{
		return isChiseledArmorItem(stack.getItem());
	}
	
	public static boolean isChiseledArmorItem(Item item)
	{
		return item != null && item instanceof ItemChiseledArmor;
	}
	
	public static boolean isDesignStack(ItemStack stack)
	{
		return isDesignItemType(ChiselsAndBitsAPIAccess.apiInstance.getItemType(stack));
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
		return cap == null ? ItemStack.EMPTY : cap.getStackInSlot(armorType.getSlotIndex(indexArmorSet));
	}
	
	public static boolean isChiseledArmorNotEmpty(ItemStack stack)
	{
		return getArmorData(getNBTOrNew(stack)).getBoolean(NBTKeys.ARMOR_NOT_EMPTY);
	}
	
}