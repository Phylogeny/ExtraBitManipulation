package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

public class Cone extends SlopedSymmetricalShape
{
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float y = getBitPosY(pos, i, j, k);
		if (isPointOffLine(y, centerY, semiDiameter)) return false;
		float dx = getBitPosDiffX(pos, i, j, centerX);
		float dz = getBitPosDiffZ(pos, j, k, centerZ);
		double dist = Math.sqrt(dx * dx + dz * dz);
		boolean inShape = isPointInCone(y, semiDiameter, dist);
		return sculptHollowShape ? inShape && !(isPointInCone(y, semiDiameterInset2, dist)
				&& !isPointOffLine(y)) : inShape;
	}
	
	private boolean isPointInCone(float val, float semiDiameter2, double dist)
	{
		return dist <= Math.abs(((centerY + (inverted ? -semiDiameter2 : semiDiameter2) - val) * semiDiameter) / height);
	}
	
}