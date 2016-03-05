package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

public class ConeElliptic extends AsymmetricalShape
{
	private float aInset2, cInset2;
	private float insetMin, insetMax, insetMin2, insetMax2;
	
	@Override
	public void init(float centerX, float centerY, float centerZ, float a, float b, float c,
			int rotation, boolean sculptHollowShape, float wallThickness, boolean openEnds)
	{
		super.init(centerX, centerY, centerZ, a, b, c, rotation, sculptHollowShape, wallThickness, openEnds);
		float h = this.b * 2;
		float hsq = h * h;
		aInset2 = this.b - (float) ((Math.sqrt(this.a * this.a + hsq) * wallThickness) / this.a);
		cInset2 = this.b - (float) ((Math.sqrt(this.c * this.c + hsq) * wallThickness) / this.c);
		float bInset2 = Math.max(aInset2, cInset2);
		insetMax = this.centerY + bInset;
		insetMin = this.centerY - bInset;
		insetMax2 = this.centerY + bInset2;
		insetMin2 = this.centerY - bInset2;
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float y = getBitPosY(pos, i, j, k);
		if (isPointOffLine(y, centerY, b)) return false;
		float dx = getBitPosDiffX(pos, i, j, k, centerX);
		float dz = getBitPosDiffZ(pos, i, j, k, centerZ);
		boolean inShape = isPointInEllipse(y, b, b, dx, dz);
		return sculptHollowShape ? inShape && !(isPointInEllipse(y, aInset2, cInset2, dx, dz)
				&& !isPointOffLine(y)) : inShape;
	}
	
	private boolean isPointOffLine(float val)
	{
		return inverted ? (!openEnds && val > insetMax) || val < insetMin2 :
			(!openEnds && val < insetMin) || val > insetMax2;
	}
	
	private boolean isPointInEllipse(float val, float semiDiameter1, float semiDiameter2, float dv1, float dv2)
	{
		float d = b * 2;
		float h = centerY - val;
		float s1 = ((h + (inverted ? -semiDiameter1 : semiDiameter1)) * a) / d;
		float s2 = ((h + (inverted ? -semiDiameter2 : semiDiameter2)) * c) / d;
		return isPointInEllipse(dv1, dv2, s1, s2);
	}
	
}