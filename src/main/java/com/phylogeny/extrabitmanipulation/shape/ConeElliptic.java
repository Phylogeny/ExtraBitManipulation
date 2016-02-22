package com.phylogeny.extrabitmanipulation.shape;

import com.phylogeny.extrabitmanipulation.reference.SculptSettings;

import net.minecraft.util.BlockPos;

public class ConeElliptic extends AsymmetricalShape
{
	private float aInset2, bInset2, cInset2;
	
	@Override
	public void init(float centerX, float centerY, float centerZ,
			float a, float b, float c)
	{
		super.init(centerX, centerY, centerZ, a, b, c);
		float h = b * 2;
		float hsq = h * h;
		aInset2 = b - (float) ((Math.sqrt(a * a + hsq) * SculptSettings.WALL_THICKNESS) / a);
		cInset2 = b - (float) ((Math.sqrt(c * c + hsq) * SculptSettings.WALL_THICKNESS) / c);
		bInset2 = Math.max(aInset2, cInset2);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float y = getBitPosY(pos, j);
		if (isPointOffLine(y, centerY, b)) return false;
		float dx = getBitPosDiffX(pos, i, centerX);
		float dz = getBitPosDiffZ(pos, k, centerZ);
		boolean inShape = isPointInEllipse(y, centerY, b, b, b, a, c, dx, dz);
		return SculptSettings.SCULPT_HOLLOW_SHAPE ? inShape && !(isPointInEllipse(y, centerY, b, aInset2, cInset2, a, c, dx, dz)
				&& !isPointOffLine(y, centerY, bInset, bInset2)) : inShape;
	}
	
	private boolean isPointOffLine(float val, float centerVal, float semiDiameter, float semiDiameter2)
	{
		return (!SculptSettings.OPEN_ENDS && val < centerVal - semiDiameter) || val > centerVal + semiDiameter2;
	}
	
	private boolean isPointInEllipse(float val, float centerVal, float semiDiameter,
			float semiDiameter2, float semiDiameter3, float s1, float s2, float dv1, float dv2)
	{
		float d = semiDiameter * 2;
		float h = centerVal - val;
		float s3 = ((h + semiDiameter2) * s1) / d;
		float s4 = ((h + semiDiameter3) * s2) / d;
		return isPointInEllipse(dv1, dv2, s3, s4);
	}
	
}