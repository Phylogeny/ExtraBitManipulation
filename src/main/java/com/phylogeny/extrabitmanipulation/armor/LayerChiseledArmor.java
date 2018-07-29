package com.phylogeny.extrabitmanipulation.armor;

import java.util.HashMap;
import java.util.Map;

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
import net.minecraft.inventory.EntityEquipmentSlot;
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

public class LayerChiseledArmor implements LayerRenderer<EntityLivingBase>
{
	private final Map<NBTTagCompound, Integer[]> movingPartsDisplayListsMap = new HashMap<NBTTagCompound, Integer[]>();
	private final ModelRenderer head, body, villagerArms, rightLeg, leftLeg;
	private final ModelBase model;
	private boolean smallArms, isIllager, isVex;
	
	public LayerChiseledArmor(RenderLivingBase<? extends EntityLivingBase> livingEntityRenderer)
	{
		model = livingEntityRenderer.getMainModel();
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
		for (Integer[] displayLists : movingPartsDisplayListsMap.values())
			deleteDisplayLists(displayLists);
		
		movingPartsDisplayListsMap.clear();
	}
	
	public void removeFromDisplayListsMap(NBTTagCompound nbt)
	{
		deleteDisplayLists(movingPartsDisplayListsMap.remove(nbt));
	}
	
	private void deleteDisplayLists(Integer[] displayLists)
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
		GlStateManager.enableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		IChiseledArmorSlotsHandler cap = entity instanceof EntityPlayer ? ChiseledArmorSlotsHandler.getCapability((EntityPlayer) entity) : null;
		Integer[] displayListsHelmet = getStackDisplayLists(entity, scale, EntityEquipmentSlot.HEAD, ArmorType.HELMET);
		Integer[] displayListsSlotHelmet = getSlotStackDisplayLists(entity, scale, cap, ArmorType.HELMET);
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
			if (displayListsHelmet != null)
				GlStateManager.callList(displayListsHelmet[0]);
			
			GlStateManager.popMatrix();
			if (displayListsSlotHelmet != null)
				GlStateManager.callList(displayListsSlotHelmet[0]);
			
			GlStateManager.popMatrix();
		}
		Integer[] displayListsChestplate = getStackDisplayLists(entity, scale, EntityEquipmentSlot.CHEST, ArmorType.CHESTPLATE);
		Integer[] displayListsSlotChestplate = getSlotStackDisplayLists(entity, scale, cap, ArmorType.CHESTPLATE);
		if (displayListsChestplate != null || displayListsSlotChestplate != null)
		{
			GlStateManager.pushMatrix();
			adjustForSneaking(entity);
			adjustForChildModel();
			boolean isPassive = !isIllager || ((AbstractIllager) entity).getArmPose() == AbstractIllager.IllagerArmPose.CROSSED;
			GlStateManager.pushMatrix();
			if (displayListsChestplate != null)
			{
				renderArmorPiece(body, displayListsChestplate[0], scale, 8);
				renderSleeve(displayListsChestplate[1], EnumHandSide.RIGHT, scale, isPassive);
				renderSleeve(displayListsChestplate[2], EnumHandSide.LEFT, scale, isPassive);
			}
			GlStateManager.popMatrix();
			if (displayListsSlotChestplate != null)
			{
				renderArmorPiece(body, displayListsSlotChestplate[0], scale, 8);
				renderSleeve(displayListsSlotChestplate[1], EnumHandSide.RIGHT, scale, isPassive);
				renderSleeve(displayListsSlotChestplate[2], EnumHandSide.LEFT, scale, isPassive);
			}
			GlStateManager.popMatrix();
		}
		Integer[] displayListsLeggings = getStackDisplayLists(entity, scale, EntityEquipmentSlot.LEGS, ArmorType.LEGGINGS);
		Integer[] displayListsSlotLeggings = getSlotStackDisplayLists(entity, scale, cap, ArmorType.LEGGINGS);
		if (displayListsLeggings != null || displayListsSlotLeggings != null)
		{
			GlStateManager.pushMatrix();
			adjustForSneaking(entity);
			adjustForChildModel();
			GlStateManager.pushMatrix();
			if (displayListsLeggings != null)
			{
				renderArmorPiece(body, displayListsLeggings[0], scale, 4);
				renderLegPieces(displayListsLeggings[1], displayListsLeggings[2], scale, 8);
			}
			GlStateManager.popMatrix();
			if (displayListsSlotLeggings != null)
			{
				renderArmorPiece(body, displayListsSlotLeggings[0], scale, 4);
				renderLegPieces(displayListsSlotLeggings[1], displayListsSlotLeggings[2], scale, 8);
			}
			GlStateManager.popMatrix();
		}
		Integer[] displayListsBoots = getStackDisplayLists(entity, scale, EntityEquipmentSlot.FEET, ArmorType.BOOTS);
		Integer[] displayListsSlotBoots = getSlotStackDisplayLists(entity, scale, cap, ArmorType.BOOTS);
		if (displayListsBoots != null || displayListsSlotBoots != null)
		{
			GlStateManager.pushMatrix();
			adjustForSneaking(entity);
			adjustForChildModel();
			GlStateManager.translate(0.0F, scale * (Configs.armorZFightingBufferTranslationFeet), 0.0F);
			GlStateManager.pushMatrix();
			if (displayListsBoots != null)
				renderLegPieces(displayListsBoots[0], displayListsBoots[1], scale, 4);
			
			GlStateManager.popMatrix();
			if (displayListsSlotBoots != null)
				renderLegPieces(displayListsSlotBoots[0], displayListsSlotBoots[1], scale, 4);
			
			GlStateManager.popMatrix();
		}
		GlStateManager.disableBlend();
	}
	
	private Integer[] getStackDisplayLists(EntityLivingBase entity, float scale, EntityEquipmentSlot slot, ArmorType armorType)
	{
		return getDisplayLists(entity, scale, armorType, entity.getItemStackFromSlot(slot));
	}
	
	private Integer[] getSlotStackDisplayLists(EntityLivingBase entity, float scale, IChiseledArmorSlotsHandler cap, ArmorType armorType)
	{
		return cap == null ? null : getDisplayLists(entity, scale, armorType, cap.getStackInSlot(armorType.ordinal()));
	}
	
	private Integer[] getDisplayLists(EntityLivingBase entity, float scale, ArmorType armorType, ItemStack stack)
	{
		Integer[] displayLists = null;
		if (stack.hasTagCompound() && stack.getItem() instanceof ItemChiseledArmor)
		{
			NBTTagCompound nbt = stack.getTagCompound();
			displayLists = movingPartsDisplayListsMap.get(ItemStackHelper.getArmorData(nbt));
			if (displayLists == null)
				displayLists = addMovingPartsDisplayListsToMap(entity, scale, nbt, armorType);
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
	
	private Integer[] addMovingPartsDisplayListsToMap(EntityLivingBase entity, float scale, NBTTagCompound armorNbt, ArmorType armorType)
	{
		Integer[] movingPartsDisplayLists = new Integer[armorType.getMovingpartCount()];
		for (int i = 0; i < movingPartsDisplayLists.length; i++)
			movingPartsDisplayLists[i] = new DataChiseledArmorPiece(armorNbt, armorType).generateDisplayList(i, entity, scale);
		
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