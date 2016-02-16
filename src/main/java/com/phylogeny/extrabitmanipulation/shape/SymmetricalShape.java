package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.AxisAlignedBB;

public class SymmetricalShape extends Shape
{
	protected float semiDiameter, semiDiameterInset;
	
	public SymmetricalShape(float centerX, float centerY, float centerZ, float semiDiameter)
	{
		super(centerX, centerY, centerZ);
		this.semiDiameter = semiDiameter;
	}
	
	@Override
	public void setWallThickness(float wallThickness)
	{
		super.setWallThickness(wallThickness);
		semiDiameterInset = reduceLength(semiDiameter);
	}
	
	@Override
	protected AxisAlignedBB getBoundingBox()
	{
		return new AxisAlignedBB(centerX - semiDiameter, centerY - semiDiameter, centerZ - semiDiameter,
				centerX + semiDiameter, centerY + semiDiameter, centerZ + semiDiameter);
	}
	
}