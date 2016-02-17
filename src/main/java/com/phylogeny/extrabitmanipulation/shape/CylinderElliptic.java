package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

public class CylinderElliptic extends AsymmetricalShape
{
	
	@Override
	public void init(float centerX, float centerY, float centerZ,
			float a, float b, float c, float wallThickness, boolean isSolid)
	{
		super.init(centerX, centerY, centerZ, a, b, c, wallThickness, isSolid);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float y = getBitPosY(pos, j);
		if (isPointOffLine(y, centerY, b)) return false;
		float dx = getBitPosDiffX(pos, i, centerX);
		float dz = getBitPosDiffZ(pos, k, centerZ);
		boolean inShape = isPointInEllipse(dx, dz, a, c);
		return isSolid ? inShape : inShape && !(isPointInEllipse(dx, dz, aInset, cInset)
				&& !isPointOffLine(y, centerY, bInset));
	}
	
}