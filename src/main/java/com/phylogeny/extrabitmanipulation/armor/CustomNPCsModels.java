package com.phylogeny.extrabitmanipulation.armor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.entity.EntityCustomNpc;

public class CustomNPCsModels
{
	
	public static ModelBiped ARMOR_MODEL_CNPC;
	public static ModelBiped ARMOR_MODEL_LEGGINGS_CNPC;
	
	public static void initModels()
	{
		ARMOR_MODEL_CNPC = new ModelChiseledArmorCNPC();
		ARMOR_MODEL_LEGGINGS_CNPC = new ModelChiseledArmorLeggingsCNPC();
	}
	
	public static boolean isCustomNPC(EntityLivingBase entity)
	{
		return entity instanceof EntityCustomNpc;
	}
	
}