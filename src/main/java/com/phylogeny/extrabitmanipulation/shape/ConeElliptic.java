package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

public class ConeElliptic extends AsymmetricalShape
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
		boolean inShape = isPointInEllipse(z, centerZ, c, a, b, dx, dy);
		return isSolid ? inShape : inShape && !(isPointInEllipse(z, centerZ, cInset, aInset, bInset, dx, dy)
				&& !isPointOffLine(z, centerZ, cInset));
	}
	
	private boolean isPointInEllipse(float val, float centerVal, float semiDiameter, float s1, float s2, float dv1, float dv2)
	{
		float d = semiDiameter * 2;
		float h = centerVal + semiDiameter - val;
		float s3 = (h * s1) / d;
		float s4 = (h * s2) / d;
		return isPointInEllipse(dv1, dv2, s3, s4);
	}
	
}