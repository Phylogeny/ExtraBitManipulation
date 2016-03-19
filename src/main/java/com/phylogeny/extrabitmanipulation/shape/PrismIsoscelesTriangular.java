package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

public class PrismIsoscelesTriangular extends AsymmetricalShape
{
	private float offsetCenterZ;
	
	@Override
	public void init(float centerX, float centerY, float centerZ, float a, float b, float c,
			int rotation, boolean sculptHollowShape, float wallThickness, boolean openEnds)
	{
		super.init(centerX, centerY, centerZ, a, b, c, rotation, sculptHollowShape, wallThickness, openEnds);
		float hsq = this.c * 2;
		hsq *= hsq;
		offsetCenterZ = this.centerZ - ((float) Math.sqrt(hsq + this.a * this.a) * wallThickness) / this.a;
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float y = getBitPosY(pos, i, j, k);
		if (isPointOffLine(y, centerY, b)) return false;
		float x = getBitPosX(pos, i, j, k);
		float z = getBitPosZ(pos, i, j, k);
		boolean inShape = isPointInTriangle(x, z, centerX, centerZ, a, c, c);
		return sculptHollowShape ? inShape && !(z >= centerZ - cInset
				&& isPointInTriangle(x, z, centerX, offsetCenterZ, a, c, c)
				&& (openEnds || !isPointOffLine(y, centerY, bInset))) : inShape;
	}
	
}