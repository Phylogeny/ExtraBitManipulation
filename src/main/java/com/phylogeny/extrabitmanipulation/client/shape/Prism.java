package com.phylogeny.extrabitmanipulation.client.shape;

import net.minecraft.client.renderer.GlStateManager;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Quadric;

public class Prism extends Quadric
{
	private boolean isPryamid, isTriangular;
	
	public Prism(boolean isPryamid, boolean isTriangular)
	{
		this.isPryamid = isPryamid;
		this.isTriangular = isTriangular;
	}
	
	public void draw(float radius, boolean isOpen)
	{
		float slope = isPryamid ? radius : 0;
		float slope2 = isTriangular ? radius : 0;
		boolean isCube = !isPryamid && !isTriangular;
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, radius, isCube ? -radius : 0);
		drawSquare(radius, isPryamid, slope, slope2);
		GlStateManager.translate(0, -radius * 2, 0);
		GlStateManager.scale(1, -1, 1);
		drawSquare(radius, isPryamid, slope, slope2);
		GlStateManager.popMatrix();
		
		GlStateManager.rotate(90, 0, 0, 1);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, radius, isCube ? -radius : 0);
		if (!isTriangular)
		{
			drawSquare(radius, isPryamid, slope, 0);
		}
		GlStateManager.translate(0, -radius * 2, 0);
		GlStateManager.scale(1, -1, 1);
		drawSquare(radius, isPryamid, slope, 0);
		GlStateManager.popMatrix();
		
		GlStateManager.rotate(90, 1, 0, 0);
		
		if (!isOpen)
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, isCube ? radius : radius * 2, -radius);
			drawSquare(radius, false, isTriangular ? radius : 0, 0);
			GlStateManager.popMatrix();
		}
		
		if (!isPryamid && !isOpen)
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, isCube ? -radius : 0, -radius);
			drawSquare(radius, false, isTriangular ? radius : 0, 0);
			GlStateManager.popMatrix();
		}
	}
	
	private void drawSquare(float radius, boolean isSlanted, float slope, float slope2)
	{
		float height = radius * 2;
		int i;
		float x, y, z;
		float inc = (float) (height / 15);
		float ratio = (radius / height) * 4F;
		float halfRadius = -radius * 0.5F;
		GL11.glBegin(GL11.GL_LINES);
		for (i = 0; i <= 15; i++)
		{
			x = i * inc - radius;
			y = slope2 > 0 ? halfRadius + x / ratio : 0;
			GL11.glVertex3f(slope > 0 ? 0 : x, isSlanted ? -slope : y, 0);
			GL11.glVertex3f(x, y, height);
		}
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINES);
		for (i = 0; i <= 15; i++)
		{
			z = i * inc;
			x = z / ratio;
			y = isSlanted ? x - radius : 0;
			if (slope == 0) x = radius;
			float s = slope2;
			if (isPryamid && slope2 > 0) s *= 1 * (z / height);
			GL11.glVertex3f(-x, y - s, z);
			GL11.glVertex3f(x, y, z);
		}
		GL11.glEnd();
	}
	
}