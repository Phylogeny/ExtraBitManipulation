package com.phylogeny.extrabitmanipulation.armor;

import net.minecraft.client.model.ModelBiped;

public class MorePlayerModelsModels
{
	public static ModelBiped ARMOR_MODEL_MPM;
	public static ModelBiped ARMOR_MODEL_LEGGINGS_MPM;
	
	public static void initModels()
	{
		ARMOR_MODEL_MPM = new ModelChiseledArmorMPM();
		ARMOR_MODEL_LEGGINGS_MPM = new ModelChiseledArmorLeggingsMPM();
	}
	
}