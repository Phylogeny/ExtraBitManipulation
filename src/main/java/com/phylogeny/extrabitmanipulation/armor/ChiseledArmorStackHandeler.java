package com.phylogeny.extrabitmanipulation.armor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import mod.chiselsandbits.render.BaseBakedPerspectiveModel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;
import com.phylogeny.extrabitmanipulation.reference.Utility;

public class ChiseledArmorStackHandeler extends ItemOverrideList
{
	private static final Map<NBTTagCompound, IBakedModel> movingPartsModelMap = new HashMap<NBTTagCompound, IBakedModel>();
	
	public ChiseledArmorStackHandeler()
	{
		super(new ArrayList<ItemOverride>());
	}
	
	public static void clearModelMap()
	{
		movingPartsModelMap.clear();
	}
	
	public static void removeFromModelMap(NBTTagCompound nbt)
	{
		movingPartsModelMap.remove(nbt);
	}
	
	@Override
	public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound armorNbt = ItemStackHelper.getArmorData(stack.getTagCompound());
			if (armorNbt.getBoolean(NBTKeys.ARMOR_NOT_EMPTY))
			{
				ItemChiseledArmor armor = (ItemChiseledArmor) stack.getItem();
				IBakedModel model = movingPartsModelMap.get(armorNbt);
				if (model == null)
				{
					DataChiseledArmorPiece armorPiece = new DataChiseledArmorPiece(stack.getTagCompound(),
							ArmorType.values()[armorNbt.getInteger(NBTKeys.ARMOR_TYPE)]);
					List<GlOperation> glOperationsPre = armorPiece.getGlobalGlOperations(true);
					List<GlOperation> glOperationsPost = armorPiece.getGlobalGlOperations(false);
					List<BakedQuad>[] quadsFace = new ArrayList[EnumFacing.VALUES.length];
					List<BakedQuad> quadsGeneric = new ArrayList<BakedQuad>();
					for (EnumFacing facing : EnumFacing.VALUES)
						quadsFace[facing.ordinal()] = new ArrayList<BakedQuad>();
					
					float[] bounds = new float[]{Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
							Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY};
					List<GlOperation> glOperationsItem;
					boolean found = false;
					for (int p = 0; p < 3; p++)
					{
						float scale = 1 + Configs.armorZFightingBufferScale + (armor.armorType == ArmorType.BOOTS && p == 0
								? Configs.armorZFightingBufferScaleRightFoot : 0.0F);
						float offset = armor.armorType == ArmorType.CHESTPLATE ? 6.0F : (armor.armorType == ArmorType.BOOTS ? 3.9F : 2.0F);
						float offsetX = p == 0 ? 0.0F : (Utility.PIXEL_F * (p == 1 ? offset : -offset));
						for (ArmorItem armorItem : armorPiece.getArmorItemsForPart(p))
						{
							if (armorItem.getStack() == null)
								continue;
							
							glOperationsItem = armorItem.getGlOperations();
							model = ClientHelper.getRenderItem().getItemModelWithOverrides(armorItem.getStack(), null, ClientHelper.getPlayer());
							Matrix4f matrix = generateMatrix(glOperationsPre, glOperationsItem, glOperationsPost);
							try
							{
								for (BakedQuad quad : model.getQuads(null, null, 0L))
								{
									quadsGeneric.add(createTransformedQuad(quad, null, armorItem.getStack(), bounds, scale, offsetX, matrix));
									found = true;
								}
								for (EnumFacing facing : EnumFacing.values())
								{
									for (BakedQuad quad : model.getQuads(null, facing, 0L))
									{
										quadsFace[facing.ordinal()].add(createTransformedQuad(quad, facing,
												armorItem.getStack(), bounds, scale, offsetX, matrix));
										found = true;
									}
								}
							}
							catch (Exception e) {}
						}
					}
					if (found)
					{
						scaleAndCenterQuads(quadsFace, quadsGeneric, bounds);
						model = new ChiseledArmorBakedModel(quadsFace, quadsGeneric);
						movingPartsModelMap.put(armorNbt, model);
					}
				}
				return Configs.armorStackModelRenderMode == ArmorStackModelRenderMode.ALWAYS_CUSTOM_MODEL || (Configs.armorStackModelRenderMode.ordinal() < 2
						&& (Configs.armorStackModelRenderMode == ArmorStackModelRenderMode.CUSTOM_MODEL_IF_HOLDING_SHIFT
							? GuiScreen.isShiftKeyDown() : !GuiScreen.isShiftKeyDown())) ? model : armor.getItemModel();
			}
		}
		return ((ItemChiseledArmor) stack.getItem()).getItemModel();
	}
	
	private Matrix4f generateMatrix(List<GlOperation> glOperationsPre, List<GlOperation> glOperationsItem, List<GlOperation> glOperationsPost)
	{
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		List<GlOperation> glOperations = new ArrayList<GlOperation>();
		glOperations.addAll(glOperationsPre);
		glOperations.addAll(glOperationsItem);
		glOperations.addAll(glOperationsPost);
		Matrix4f temp;
		Vector3f scaleVec;
		for (GlOperation glOperation : glOperations)
		{
			switch (glOperation.getType())
			{
				case ROTATION:		temp = new Matrix4f();
									temp.set(new AxisAngle4f(glOperation.getX(), glOperation.getY(),
											glOperation.getZ(), (float) Math.toRadians(glOperation.getAngle())));
									matrix.mul(temp);
									break;
				case TRANSLATION:	temp = new Matrix4f();
									temp.set(new Vector3f(glOperation.getX(), glOperation.getY(), glOperation.getZ()));
									matrix.mul(temp);
									break;
				case SCALE:			scaleVec = new Vector3f(glOperation.getX(), glOperation.getY(), glOperation.getZ());
									temp = new Matrix4f();
									temp.setIdentity();
									temp.m00 = scaleVec.x;
									temp.m11 = scaleVec.y;
									temp.m22 = scaleVec.z;
									matrix.mul(temp);
			}
		}
		temp = new Matrix4f();
		temp.set(new Vector3f(-0.5F, -0.5F, -0.5F));
		matrix.mul(temp);
		return matrix;
	}
	
	private void scaleAndCenterQuads(List<BakedQuad>[] quadsFace, List<BakedQuad> quadsGeneric, float[] bounds)
	{
		float dimX = bounds[3] - bounds[0];
		float dimY = bounds[4] - bounds[1];
		float dimZ = bounds[5] - bounds[2];
		float scale = 1 / Math.max(dimX, Math.max(dimY, dimZ));
		float translationX = 0.5F - (bounds[3] + bounds[0]) * 0.5F * scale;
		float translationY = 0.5F - (bounds[4] + bounds[1]) * 0.5F * scale;
		float translationZ = 0.5F - (bounds[5] + bounds[2]) * 0.5F * scale;
		for (BakedQuad quad : quadsGeneric)
			scaleAndCenterQuad(quad, scale, translationX, translationY, translationZ);
		
		for (EnumFacing facing : EnumFacing.values())
		{
			for (BakedQuad quad : quadsFace[facing.ordinal()])
				scaleAndCenterQuad(quad, scale, translationX, translationY, translationZ);
		}
	}
	
	private void scaleAndCenterQuad(BakedQuad quad, float scale, float translationX, float translationY, float translationZ)
	{
		int size = quad.getFormat().getIntegerSize();
		int[] data = quad.getVertexData();
		for (int i = 0; i < 4; i++)
		{
			int index = size * i;
			data[index] = Float.floatToRawIntBits(Float.intBitsToFloat(data[index]) * scale + translationX);
			data[index + 1] = Float.floatToRawIntBits(Float.intBitsToFloat(data[index + 1]) * scale + translationY);
			data[index + 2] = Float.floatToRawIntBits(Float.intBitsToFloat(data[index + 2]) * scale + translationZ);
		}
	}
	
	private BakedQuad createTransformedQuad(BakedQuad quad, EnumFacing facing, ItemStack stack, float[] bounds, float scale, float offsetX, Matrix4f matrix)
	{
		int size = quad.getFormat().getIntegerSize();
		int[] data = quad.getVertexData().clone();
		float x, y, z;
		Vector4f vec;
		int index;
		for (int i = 0; i < 4; i++)
		{
			index = size * i;
			x = Float.intBitsToFloat(data[index]);
			y = Float.intBitsToFloat(data[index + 1]);
			z = Float.intBitsToFloat(data[index + 2]);
			vec = new Vector4f(x, y, z, 1);
			matrix.transform(vec);
			x = vec.x * scale + offsetX;
			y = vec.y * scale;
			z = vec.z * scale;
			if (x < bounds[0])
				bounds[0] = x;
			
			if (x > bounds[3])
				bounds[3] = x;
			
			if (y < bounds[1])
				bounds[1] = y;
			
			if (y > bounds[4])
				bounds[4] = y;
			
			if (z < bounds[2])
				bounds[2] = z;
			
			if (z > bounds[5])
				bounds[5] = z;
			
			data[index] = Float.floatToRawIntBits(x);
			data[index + 1] = Float.floatToRawIntBits(y);
			data[index + 2] = Float.floatToRawIntBits(z);
		}
		return new BakedQuad(data, ClientHelper.getItemColors().getColorFromItemstack(stack, quad.getTintIndex()),
				facing, quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat());
	}
	
	public static class ChiseledArmorBakedModel extends BaseBakedPerspectiveModel
	{
		private final ItemOverrideList overrides;
		private List<BakedQuad>[] face;
		private List<BakedQuad> generic;
		
		public ChiseledArmorBakedModel(List<BakedQuad>[] face, List<BakedQuad> generic)
		{
			this();
			this.face = face;
			this.generic = generic;
		}
		
		public ChiseledArmorBakedModel()
		{
			overrides = new ChiseledArmorStackHandeler();
		}
		
		@Override
		public boolean isAmbientOcclusion()
		{
			return true;
		}
		
		@Override
		public boolean isGui3d()
		{
			return true;
		}
		
		@Override
		public boolean isBuiltInRenderer()
		{
			return false;
		}
		
		@Override
		public TextureAtlasSprite getParticleTexture()
		{
			return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
		}
		
		@Override
		public ItemCameraTransforms getItemCameraTransforms()
		{
			return ItemCameraTransforms.DEFAULT;
		}
		
		@Override
		public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand )
		{
			return generic == null ? Collections.EMPTY_LIST : (side != null ? face[side.ordinal()] : generic);
		}
		
		@Override
		public ItemOverrideList getOverrides()
		{
			return overrides;
		}
		
	}
	
	public static enum ArmorStackModelRenderMode
	{
		DEFAULT_MODEL_IF_HOLDING_SHIFT, CUSTOM_MODEL_IF_HOLDING_SHIFT, ALWAYS_CUSTOM_MODEL, ALWAYS_DEFAULT_MODEL;
	}
	
}