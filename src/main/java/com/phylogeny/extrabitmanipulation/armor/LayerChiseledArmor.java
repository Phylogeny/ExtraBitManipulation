package com.phylogeny.extrabitmanipulation.armor;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelIllager;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelVex;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.model.ModelVindicator;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;
import com.phylogeny.extrabitmanipulation.reference.Utility;

public class LayerChiseledArmor implements LayerRenderer<EntityLivingBase>
{
	private final Map<NBTTagCompound, Integer[]> movingPartsDisplayListsMap = new HashMap<NBTTagCompound, Integer[]>();
	private final ModelRenderer head, body, villagerArms, rightLeg, leftLeg;
	private final ModelBase model;
	private boolean smallArms, isVindicator, isVex;
	
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
		else if (model instanceof ModelVindicator)
		{
			ModelIllager modelVillager = ((ModelIllager) model);
			head = modelVillager.head;
			body = modelVillager.body;
			rightLeg = modelVillager.leg0;
			leftLeg = modelVillager.leg1;
			villagerArms = modelVillager.arms;
			isVindicator = true;
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
				smallArms = ReflectionHelper.getPrivateValue(ModelPlayer.class, (ModelPlayer) model, "smallArms", "field_178735_y");
			
			isVex = model instanceof ModelVex;
		}
	}
	
	public void clearDisplayListsMap()
	{
		for (Integer[] displayLists : movingPartsDisplayListsMap.values())
			deleteDisplayLists(displayLists);
		
		movingPartsDisplayListsMap.clear();
	}
	
	public void removeFromDisplayListsMap(NBTTagCompound... nbtTags)
	{
		for (NBTTagCompound nbt : nbtTags)
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
		ItemStack headStack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		if (headStack.hasTagCompound() && headStack.getItem() instanceof ItemChiseledArmor)
		{
			GlStateManager.pushMatrix();
			NBTTagCompound nbt = headStack.getTagCompound();
			Integer[] displayLists = movingPartsDisplayListsMap.get(getArmorData(nbt));
			if (displayLists == null)
				displayLists = addMovingPartsDisplayListsToMap(entity, scale, new DataChiseledArmorPiece(nbt, ArmorType.HELMET), nbt, 1);
			
			adjustForSneaking(entity);
			if (entity.isChild() && !(entity instanceof EntityVillager))
			{
				GlStateManager.scale(0.75F, 0.75F, 0.75F);
				GlStateManager.translate(0.0F, 16.0F * Utility.PIXEL_F, 0.0F);
			}
			head.postRender(scale);
			GlStateManager.translate(0.0F, -scale * (8 + Configs.armorZFightingBufferScale), 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
			
			if (entity instanceof EntityVillager || entity instanceof EntityZombieVillager || entity instanceof EntityVindicator)
				GlStateManager.translate(0.0F, scale * 2, 0.0F);
			
			GlStateManager.callList(displayLists[0]);
			GlStateManager.popMatrix();
		}
		ItemStack chestStack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if (chestStack.hasTagCompound() && chestStack.getItem() instanceof ItemChiseledArmor)
		{
			GlStateManager.pushMatrix();
			NBTTagCompound nbt = chestStack.getTagCompound();
			Integer[] displayLists = movingPartsDisplayListsMap.get(getArmorData(nbt));
			if (displayLists == null)
				displayLists = addMovingPartsDisplayListsToMap(entity, scale, new DataChiseledArmorPiece(nbt, ArmorType.CHESTPLATE), nbt, 3);
			
			adjustForSneaking(entity);
			adjustForChildModel();
			renderArmorPiece(body, displayLists[0], scale, 8);
			boolean isPassive = !isVindicator || !((EntityVindicator) entity).isAggressive();
			renderSleeve(displayLists[1], EnumHandSide.RIGHT, scale, isPassive);
			renderSleeve(displayLists[2], EnumHandSide.LEFT, scale, isPassive);
			GlStateManager.popMatrix();
		}
		ItemStack legsStack = entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
		if (legsStack.hasTagCompound() && legsStack.getItem() instanceof ItemChiseledArmor)
		{
			GlStateManager.pushMatrix();
			NBTTagCompound nbt = legsStack.getTagCompound();
			Integer[] displayLists = movingPartsDisplayListsMap.get(getArmorData(nbt));
			if (displayLists == null)
				displayLists = addMovingPartsDisplayListsToMap(entity, scale, new DataChiseledArmorPiece(nbt, ArmorType.LEGGINGS), nbt, 3);
			
			adjustForSneaking(entity);
			adjustForChildModel();
			renderArmorPiece(body, displayLists[0], scale, 4);
			
			renderLegPieces(displayLists[1], displayLists[2], scale, 8);
			GlStateManager.popMatrix();
		}
		ItemStack feetStack = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
		if (feetStack.hasTagCompound() && feetStack.getItem() instanceof ItemChiseledArmor)
		{
			GlStateManager.pushMatrix();
			NBTTagCompound nbt = feetStack.getTagCompound();
			Integer[] displayLists = movingPartsDisplayListsMap.get(getArmorData(nbt));
			if (displayLists == null)
				displayLists = addMovingPartsDisplayListsToMap(entity, scale, new DataChiseledArmorPiece(nbt, ArmorType.BOOTS), nbt, 2);
			
			adjustForSneaking(entity);
			adjustForChildModel();
			renderLegPieces(displayLists[0], displayLists[1], scale, 4);
			GlStateManager.popMatrix();
		}
		GlStateManager.disableBlend();
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
	
	private Integer[] addMovingPartsDisplayListsToMap(EntityLivingBase entity, float scale,
			DataChiseledArmorPiece armorPiece, NBTTagCompound armorNbt, int movingpartCount)
	{
		Integer[] movingPartsDisplayLists = new Integer[movingpartCount];
		for (int i = 0; i < movingPartsDisplayLists.length; i++)
			movingPartsDisplayLists[i] = armorPiece.generateDisplayList(i, entity, scale);
		
		movingPartsDisplayListsMap.put(getArmorData(armorNbt), movingPartsDisplayLists);
		return movingPartsDisplayLists;
	}
	
	private NBTTagCompound getArmorData(NBTTagCompound armorNbt)
	{
		return armorNbt.getCompoundTag(NBTKeys.ARMOR_DATA);
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
				if (isVindicator)
					((ModelVindicator) model).getArm(handSide).postRender(scale);
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