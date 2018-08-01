package com.phylogeny.extrabitmanipulation.armor.model.vanilla;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.math.MathHelper;

import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.Utility;

public class ModelChiseledArmor extends ModelChiseledArmorBase
{
	
	public ModelChiseledArmor()
	{
		super();
		float angle90 = (float) Math.toRadians(90);
		float angle180 = (float) Math.toRadians(180);
		float angle270 = (float) Math.toRadians(270);
		
		//Head
		bipedHeadwear = new ModelRenderer(this, 0, 0);
		bipedHead = new ModelRenderer(this, 0, 0);
		bipedHead.addBox(-5.0F, -9.0F, -5.0F, 10, 1, 10, scale);
		ModelRenderer headFront1 = new ModelRenderer(this, 44, 0);
		headFront1.addBox(-5.0F, -8.0F, -4.0F, 1, 3, 9, scale);
		setRotationAngles(headFront1, 0.0F, angle270, 0.0F);
		ModelRenderer headFront2 = new ModelRenderer(this, 0, 0);
		headFront2.setRotationPoint(-1.0F, -5.0F, -5.0F);
		headFront2.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, scale);
		ModelRenderer headBack1 = new ModelRenderer(this, 44, 0);
		headBack1.addBox(-5.0F, -8.0F, -4.0F, 1, 3, 9, scale);
		setRotationAngles(headBack1, 0.0F, angle90, 0.0F);
		ModelRenderer headBack2 = new ModelRenderer(this, 31, 0);
		headBack2.setRotationPoint(-5.0F, -5.0F, 4.0F);
		headBack2.addBox(0.0F, 0.0F, 0.0F, 10, 3, 1, scale);
		ModelRenderer headBack3 = new ModelRenderer(this, 0, 4);
		headBack3.setRotationPoint(-2.0F, -2.0F, 4.0F);
		headBack3.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, scale);
		ModelRenderer headRight1 = new ModelRenderer(this, 44, 0);
		headRight1.addBox(-5.0F, -8.0F, -4.0F, 1, 3, 9, scale);
		ModelRenderer headRight2 = new ModelRenderer(this, 0, 12);
		headRight2.setRotationPoint(4.0F, -5.0F, -5.0F);
		headRight2.addBox(0.0F, 0.0F, 0.0F, 1, 1, 5, scale);
		ModelRenderer headRight3 = new ModelRenderer(this, 26, 12);
		headRight3.setRotationPoint(4.0F, -5.0F, 0.0F);
		headRight3.addBox(0.0F, 0.0F, 0.0F, 1, 2, 4, scale);
		ModelRenderer headLeft1 = new ModelRenderer(this, 44, 0);
		headLeft1.addBox(-5.0F, -8.0F, -4.0F, 1, 3, 9, scale);
		setRotationAngles(headLeft1, 0.0F, angle180, 0.0F);
		ModelRenderer headLeft2 = new ModelRenderer(this, 13, 12);
		headLeft2.setRotationPoint(-5.0F, -5.0F, -5.0F);
		headLeft2.addBox(0.0F, 0.0F, 0.0F, 1, 1, 5, scale);
		ModelRenderer headLeft3 = new ModelRenderer(this, 37, 12);
		headLeft3.setRotationPoint(-5.0F, -5.0F, 0.0F);
		headLeft3.addBox(0.0F, 0.0F, 0.0F, 1, 2, 4, scale);
		bipedHead.addChild(headFront1);
		bipedHead.addChild(headFront2);
		bipedHead.addChild(headBack1);
		bipedHead.addChild(headBack2);
		bipedHead.addChild(headBack3);
		bipedHead.addChild(headRight1);
		bipedHead.addChild(headRight2);
		bipedHead.addChild(headRight3);
		bipedHead.addChild(headLeft1);
		bipedHead.addChild(headLeft2);
		bipedHead.addChild(headLeft3);
		
		//Body
		bipedBody = new ModelRenderer(this, 64, 52);
		bipedBody.addBox(-5.0F, -1.0F, 2.0F, 10, 11, 1, scale);
		ModelRenderer bodyRight = new ModelRenderer(this, 64, 30);
		bodyRight.addBox(-5.0F, 4.0F, -2.0F, 1, 6, 4, scale);
		ModelRenderer bodyLeft = new ModelRenderer(this, 75, 30);
		bodyLeft.addBox(4.0F, 4.0F, -2.0F, 1, 6, 4, scale);
		ModelRenderer bodyBack = new ModelRenderer(this, 68, 12);
		bodyBack.addBox(-4.0F, 10.0F, 2.0F, 8, 1, 1, scale);
		ModelRenderer bodyTop1 = new ModelRenderer(this, 68, 23);
		bodyTop1.addBox(-5.0F, -1.0F, -3.0F, 3, 1, 5, scale);
		ModelRenderer bodyTop2 = new ModelRenderer(this, 68, 16);
		bodyTop2.addBox(2.0F, -1.0F, -3.0F, 3, 1, 5, scale);
		ModelRenderer bodyFront1 = new ModelRenderer(this, 64, 41);
		bodyFront1.addBox(-5.0F, 1.0F, -3.0F, 10, 9, 1, scale);
		ModelRenderer bodyFront2 = new ModelRenderer(this, 68, 9);
		bodyFront2.addBox(-4.0F, 10.0F, -3.0F, 8, 1, 1, scale);
		ModelRenderer 	bodyFront3 = new ModelRenderer(this, 68, 6);
		bodyFront3.addBox(-3.0F, 11.0F, -3.0F, 6, 1, 1, scale);
		ModelRenderer bodyFront4 = new ModelRenderer(this, 68, 3);
		bodyFront4.addBox(-5.0F, 0.0F, -3.0F, 4, 1, 1, scale);
		ModelRenderer bodyFront5 = new ModelRenderer(this, 68, 0);
		bodyFront5.addBox(1.0F, 0.0F, -3.0F, 4, 1, 1, scale);
		bipedBody.addChild(bodyRight);
		bipedBody.addChild(bodyLeft);
		bipedBody.addChild(bodyBack);
		bipedBody.addChild(bodyTop1);
		bipedBody.addChild(bodyTop2);
		bipedBody.addChild(bodyFront1);
		bipedBody.addChild(bodyFront2);
		bipedBody.addChild(bodyFront3);
		bipedBody.addChild(bodyFront4);
		bipedBody.addChild(bodyFront5);
		
		//Right Arm
		bipedRightArm = new ModelRenderer(this, 0, 0);
		bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		ModelRenderer armRightTop = new ModelRenderer(this, 0, 22);
		armRightTop.setRotationPoint(-5.0F, 8.0F, 0.0F);
		armRightTop.addBox(-7.0F, -11.0F, -3.0F, 6, 1, 6, scale);
		setRotationAngles(armRightTop, 0.0F, angle180, 0.0F);
		ModelRenderer armRightfront1 = new ModelRenderer(this, 25, 23);
		armRightfront1.setRotationPoint(5.0F, 0.0F, 0.0F);
		armRightfront1.addBox(6.0F, -2.0F, -3.0F, 3, 5, 1, scale);
		setRotationAngles(armRightfront1, 0.0F, angle180, 0.0F);
		ModelRenderer armRightFront2 = new ModelRenderer(this, 43, 25);
		armRightFront2.setRotationPoint(5.0F, 0.0F, 0.0F);
		armRightFront2.addBox(3.0F, -2.0F, -3.0F, 3, 3, 1, scale);
		setRotationAngles(armRightFront2, 0.0F, angle180, 0.0F);
		ModelRenderer armRightBack1 = new ModelRenderer(this, 34, 31);
		armRightBack1.setRotationPoint(5.0F, 0.0F, 0.0F);
		armRightBack1.addBox(6.0F, -2.0F, 2.0F, 3, 5, 1, scale);
		setRotationAngles(armRightBack1, 0.0F, angle180, 0.0F);
		ModelRenderer armRightBack2 = new ModelRenderer(this, 52, 25);
		armRightBack2.setRotationPoint(5.0F, 0.0F, 0.0F);
		armRightBack2.addBox(3.0F, -2.0F, 2.0F, 3, 3, 1, scale);
		setRotationAngles(armRightBack2, 0.0F, angle180, 0.0F);
		ModelRenderer armRightSide = new ModelRenderer(this, 49, 40);
		armRightSide.setRotationPoint(7.0F, 0.0F, 0.0F);
		armRightSide.addBox(10.0F, -3.0F, -3.0F, 1, 6, 6, scale);
		setRotationAngles(armRightSide, 0.0F, angle180, 0.0F);
		bipedRightArm.addChild(armRightTop);
		bipedRightArm.addChild(armRightfront1);
		bipedRightArm.addChild(armRightFront2);
		bipedRightArm.addChild(armRightBack1);
		bipedRightArm.addChild(armRightBack2);
		bipedRightArm.addChild(armRightSide);
		
		//Left Arm
		bipedLeftArm = new ModelRenderer(this, 0, 30);
		bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		bipedLeftArm.addBox(-2.0F, -3.0F, -3.0F, 6, 1, 6, scale);
		ModelRenderer armLeftFront1 = new ModelRenderer(this, 25, 31);
		armLeftFront1.setRotationPoint(5.0F, 2.0F, 0.0F);
		armLeftFront1.addBox(-4.0F, -4.0F, -3.0F, 3, 5, 1, scale);
		ModelRenderer armLeftFront2 = new ModelRenderer(this, 43, 33);
		armLeftFront2.setRotationPoint(5.0F, 2.0F, 0.0F);
		armLeftFront2.addBox(-7.0F, -4.0F, -3.0F, 3, 3, 1, scale);
		ModelRenderer armLeftBack1 = new ModelRenderer(this, 34, 23);
		armLeftBack1.setRotationPoint(5.0F, 2.0F, 0.0F);
		armLeftBack1.addBox(-4.0F, -4.0F, 2.0F, 3, 5, 1, scale);
		ModelRenderer armLeftBack2 = new ModelRenderer(this, 52, 33);
		armLeftBack2.setRotationPoint(5.0F, 2.0F, 0.0F);
		armLeftBack2.addBox(-7.0F, -4.0F, 2.0F, 3, 3, 1, scale);
		ModelRenderer armLeftSide = new ModelRenderer(this, 49, 40);
		armLeftSide.setRotationPoint(7.0F, 2.0F, 0.0F);
		armLeftSide.addBox(-4.0F, -5.0F, -3.0F, 1, 6, 6, scale);
		bipedLeftArm.addChild(armLeftFront1);
		bipedLeftArm.addChild(armLeftFront2);
		bipedLeftArm.addChild(armLeftBack1);
		bipedLeftArm.addChild(armLeftBack2);
		bipedLeftArm.addChild(armLeftSide);
		
		//Right Foot
		float scale2 = scale + Configs.armorZFightingBufferScaleRightLegOrFoot;
		bipedRightLeg = new ModelRenderer(this, 16, 57);
		bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		bipedRightLeg.addBox(-3.0F, 11.0F, -3.0F, 6, 1, 6, scale2);
		ModelRenderer footRightFront = new ModelRenderer(this, 0, 57);
		footRightFront.setRotationPoint(-1.9F, 12.0F, 0.0F);
		footRightFront.addBox(0.0F, -6.0F, -3.0F, 5, 5, 1, scale2);
		ModelRenderer footRightBack = new ModelRenderer(this, 0, 57);
		footRightBack.setRotationPoint(-1.9F, 12.0F, 0.0F);
		footRightBack.addBox(-4.0F, -6.0F, -3.0F, 5, 5, 1, scale2);
		setRotationAngles(footRightBack, 0.0F, angle180, 0.0F);
		ModelRenderer footRightSide1 = new ModelRenderer(this, 0, 57);
		footRightSide1.setRotationPoint(-1.9F, 12.0F, 0.0F);
		footRightSide1.addBox(-2.0F, -6.0F, -1.0F, 5, 5, 1, scale2);
		setRotationAngles(footRightSide1, 0.0F, angle90, 0.0F);
		ModelRenderer footRightSide2 = new ModelRenderer(this, 0, 57);
		footRightSide2.setRotationPoint(-1.9F, 12.0F, 0.0F);
		footRightSide2.addBox(-2.0F, -6.0F, -5.0F, 5, 5, 1, scale2);
		setRotationAngles(footRightSide2, 0.0F, angle270, 0.0F);
		bipedRightLeg.addChild(footRightFront);
		bipedRightLeg.addChild(footRightBack);
		bipedRightLeg.addChild(footRightSide1);
		bipedRightLeg.addChild(footRightSide2);
		
		//Left Foot
		bipedLeftLeg = new ModelRenderer(this, 16, 57);
		bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		bipedLeftLeg.addBox(-3.0F, 11.0F, -3.0F, 6, 1, 6, scale);
		ModelRenderer footLeftFront = new ModelRenderer(this, 0, 57);
		footLeftFront.setRotationPoint(-1.9F, 12.0F, 0.0F);
		footLeftFront.addBox(0.0F, -6.0F, -3.0F, 5, 5, 1, scale);
		ModelRenderer footLeftBack = new ModelRenderer(this, 0, 57);
		footLeftBack.setRotationPoint(-1.9F, 12.0F, 0.0F);
		footLeftBack.addBox(-4.0F, -6.0F, -3.0F, 5, 5, 1, scale);
		setRotationAngles(footLeftBack, 0.0F, angle180, 0.0F);
		ModelRenderer footLeftSide1 = new ModelRenderer(this, 0, 57);
		footLeftSide1.setRotationPoint(-1.9F, 12.0F, 0.0F);
		footLeftSide1.addBox(-2.0F, -6.0F, -1.0F, 5, 5, 1, scale);
		setRotationAngles(footLeftSide1, 0.0F, angle90, 0.0F);
		ModelRenderer footLeftSide2 = new ModelRenderer(this, 0, 57);
		footLeftSide2.setRotationPoint(-1.9F, 12.0F, 0.0F);
		footLeftSide2.addBox(-2.0F, -6.0F, -5.0F, 5, 5, 1, scale);
		setRotationAngles(footLeftSide2, 0.0F, angle270, 0.0F);
		bipedLeftLeg.addChild(footLeftFront);
		bipedLeftLeg.addChild(footLeftBack);
		bipedLeftLeg.addChild(footLeftSide1);
		bipedLeftLeg.addChild(footLeftSide2);
	}
	
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity)
	{
		if (entity instanceof EntityArmorStand)
		{
			EntityArmorStand entityArmorStand = (EntityArmorStand) entity;
			bipedHead.rotateAngleX = 0.017453292F * entityArmorStand.getHeadRotation().getX();
			bipedHead.rotateAngleY = 0.017453292F * entityArmorStand.getHeadRotation().getY();
			bipedHead.rotateAngleZ = 0.017453292F * entityArmorStand.getHeadRotation().getZ();
			bipedHead.setRotationPoint(0.0F, 1.0F, 0.0F);
			bipedBody.rotateAngleX = 0.017453292F * entityArmorStand.getBodyRotation().getX();
			bipedBody.rotateAngleY = 0.017453292F * entityArmorStand.getBodyRotation().getY();
			bipedBody.rotateAngleZ = 0.017453292F * entityArmorStand.getBodyRotation().getZ();
			bipedLeftArm.rotateAngleX = 0.017453292F * entityArmorStand.getLeftArmRotation().getX();
			bipedLeftArm.rotateAngleY = 0.017453292F * entityArmorStand.getLeftArmRotation().getY();
			bipedLeftArm.rotateAngleZ = 0.017453292F * entityArmorStand.getLeftArmRotation().getZ();
			bipedRightArm.rotateAngleX = 0.017453292F * entityArmorStand.getRightArmRotation().getX();
			bipedRightArm.rotateAngleY = 0.017453292F * entityArmorStand.getRightArmRotation().getY();
			bipedRightArm.rotateAngleZ = 0.017453292F * entityArmorStand.getRightArmRotation().getZ();
			bipedLeftLeg.rotateAngleX = 0.017453292F * entityArmorStand.getLeftLegRotation().getX();
			bipedLeftLeg.rotateAngleY = 0.017453292F * entityArmorStand.getLeftLegRotation().getY();
			bipedLeftLeg.rotateAngleZ = 0.017453292F * entityArmorStand.getLeftLegRotation().getZ();
			bipedLeftLeg.setRotationPoint(1.9F, 11.0F, 0.0F);
			bipedRightLeg.rotateAngleX = 0.017453292F * entityArmorStand.getRightLegRotation().getX();
			bipedRightLeg.rotateAngleY = 0.017453292F * entityArmorStand.getRightLegRotation().getY();
			bipedRightLeg.rotateAngleZ = 0.017453292F * entityArmorStand.getRightLegRotation().getZ();
			bipedRightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
			copyModelAngles(bipedHead, bipedHeadwear);
			return;
		}
		bipedHead.offsetY = entity instanceof EntityZombie && ((EntityZombie) entity).isVillager()? -Utility.PIXEL_F * 2 : 0.0F;
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
		if (entity instanceof EntityZombie)
		{
			boolean flag = entity instanceof EntityZombie && ((EntityZombie) entity).isArmsRaised();
			float f = MathHelper.sin(this.swingProgress * (float) Math.PI);
			float f1 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - swingProgress)) * (float) Math.PI);
			bipedRightArm.rotateAngleZ = 0.0F;
			bipedLeftArm.rotateAngleZ = 0.0F;
			bipedRightArm.rotateAngleY = -(0.1F - f * 0.6F);
			bipedLeftArm.rotateAngleY = 0.1F - f * 0.6F;
			float f2 = -(float) Math.PI / (flag ? 1.5F : 2.25F);
			bipedRightArm.rotateAngleX = f2;
			bipedLeftArm.rotateAngleX = f2;
			bipedRightArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
			bipedLeftArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
			bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
			bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
			bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
			bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
		}
	}
	
}