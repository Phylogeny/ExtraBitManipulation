package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

public class SquarePyramid extends SlopedSymmetricalShape
{
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float y = getBitPosY(pos, i, j, k);
		if (isPointOffLine(y, centerY, semiDiameter)) return false;
		float dx = getBitPosDiffX(pos, i, j, k, centerX);
		float dz = getBitPosDiffZ(pos, i, j, k, centerZ);
		boolean inShape = isPointIn2DShape(y, semiDiameter, dx, dz);
		return sculptHollowShape ? inShape && !(isPointIn2DShape(y, semiDiameterInset2, dx, dz)
				&& !isPointOffLine(y)) : inShape;
	}
	
	private boolean isPointIn2DShape(float val, float semiDiameter2, float dv1, float dv2)
	{
		float d = semiDiameter * 2;
		float s = Math.abs(((centerY + (inverted ? -semiDiameter2 : semiDiameter2) - val) * semiDiameter) / d);
		return isPointInRectangle(dv1, dv2, s, s);
	}
	
}