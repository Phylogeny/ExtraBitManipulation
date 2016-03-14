package com.phylogeny.extrabitmanipulation.shape;

public class SlopedSymmetricalShape extends SymmetricalShape
{
	protected float height, semiDiameterInset2;
	private float insetMin, insetMax, insetMin2, insetMax2;
	
	@Override
	public void init(float centerX, float centerY, float centerZ, float radius, int rotation,
			boolean sculptHollowShape, float wallThickness, boolean openEnds)
	{
		super.init(centerX, centerY, centerZ, radius, rotation, sculptHollowShape, wallThickness, openEnds);
		float r = semiDiameter;
		height = r * 2;
		semiDiameterInset2 = r - (float) ((Math.sqrt(r * r + height * height) * wallThickness) / r);
		insetMax = this.centerY + semiDiameterInset;
		insetMin = this.centerY - semiDiameterInset;
		insetMax2 = this.centerY + semiDiameterInset2;
		insetMin2 = this.centerY - semiDiameterInset2;
	}
	
	protected boolean isPointOffLine(float val)
	{
		return inverted ? (!openEnds && val > insetMax) || val < insetMin2 :
			(!openEnds && val < insetMin) || val > insetMax2;
	}
	
}