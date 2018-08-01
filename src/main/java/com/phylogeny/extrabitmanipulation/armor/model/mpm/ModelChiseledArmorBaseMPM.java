package com.phylogeny.extrabitmanipulation.armor.model.mpm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.phylogeny.extrabitmanipulation.reference.Configs;

import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import noppes.mpm.client.model.ModelBipedAlt;
import noppes.mpm.client.model.ModelScaleRenderer;
import noppes.mpm.constants.EnumParts;

public class ModelChiseledArmorBaseMPM extends ModelBipedAlt
{
	protected float scale;
	
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
	
	protected ModelScaleRenderer createScaledModel(int texOffX, int texOffY, EnumParts part)
	{
		ModelScaleRenderer model = new ModelScaleRenderer(this, texOffX, texOffY, part);
		model.textureHeight = textureHeight;
		model.textureWidth = textureWidth;
		Map<EnumParts, List<ModelScaleRenderer>> map = (Map<EnumParts, List<ModelScaleRenderer>>)
				ObfuscationReflectionHelper.getPrivateValue(ModelBipedAlt.class, this, "map");
		List<ModelScaleRenderer> list = map.get(part);
		if (list == null)
		{
		  map.put(part, list = new ArrayList());
		}
		list.add(model);
		return model;
	}
	
}