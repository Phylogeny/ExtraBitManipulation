package com.phylogeny.extrabitmanipulation.armor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.phylogeny.extrabitmanipulation.reference.Configs;

import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import noppes.mpm.client.model.ModelBipedAlt;
import noppes.mpm.client.model.ModelScaleRenderer;
import noppes.mpm.constants.EnumParts;

public class ModelChiseledArmorBaseMPM extends ModelBipedAlt
{
	protected float scale;
	private static Field map;
	
	public ModelChiseledArmorBaseMPM(float scale)
	{
		super(scale);
		textureWidth = 86;
		textureHeight = 64;
		scale = Configs.armorZFightingBufferScale;
	}
	
	protected void setRotationAngles(ModelRenderer modelRenderer, float angleX, float angleY, float angleZ)
	{
		modelRenderer.rotateAngleX = angleX;
		modelRenderer.rotateAngleY = angleY;
		modelRenderer.rotateAngleZ = angleZ;
	}
	
	public static void initReflectionFieldsClient()
	{
		map = ReflectionHelper.findField(ModelBipedAlt.class, "map");
	}
	
	protected ModelScaleRenderer createScaledModel(int texOffX, int texOffY, EnumParts part)
	{
		ModelScaleRenderer model = new ModelScaleRenderer(this, texOffX, texOffY, part);
		model.textureHeight = textureHeight;
		model.textureWidth = textureWidth;
		Map<EnumParts, List<ModelScaleRenderer>> map;
		try
		{
			map = (Map<EnumParts, List<ModelScaleRenderer>>) ModelChiseledArmorBaseMPM.map.get(this);
			List<ModelScaleRenderer> list = map.get(part);
			if (list == null)
			{
			  map.put(part, list = new ArrayList());
			}
			list.add(model);
		}
		catch (IllegalArgumentException | IllegalAccessException e) {}
		return model;
	}
	
}