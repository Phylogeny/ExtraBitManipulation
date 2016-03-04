package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

public class Cone extends SymmetricalShape
{
	private float semiDiameterInset2;
	
	@Override
	public void init(float centerX, float centerY, float centerZ, float radius, int rotation,
			boolean sculptHollowShape, float wallThickness, boolean openEnds)
	{
		super.init(centerX, centerY, centerZ, radius, rotation, sculptHollowShape, wallThickness, openEnds);
		semiDiameterInset2 = semiDiameterInset;
		float r = semiDiameter;
		float d = r * 2;
		semiDiameterInset = r - (float) ((Math.sqrt(r * r + d * d) * wallThickness) / r);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float y = getBitPosY(pos, i, j, k);
		if (isPointOffLine(y, centerY, semiDiameter)) return false;
		float dx = getBitPosDiffX(pos, i, j, k, centerX);
		float dz = getBitPosDiffZ(pos, i, j, k, centerZ);
		double dist = Math.sqrt(dx * dx + dz * dz);
		boolean inShape = isPointInCircle(y, centerY, semiDiameter, semiDiameter, dist);
		return sculptHollowShape ? inShape && !(isPointInCircle(y, centerY, semiDiameter, semiDiameterInset, dist)
				&& (openEnds || !(isPointOffLine(y, centerY, semiDiameterInset2)))) : inShape;
	}
	
	private boolean isPointOffLine(float val, float centerVal)
	{
		return inverted ? (!openEnds && val > centerVal + semiDiameterInset2) || val < centerVal - semiDiameterInset :
			(!openEnds && val < centerVal - semiDiameterInset) || val > centerVal + semiDiameterInset2;
	}
	
	private boolean isPointInCircle(float val, float centerVal, float semiDiameter, float semiDiameter2, double dist)
	{
		float d = semiDiameter * 2;
		return dist <= ((centerVal + semiDiameter2 - val) * semiDiameter) / d;
	}
	
}