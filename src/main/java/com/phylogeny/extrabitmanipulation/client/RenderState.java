package com.phylogeny.extrabitmanipulation.client;

import java.util.List;
import java.util.concurrent.Callable;

import com.phylogeny.extrabitmanipulation.reference.Reference;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.pipeline.LightUtil;

public class RenderState
{
	private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	
	public static void renderStateIntoGUI(final IBlockState state, int x, int y)
	{
		BlockModelShapes blockModelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
		IBakedModel model = blockModelShapes.getModelForState(state);
		boolean emptyModel;
		try
		{
			boolean missingModel = isMissingModel(blockModelShapes, model);
			emptyModel = missingModel || model.getGeneralQuads().isEmpty();
			if (!missingModel && emptyModel)
			{
				for (EnumFacing enumfacing : EnumFacing.values())
				{
					if (!model.getFaceQuads(enumfacing).isEmpty())
					{
						emptyModel = false;
						break;
					}
				}
			}
		}
		catch (Exception e)
		{
			emptyModel = true;
		}
		Block block = state.getBlock();
		ItemStack stack = new ItemStack(block, 1, block.getMetaFromState(state));
		if (isNullItem(block, stack))
			stack = null;
		
		boolean isVanillaChest = block == Blocks.chest || block == Blocks.ender_chest || block == Blocks.trapped_chest;
		if (stack != null && emptyModel)
		{
			model = getItemModelWithOverrides(stack);
			if (!isVanillaChest && isMissingModel(blockModelShapes, model))
			{
				stack = new ItemStack(block);
				if (isNullItem(block, stack))
					stack = null;
				
				if (stack != null)
					model = getItemModelWithOverrides(stack);
			}
		}
		boolean renderAsTileEntity = stack != null && (model.isBuiltInRenderer() || isVanillaChest);
		try
		{
			renderStateModelIntoGUI(model, stack, renderAsTileEntity, x, y, 0, 0, -1);
		}
		catch (Throwable throwable)
		{
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering block state in " + Reference.MOD_ID + " bit mapping GUI");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Block state being rendered");
			crashreportcategory.addCrashSectionCallable("Block State", new Callable<String>()
			{
				@Override
				public String call() throws Exception
				{
					return String.valueOf(state);
				}
			});
			if (stack != null)
			{
				final ItemStack stack2 = stack.copy();
				crashreportcategory.addCrashSectionCallable("State's Item Type", new Callable<String>()
				{
					@Override
					public String call() throws Exception
					{
						return String.valueOf(stack2.getItem());
					}
				});
				crashreportcategory.addCrashSectionCallable("State's Item Aux", new Callable<String>()
				{
					@Override
					public String call() throws Exception
					{
						return String.valueOf(stack2.getMetadata());
					}
				});
				crashreportcategory.addCrashSectionCallable("State's Item NBT", new Callable<String>()
				{
					@Override
					public String call() throws Exception
					{
						return String.valueOf(stack2.getTagCompound());
					}
				});
				crashreportcategory.addCrashSectionCallable("State's Item Foil", new Callable<String>()
				{
					@Override
					public String call() throws Exception
					{
						return String.valueOf(stack2.hasEffect());
					}
				});
			}
			throw new ReportedException(crashreport);
		}
	}
	
	public static IBakedModel getItemModelWithOverrides(ItemStack stack)
	{
		return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
	}
	
	private static boolean isNullItem(final Block block, ItemStack stack)
	{
		return stack.getItem() == null || block == Blocks.standing_banner || block == Blocks.barrier;
	}
	
	private static boolean isMissingModel(BlockModelShapes blockModelShapes, IBakedModel model)
	{
		return model.equals(blockModelShapes.getModelManager().getMissingModel());
	}
	
	public static void renderStateModelIntoGUI(IBakedModel model, ItemStack stack,
			boolean renderAsTileEntity, int x, int y, float angleX, float angleY, float scale)
	{
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		GlStateManager.pushMatrix();
		textureManager.bindTexture(TextureMap.locationBlocksTexture);
		textureManager.getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(770, 771);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		setupGuiTransform(x, y, model);
		renderState(model, stack, renderAsTileEntity, angleX, angleY, scale);
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
		textureManager.bindTexture(TextureMap.locationBlocksTexture);
		textureManager.getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
	}
	
	private static void renderState(IBakedModel model, ItemStack stack, boolean renderAsTileEntity, float angleX, float angleY, float scale)
	{
		boolean autoScale = scale < 0;
		if (autoScale)
			scale = 1;
		
		GlStateManager.pushMatrix();
		if (autoScale)
		{
			try
			{
				int[] data;
				float x, y, z;
				float minX = Float.POSITIVE_INFINITY;
				float minY = Float.POSITIVE_INFINITY;
				float minZ = Float.POSITIVE_INFINITY;
				float maxX = Float.NEGATIVE_INFINITY;
				float maxY = Float.NEGATIVE_INFINITY;
				float maxZ = Float.NEGATIVE_INFINITY;
				for (BakedQuad quad : model.getGeneralQuads())
				{
					data = quad.getVertexData();
					for(int i = 0; i < 4; i++)
					{
						int index = 7 * i;
						x = Float.intBitsToFloat(data[index]);
						if (x < minX)
							minX = x;
						
						if (x > maxX)
							maxX = x;
						
						y = Float.intBitsToFloat(data[index + 1]);
						if (y < minY)
							minY = y;
						
						if (y > maxY)
							maxY = y;
						
						z = Float.intBitsToFloat(data[index + 2]);
						if (z < minZ)
							minZ = z;
						
						if (z > maxZ)
							maxZ = z;
					}
				}
				for (EnumFacing enumfacing : EnumFacing.values())
				{
					for (BakedQuad quad : model.getFaceQuads(enumfacing))
					{
						data = quad.getVertexData();
						for(int i = 0; i < 4; i++)
						{
							int index = 7 * i;
							x = Float.intBitsToFloat(data[index]);
							if (x < minX)
								minX = x;
							
							if (x > maxX)
								maxX = x;
							
							y = Float.intBitsToFloat(data[index + 1]);
							if (y < minY)
								minY = y;
							
							if (y > maxY)
								maxY = y;
							
							z = Float.intBitsToFloat(data[index + 2]);
							if (z < minZ)
								minZ = z;
							
							if (z > maxZ)
								maxZ = z;
						}
					}
				}
				scale = 1 / Math.max(1.0F, Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ)));
			}
			catch (Exception e) {}
			scale *= 0.65F;
		}
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.rotate(angleX, 1, 0, 0);
		GlStateManager.rotate(angleY, 0, 1, 0);
		
		if (renderAsTileEntity)
		{
			if (autoScale)
			{
				GlStateManager.rotate(45, 0, 1, 0);
				GlStateManager.rotate(30, 1, 0, 1);
			}
			GlStateManager.translate(-0.5F, -0.5F, -0.5F);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableRescaleNormal();
			TileEntityItemStackRenderer.instance.renderByItem(stack);
		}
		else
		{
			if (autoScale)
			{
				GlStateManager.rotate(225, 0, 1, 0);
				GlStateManager.rotate(30, -1, 0, -1);
			}
			GlStateManager.translate(-0.5F, -0.5F, -0.5F);
			renderModel(model, -1, stack);
			if (stack != null && stack.hasEffect())
				renderEffect(model);
		}
		GlStateManager.popMatrix();
	}
	
	private static void setupGuiTransform(int x, int y, IBakedModel model)
	{
		GlStateManager.translate(x + 6, y + 2, 100.0F + Minecraft.getMinecraft().getRenderItem().zLevel + 400);
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
	
	private static void renderModel(IBakedModel model, int color, ItemStack stack)
	{
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.ITEM);
		try
		{
			for (EnumFacing enumfacing : EnumFacing.values())
			{
				renderQuads(worldrenderer, model.getFaceQuads(enumfacing), color, stack);
			}
			renderQuads(worldrenderer, model.getGeneralQuads(), color, stack);
		}
		catch (Exception e) {}
		finally
		{
			tessellator.draw();
		}
	}
	
	private static void renderEffect(IBakedModel model)
	{
		GlStateManager.depthMask(false);
		GlStateManager.depthFunc(514);
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(768, 1);
		Minecraft.getMinecraft().getTextureManager().bindTexture(RES_ITEM_GLINT);
		GlStateManager.matrixMode(5890);
		GlStateManager.pushMatrix();
		GlStateManager.scale(8.0F, 8.0F, 8.0F);
		float f = Minecraft.getSystemTime() % 3000L / 3000.0F / 8.0F;
		GlStateManager.translate(f, 0.0F, 0.0F);
		GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
		renderModel(model, -8372020, null);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.scale(8.0F, 8.0F, 8.0F);
		float f1 = Minecraft.getSystemTime() % 4873L / 4873.0F / 8.0F;
		GlStateManager.translate(-f1, 0.0F, 0.0F);
		GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
		renderModel(model, -8372020, null);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.blendFunc(770, 771);
		GlStateManager.enableLighting();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
	}
	
	private static void renderQuads(WorldRenderer worldrenderer, List<BakedQuad> quads, int color, ItemStack stack)
	{
		boolean flag = color == -1 && stack != null;
		int i = 0;
		for (int j = quads.size(); i < j; ++i)
		{
			BakedQuad bakedquad = quads.get(i);
			int k = color;
			if (flag && bakedquad.hasTintIndex())
			{
				k = stack.getItem().getColorFromItemStack(stack, bakedquad.getTintIndex());
				if (EntityRenderer.anaglyphEnable)
					k = TextureUtil.anaglyphColor(k);
				
				k = k | -16777216;
			}
			LightUtil.renderQuadColor(worldrenderer, bakedquad, k);
		}
	}
	
}