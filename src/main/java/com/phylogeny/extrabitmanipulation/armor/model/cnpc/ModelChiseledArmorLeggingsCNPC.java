package com.phylogeny.extrabitmanipulation.armor.model.cnpc;

import com.phylogeny.extrabitmanipulation.reference.Configs;

import noppes.npcs.constants.EnumParts;

public class ModelChiseledArmorLeggingsCNPC extends ModelChiseledArmorBaseCNPC
{
	
	public ModelChiseledArmorLeggingsCNPC()
	{
		super(1);
		scale += Configs.armorZFightingBufferScaleRightLegOrFoot;
		
		//Pelvis
		bipedBody = createScaledModel(0, 45, EnumParts.BODY);
		bipedBody.addBox(-4.0F, 7.0F, -2.0F, 8, 5, 4, scale);
		
		//Right Leg
		bipedRightLeg = createScaledModel(25, 40, EnumParts.LEG_RIGHT);
		bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 4, scale + Configs.armorZFightingBufferScaleRightLegOrFoot);
		
		//Left Leg
		bipedLeftLeg = createScaledModel(25, 40, EnumParts.LEG_LEFT);
		bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 4, scale);
	}
	
}