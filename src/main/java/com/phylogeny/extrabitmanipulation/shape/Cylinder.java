package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.math.BlockPos;

public class Cylinder extends SymmetricalShape
{
	private float diameterSq, diameterInsetSq;
	
	@Override
	public void init(float centerX, float centerY, float centerZ, float radius, int direction,
			boolean sculptHollowShape, float wallThickness, boolean openEnds)
	{
		super.init(centerX, centerY, centerZ, radius, direction, sculptHollowShape, wallThickness, openEnds);
		diameterSq = semiDiameter * semiDiameter;
		diameterInsetSq = semiDiameterInset * semiDiameterInset;
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float y = getBitPosY(pos, i, j, k);
		if (isPointOffLine(y, centerY, semiDiameter)) return false;
		float dx = getBitPosDiffX(pos, i, j, centerX);
		float dz = getBitPosDiffZ(pos, j, k, centerZ);
		float dist = dx * dx + dz * dz;
		boolean inShape = isPointInCircle(diameterSq, dist);
		return sculptHollowShape ? inShape && !(isPointInCircle(diameterInsetSq, dist)
				&& (openEnds || !(isPointOffLine(y, centerY, semiDiameterInset)))) : inShape;
	}
	
	private boolean isPointInCircle(float semiDiameter, float dist)
	{
		return dist <= semiDiameter;
	}
	
}