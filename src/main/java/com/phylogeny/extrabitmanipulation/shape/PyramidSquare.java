package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.math.BlockPos;

public class PyramidSquare extends SlopedSymmetricalShape
{
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float y = getBitPosY(pos, i, j, k);
		if (isPointOffLine(y, centerY, semiDiameter))
			return false;
		
		float dx = getBitPosDiffX(pos, i, j, centerX);
		float dz = getBitPosDiffZ(pos, j, k, centerZ);
		boolean inShape = isPointInPyramid(y, semiDiameter, dx, dz);
		return sculptHollowShape ? inShape && !(isPointInPyramid(y, semiDiameterInset2, dx, dz) && !isPointOffLine(y)) : inShape;
	}
	
	private boolean isPointInPyramid(float val, float semiDiameter2, float dv1, float dv2)
	{
		float s = Math.abs(((centerY + (inverted ? -semiDiameter2 : semiDiameter2) - val) * semiDiameter) / height);
		return isPointInRectangle(dv1, dv2, s, s);
	}
	
}