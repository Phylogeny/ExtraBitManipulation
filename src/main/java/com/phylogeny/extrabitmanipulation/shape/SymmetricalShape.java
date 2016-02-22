package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.AxisAlignedBB;

public class SymmetricalShape extends Shape
{
	protected float semiDiameter, semiDiameterInset;
	
	public void init(float centerX, float centerY, float centerZ, float semiDiameter)
	{
		init(centerX, centerY, centerZ);
		this.semiDiameter = semiDiameter;
		semiDiameterInset = reduceLength(semiDiameter);
	}
	
	@Override
	protected AxisAlignedBB getBoundingBox()
	{
		return new AxisAlignedBB(centerX - semiDiameter, centerY - semiDiameter, centerZ - semiDiameter,
				centerX + semiDiameter, centerY + semiDiameter, centerZ + semiDiameter);
	}
	
}