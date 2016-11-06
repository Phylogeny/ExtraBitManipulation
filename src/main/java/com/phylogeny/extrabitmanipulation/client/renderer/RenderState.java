package com.phylogeny.extrabitmanipulation.client.renderer;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.pipeline.LightUtil;

public class RenderState
{
	private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	
	public static void renderStateModelIntoGUI(IBlockState state, int x, int y)
	{
		BlockRendererDispatcher rendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
		IBakedModel model = rendererDispatcher.getBlockModelShapes().getModelForState(state);
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		GlStateManager.pushMatrix();
		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		setupGuiTransform(x, y, model);
		model = ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.HEAD, false);
		renderState(state, model);
		GlStateManager.disableAlpha();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
	}
	
	private static void renderState(IBlockState state, IBakedModel model)
	{
		Block block = state.getBlock();
		ItemStack stack = new ItemStack(block, 1, block.getMetaFromState(state));
		if (stack.getItem() == null)
			stack = null;
		
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.65F, 0.65F, 0.65F);
		if (stack != null && (model.isBuiltInRenderer() || block == Blocks.CHEST || block == Blocks.ENDER_CHEST || block == Blocks.TRAPPED_CHEST))
		{
			GlStateManager.rotate(45, 0, 1, 0);
			GlStateManager.rotate(30, 1, 0, 1);
			GlStateManager.translate(-0.5F, -0.5F, -0.5F);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableRescaleNormal();
			TileEntityItemStackRenderer.instance.renderByItem(stack);
		}
		else
		{
			if (block instanceof BlockStairs)
			{
				GlStateManager.rotate(135, 0, 1, 0);
				GlStateManager.rotate(30, 1, 0, 1);
			}
			else
			{
				GlStateManager.rotate(225, 0, 1, 0);
				GlStateManager.rotate(30, -1, 0, -1);
			}
			GlStateManager.translate(-0.5F, -0.5F, -0.5F);
			renderModel(state, model, -1, stack);
			if (stack != null && stack.hasEffect())
				renderEffect(state, model);
		}
		GlStateManager.popMatrix();
	}
	
	private static void setupGuiTransform(int x, int y, IBakedModel model)
	{
		GlStateManager.translate(x + 6, y + 2, 100.0F + Minecraft.getMinecraft().getRenderItem().zLevel);
		GlStateManager.translate(8.0F, 8.0F, 0.0F);
		GlStateManager.scale(1.0F, -1.0F, 1.0F);
		GlStateManager.scale(16.0F, 16.0F, 16.0F);
		if (model.isGui3d())
		{
			GlStateManager.enableLighting();
		}
		else
		{
			GlStateManager.disableLighting();
		}
	}
	
	private static void renderModel(IBlockState state, IBakedModel model, int color, ItemStack stack)
	{
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.ITEM);
		for (EnumFacing enumfacing : EnumFacing.values())
		{
			renderQuads(vertexbuffer, model.getQuads(state, enumfacing, 0L), color, stack);
		}
		renderQuads(vertexbuffer, model.getQuads(state, null, 0L), color, stack);
		tessellator.draw();
	}
	
	private static void renderEffect(IBlockState state, IBakedModel model)
	{
		GlStateManager.depthMask(false);
		GlStateManager.depthFunc(514);
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
		Minecraft.getMinecraft().getTextureManager().bindTexture(RES_ITEM_GLINT);
		GlStateManager.matrixMode(5890);
		GlStateManager.pushMatrix();
		GlStateManager.scale(8.0F, 8.0F, 8.0F);
		float f = Minecraft.getSystemTime() % 3000L / 3000.0F / 8.0F;
		GlStateManager.translate(f, 0.0F, 0.0F);
		GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
		renderModel(state, model, -8372020, null);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.scale(8.0F, 8.0F, 8.0F);
		float f1 = Minecraft.getSystemTime() % 4873L / 4873.0F / 8.0F;
		GlStateManager.translate(-f1, 0.0F, 0.0F);
		GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
		renderModel(state, model, -8372020, null);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableLighting();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
	}
	
	private static void renderQuads(VertexBuffer vertexbuffer, List<BakedQuad> quads, int color, ItemStack stack)
	{
		boolean flag = color == -1 && stack != null;
		int i = 0;
		for (int j = quads.size(); i < j; ++i)
		{
			BakedQuad bakedquad = quads.get(i);
			int k = color;
			if (flag && bakedquad.hasTintIndex())
			{
				k = Minecraft.getMinecraft().getItemColors().getColorFromItemstack(stack, bakedquad.getTintIndex());
				if (EntityRenderer.anaglyphEnable)
					k = TextureUtil.anaglyphColor(k);
				
				k = k | -16777216;
			}
			LightUtil.renderQuadColor(vertexbuffer, bakedquad, k);
		}
	}
	
}