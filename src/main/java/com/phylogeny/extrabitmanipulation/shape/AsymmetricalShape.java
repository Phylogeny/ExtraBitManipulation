package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.AxisAlignedBB;

public abstract class AsymmetricalShape extends Shape
{
	protected float a, b, c, aInset, bInset, cInset;
	protected boolean isEquilateral;
	
	public void init(float centerX, float centerY, float centerZ, float a, float b, float c,
			int direction, boolean sculptHollowShape, float wallThickness, boolean openEnds)
	{
		init(centerX, centerY, centerZ, direction, sculptHollowShape, wallThickness, openEnds);
		float v;
		if (this.direction > 1)
		{
			if (this.direction > 3)
			{
				v = a;
				a = b;
				b = v;
			}
			else
			{
				v = c;
				c = b;
				b = v;
			}
		}
		this.a = a; 
		this.b = b;
		this.c = c;
		aInset = reduceLength(this.a);
		bInset = reduceLength(this.b);
		cInset = reduceLength(this.c);
	}
	
	public void setEquilateral(boolean isEquilateral)
	{
		this.isEquilateral = isEquilateral;
	}
	
	@Override
	protected AxisAlignedBB getBoundingBox()
	{
		return new AxisAlignedBB(centerX - a, centerY - b, centerZ - c, centerX + a, centerY + b, centerZ + c);
	}
	
	protected boolean isPointInEllipse(float dv1, float dv2, float s1, float s2)
	{
		float v1 = dv1 / s1;
		float v2 = dv2 / s2;
		return v1 * v1 + v2 * v2 <= 1;
	}
	
}