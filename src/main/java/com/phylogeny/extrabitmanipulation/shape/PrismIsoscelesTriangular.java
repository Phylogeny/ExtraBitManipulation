package com.phylogeny.extrabitmanipulation.shape;

import com.phylogeny.extrabitmanipulation.reference.Utility;

import net.minecraft.util.BlockPos;

public class PrismIsoscelesTriangular extends AsymmetricalShape
{
	private float offsetCenter;
	private boolean isTwisted;
	
	@Override
	public void init(float centerX, float centerY, float centerZ, float a, float b, float c,
			int rotation, boolean sculptHollowShape, float wallThickness, boolean openEnds)
	{
		int roll = rotation / 6;
		rotation %= 6;
		isTwisted = roll % 2 == (rotation == 2 || rotation == 3 ? 0 : 1);
		super.init(centerX, centerY, centerZ, a, b, c, rotation, sculptHollowShape, wallThickness, openEnds);
		if (isEquilateral)
		{
			float contract = this.b - this.b * (float) Math.cos(0.523599);
			this.centerY -= inverted ? -contract : contract;
			this.b -= contract;
			bInset = reduceLength(this.b);
		}
		float hsq = this.b * 2;
		hsq *= hsq;
		float offset = isTwisted ? (((float) Math.sqrt(hsq + this.a * this.a) * wallThickness) / this.a) :
			(((float) Math.sqrt(hsq + this.c * this.c) * wallThickness) / this.c);
		offsetCenter = this.centerY - (inverted ? -offset : offset);
		if (inverted)
		{
			this.b *= -1;
		}
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		if (isTwisted)
		{
			float z = getBitPosZ(pos, i, j, k);
			if (isPointOffLine(z, centerZ, c)) return false;
			float x = getBitPosX(pos, i, j, k);
			float y = getBitPosY(pos, i, j, k);
			boolean inShape = isPointInTriangle(x, y, centerX, centerY, a, b);
			return sculptHollowShape ? inShape && !((inverted ? y <= centerY + bInset : y >= centerY - bInset)
					&& isPointInTriangle(x, y, centerX, offsetCenter, a, b)
					&& (openEnds || !isPointOffLine(z, centerZ, cInset))) : inShape;
		}
		else
		{
			float x = getBitPosX(pos, i, j, k);
			if (isPointOffLine(x, centerX, a)) return false;
			float z = getBitPosZ(pos, i, j, k);
			float y = getBitPosY(pos, i, j, k);
			boolean inShape = isPointInTriangle(z, y, centerZ, centerY, c, b);
			return sculptHollowShape ? inShape && !((inverted ? y <= centerY + bInset : y >= centerY - bInset)
					&& isPointInTriangle(z, y, centerZ, offsetCenter, c, b)
					&& (openEnds || !isPointOffLine(x, centerX, aInset))) : inShape;
		}
	}
	
}