package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

public abstract class SlopedAsymmetricalShape extends AsymmetricalShape
{
	protected float height, aInset2, cInset2;
	private float insetMin, insetMax, insetMin2, insetMax2;
	
	@Override
	public void init(float centerX, float centerY, float centerZ, float a, float b, float c,
			int direction, boolean sculptHollowShape, float wallThickness, boolean openEnds)
	{
		super.init(centerX, centerY, centerZ, a, b, c, direction, sculptHollowShape, wallThickness, openEnds);
		height = this.b * 2;
		float hsq = height * height;
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
		if (isPointOffLine(y, centerY, b))
			return false;
		
		float dx = getBitPosDiffX(pos, i, j, centerX);
		float dz = getBitPosDiffZ(pos, j, k, centerZ);
		boolean inShape = isPointIn2DShape(y, b, b, dx, dz);
		return sculptHollowShape ? inShape && !(isPointIn2DShape(y, aInset2, cInset2, dx, dz) && !isPointOffLine(y)) : inShape;
	}
	
	protected boolean isPointOffLine(float val)
	{
		return inverted ? (!openEnds && val > insetMax) || val < insetMin2 : (!openEnds && val < insetMin) || val > insetMax2;
	}
	
	protected abstract boolean isPointIn2DShape(float val, float semiDiameter1, float semiDiameter2, float dv1, float dv2);
	
}