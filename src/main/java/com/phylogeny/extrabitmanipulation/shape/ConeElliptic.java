package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

public class ConeElliptic extends AsymmetricalShape
{
	private float aInset2, bInset2, cInset2;
	private boolean inverted;
	
	@Override
	public void init(float centerX, float centerY, float centerZ, float a, float b, float c,
			int rotation, boolean sculptHollowShape, float wallThickness, boolean openEnds)
	{
		super.init(centerX, centerY, centerZ, a, b, c, rotation, sculptHollowShape, wallThickness, openEnds);
		float h = this.b * 2;
		float hsq = h * h;
		aInset2 = this.b - (float) ((Math.sqrt(this.a * this.a + hsq) * wallThickness) / this.a);
		cInset2 = this.b - (float) ((Math.sqrt(this.c * this.c + hsq) * wallThickness) / this.c);
		bInset2 = Math.max(aInset2, cInset2);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float y = getBitPosY(pos, i, j, k);
		if (isPointOffLine(y, centerY, b)) return false;
		float dx = getBitPosDiffX(pos, i, j, k, centerX);
		float dz = getBitPosDiffZ(pos, i, j, k, centerZ);
		boolean inShape = isPointInEllipse(y, centerY, b, b, b, a, c, dx, dz);
		return sculptHollowShape ? inShape && !(isPointInEllipse(y, centerY, b, aInset2, cInset2, a, c, dx, dz)
				&& !isPointOffLine(y, centerY, bInset, bInset2)) : inShape;
	}
	
	private boolean isPointOffLine(float val, float centerVal, float semiDiameter, float semiDiameter2)
	{
		return inverted ? (!openEnds && val > centerVal + semiDiameter) || val < centerVal - semiDiameter2 :
			(!openEnds && val < centerVal - semiDiameter) || val > centerVal + semiDiameter2;
	}
	
	private boolean isPointInEllipse(float val, float centerVal, float semiDiameter,
			float semiDiameter2, float semiDiameter3, float s1, float s2, float dv1, float dv2)
	{
		float d = semiDiameter * 2;
		float h = centerVal - val;
		float s3 = ((h + (inverted ? -semiDiameter2 : semiDiameter2)) * s1) / d;
		float s4 = ((h + (inverted ? -semiDiameter3 : semiDiameter3)) * s2) / d;
		return isPointInEllipse(dv1, dv2, s3, s4);
	}
	
}