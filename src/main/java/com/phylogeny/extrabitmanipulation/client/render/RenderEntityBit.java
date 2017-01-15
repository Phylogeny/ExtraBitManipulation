package com.phylogeny.extrabitmanipulation.client.render;

import javax.annotation.Nullable;

import com.phylogeny.extrabitmanipulation.entity.EntityBit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderEntityBit extends Render<EntityBit>
{
	
	public RenderEntityBit(RenderManager renderManager)
	{
		super(renderManager);
	}
	
	@Override
	public void doRender(EntityBit entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		if (entity.getBitStack() != null)
		{
			if (renderOutlines)
			{
				GlStateManager.enableColorMaterial();
				GlStateManager.enableOutlineMode(getTeamColor(entity));
			}
			GlStateManager.pushMatrix();
			GlStateManager.translate((float)x, (float)y, (float)z);
			Minecraft.getMinecraft().getRenderItem().renderItem(entity.getBitStack(), TransformType.GROUND);
			GlStateManager.popMatrix();
			if (renderOutlines)
			{
				GlStateManager.disableOutlineMode();
				GlStateManager.disableColorMaterial();
			}
		}
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
	
	@Override
	@Nullable
	protected ResourceLocation getEntityTexture(EntityBit entity)
	{
		return null;
	}
	
}