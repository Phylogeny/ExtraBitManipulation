package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

public class Cone extends SymmetricalShape
{
	
	@Override
	public void init(float centerX, float centerY, float centerZ, float radius, float wallThickness, boolean isSolid)
	{
		super.init(centerX, centerY, centerZ, radius, wallThickness, isSolid);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float y = getBitPosY(pos, j);
		if (isPointOffLine(y, centerY, semiDiameter)) return false;
		float dx = getBitPosDiffX(pos, i, centerX);
		float dz = getBitPosDiffZ(pos, k, centerZ);
		double dist = Math.sqrt(dx * dx + dz * dz);
		boolean inShape = isPointInCircle(y, centerY, semiDiameter, dist);
		return isSolid ? inShape : inShape && !(isPointInCircle(y, centerY, semiDiameterInset, dist)
				&& !(isPointOffLine(y, centerY, semiDiameterInset)));
	}
	
	private boolean isPointInCircle(float val, float centerVal, float semiDiameter, double dist)
	{
		float d = semiDiameter * 2;
		return dist <= ((centerVal + semiDiameter - val) * semiDiameter) / d;
	}
	
}