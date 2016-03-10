package com.phylogeny.extrabitmanipulation.shape;

public class ConeElliptic extends SlopedAsymmetricalShape
{
	
	@Override
	protected boolean isPointIn2DShape(float val, float semiDiameter1, float semiDiameter2, float dv1, float dv2)
	{
		float d = b * 2;
		float h = centerY - val;
		float s1 = ((h + (inverted ? -semiDiameter1 : semiDiameter1)) * a) / d;
		float s2 = ((h + (inverted ? -semiDiameter2 : semiDiameter2)) * c) / d;
		return isPointInEllipse(dv1, dv2, s1, s2);
	}
	
}