package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

public class CylinderElliptic extends AsymmetricalShape
{
	
	@Override
	public void init(float centerX, float centerY, float centerZ, float a, float b, float c,
			int rotation, boolean sculptHollowShape, float wallThickness, boolean openEnds)
	{
		super.init(centerX, centerY, centerZ, a, b, c, rotation, sculptHollowShape, wallThickness, openEnds);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float y = getBitPosY(pos, i, j, k);
		if (isPointOffLine(y, centerY, b)) return false;
		float dx = getBitPosDiffX(pos, i, j, k, centerX);
		float dz = getBitPosDiffZ(pos, i, j, k, centerZ);
		boolean inShape = isPointInEllipse(dx, dz, a, c);
		return sculptHollowShape ? inShape && !(isPointInEllipse(dx, dz, aInset, cInset)
				&& (openEnds || !isPointOffLine(y, centerY, bInset))) : inShape;
	}
	
}