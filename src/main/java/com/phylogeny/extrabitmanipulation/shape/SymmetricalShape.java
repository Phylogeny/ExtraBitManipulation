package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.AxisAlignedBB;

public class SymmetricalShape extends Shape
{
	protected float semiDiameter;
	
	public SymmetricalShape(float centerX, float centerY, float centerZ, float semiDiameter)
	{
		super(centerX, centerY, centerZ);
		this.semiDiameter = semiDiameter;
	}
	
	@Override
	protected AxisAlignedBB getBoundingBox()
	{
		return new AxisAlignedBB(centerX - semiDiameter, centerY - semiDiameter, centerZ - semiDiameter,
				centerX + semiDiameter, centerY + semiDiameter, centerZ + semiDiameter);
	}
	
}