package com.phylogeny.extrabitmanipulation.armor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

import com.phylogeny.extrabitmanipulation.reference.Configs;

public class ModelChiseledArmorBase extends ModelBiped
{
	protected float scale;
	
	public ModelChiseledArmorBase()
	{
		textureWidth = 86;
		textureHeight = 64;
		scale = Configs.armorZFightingBufferScale;
	}
	
	protected void setRotationAngles(ModelRenderer modelRenderer, float angleX, float angleY, float angleZ)
	{
		modelRenderer.rotateAngleX = angleX;
		modelRenderer.rotateAngleY = angleY;
		modelRenderer.rotateAngleZ = angleZ;
	}
	
}