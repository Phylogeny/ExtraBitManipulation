package com.phylogeny.extrabitmanipulation.armor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
//import net.minecraft.client.model.ModelEvoker;
import net.minecraft.client.model.ModelIllager;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelVex;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHandSide;

import com.phylogeny.extrabitmanipulation.armor.capability.ChiseledArmorSlotsHandler;
import com.phylogeny.extrabitmanipulation.armor.capability.IChiseledArmorSlotsHandler;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.init.ReflectionExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

public class LayerChiseledArmor implements LayerRenderer<EntityLivingBase>
{
	private final Map<NBTTagCompound, List<Integer>> movingPartsDisplayListsMap = new HashMap<NBTTagCompound, List<Integer>>();
	private ModelRenderer head, body, villagerArms, rightLeg, leftLeg;
	private ModelBase model;
	private boolean smallArms, isIllager, isVex;
	private RenderLivingBase<? extends EntityLivingBase> livingEntityRenderer;
	
	public LayerChiseledArmor(RenderLivingBase<? extends EntityLivingBase> livingEntityRenderer)
	{
		this.livingEntityRenderer = livingEntityRenderer;
		updateModelAndRenderers();
	}
	
	public void updateModelAndRenderers()
	{
		ModelBase modelNew = livingEntityRenderer.getMainModel();
		if (modelNew == model)
			return;
		
		model = modelNew;
		if (model instanceof ModelVillager)
		{
			ModelVillager modelVillager = ((ModelVillager) model);
			head = modelVillager.villagerHead;
			body = modelVillager.villagerBody;
			rightLeg = modelVillager.rightVillagerLeg;
			leftLeg = modelVillager.leftVillagerLeg;
			villagerArms = modelVillager.villagerArms;
		}
		else if (model instanceof ModelIllager)
		{
			ModelIllager modelVillager = ((ModelIllager) model);
			head = modelVillager.head;
			body = modelVillager.body;
			rightLeg = modelVillager.leg0;
			leftLeg = modelVillager.leg1;
			villagerArms = modelVillager.arms;
			isIllager = true;
		}
		else
		{
			ModelBiped modelBiped = ((ModelBiped) model);
			head = modelBiped.bipedHead;
			body = modelBiped.bipedBody;
			rightLeg = modelBiped.bipedRightLeg;
			leftLeg = modelBiped.bipedLeftLeg;
			villagerArms = null;
			if (model instanceof ModelPlayer)
				smallArms = ReflectionExtraBitManipulation.areArmsSmall((ModelPlayer) model);
			
			isVex = model instanceof ModelVex;
		}
	}
	
	public void clearDisplayListsMap()
	{
		for (List<Integer> displayLists : movingPartsDisplayListsMap.values())
			deleteDisplayLists(displayLists);
		
		movingPartsDisplayListsMap.clear();
	}
	
	public void removeFromDisplayListsMap(NBTTagCompound nbt)
	{
		deleteDisplayLists(movingPartsDisplayListsMap.remove(nbt));
	}
	
	private void deleteDisplayLists(List<Integer> displayLists)
	{
		if (displayLists != null)
		{
			for (Integer displayList : displayLists)
				GLAllocation.deleteDisplayLists(displayList);
		}
	}
	
	@Override
	public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		updateModelAndRenderers();
		GlStateManager.enableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		IChiseledArmorSlotsHandler cap = entity instanceof EntityPlayer ? ChiseledArmorSlotsHandler.getCapability((EntityPlayer) entity) : null;
		List<Integer> displayListsHelmet = getStackDisplayLists(entity, scale, ArmorType.HELMET);
		List<Integer> displayListsSlotHelmet = getSlotStackDisplayLists(entity, scale, cap, ArmorType.HELMET);
		if (displayListsHelmet != null || displayListsSlotHelmet != null)
		{
			GlStateManager.pushMatrix();
			adjustForSneaking(entity);
			if (entity.isChild() && !(entity instanceof EntityVillager))
			{
				GlStateManager.scale(0.75F, 0.75F, 0.75F);
				GlStateManager.translate(0.0F, 1.0F, 0.0F);
			}
			head.postRender(scale);
			GlStateManager.translate(0.0F, -scale * (8 + Configs.armorZFightingBufferScale), 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
			
			if (entity instanceof EntityVillager || entity instanceof EntityZombieVillager || entity instanceof AbstractIllager)
				GlStateManager.translate(0.0F, scale * 2, 0.0F);
			
			GlStateManager.pushMatrix();
			if (displayListsHelmet != null && (cap == null || !cap.hasArmorType(0)))
				GlStateManager.callList(displayListsHelmet.get(0));
			
			GlStateManager.popMatrix();
			if (displayListsSlotHelmet != null)
			{
				for (Integer i : displayListsSlotHelmet)
					GlStateManager.callList(i);
			}
			GlStateManager.popMatrix();
		}
		List<Integer> displayListsChestplate = getStackDisplayLists(entity, scale, ArmorType.CHESTPLATE);
		List<Integer> displayListsSlotChestplate = getSlotStackDisplayLists(entity, scale, cap, ArmorType.CHESTPLATE);
		if (displayListsChestplate != null || displayListsSlotChestplate != null)
		{
			GlStateManager.pushMatrix();
			adjustForSneaking(entity);
			adjustForChildModel();
			boolean isPassive = !isIllager || ((AbstractIllager) entity).getArmPose() == AbstractIllager.IllagerArmPose.CROSSED;
			GlStateManager.pushMatrix();
			if (displayListsChestplate != null && (cap == null || !cap.hasArmorType(1)))
			{
				renderArmorPiece(body, displayListsChestplate.get(0), scale, 8);
				renderSleeve(displayListsChestplate.get(1), EnumHandSide.RIGHT, scale, isPassive);
				renderSleeve(displayListsChestplate.get(2), EnumHandSide.LEFT, scale, isPassive);
			}
			GlStateManager.popMatrix();
			if (displayListsSlotChestplate != null)
			{
				for (int i = 0; i < displayListsSlotChestplate.size(); i += 3)
				{
					renderArmorPiece(body, displayListsSlotChestplate.get(i), scale, 8);
					renderSleeve(displayListsSlotChestplate.get(i + 1), EnumHandSide.RIGHT, scale, isPassive);
					renderSleeve(displayListsSlotChestplate.get(i + 2), EnumHandSide.LEFT, scale, isPassive);
				}
			}
			GlStateManager.popMatrix();
		}
		List<Integer> displayListsLeggings = getStackDisplayLists(entity, scale, ArmorType.LEGGINGS);
		List<Integer> displayListsSlotLeggings = getSlotStackDisplayLists(entity, scale, cap, ArmorType.LEGGINGS);
		if (displayListsLeggings != null || displayListsSlotLeggings != null)
		{
			GlStateManager.pushMatrix();
			adjustForSneaking(entity);
			adjustForChildModel();
			GlStateManager.pushMatrix();
			if (displayListsLeggings != null && (cap == null || !cap.hasArmorType(2)))
			{
				renderArmorPiece(body, displayListsLeggings.get(0), scale, 4);
				renderLegPieces(displayListsLeggings.get(1), displayListsLeggings.get(2), scale, 8);
			}
			GlStateManager.popMatrix();
			if (displayListsSlotLeggings != null)
			{
				for (int i = 0; i < displayListsSlotLeggings.size(); i += 3)
				{
					renderArmorPiece(body, displayListsSlotLeggings.get(i), scale, 4);
					renderLegPieces(displayListsSlotLeggings.get(i + 1), displayListsSlotLeggings.get(i + 2), scale, 8);
				}
			}
			GlStateManager.popMatrix();
		}
		List<Integer> displayListsBoots = getStackDisplayLists(entity, scale, ArmorType.BOOTS);
		List<Integer> displayListsSlotBoots = getSlotStackDisplayLists(entity, scale, cap, ArmorType.BOOTS);
		if (displayListsBoots != null || displayListsSlotBoots != null)
		{
			GlStateManager.pushMatrix();
			adjustForSneaking(entity);
			adjustForChildModel();
			GlStateManager.translate(0.0F, scale * (Configs.armorZFightingBufferTranslationFeet), 0.0F);
			GlStateManager.pushMatrix();
			if (displayListsBoots != null && (cap == null || !cap.hasArmorType(3)))
				renderLegPieces(displayListsBoots.get(0), displayListsBoots.get(1), scale, 4);
			
			GlStateManager.popMatrix();
			if (displayListsSlotBoots != null)
			{
				for (int i = 0; i < displayListsSlotBoots.size(); i += 2)
					renderLegPieces(displayListsSlotBoots.get(i), displayListsSlotBoots.get(i + 1), scale, 4);
			}
			
			GlStateManager.popMatrix();
		}
		GlStateManager.disableBlend();
	}
	
	private List<Integer> getStackDisplayLists(EntityLivingBase entity, float scale, ArmorType armorType)
	{
		return getDisplayLists(entity, scale, armorType, entity.getItemStackFromSlot(armorType.getEquipmentSlot()), null);
	}
	
	private List<Integer> getSlotStackDisplayLists(EntityLivingBase entity, float scale, IChiseledArmorSlotsHandler cap, ArmorType armorType)
	{
		if (cap == null || !cap.hasArmor())
			return null;
		
		return getDisplayLists(entity, scale, armorType, ItemStack.EMPTY, cap);
	}
	
	private List<Integer> getDisplayLists(EntityLivingBase entity, float scale, ArmorType armorType, ItemStack stack, @Nullable IChiseledArmorSlotsHandler cap)
	{
		List<Integer> displayLists = null;
		int countSet = cap == null ? 1 : ChiseledArmorSlotsHandler.COUNT_SETS;
		for (int i = 0; i < countSet; i++)
		{
			if (cap != null)
			{
				if (!cap.hasArmorSet(i))
					continue;
				
				stack = cap.getStackInSlot(i * ChiseledArmorSlotsHandler.COUNT_TYPES + armorType.ordinal());
			}
			if (stack.hasTagCompound() && stack.getItem() instanceof ItemChiseledArmor)
			{
				NBTTagCompound nbt = stack.getTagCompound();
				NBTTagCompound armoreData = ItemStackHelper.getArmorData(nbt);
				if (!armoreData.getBoolean(NBTKeys.ARMOR_NOT_EMPTY))
					continue;
				
				List<Integer> displayListsItem = movingPartsDisplayListsMap.get(armoreData);
				if (displayListsItem == null)
					displayListsItem = addMovingPartsDisplayListsToMap(entity, scale, nbt, armorType);
				
				if (displayLists == null)
					displayLists = new ArrayList<>();
				
				displayLists.addAll(displayListsItem);
			}
		}
		return displayLists;
	}
	
	private void adjustForSneaking(EntityLivingBase entity)
	{
		if (entity.isSneaking())
			GlStateManager.translate(0.0F, 0.2F, 0.0F);
	}
	
	private void adjustForChildModel()
	{
		if (model.isChild)
		{
			GlStateManager.translate(0.0F, 0.75F, 0.0F);
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
		}
	}
	
	private List<Integer> addMovingPartsDisplayListsToMap(EntityLivingBase entity, float scale, NBTTagCompound armorNbt, ArmorType armorType)
	{
		List<Integer> movingPartsDisplayLists = new ArrayList<>();
		for (int i = 0; i < armorType.getMovingpartCount(); i++)
			movingPartsDisplayLists.add(new DataChiseledArmorPiece(armorNbt, armorType).generateDisplayList(i, entity, scale));
		
		movingPartsDisplayListsMap.put(ItemStackHelper.getArmorData(armorNbt), movingPartsDisplayLists);
		return movingPartsDisplayLists;
	}
	
	private void renderLegPieces(int displayListRight, int displayListLeft, float scale, float offsetY)
	{
		renderArmorPiece(rightLeg, displayListRight, scale, isVex ? -scale * 2 : 0.0F, offsetY);
		if (!isVex)
			renderArmorPiece(leftLeg, displayListLeft, scale, offsetY);
	}
	
	private void renderArmorPiece(ModelRenderer modelArmorPiece, int displayList, float scale, float offsetX, float offsetY)
	{
		GlStateManager.pushMatrix();
		modelArmorPiece.postRender(scale);
		renderArmorPiece(displayList, scale, offsetX, offsetY);
	}
	
	private void renderArmorPiece(ModelRenderer modelArmorPiece, int displayList, float scale, float offsetY)
	{
		renderArmorPiece(modelArmorPiece, displayList, scale, 0.0F, offsetY);
	}
	
	private void renderArmorPiece(int displayList, float scale, float offsetX, float offsetY)
	{
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(offsetX, -scale * offsetY, 0.0F);
		GlStateManager.callList(displayList);
		GlStateManager.popMatrix();
	}
	
	private void renderSleeve(int displayList, EnumHandSide handSide, float scale, boolean isPassive)
	{
		GlStateManager.pushMatrix();
		int armOffset;
		if (villagerArms != null && isPassive)
		{
			villagerArms.postRender(scale);
			armOffset = 6;
		}
		else
		{
			if (smallArms)
			{
				ModelBiped modelBiped = (ModelBiped) model;
				float f = 0.5F;
				ModelRenderer modelArm;
				if (handSide == EnumHandSide.RIGHT)
				{
					modelArm = modelBiped.bipedRightArm;
					f *= -1;
				}
				else
				{
					modelArm = modelBiped.bipedLeftArm;
				}
				modelArm.rotationPointX += f;
				modelBiped.postRenderArm(scale, handSide);
				modelArm.rotationPointX -= f;
			}
			else
			{
				if (isIllager)
					((ModelIllager) model).getArm(handSide).postRender(scale);
				else
					((ModelBiped) model).postRenderArm(scale, handSide);
			}
			armOffset = 1;
		}
		renderArmorPiece(displayList, scale, (handSide == EnumHandSide.LEFT ? -armOffset : armOffset) * scale, 6);
	}
	
	@Override
	public boolean shouldCombineTextures()
	{
		return false;
	}
	
}