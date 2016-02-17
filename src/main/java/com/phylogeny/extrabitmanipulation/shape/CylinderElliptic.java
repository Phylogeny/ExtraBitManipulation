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
		float z = getBitPosZ(pos, k);
		if (isPointOffLine(z, centerZ, c)) return false;
		float dx = getBitPosDiffX(pos, i, centerX);
		float dy = getBitPosDiffY(pos, j, centerY);
		boolean inShape = isPointInEllipse(dx, dy, a, b);
		return isSolid ? inShape : inShape && !(isPointInEllipse(dx, dy, aInset, bInset)
				&& !isPointOffLine(z, centerZ, cInset));
	}
	
}