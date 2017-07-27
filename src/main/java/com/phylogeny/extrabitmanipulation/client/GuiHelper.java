package com.phylogeny.extrabitmanipulation.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiHelper
{
	private static final int OFFSET_MAX = 400;
	
	public static void glScissor(int x, int y, int width, int height)
	{
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		int scaleFactor = getScaleFactor();
		GL11.glScissor(x * scaleFactor, Minecraft.getMinecraft().displayHeight - (y + height) * scaleFactor, width * scaleFactor, height * scaleFactor);
	}
	
	public static void glScissorDisable()
	{
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	public static int getScaleFactor()
	{
		return (new ScaledResolution(Minecraft.getMinecraft())).getScaleFactor();
	}
	
	public static GuiScreen getOpenGui()
	{
		return Minecraft.getMinecraft().currentScreen;
	}
	
	public static boolean isCursorInsideBox(AxisAlignedBB box, int mouseX, int mouseY)
	{
		return box.expandXyz(1).isVecInside(new Vec3d(mouseX, mouseY, 0));
	}
	
	public static void drawRect(double left, double top, double right, double bottom, int color)
	{
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, (color >> 24 & 255) / 255.0F);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		buffer.pos(left, bottom, 0.0D).endVertex();
		buffer.pos(right, bottom, 0.0D).endVertex();
		buffer.pos(right, top, 0.0D).endVertex();
		buffer.pos(left, top, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
	
	public static void drawTexturedRect(double left, double top, double right, double bottom)
	{
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
		GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		Tessellator t = Tessellator.getInstance();
		VertexBuffer buffer = t.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(left, top, 0).tex(0, 0).endVertex();
		buffer.pos(left, bottom, 0).tex(0, 1).endVertex();
		buffer.pos(right, bottom, 0).tex(1, 1).endVertex();
		buffer.pos(right, top, 0).tex(1, 0).endVertex();
		t.draw();
		GlStateManager.disableBlend();
	}
	
	public static Pair<Float, Float> changeScale(float scale, float amount, float max)
	{
		amount *= scale;
		float previewStackInitialScale = scale;
		scale += amount;
		if (scale < 0.1)
		{
			scale = 0.1F;
			return new ImmutablePair<Float, Float>(scale, (previewStackInitialScale - scale) / amount);
		}
		if (scale > max)
		{
			scale = max;
			return new ImmutablePair<Float, Float>(scale, (scale - previewStackInitialScale) / amount);
		}
		return new ImmutablePair<Float, Float>(scale, 1.0F);
	}

	public static Triple<Vec3d, Vec3d, Float> dragObject(int clickedMouseButton, float deltaX, float deltaY, Vec3d translationInitialVec,
			Vec3d rotationVec, float scale, float scaleMax, float rotationMultiplier, boolean affectRotation)
	{
		MutableTriple<Vec3d, Vec3d, Float> triple = new MutableTriple<Vec3d, Vec3d, Float>(translationInitialVec, rotationVec, scale);
		if (clickedMouseButton == 0)
		{
			if (GuiScreen.isShiftKeyDown() || GuiScreen.isCtrlKeyDown())
			{
				triple.setRight(changeScale(scale, deltaY * 0.05F, scaleMax).getLeft());
			}
			else if (affectRotation)
			{
				double angleX = rotationVec.xCoord - (deltaY / scale) * rotationMultiplier;
				double angleY = rotationVec.yCoord - (deltaX / scale) * rotationMultiplier;
				if (angleX < -90 || angleX > 90)
					angleX = 90 * (angleX > 0 ? 1 : -1);
				
				triple.setMiddle(new Vec3d(angleX, angleY, 0));
			}
		}
		else if (clickedMouseButton == 1)
		{
			double offsetX = translationInitialVec.xCoord - deltaX;
			if (offsetX < -OFFSET_MAX || offsetX > OFFSET_MAX)
				offsetX = OFFSET_MAX * (offsetX > 0 ? 1 : -1);
			
			double offsetY = translationInitialVec.yCoord - deltaY;
			if (offsetY < -OFFSET_MAX || offsetY > OFFSET_MAX)
				offsetY = OFFSET_MAX * (offsetY > 0 ? 1 : -1);
			
			triple.setLeft(new Vec3d(offsetX, offsetY, 0));
		}
		return triple;
	}

	public static Pair<Vec3d, Float> scaleObjectWithMouseWheel(GuiScreen screen, AxisAlignedBB box, Vec3d translationVec, float scale, float scaleMax)
	{
		MutablePair<Vec3d, Float> pair = new MutablePair<Vec3d, Float>(translationVec, scale);
		if (Mouse.getEventDWheel() == 0)
			return pair;
		
		int mouseX = Mouse.getEventX() * screen.width / screen.mc.displayWidth;
		int mouseY = screen.height - Mouse.getEventY() * screen.height / screen.mc.displayHeight - 1;
		if (!GuiHelper.isCursorInsideBox(box, mouseX, mouseY))
			return pair;
		
		Pair<Float, Float> scaleNew = changeScale(scale, Mouse.getEventDWheel() * 0.005F, scaleMax);
		pair.setRight(scaleNew.getLeft());
		float remainder = scaleNew.getRight();
		if (remainder == 0)
			return pair;
		
		int x = mouseX - (int) (translationVec.xCoord + box.maxX / 2.0F + box.minX / 2.0F);
		int y = mouseY - (int) (translationVec.yCoord + box.maxY / 2.0F + box.minY / 2.0F);
		float offset = (Mouse.getEventDWheel() > 0 ? -1 : 1) * 0.15F * remainder;
		pair.setLeft(translationVec.addVector(x * offset, y * offset, 0));
		return pair;
	}
	
}