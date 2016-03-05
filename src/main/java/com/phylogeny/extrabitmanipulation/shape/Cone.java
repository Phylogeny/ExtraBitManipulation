package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

public class Cone extends SymmetricalShape
{
	private float insetMin, insetMax, insetMin2, insetMax2;
	
	@Override
	public void init(float centerX, float centerY, float centerZ, float radius, int rotation,
			boolean sculptHollowShape, float wallThickness, boolean openEnds)
	{
		super.init(centerX, centerY, centerZ, radius, rotation, sculptHollowShape, wallThickness, openEnds);
		float r = semiDiameter;
		float d = r * 2;
		float semiDiameterInset2 = r - (float) ((Math.sqrt(r * r + d * d) * wallThickness) / r);
		insetMax = this.centerY + semiDiameterInset;
		insetMin = this.centerY - semiDiameterInset;
		insetMax2 = this.centerY + semiDiameterInset2;
		insetMin2 = this.centerY - semiDiameterInset2;
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float y = getBitPosY(pos, i, j, k);
		if (isPointOffLine(y, centerY, semiDiameter)) return false;
		float dx = getBitPosDiffX(pos, i, j, k, centerX);
		float dz = getBitPosDiffZ(pos, i, j, k, centerZ);
		double dist = Math.sqrt(dx * dx + dz * dz);
		boolean inShape = isPointInCircle(y, semiDiameter, dist);
		return sculptHollowShape ? inShape && !(isPointInCircle(y, semiDiameterInset, dist)
				&& !isPointOffLine(y)) : inShape;
	}
	
	private boolean isPointOffLine(float val)
	{
		return inverted ? (!openEnds && val > insetMax) || val < insetMin2 :
			(!openEnds && val < insetMin) || val > insetMax2;
	}
	
	private boolean isPointInCircle(float val, float semiDiameter2, double dist)
	{
		float d = semiDiameter * 2;
		return dist <= Math.abs(((centerY + (inverted ? -semiDiameter2 : semiDiameter2) - val) * semiDiameter) / d);
	}
	
}