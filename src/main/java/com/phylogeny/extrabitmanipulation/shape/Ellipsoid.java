package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

public class Ellipsoid extends AsymmetricalShape
{

	public void init(float centerX, float centerY, float centerZ,
			float a, float b, float c, float wallThickness, boolean isSolid)
	{
		super.init(centerX, centerY, centerZ, a, b, c, wallThickness, isSolid);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float dx = getBitPosDiff(pos, i, centerX);
		float dy = getBitPosDiff(pos, j, centerY);
		float dz = getBitPosDiff(pos, k, centerZ);
		boolean inShape = isPointInsideisEllipsoid(dx, dy, dz, a, b, c);
		return isSolid ? inShape : inShape && !isPointInsideisEllipsoid(dx, dy, dz, aInset, bInset, cInset);
	}
	
	private boolean isPointInsideisEllipsoid(float dx, float dy, float dz, float a, float b, float c)
	{
		float vx = dx / a;
		float vy = dy / b;
		float vz = dz / c;
		return vx * vx + vy * vy + vz * vz <= 1;
	}
	
}