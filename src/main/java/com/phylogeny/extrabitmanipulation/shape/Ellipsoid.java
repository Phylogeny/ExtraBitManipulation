package com.phylogeny.extrabitmanipulation.shape;

import com.phylogeny.extrabitmanipulation.reference.SculptSettings;

import net.minecraft.util.BlockPos;

public class Ellipsoid extends AsymmetricalShape
{
	
	@Override
	public void init(float centerX, float centerY, float centerZ, float a, float b, float c)
	{
		super.init(centerX, centerY, centerZ, a, b, c);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float dx = getBitPosDiffX(pos, i, centerX);
		float dy = getBitPosDiffY(pos, j, centerY);
		float dz = getBitPosDiffZ(pos, k, centerZ);
		boolean inShape = isPointInsideisEllipsoid(dx, dy, dz, a, b, c);
		return SculptSettings.SCULPT_HOLLOW_SHAPE ? inShape && !isPointInsideisEllipsoid(dx, dy, dz, aInset, bInset, cInset) : inShape;
	}
	
	private boolean isPointInsideisEllipsoid(float dx, float dy, float dz, float a, float b, float c)
	{
		float vx = dx / a;
		float vy = dy / b;
		float vz = dz / c;
		return vx * vx + vy * vy + vz * vz <= 1;
	}
	
}