package com.phylogeny.extrabitmanipulation.config;

public class ConfigShapeRenderPair
{
	public ConfigShapeRender boundingBox, envelopedShape;
	
	public ConfigShapeRenderPair(ConfigShapeRender boundingBox,
			ConfigShapeRender envelopedShape)
	{
		this.boundingBox = boundingBox;
		this.envelopedShape = envelopedShape;
	}
	
	public boolean hasEnvelopedShape()
	{
		return envelopedShape != null;
	}
	
}