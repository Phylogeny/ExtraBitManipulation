package com.phylogeny.extrabitmanipulation.armor.model.cnpc;

import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ModelMovingPart;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.client.model.ModelScaleRenderer;
import noppes.npcs.constants.EnumParts;
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
	
	public static boolean isModelRendererCNPC(ModelRenderer renderer)
	{
		return renderer instanceof ModelScaleRenderer;
	}
	
	public static ModelRenderer getEmptyModelRenderer(ModelBiped model, ModelRenderer renderer, ModelMovingPart part)
	{
		ModelScaleRenderer modelRenderer = new ModelScaleRenderer(model, EnumParts.values()[part.getPartIndexNoppes()]);
		modelRenderer.config = ((ModelScaleRenderer) renderer).config;
		return modelRenderer;
	}
	
	public static boolean isCustomNPC(EntityLivingBase entity)
	{
		return entity instanceof EntityCustomNpc;
	}
	
}