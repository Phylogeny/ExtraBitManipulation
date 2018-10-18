package com.phylogeny.extrabitmanipulation.armor.model.mpm;

import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ModelMovingPart;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import noppes.mpm.client.model.ModelScaleRenderer;
import noppes.mpm.constants.EnumParts;

public class MorePlayerModelsModels
{
	public static ModelBiped ARMOR_MODEL_MPM;
	public static ModelBiped ARMOR_MODEL_LEGGINGS_MPM;
	
	public static void initModels()
	{
		ARMOR_MODEL_MPM = new ModelChiseledArmorMPM();
		ARMOR_MODEL_LEGGINGS_MPM = new ModelChiseledArmorLeggingsMPM();
	}
	
	public static boolean isModelRendererMPM(ModelRenderer renderer)
	{
		return renderer instanceof ModelScaleRenderer;
	}
	
	public static ModelRenderer getEmptyModelRenderer(ModelBiped model, ModelRenderer renderer, ModelMovingPart part)
	{
		ModelScaleRenderer modelRenderer = new ModelScaleRenderer(model, EnumParts.values()[part.getPartIndexNoppes()]);
		modelRenderer.config = ((ModelScaleRenderer) renderer).config;
		return modelRenderer;
	}
	
}