package com.phylogeny.extrabitmanipulation.armor;

import java.util.ArrayList;
import java.util.List;

import mod.chiselsandbits.api.ItemType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

public class ArmorItem
{
	private List<GlOperation> glOperations = new ArrayList<GlOperation>();
	private ItemStack stack;
	
	public ArmorItem() {}
	
	public ArmorItem(ItemStack stack)
	{
		this.stack = stack;
	}
	
	public ArmorItem(NBTTagCompound nbt)
	{
		stack = ItemStackHelper.loadStackFromNBT(nbt, NBTKeys.ARMOR_ITEM);
		GlOperation.loadListFromNBT(nbt, NBTKeys.ARMOR_GL_OPERATIONS, glOperations);
	}
	
	public void addGlOperation(GlOperation glOperation)
	{
		glOperations.add(glOperation);
	}
	
	public void addGlOperation(int index, GlOperation glOperation)
	{
		glOperations.add(index, glOperation);
	}
	
	public void removeGlOperation(int index)
	{
		glOperations.remove(index);
	}
	
	public List<GlOperation> getGlOperations()
	{
		List<GlOperation> glOperations = new ArrayList<GlOperation>();
		glOperations.addAll(this.glOperations);
		return glOperations;
	}
	
	public void render(EntityLivingBase entity, float scale, boolean isRightLegOrFoot)
	{
		float scale2 = 32 * scale + Configs.armorZFightingBufferScale;
		if (isRightLegOrFoot)
			scale2 += Configs.armorZFightingBufferScaleRightLegOrFoot;
		
		GlStateManager.scale(scale2, scale2, scale2);
		if (ChiselsAndBitsAPIAccess.apiInstance.getItemType(stack) != ItemType.CHISLED_BLOCK)
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
		
		Minecraft.getMinecraft().getItemRenderer().renderItem(entity, stack, TransformType.NONE);
	}
	
	public void executeGlOperations()
	{
		GlOperation.executeList(glOperations);
	}
	
	public boolean isEmpty()
	{
		return stack == null;
	}
	
	public void saveToNBT(NBTTagCompound nbt)
	{
		ItemStackHelper.saveStackToNBT(nbt, stack, NBTKeys.ARMOR_ITEM);
		GlOperation.saveListToNBT(nbt, NBTKeys.ARMOR_GL_OPERATIONS, glOperations);
	}
	
	public ItemStack getStack()
	{
		return stack;
	}
	
	public void setStack(ItemStack stack)
	{
		this.stack = stack;
	}
	
}