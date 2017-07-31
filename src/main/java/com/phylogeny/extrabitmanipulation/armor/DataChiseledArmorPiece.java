package com.phylogeny.extrabitmanipulation.armor;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

import org.lwjgl.opengl.GL11;

import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

public class DataChiseledArmorPiece
{
	private List<GlOperation> globalGlOperationsPre = new ArrayList<GlOperation>();
	private List<GlOperation> globalGlOperationsPost = new ArrayList<GlOperation>();
	private List<ArmorItem>[] partItemLists;
	private ArmorType armorType;
	
	public DataChiseledArmorPiece(ArmorType armorType)
	{
		this.armorType = armorType;
		partItemLists = new ArrayList[armorType.getMovingpartCount()];
		for (int i = 0; i < partItemLists.length; i++)
		{
			partItemLists[i] = new ArrayList<ArmorItem>();
		}
	}
	
	public DataChiseledArmorPiece(NBTTagCompound nbt, ArmorType armorType)
	{
		this(armorType);
		loadFromNBT(nbt);
	}
	
	private List<GlOperation> getGlobalGlOperationsInternal(boolean isPre)
	{
		return isPre ? globalGlOperationsPre : globalGlOperationsPost;
	}
	
	public List<GlOperation> getGlobalGlOperations(boolean isPre)
	{
		List<GlOperation> glOperations = new ArrayList<GlOperation>();
		glOperations.addAll(getGlobalGlOperationsInternal(isPre));
		return glOperations;
	}
	
	public void addGlobalGlOperation(GlOperation glOperation, boolean isPre)
	{
		getGlobalGlOperationsInternal(isPre).add(glOperation);
	}
	
	public void addGlobalGlOperation(int index, GlOperation glOperation, boolean isPre)
	{
		getGlobalGlOperationsInternal(isPre).add(index, glOperation);
	}
	
	public void removeGlobalGlOperation(int index, boolean isPre)
	{
		getGlobalGlOperationsInternal(isPre).remove(index);
	}
	
	public void addItemToPart(int partIndex, ArmorItem armorItem)
	{
		if (outOfPartRange(partIndex))
			return;
		
		partItemLists[partIndex].add(armorItem);
	}
	
	public void addItemToPart(int partIndex, int armorItemIndex, ArmorItem armorItem)
	{
		if (outOfPartRange(partIndex))
			return;
		
		partItemLists[partIndex].add(armorItemIndex, armorItem);
	}
	
	public void removeItemFromPart(int partIndex, int armorItemIndex)
	{
		if (outOfPartRange(partIndex))
			return;
		
		partItemLists[partIndex].remove(armorItemIndex);
	}
	
	public List<ArmorItem> getArmorItemsForPart(int partIndex)
	{
		List<ArmorItem> armorItems = new ArrayList<ArmorItem>();
		if (outOfPartRange(partIndex))
			return armorItems;
		
		armorItems.addAll(partItemLists[partIndex]);
		return armorItems;
	}
	
	public ArmorItem getArmorItemForPart(int partIndex, int armorItemIndex)
	{
		return outOfPartRange(partIndex) ? new ArmorItem() : partItemLists[partIndex].get(armorItemIndex);
	}
	
	public int generateDisplayList(int partIndex, EntityLivingBase entity, float scale)
	{
		GlStateManager.pushMatrix();
		int displayList = GLAllocation.generateDisplayLists(1);
		GlStateManager.glNewList(displayList, GL11.GL_COMPILE);
		GlOperation.executeList(globalGlOperationsPre);
		for (ArmorItem armorItem : partItemLists[partIndex])
		{
			if (armorItem.isEmpty())
				continue;
			
			GlStateManager.pushMatrix();
			armorItem.executeGlOperations();
			GlOperation.executeList(globalGlOperationsPost);
			armorItem.render(entity, scale, armorType == ArmorType.BOOTS && partIndex == 0);
			GlStateManager.popMatrix();
		}
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.glEndList();
		GlStateManager.popMatrix();
		return displayList;
	}
	
	private boolean outOfPartRange(int partIndex)
	{
		return partIndex < 0 || partIndex >= partItemLists.length;
	}
	
	public void saveToNBT(NBTTagCompound nbt)
	{
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger(NBTKeys.ARMOR_TYPE, armorType.ordinal());
		NBTTagList movingParts = new NBTTagList();
		boolean empty = true;
		for (List<ArmorItem> partItemList : partItemLists)
		{
			NBTTagList itemList = new NBTTagList();
			for (ArmorItem armorItem : partItemList)
			{
				NBTTagCompound armorItemNbt = new NBTTagCompound();
				armorItem.saveToNBT(armorItemNbt);
				itemList.appendTag(armorItemNbt);
				if (!armorItem.getStack().isEmpty())
					empty = false;
			}
			movingParts.appendTag(itemList);
		}
		data.setTag(NBTKeys.ARMOR_PART_DATA, movingParts);
		data.setBoolean(NBTKeys.ARMOR_NOT_EMPTY, !empty);
		GlOperation.saveListToNBT(data, NBTKeys.ARMOR_GL_OPERATIONS_PRE, globalGlOperationsPre);
		GlOperation.saveListToNBT(data, NBTKeys.ARMOR_GL_OPERATIONS_POST, globalGlOperationsPost);
		nbt.setTag(NBTKeys.ARMOR_DATA, data);
	}
	
	public void loadFromNBT(NBTTagCompound nbt)
	{
		NBTTagCompound data = nbt.getCompoundTag(NBTKeys.ARMOR_DATA);
		NBTTagList movingParts = data.getTagList(NBTKeys.ARMOR_PART_DATA, NBT.TAG_LIST);
		for (int i = 0; i < movingParts.tagCount(); i++)
		{
			NBTBase nbtBase = movingParts.get(i);
			if (nbtBase.getId() != NBT.TAG_LIST)
				continue;
			
			partItemLists[i].clear();
			NBTTagList itemList = (NBTTagList) nbtBase;
			for (int j = 0; j < itemList.tagCount(); j++)
			{
				partItemLists[i].add(new ArmorItem(itemList.getCompoundTagAt(j)));
			}
		}
		GlOperation.loadListFromNBT(data, NBTKeys.ARMOR_GL_OPERATIONS_PRE, globalGlOperationsPre);
		GlOperation.loadListFromNBT(data, NBTKeys.ARMOR_GL_OPERATIONS_POST, globalGlOperationsPost);
	}
	
	public static void setPartData(NBTTagCompound data, NBTTagList movingParts)
	{
		data.setTag(NBTKeys.ARMOR_PART_DATA, movingParts);
		boolean empty = true;
		for (int i = 0; i < movingParts.tagCount(); i++)
		{
			NBTBase nbtBase = movingParts.get(i);
			if (nbtBase.getId() != NBT.TAG_LIST)
				continue;
			
			NBTTagList itemList = (NBTTagList) nbtBase;
			for (int j = 0; j < itemList.tagCount(); j++)
			{
				if (!ItemStackHelper.loadStackFromNBT(itemList.getCompoundTagAt(j), NBTKeys.ARMOR_ITEM).isEmpty())
				{
					empty = false;
					break;
				}
			}
			if (!empty) break;
		}
		data.setBoolean(NBTKeys.ARMOR_NOT_EMPTY, !empty);
	}
	
}