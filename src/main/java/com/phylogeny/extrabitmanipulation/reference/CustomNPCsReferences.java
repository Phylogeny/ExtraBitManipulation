package com.phylogeny.extrabitmanipulation.reference;

import com.phylogeny.extrabitmanipulation.armor.ModelChiseledArmorLeggingsCNPC;
import com.phylogeny.extrabitmanipulation.armor.ModelChiseledArmorCNPC;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.entity.EntityCustomNpc;

public class CustomNPCsReferences
{
	public static final String MOD_ID = "customnpcs";
	public static boolean isLoaded;
	public static ModelBiped ARMOR_MODEL_CNPC;
	public static ModelBiped ARMOR_MODEL_LEGGINGS_CNPC;
	
	public static void initModels()
	{
		ARMOR_MODEL_CNPC = new ModelChiseledArmorCNPC();
		ARMOR_MODEL_LEGGINGS_CNPC = new ModelChiseledArmorLeggingsCNPC();
	}
	
	public static boolean isCustomNPC(EntityLivingBase entity)
	{
		return isLoaded && entity instanceof EntityCustomNpc;
	}
	
}