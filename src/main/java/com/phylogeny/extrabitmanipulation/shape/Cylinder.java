package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

public class Cylinder extends SymmetricalShape
{
	
	@Override
	public void init(float centerX, float centerY, float centerZ, float radius, int rotation,
			boolean sculptHollowShape, float wallThickness, boolean openEnds)
	{
		super.init(centerX, centerY, centerZ, radius, rotation, sculptHollowShape, wallThickness, openEnds);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float y = getBitPosY(pos, i, j, k);
		if (isPointOffLine(y, centerY, semiDiameter)) return false;
		float dx = getBitPosDiffX(pos, i, j, k, centerX);
		float dz = getBitPosDiffZ(pos, i, j, k, centerZ);
		float dist = dx * dx + dz * dz;
		boolean inShape = isPointInCircle(semiDiameter, dist);
		return sculptHollowShape ? inShape && !(isPointInCircle(semiDiameterInset, dist)
				&& (openEnds || !(isPointOffLine(y, centerY, semiDiameterInset)))) : inShape;
	}
	
	private boolean isPointInCircle(float semiDiameter, float dist)
	{
		return dist / (semiDiameter * semiDiameter) <= 1;
	}
	
}