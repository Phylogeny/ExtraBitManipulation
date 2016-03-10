package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

public class Cone extends SlopedSymmetricalShape
{
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float y = getBitPosY(pos, i, j, k);
		if (isPointOffLine(y, centerY, semiDiameter)) return false;
		float dx = getBitPosDiffX(pos, i, j, k, centerX);
		float dz = getBitPosDiffZ(pos, i, j, k, centerZ);
		double dist = Math.sqrt(dx * dx + dz * dz);
		boolean inShape = isPointIn2DShape(y, semiDiameter, dist);
		return sculptHollowShape ? inShape && !(isPointIn2DShape(y, semiDiameterInset2, dist)
				&& !isPointOffLine(y)) : inShape;
	}
	
	private boolean isPointIn2DShape(float val, float semiDiameter2, double dist)
	{
		float d = semiDiameter * 2;
		return dist <= Math.abs(((centerY + (inverted ? -semiDiameter2 : semiDiameter2) - val) * semiDiameter) / d);
	}
	
}