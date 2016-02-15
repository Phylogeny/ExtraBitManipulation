package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.AxisAlignedBB;

public class AsymmetricalShape extends Shape
{
	protected float a, b, c, aInset, bInset, cInset;
	
	public AsymmetricalShape(float centerX, float centerY, float centerZ, float a, float b, float c)
	{
		super(centerX, centerY, centerZ);
		this.a = a; 
		this.b = b;
		this.c = c;
		aInset = reduceLength(a);
		bInset = reduceLength(b);
		cInset = reduceLength(c);
	}
	
	@Override
	protected AxisAlignedBB getBoundingBox()
	{
		return new AxisAlignedBB(centerX - a, centerY - b, centerZ - c,
				centerX + a, centerY + b, centerZ + c);
	}
	
}