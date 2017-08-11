package com.phylogeny.extrabitmanipulation.armor;

import net.minecraft.client.model.ModelRenderer;

import com.phylogeny.extrabitmanipulation.reference.Configs;

public class ModelChiseledArmorLeggings extends ModelChiseledArmorBase
{
	
	public ModelChiseledArmorLeggings()
	{
		super();
		
		//Pelvis
		bipedBody = new ModelRenderer(this, 0, 45);
		bipedBody.addBox(-4.0F, 7.0F, -2.0F, 8, 5, 4, scale);
		
		//Right Leg
		bipedRightLeg = new ModelRenderer(this, 25, 40);
		bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 4, scale + Configs.armorZFightingBufferScaleRightLegOrFoot);
		
		//Left Leg
		bipedLeftLeg = new ModelRenderer(this, 25, 40);
		bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 4, scale);
	}
	
}